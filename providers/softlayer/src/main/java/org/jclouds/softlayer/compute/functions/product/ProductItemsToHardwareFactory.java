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

package org.jclouds.softlayer.compute.functions.product;

import com.google.common.base.Function;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.softlayer.PackageSpecific;
import org.jclouds.softlayer.compute.functions.product.guest.VirtualGuestProductItemsToHardware;
import org.jclouds.softlayer.compute.functions.product.server.HardwareServerProductItemsToHardware;
import org.jclouds.softlayer.compute.functions.product.server.SingleXeon3200SeriesProductItemsToHardware;
import org.jclouds.softlayer.domain.product.ProductItem;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is responsible for creating the proper product items to hardware conversion,
 * depending on the package id.
 *
 * @author Eli Polonsky
 */
@Singleton
public class ProductItemsToHardwareFactory implements PackageSpecific<Function<Iterable<ProductItem>, Hardware>> {

   private VirtualGuestProductItemsToHardware virtualGuestProductItemsToHardware;
   private HardwareServerProductItemsToHardware hardwareServerProductItemsToHardware;
   private SingleXeon3200SeriesProductItemsToHardware singleXeon3200SeriesProductItemsToHardware;

   @Inject
   public ProductItemsToHardwareFactory(VirtualGuestProductItemsToHardware virtualGuestProductItemsToHardware,
                                        HardwareServerProductItemsToHardware hardwareServerProductItemsToHardware,
                                        SingleXeon3200SeriesProductItemsToHardware singleXeon3200SeriesProductItemsToHardware) {
      this.virtualGuestProductItemsToHardware = virtualGuestProductItemsToHardware;
      this.hardwareServerProductItemsToHardware = hardwareServerProductItemsToHardware;
      this.singleXeon3200SeriesProductItemsToHardware = singleXeon3200SeriesProductItemsToHardware;
   }

   @Override
   public Function<Iterable<ProductItem>, Hardware> create(int packageId) {

      switch (packageId) {

         case 46:
            return virtualGuestProductItemsToHardware;
         case 50:
            return hardwareServerProductItemsToHardware;
         case 23:
            return singleXeon3200SeriesProductItemsToHardware;
         default:
            throw new UnsupportedOperationException("cannot find implementation for package id " + packageId);

      }

   }
}
