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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemCategory;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.jclouds.softlayer.compute.functions.product.ProductItemsToHardware.hardwareId;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Tests {@code SingleXeon3200SeriesProductItemsToHardware}
 *
 * @author Eli Polonsky
 */
@Test(groups = "unit")
public class SingleXeon3200SeriesProductItemsToHardwareTest {

   private SingleXeon3200SeriesProductItemsToHardware toHardware;
   private ProductItem cpuItem;
   private ProductItem ramItem;
   private ProductItem volumeItem;
   private ProductItem uplinkItem;
   private ProductItem bandwidthItem;

   @BeforeMethod
   public void setup() {

      toHardware = Guice.createInjector().getInstance(SingleXeon3200SeriesProductItemsToHardware.class);

      cpuItem = ProductItem.builder()
              .id(852)
              .description("Single Processor Quad Core Xeon 3230 - 2.60GHz (Kentsfield) - 2 x 4MB cache")
              .capacity(0F)
              .categories(ProductItemCategory.builder().categoryCode("server").build())
              .prices(ProductItemPrice.builder().id(1613).build())
              .build();

      ramItem = ProductItem.builder()
              .id(127)
              .description("8 GB DDR2 667")
              .capacity(8F)
              .categories(ProductItemCategory.builder().categoryCode("ram").build())
              .prices(ProductItemPrice.builder().id(21003).build())
              .build();

      volumeItem = ProductItem.builder().id(269).description("147GB SA-SCSI 15K RPM").capacity(147F).prices(
              ProductItemPrice.builder().id(827).build()).categories(
              ProductItemCategory.builder().categoryCode("disk0").build()).build();

      uplinkItem = ProductItem.builder().id(186)
              .description("10 Mbps Public & Private Networks")
              .capacity(10F).categories(ProductItemCategory.builder().id(26).categoryCode("port_speed").build())
              .prices(ProductItemPrice.builder().id(272).build())
              .build();

      bandwidthItem = ProductItem.builder().id(102)
              .description("10000 GB Bandwidth")
              .capacity(10000F).categories(ProductItemCategory.builder().id(10).categoryCode("bandwidth").build())
              .prices(ProductItemPrice.builder().id(131).build())
              .build();
   }

   @Test
   public void testHardwareId() {
      ProductItem item1 = ProductItem.builder().prices(ProductItemPrice.builder().id(123).build()).build();
      ProductItem item2 = ProductItem.builder().prices(ProductItemPrice.builder().id(456).build()).build();
      ProductItem item3 = ProductItem.builder().prices(ProductItemPrice.builder().id(789).build()).build();

      String id = hardwareId().apply(ImmutableList.of(item1, item2, item3));
      assertEquals("123,456,789", id);
   }

   @Test
   public void testHardware() {

      Hardware hardware = toHardware.apply(ImmutableSet.of(cpuItem, ramItem, volumeItem, uplinkItem, bandwidthItem));

      assertEquals("1613,21003,827,272,131", hardware.getId());

      List<? extends Processor> processors = hardware.getProcessors();
      assertEquals(1, processors.size());
      assertEquals(4.0, processors.get(0).getCores());
      assertEquals(2.6, processors.get(0).getSpeed());

      assertEquals(8, hardware.getRam());

      List<? extends Volume> volumes = hardware.getVolumes();
      assertEquals(1, volumes.size());
      Volume volume = volumes.get(0);
      assertEquals(147F, volume.getSize());
      assertEquals(Volume.Type.LOCAL, volume.getType());
      assertEquals(true, volume.isBootDevice());

   }

   @Test
   public void testHardwareWithTwoDisks() {
      ProductItem localVolumeItem = ProductItem.builder().id(4).description("500GB SATA II").capacity(500F).prices(
              ProductItemPrice.builder().id(987).build()).categories(
              ProductItemCategory.builder().categoryCode("disk1").build()).build();

      Hardware hardware = toHardware.apply(ImmutableSet.of(cpuItem, ramItem, volumeItem,localVolumeItem,
              uplinkItem, bandwidthItem));

      List<? extends Volume> volumes = hardware.getVolumes();
      assertEquals(2, volumes.size());
      Volume volume = volumes.get(0);
      assertEquals(147F, volume.getSize());
      assertEquals(Volume.Type.LOCAL, volume.getType());
      assertEquals(true, volume.isBootDevice());

      Volume volume1 = volumes.get(1);
      assertEquals(500F, volume1.getSize());
      assertEquals(Volume.Type.LOCAL, volume1.getType());
      assertEquals(false, volume1.isBootDevice());
   }

   @Test
   public void testHardwareWithBlankSpaceAtTheEndOfDescription() {

      cpuItem = cpuItem.toBuilder()
              .description("Single Processor Quad Core Xeon 3230 - 2.60GHz (Kentsfield) - 2 x 4MB cache ")
              .capacity(0F)
              .build();

      Hardware hardware = toHardware.apply(ImmutableSet.of(cpuItem, ramItem, volumeItem, uplinkItem, bandwidthItem));

      assertEquals("1613,21003,827,272,131", hardware.getId());

      List<? extends Processor> processors = hardware.getProcessors();
      assertEquals(1, processors.size());
      assertEquals(4.0, processors.get(0).getCores());

      assertEquals(8, hardware.getRam());

      List<? extends Volume> volumes = hardware.getVolumes();
      assertEquals(1, volumes.size());
      assertEquals(147F, volumes.get(0).getSize());
   }

}
