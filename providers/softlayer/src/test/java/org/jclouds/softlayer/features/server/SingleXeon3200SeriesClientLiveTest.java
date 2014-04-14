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

package org.jclouds.softlayer.features.server;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.compute.domain.Template;
import org.jclouds.softlayer.SingleXeon3200SeriesProperties;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductPackage;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.features.ProductPackageClientLiveTest;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Random;

import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
import static org.testng.Assert.assertEquals;

/**
 *
 * @author Eli Polonsky
 */
public class SingleXeon3200SeriesClientLiveTest extends HardwareServerClientLiveTest {

   private static final String TEST_HOSTNAME_PREFIX = "livetest";

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.putAll(new SingleXeon3200SeriesProperties().sharedProperties());
      return properties;
   }

   @Test(groups = "live")
   public void testVerifyOrder() {

      int pkgId = Iterables.find(api.getAccountClient().getReducedActivePackages(),
              named(ProductPackageClientLiveTest.SINGLE_XEON_3200_DEDICATED_SERVER_PACKAGE_NAME)).getId();
      ProductPackage productPackage = api.getProductPackageClient().getProductPackage(pkgId);

      HardwareServer server = HardwareServer.builder().domain("jclouds.org").hostname(
              TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      templateBuilder.locationId("37473").hardwareId("1613,21001,1517,272,36");
      Template template = templateBuilder.build();

      ProductOrder order = ProductOrder.builder()
              .packageId(productPackage.getId())
              .quantity(1)
              .location(template.getLocation().getId())
              .useHourlyPricing(false)
              .prices(getPrices(template))
              .hardwareServers(server).build();

      ProductOrder order2 = api().verifyHardwareServerOrder(order);
      assertEquals(order.getPrices(), order2.getPrices());
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
