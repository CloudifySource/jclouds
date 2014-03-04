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

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.softlayer.HardwareServerProperties;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductPackage;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.features.BaseSoftLayerClientLiveTest;
import org.jclouds.softlayer.features.ProductPackageClientLiveTest;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Random;
import java.util.Set;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.capacity;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
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

      int pkgId = Iterables.find(api.getAccountClient().getReducedActivePackages(),
              named(ProductPackageClientLiveTest.BARE_METAL_INSTANCE_PACKAGE_NAME)).getId();
      ProductPackage productPackage = api.getProductPackageClient().getProductPackage(pkgId);

      HardwareServer server = HardwareServer.builder().domain("jclouds.org").hostname(
               TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      templateBuilder.locationId("37473");
      Template template = templateBuilder.build();

      ProductOrder order = ProductOrder.builder()
              .packageId(productPackage.getId())
              .quantity(1)
              .location(template.getLocation().getId())
              .useHourlyPricing(true)
              .prices(getPrices(template, productPackage))
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

   protected Iterable<ProductItemPrice> getPrices(Template template, ProductPackage productPackage) {
      Builder<ProductItemPrice> result = ImmutableSet.builder();

      int imageId = Integer.parseInt(template.getImage().getId());
      result.add(ProductItemPrice.builder().id(imageId).build());

      Iterable<String> hardwareIds = Splitter.on(",").split(template.getHardware().getId());
      for (String hardwareId : hardwareIds) {
         int id = Integer.parseInt(hardwareId);
         result.add(ProductItemPrice.builder().id(id).build());
      }
      float portSpeed = 10f;
      ProductItem uplinkItem = find(productPackage.getItems(),
              and(capacity(portSpeed), categoryCode("port_speed")));
      result.add(get(uplinkItem.getPrices(), 0));
      result.addAll(defaultPrices);
      return result.build();
   }
}
