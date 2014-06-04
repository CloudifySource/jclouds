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
package org.jclouds.softlayer.features.server;

import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_DISK_CONTROLLER_ID;
import static org.testng.Assert.assertEquals;

import java.util.Properties;
import java.util.Random;

import org.jclouds.compute.domain.Template;
import org.jclouds.softlayer.DualXeon5500SeriesMultipleDisksProperties;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductPackage;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.features.ProductPackageClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * 
 * @author adaml
 *
 */
@Test(groups = "live", singleThreaded = true, testName = "DualXeon5500SeriesClientWithMultipleDisksLiveTest")
public class DualXeon5500SeriesClientWithMultipleDisksLiveTest extends HardwareServerClientLiveTest {
	   private static final String TEST_HOSTNAME_PREFIX = "livetest";
	   
	   @Override
	   protected Properties setupProperties() {
	      Properties properties = super.setupProperties();
	      properties.putAll(new DualXeon5500SeriesMultipleDisksProperties().sharedProperties());
	      //14, 471
	      //4281,471,471,471,471,4281
	      properties.setProperty(PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS, "3989,3989,3989,3989");
	      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_HARDWARE_DISK_CONTROLLER_ID, "489");
	      return properties;
	   }

	   @Test
	   public void testVerifyOrderDedicated() {
		   
	      int pkgId = Iterables.find(api.getAccountClient().getReducedActivePackages(),
	              named(ProductPackageClientLiveTest.DUAL_XEON_5500_DEDICATED_SERVER_MULTI_DISK_PACKAGE_NAME)).getId();
	      ProductPackage productPackage = api.getProductPackageClient().getProductPackage(pkgId);

	      HardwareServer server = HardwareServer.builder().domain("jclouds.org").hostname(
	              TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

	      //"2091,1956,1267,272,36"
	      templateBuilder.locationId("37473").hardwareId("1111,828,1091,1284,249").imageId("4321");
	      Template template = templateBuilder.build();
	      
	      

	      adapter.validateOrder(template, server);

//	      assertEquals(order.getPrices(), order2.getPrices());
	   }

	   protected ImmutableList<ProductItemPrice> getPrices(Template template) {
		   ImmutableList.Builder<ProductItemPrice> result = ImmutableList.builder();

	      int imageId = Integer.parseInt(template.getImage().getId());
	      result.add(ProductItemPrice.builder().id(imageId).build());

	      Iterable<String> hardwareIds = Splitter.on(",").split(template.getHardware().getId());
	      for (String hardwareId : hardwareIds) {
	         int id = Integer.parseInt(hardwareId);
	         result.add(ProductItemPrice.builder().id(id).build());
	      }
	      result.addAll(defaultPrices);
	      return result.build();
	   }
}
