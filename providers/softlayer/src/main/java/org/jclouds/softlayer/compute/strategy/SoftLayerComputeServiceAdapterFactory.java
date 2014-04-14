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
package org.jclouds.softlayer.compute.strategy;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.softlayer.PackageSpecific;
import org.jclouds.softlayer.compute.strategy.guest.SoftLayerVirtualGuestComputeServiceAdapter;
import org.jclouds.softlayer.compute.strategy.server.SoftLayerDualXeon5500SeriesComputeServiceAdapter;
import org.jclouds.softlayer.compute.strategy.server.SoftLayerHardwareServerComputeServiceAdapter;
import org.jclouds.softlayer.compute.strategy.server.SoftLayerSingleXeon3200SeriesComputeServiceAdapter;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.SoftLayerNode;
import org.jclouds.softlayer.domain.product.ProductItem;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is responsible for providing the appropriate adapter implementation for the given package id.
 *
 * @author Eli Polonsky
 */
@Singleton
public class SoftLayerComputeServiceAdapterFactory implements PackageSpecific<ComputeServiceAdapter<SoftLayerNode,
        Iterable<ProductItem>, ProductItem, Datacenter>>{

   private SoftLayerHardwareServerComputeServiceAdapter hardwareServerComputeServiceAdapter;
   private SoftLayerVirtualGuestComputeServiceAdapter virtualGuestComputeServiceAdapter;
   private SoftLayerSingleXeon3200SeriesComputeServiceAdapter singleXeon3200SeriesComputeServiceAdapter;
   private SoftLayerDualXeon5500SeriesComputeServiceAdapter dualXeon5500SeriesComputeServiceAdapter;

   @Inject
   public SoftLayerComputeServiceAdapterFactory(
           SoftLayerHardwareServerComputeServiceAdapter hardwareServerComputeServiceAdapter,
           SoftLayerVirtualGuestComputeServiceAdapter virtualGuestComputeServiceAdapter,
           SoftLayerSingleXeon3200SeriesComputeServiceAdapter singleXeon3200SeriesComputeServiceAdapter,
           SoftLayerDualXeon5500SeriesComputeServiceAdapter dualXeon5500SeriesComputeServiceAdapter) {
      this.hardwareServerComputeServiceAdapter = hardwareServerComputeServiceAdapter;
      this.virtualGuestComputeServiceAdapter = virtualGuestComputeServiceAdapter;
      this.singleXeon3200SeriesComputeServiceAdapter = singleXeon3200SeriesComputeServiceAdapter;
      this.dualXeon5500SeriesComputeServiceAdapter = dualXeon5500SeriesComputeServiceAdapter;
   }

   @Override
   public ComputeServiceAdapter<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter> create(int packageId) {

      switch (packageId) {
         case 46:
            return virtualGuestComputeServiceAdapter;
         case 50:
            return hardwareServerComputeServiceAdapter;
         case 23:
            return singleXeon3200SeriesComputeServiceAdapter;
         case 44:
        	 return dualXeon5500SeriesComputeServiceAdapter;
         case 42:
        	 return dualXeon5500SeriesComputeServiceAdapter;
         default:
            throw new UnsupportedOperationException("cannot find implementation of "
                    + ComputeServiceAdapter.class + "for package id " + packageId);
      }
   }

}
