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

package org.jclouds.softlayer.compute.functions.product.server;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.compute.functions.product.ProductItems;
import org.jclouds.softlayer.compute.functions.product.ProductItemsToHardware;
import org.jclouds.softlayer.domain.product.ProductItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;

/**
 * Converts a set of product items that belong to the hardware server (Bare Metal Instance) package to hardware.
 *
 * Hardware id in case of hardware server provisioning consists of the following parts:
 *
 * 1. Number of CPU Cores and Speed + Ram
 * 2. Disk Storage
 * 3. Uplink port speed
 *
 * All of these hardware configurations are configurable in separate, and are embedded within their own id's.
 *
 * Note that RAM in not configurable separately, but is part of the CPU description.
 *
 * For example:
 *
 * 1922,19,272 --> 4 x 2.0 GHz Core Bare Metal Instance - 4 GB Ram, 250GB SATA II, 10 Mbps Public & Private Networks
 *
 * @author Eli Polonsky
 *
 */
public class HardwareServerProductItemsToHardware implements Function<Iterable<ProductItem>, Hardware> {

   private static final String SERVER_DISK_CATEGORY_REGEX = "disk[0-9]";
   private static final String FIRST_SERVER_DISK = "disk0";
   private static final String STORAGE_AREA_NETWORK = "SAN";

   private static final String SERVER_CPU_DESCRIPTION_REGEX = "[0-9]+ x ([.0-9]+) GHz Core Bare Metal Instance - ([.0-9]+) GB Ram";

   private final Pattern serverDiskCategoryRegex;
   private final Pattern serverCpuDescriptionRegex;

   public HardwareServerProductItemsToHardware() {
      this.serverDiskCategoryRegex = checkNotNull(Pattern.compile(SERVER_DISK_CATEGORY_REGEX), "serverDiskCategoryRegex");
      this.serverCpuDescriptionRegex = checkNotNull(Pattern.compile(SERVER_CPU_DESCRIPTION_REGEX), "serverCpuDescriptionRegex");
   }

   @Override
   public Hardware apply(@Nullable Iterable<ProductItem> items) {

      HardwareBuilder hardwareBuilder = new HardwareBuilder();

      ProductItem ramAndCoresItem = get(filter(items, categoryCode("server_core")), 0);
      ProductItem volumeItem = get(filter(items, categoryCode("disk0")), 0);
      ProductItem uplinkItem = get(filter(items, categoryCode("port_speed")), 0);

      String hardwareId = ProductItemsToHardware.hardwareId().apply(ImmutableList.of(ramAndCoresItem, volumeItem,
              uplinkItem));

      hardwareBuilder.ids(hardwareId);

      Matcher cpuAndRamMatcher = serverCpuDescriptionRegex.matcher(ramAndCoresItem.getDescription().trim());

      if (cpuAndRamMatcher.matches() && cpuAndRamMatcher.groupCount() == 2) { // need RAM and CPU speed
         double cores = ProductItems.capacity().apply(ramAndCoresItem).doubleValue();
         hardwareBuilder.processor(new Processor(cores, Double.parseDouble(cpuAndRamMatcher.group(1))));
         hardwareBuilder.ram(Integer.parseInt(cpuAndRamMatcher.group(2)));
      } else {
         throw new IllegalStateException("failed matching [" + ramAndCoresItem.getDescription() + "] by " + SERVER_CPU_DESCRIPTION_REGEX);
      }

      return hardwareBuilder.volumes(Iterables.transform(filter(items, categoryCodeMatches(serverDiskCategoryRegex)),
              new Function<ProductItem, Volume>() {
                 @Override
                 public Volume apply(ProductItem item) {
                    float volumeSize = ProductItems.capacity().apply(item);
                    return new VolumeImpl(
                            item.getId() + "",
                            item.getDescription().contains(STORAGE_AREA_NETWORK) ? Volume.Type.SAN : Volume.Type.LOCAL,
                            volumeSize, null, categoryCode(FIRST_SERVER_DISK).apply(item), false);
                 }
              })).build();
   }
}
