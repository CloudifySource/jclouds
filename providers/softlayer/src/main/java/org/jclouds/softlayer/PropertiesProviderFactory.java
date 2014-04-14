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
package org.jclouds.softlayer;

import javax.inject.Inject;

/**
 * This class is responsible for providing the appropriate properties provider implementation for the given package id.
 *
 * @author Eli Polonsky
 */
public class PropertiesProviderFactory implements PackageSpecific<PropertiesProvider> {

   private SingleXeon3200SeriesProperties singleXeon3200SeriesProperties;
   private DualXeon5500SeriesProperties dualXeon5500SeriesProperties;
   private HardwareServerProperties hardwareServerProperties;
   private VirtualGuestProperties virtualGuestProperties;
   private DualXeon5500SeriesMultipleDisksProperties dualXeon5500SeriesMultipleDisksProperties;

   @Inject
   public PropertiesProviderFactory(SingleXeon3200SeriesProperties singleXeon3200SeriesProperties,
		   							DualXeon5500SeriesProperties dualXeon5500SeriesProperties,
		   							DualXeon5500SeriesMultipleDisksProperties dualXeon5500SeriesMultipleDisksProperties,
                                    HardwareServerProperties hardwareServerProperties,
                                    VirtualGuestProperties virtualGuestProperties) {
      this.singleXeon3200SeriesProperties = singleXeon3200SeriesProperties;
      this.dualXeon5500SeriesProperties = dualXeon5500SeriesProperties;
      this.dualXeon5500SeriesMultipleDisksProperties = dualXeon5500SeriesMultipleDisksProperties;
      this.hardwareServerProperties = hardwareServerProperties;
      this.virtualGuestProperties = virtualGuestProperties;
   }

   @Override
   public PropertiesProvider create(int packageId) {

      switch (packageId) {

         case 23:
            return singleXeon3200SeriesProperties;
         case 50:
            return hardwareServerProperties;
         case 46:
            return virtualGuestProperties;
         case 44:
             return dualXeon5500SeriesMultipleDisksProperties;
         case 42:
        	 return dualXeon5500SeriesProperties;
         default:
            throw new UnsupportedOperationException("cannot find properties provider for package id " + packageId);
      }

   }

}
