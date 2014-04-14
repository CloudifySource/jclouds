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
package org.jclouds.softlayer.compute.functions.product.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.itemId;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;

import java.util.regex.Pattern;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.compute.functions.product.ProductItems;
import org.jclouds.softlayer.compute.functions.product.ProductItemsToHardware;
import org.jclouds.softlayer.domain.product.ProductItem;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

public class DualXeon5500SeriesProductItemsToHardware extends HardwareServerProductItemsToHardware {
	   
	private static final String SERVER_DISK_CATEGORY_REGEX = "disk[0-11]";
	private static final String STORAGE_AREA_NETWORK = "SAN";

	private final Pattern serverDiskCategoryRegex;

	public DualXeon5500SeriesProductItemsToHardware() {
		this.serverDiskCategoryRegex = checkNotNull(Pattern.compile(SERVER_DISK_CATEGORY_REGEX), "serverDiskCategoryRegex");
	}

	@Override
	public Hardware apply(@Nullable Iterable<ProductItem> items) {

		HardwareBuilder hardwareBuilder = new HardwareBuilder();

		final ProductItem serverItem = get(filter(items, categoryCode("server")), 0);
		final ProductItem ramItem =    get(filter(items, categoryCode("ram")), 0);
		final ProductItem volumeItem = get(filter(items, categoryCode("disk0")), 0);
		final ProductItem uplinkItem = get(filter(items, categoryCode("port_speed")), 0);
		final ProductItem bandwidth = get(filter(items, categoryCode("bandwidth")), 0);

		ImmutableList<ProductItem> immutableHardwareItems = createImmutableHardwareItemList(items,
				serverItem, ramItem, volumeItem, uplinkItem, bandwidth);
		
		String pricesId = ProductItemsToHardware.hardwareId().apply(immutableHardwareItems);

		String itemsId = ProductItemsToHardware.providerHardwareId().apply(immutableHardwareItems);
		
		hardwareBuilder.id(pricesId).providerId(itemsId).processor(new Processor(8, 2.4)).ram(ramItem.getCapacity().intValue());

		return hardwareBuilder.volumes(Iterables.transform(filter(items, categoryCodeMatches(serverDiskCategoryRegex)),
				new Function<ProductItem, Volume>() {
			@Override
			public Volume apply(ProductItem item) {
				float volumeSize = ProductItems.capacity().apply(item);
				return new VolumeImpl(
						item.getId() + "",
						item.getDescription().contains(STORAGE_AREA_NETWORK) ? Volume.Type.SAN : Volume.Type.LOCAL,
								volumeSize, null, item.equals(volumeItem), false);
			}
		})).build();
	}

	private ImmutableList<ProductItem> createImmutableHardwareItemList(Iterable<ProductItem> items,
			final ProductItem serverItem, final ProductItem ramItem,
			final ProductItem volumeItem, final ProductItem uplinkItem,
			final ProductItem bandwidth) {
		Builder<ProductItem> hardwareItems = ImmutableList.builder();
		
		hardwareItems.add(serverItem, ramItem, volumeItem, uplinkItem, bandwidth);
		
		Iterable<ProductItem> additionalDisks = creareAdditionalDisksList(items);
		
		hardwareItems.addAll(additionalDisks);
		
		Iterable<ProductItem> diskController = filter(items, categoryCode("disk_controller"));
		if (diskController.iterator().hasNext()) {
			hardwareItems.add(get(diskController, 0));
		}
		
		return hardwareItems.build();
	}

	private Iterable<ProductItem> creareAdditionalDisksList(
			Iterable<ProductItem> items) {
		Builder<ProductItem> diskItems = ImmutableList.builder();
		final ProductItem firstDisk = get(filter(items, categoryCode("disk0")), 0);
		Iterable<ProductItem> additionalDisks = filter(items, categoryCodeMatches(serverDiskCategoryRegex));
		boolean firstDiskFound = false;
		for (ProductItem productItem : additionalDisks) {
			if (firstDisk.equals(productItem) && !firstDiskFound) {
				firstDiskFound = true;
			} else {
				diskItems.add(productItem);
			}
		}
		return diskItems.build();
	}

}
