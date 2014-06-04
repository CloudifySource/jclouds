/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jclouds.softlayer.compute.functions.product.guest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableList.Builder;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.compute.functions.product.ProductItems;
import org.jclouds.softlayer.compute.functions.product.ProductItemsToHardware;
import org.jclouds.softlayer.domain.product.ProductItem;

import javax.inject.Singleton;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.matches;

/**
 * Converts a set of product items that belong to the virtual guest (Cloud Server) package to hardware.
 *
 * Hardware id in case of virtual guest provisioning consists of the following parts:
 *
 * 1. Number of CPU Cores and Speed
 * 2. Amount of Ram
 * 3. Disk Storage
 * 4. Uplink port speed
 *
 * All of these hardware configurations are configurable in separate, and are embedded within their own id's.
 *
 * For example:
 *
 * 1640,2238,13899,272 --> 1 x 2.0 GHz Core, 6 GB Ram, 25 GB (LOCAL) Disk Storage, 10 Mbps Public & Private Networks
 *
 * @author Eli Polonsky
 */
@Singleton
public class VirtualGuestProductItemsToHardware implements Function<Iterable<ProductItem>, Hardware> {

   private static final String BANDWIDTH = "bandwidth";
private static final String GUEST_CPU_DESCRIPTION_REGEX = "(Private )?[0-9]+ x ([.0-9]+) GHz Core[s]?";
   private static final String GUEST_DISK_CATEGORY_REGEX =  "guest_disk[0-9]";
   private static final String RAM_CATEGORY = "ram";
   private static final String FIRST_GUEST_DISK = "guest_disk0";
   private static final String PORT_SPEED_CATEGORY = "port_speed";
   private static final double DEFAULT_CORE_SPEED = 2.0;
   private static final String STORAGE_AREA_NETWORK = "SAN";

   private final Pattern guestCpuDescriptionRegex;
   private final Pattern guestDiskCategoryRegex;

   public VirtualGuestProductItemsToHardware() {
      this.guestCpuDescriptionRegex = checkNotNull(Pattern.compile(GUEST_CPU_DESCRIPTION_REGEX), "guestCpuDescriptionRegex");
      this.guestDiskCategoryRegex = checkNotNull(Pattern.compile(GUEST_DISK_CATEGORY_REGEX), "guestDiskCategoryRegex");
   }

   @Override
   public Hardware apply(@Nullable Iterable<ProductItem> items) {

      ProductItem coresItem = getOnlyElement(filter(items, matches(guestCpuDescriptionRegex)));
      ProductItem ramItem = getOnlyElement(filter(items, categoryCode(RAM_CATEGORY)));
      ProductItem volumeItem = get(filter(items, categoryCode(FIRST_GUEST_DISK)), 0);
      ProductItem uplinkItem = get(filter(items, categoryCode(PORT_SPEED_CATEGORY)), 0);
      ProductItem bandwidth = get(filter(items, categoryCode(BANDWIDTH)), 0);

		ImmutableList<ProductItem> immutableGuestItems = createImmutableGuestItemList(items,
				coresItem, ramItem, volumeItem, uplinkItem, bandwidth);
      String hardwareId = ProductItemsToHardware.allHardwareIdPriceCombinations().apply(immutableGuestItems);
      String itemsId = ProductItemsToHardware.providerHardwareId().apply(immutableGuestItems);
      double cores = ProductItems.capacity().apply(coresItem).doubleValue();
      Matcher cpuMatcher = guestCpuDescriptionRegex.matcher(coresItem.getDescription());
      double coreSpeed = (cpuMatcher.matches()) ? Double.parseDouble(cpuMatcher.group(cpuMatcher.groupCount())) : DEFAULT_CORE_SPEED;
      int ram = ProductItems.capacity().apply(ramItem).intValue();

      return new HardwareBuilder().ids(hardwareId).providerId(itemsId).processors(ImmutableList.of(new Processor(cores, coreSpeed))).ram(
              ram)
              .hypervisor("XenServer")
              .volumes(
                      Iterables.transform(filter(items, categoryCodeMatches(guestDiskCategoryRegex)),
                              new Function<ProductItem, Volume>() {
                                 @Override
                                 public Volume apply(ProductItem item) {
                                    float volumeSize = ProductItems.capacity().apply(item);
                                    return new VolumeImpl(
                                            item.getId() + "",
                                            item.getDescription().contains(STORAGE_AREA_NETWORK) ? Volume.Type.SAN :
                                                    Volume.Type.LOCAL,
                                            volumeSize, null, categoryCode(FIRST_GUEST_DISK).apply(item), false);
                                 }
                              })).build();

   }
   
	private ImmutableList<ProductItem> createImmutableGuestItemList(Iterable<ProductItem> items,
			final ProductItem coresItem, final ProductItem ramItem,
			final ProductItem volumeItem, final ProductItem uplinkItem, 
			final ProductItem bandwidth) {
		
		final Builder<ProductItem> hardwareItems = ImmutableList.builder();
		
		hardwareItems.add(coresItem, ramItem, volumeItem, uplinkItem, bandwidth);
		
		final Iterable<ProductItem> additionalDisks = createAdditionalDisksList(items);
		
		hardwareItems.addAll(additionalDisks);
		
		return hardwareItems.build();
	}

	private Iterable<ProductItem> createAdditionalDisksList(
			Iterable<ProductItem> items) {
		Builder<ProductItem> diskItems = ImmutableList.builder();
		final ProductItem firstDisk = get(filter(items, categoryCode("guest_disk0")), 0);
		Iterable<ProductItem> additionalDisks = filter(items, categoryCodeMatches(guestDiskCategoryRegex));
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
