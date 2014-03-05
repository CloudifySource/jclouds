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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.softlayer.HardwareServerProperties;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.features.BaseSoftLayerClientLiveTest;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Random;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code HardwareServerClient}
 *
 * @author Eli Polonsky
 */
@Test(groups = "live")
public class HardwareServerClientLiveTest extends BaseSoftLayerClientLiveTest {

   private static final String TEST_HOSTNAME_PREFIX = "livetest";
   protected TemplateBuilder templateBuilder;

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.putAll(new HardwareServerProperties().sharedProperties());
      return properties;
   }

   @Test
   public void testListHardwareServers() throws Exception {
      Set<HardwareServer> response = api().listHardwareServers();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (HardwareServer hs : response) {
         HardwareServer newDetails = api().getHardwareServer(hs.getId());
         assertEquals(hs.getId(), newDetails.getId());
         checkHardwareServer(hs);
      }
   }

   @Test(groups = "live")
   public void testVerifyOrder() {

      HardwareServer server = HardwareServer.builder().domain("jclouds.org").hostname(
               TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder()
              .packageId(50)
              .quantity(1)
              .location("37473")
              .useHourlyPricing(true)
              .prices(getPrices(1922, 19, 272, 13963))
              .hardwareServers(server).build();

      ProductOrder order2 = api().verifyHardwareServerOrder(order);
      assertEquals(order.getPrices(), order2.getPrices());
   }

   protected Iterable<ProductItemPrice> defaultPrices;

   @Override
   protected SoftLayerClient create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      defaultPrices = injector.getInstance(Key.get(new TypeLiteral<Iterable<ProductItemPrice>>() {
      }));
      return injector.getInstance(SoftLayerClient.class);
   }

   protected HardwareServerClient api() {
      return api.getHardwareServerClient();
   }

   private void checkHardwareServer(HardwareServer hs) {
      if (hs.getBillingItemId() == -1)
         return;// Quotes and shutting down server

      assert hs.getAccountId() > 0 : hs;
      assert hs.getDomain() != null : hs;
      assert hs.getFullyQualifiedDomainName() != null : hs;
      assert hs.getHostname() != null : hs;
      assert hs.getId() > 0 : hs;
      assert hs.getPrimaryBackendIpAddress() != null : hs;
      assert hs.getPrimaryIpAddress() != null : hs;
   }

   private Iterable<ProductItemPrice> getPrices(Integer... prices) {
      Builder<ProductItemPrice> result = ImmutableSet.builder();
      for (Integer price : prices) {
         ProductItemPrice itemPrice = ProductItemPrice.builder().id(price).build();
         result.add(itemPrice);
      }
      result.addAll(defaultPrices);
      return result.build();
   }
}
