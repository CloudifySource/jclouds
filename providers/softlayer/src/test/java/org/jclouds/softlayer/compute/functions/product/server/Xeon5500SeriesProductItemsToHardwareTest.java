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

import org.jclouds.compute.domain.Hardware;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemCategory;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * 
 * @author adaml
 *
 */
@Test(groups = "unit")
public class Xeon5500SeriesProductItemsToHardwareTest {

		private DualXeon5500SeriesProductItemsToHardware toHardware;
		private ProductItem cpuItem;
		private ProductItem ramItem;
		private ProductItem volumeItem;
		private ProductItem uplinkItem;
		private ProductItem bandwidthItem;
		private ProductItem diskControllerItem;
		
		   @BeforeMethod
		   public void setup() {

		      toHardware = Guice.createInjector().getInstance(DualXeon5500SeriesProductItemsToHardware.class);

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
		      
		      diskControllerItem = ProductItem.builder().id(546)
		              .description("RAID 0")
		              .capacity(10000F).categories(ProductItemCategory.builder().id(778).categoryCode("disk_controller").build())
		              .prices(ProductItemPrice.builder().id(909).build())
		              .build();
		   }
		   
		   @Test
		   public void testHardwareWithMultipleDisks() {
		      ProductItem diskItemOne = ProductItem.builder().id(4).description("500GB SATA II").capacity(500F).prices(
		              ProductItemPrice.builder().id(987).itemId(4).build(), ProductItemPrice.builder().id(988).itemId(4).build()).categories(
		              ProductItemCategory.builder().categoryCode("disk1").id(12).build()).build();
		      
		      ProductItem diskItemTwo = ProductItem.builder().id(5).description("500GB SATA II").capacity(500F).prices(
		              ProductItemPrice.builder().id(1000).itemId(5).build(), ProductItemPrice.builder().id(1001).itemId(5).build()).categories(
		              ProductItemCategory.builder().categoryCode("disk2").id(13).build()).build();
		      
		      ProductItem diskItemThree = ProductItem.builder().id(6).description("500GB SATA II").capacity(500F).prices(
		              ProductItemPrice.builder().id(2000).itemId(6).build(), ProductItemPrice.builder().id(2001).itemId(6).build()).categories(
		              ProductItemCategory.builder().categoryCode("disk3").id(14).build()).build();
		      
		      Hardware hardware = toHardware.apply(ImmutableSet.of(cpuItem, ramItem, volumeItem, diskItemOne, diskItemTwo, diskItemThree, uplinkItem, bandwidthItem, diskControllerItem));
		      
		      // Check all possible price combos are accountable for.
		      Assert.assertEquals("1613,21003,827,272,131,909,987,1000,2000;" +
		      		"1613,21003,827,272,131,909,987,1000,2001;" +
		      		"1613,21003,827,272,131,909,987,1001,2000;" +
		      		"1613,21003,827,272,131,909,987,1001,2001;" +
		      		"1613,21003,827,272,131,909,988,1000,2000;" +
		      		"1613,21003,827,272,131,909,988,1000,2001;" +
		      		"1613,21003,827,272,131,909,988,1001,2000;" +
		      		"1613,21003,827,272,131,909,988,1001,2001", hardware.getId());

		      System.out.println(hardware.getId());
		   }


}
