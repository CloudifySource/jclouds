/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.softlayer.compute.strategy.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.itemId;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.priceId;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_USE_HOURLY_PRICING;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_DISK_CONTROLLER_ID;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductPackage;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList.Builder;


/**
 * Hardware adapter with multiple disk and RAID support.
 * 
 * @author adaml
 *
 */
@Singleton
public class SoftLayerDualXeon5500SeriesComputeServiceAdapter extends SoftLayerHardwareServerComputeServiceAdapter {

	private static final String SERVER_DISK_CATEGORY_REGEX = "disk[1-11]";
	private final Pattern serverDiskCategoryRegex;
	private String externalDisksId;
	private String diskControllerId;

	@Inject
	public SoftLayerDualXeon5500SeriesComputeServiceAdapter(SoftLayerClient client,
			HardwareServerHasLoginDetailsPresent serverHasLoginDetailsPresent,
			HardwareServerHasNoRunningTransactions serverHasNoActiveTransactionsTester,
			HardwareServerStartedTransactions serverHasActiveTransactionsTester,
			HardwareProductOrderApprovedAndServerIsPresent hardwareProductOrderApprovedAndServerIsPresent,
			HardwareProductOrderApprovedAndServerIsPresentAccordingToServerName hardwareProductOrderApprovedAndServerIsPresentByName,
			@Memoized Supplier<ProductPackage> productPackageSupplier,
			Iterable<ProductItemPrice> prices,
			@Named(PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY) long serverLoginDelay,
			@Named(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY) long activeTransactionsEndedDelay,
			@Named(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY) long activeTransactionsStartedDelay,
			@Named(PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY) long hardwareApprovedDelay,
			@Named(PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS) String multipleDisksHardwareId,
			@Named(PROPERTY_SOFTLAYER_SERVER_HARDWARE_DISK_CONTROLLER_ID) String diskControllerHardwareId,
			@Named(PROPERTY_SOFTLAYER_SERVER_HARDWARE_USE_HOURLY_PRICING) boolean useHourlyPricing) {
		super(client, serverHasLoginDetailsPresent, serverHasNoActiveTransactionsTester,
				serverHasActiveTransactionsTester,
				hardwareProductOrderApprovedAndServerIsPresent, hardwareProductOrderApprovedAndServerIsPresentByName, productPackageSupplier, prices, serverLoginDelay,
				activeTransactionsEndedDelay, activeTransactionsStartedDelay, hardwareApprovedDelay, useHourlyPricing);
		this.serverDiskCategoryRegex = checkNotNull(Pattern.compile(SERVER_DISK_CATEGORY_REGEX), "serverDiskCategoryRegex");
		this.externalDisksId = multipleDisksHardwareId;
		this.diskControllerId = diskControllerHardwareId;
	}

	@Override
	public Iterable<Iterable<ProductItem>> listHardwareProfiles() {

		final ImmutableSet.Builder<Iterable<ProductItem>> result = ImmutableSet.builder();
		final ProductPackage productPackage = productPackageSupplier.get();
		final Set<ProductItem> items = productPackage.getItems();
		final ImmutableList<ProductItem> disks = listDisksAndControllers(items);
		
		for (ProductItem cpuItem : filter(items, categoryCode("server"))) {
			for (ProductItem ramItem : filter(items, categoryCode("ram"))) {
				for (ProductItem firstDiskItem : filter(items, categoryCode("disk0"))) {
					for (ProductItem uplinkItem : filter(items, categoryCode("port_speed"))) {
						for (ProductItem bandwidth : filter(items, categoryCode("bandwidth"))) {

							Builder<ProductItem> hardwareBuilder = ImmutableList.builder();
							hardwareBuilder.add(cpuItem, ramItem,firstDiskItem, uplinkItem, bandwidth);
							hardwareBuilder.addAll(disks);
							result.add(hardwareBuilder.build());
						}
					}
				}
			}
		}

		return result.build();
	}

	private ImmutableList<ProductItem> listDisksAndControllers(Set<ProductItem> items) {
		
		Builder<ProductItem> disksAndControllerListBuilder = ImmutableList.builder();
		
		// There could only be one disk controller. RAID/NON-RAID.
		ProductItem diskControllerItem = get(filter(items, priceId(this.diskControllerId)), 0, null);
		if (diskControllerItem == null) {
			diskControllerItem = get(filter(items, itemId(this.diskControllerId)), 0, null);
		}
		if (diskControllerItem == null) {
			throw new NoSuchElementException("Failed listing hardwares having disk controller with ID: " 
					+ this.diskControllerId);
		}
		
		disksAndControllerListBuilder.add(diskControllerItem);
		
		if (!this.externalDisksId.equals("")) {
			Iterable<ProductItem> allDiskItems = filter(items, categoryCodeMatches(serverDiskCategoryRegex));
			ImmutableList<ProductItem> diskItemsList = listDisksByPrice(allDiskItems);
			
			if (diskItemsList == null) {
				diskItemsList = listDisksByItemId(allDiskItems);
			}
			
			if (diskItemsList == null) {
				throw new NoSuchElementException("Failed listing hardwares having additional disk items with ids: " 
						+ this.externalDisksId + ". disk items could not be resolved by item or price id.");
			}
			
			disksAndControllerListBuilder.addAll(diskItemsList);
		}

		return disksAndControllerListBuilder.build();
	}

	private ImmutableList<ProductItem> listDisksByPrice(
			final Iterable<ProductItem> diskItems) {
		
		Builder<ProductItem> diskList = ImmutableList.builder();
		Iterable<String> pricesId = Splitter.on(",").split(this.externalDisksId);
		
		for (String priceId : pricesId) {
			//price IDs are unique. expecting one result.
			Iterable<ProductItem> diskByPriceId = filter(diskItems, priceId(priceId));
			
			if (!diskByPriceId.iterator().hasNext()) {
				logger.debug("Additional disk with price id " + priceId + " was not found."
						+ " Assuming item id is used.");
				return null;
			}
			
			diskList.add(get(diskByPriceId, 0));
		}
		
		return diskList.build();
	}
	
	private ImmutableList<ProductItem> listDisksByItemId(
			Iterable<ProductItem> diskItems) {
		
		Builder<ProductItem> diskList = ImmutableList.builder();
		Iterable<String> itemsId = Splitter.on(",").split(this.externalDisksId);;
		
		for (String priceId : itemsId) {
			//item IDs are unique. expecting one result.
			Iterable<ProductItem> diskByItemId = filter(diskItems, itemId(priceId));
			
			if (!diskByItemId.iterator().hasNext()) {
				logger.warn("Additional disk with item id " + priceId + " was not found.");
				return null;
			}
			
			diskList.add(get(diskByItemId, 0));
		}
		
		return diskList.build();
	}
}
