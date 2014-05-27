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
package org.jclouds.softlayer.features.guest;

import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.VirtualGuestProperties;
import org.jclouds.softlayer.domain.guest.VirtualGuest;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductOrderReceipt;
import org.jclouds.softlayer.features.BaseSoftLayerClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VirtualGuestClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live")
public class VirtualGuestClientLiveTest extends BaseSoftLayerClientLiveTest {

   private static final String TEST_HOSTNAME_PREFIX = "livetest";
   private static final int PACKAGE_ID = 46;
   private TemplateBuilder templateBuilder;

   @Test
   public void testListVirtualGuests() throws Exception {
      Set<VirtualGuest> response = api().listVirtualGuests();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (VirtualGuest vg : response) {
         VirtualGuest newDetails = api().getVirtualGuest(vg.getId());
         assertEquals(vg.getId(), newDetails.getId());
         checkVirtualGuest(vg);
      }
   }
   
   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.putAll(new VirtualGuestProperties().sharedProperties());
      properties.setProperty(PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS, "922,922,922,922");
      return properties;
   }

   @Test(groups = "live")
   public void testCancelAndPlaceOrder() {
      for (VirtualGuest guest : api().listVirtualGuests()) {
         if (guest.getHostname().startsWith(TEST_HOSTNAME_PREFIX)) {
            if (guest.getBillingItemId() != -1) {
               api().cancelService(guest.getBillingItemId());
            }
         }
      }

      VirtualGuest guest = VirtualGuest.builder().domain("jclouds.org").hostname(
               TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder()
              .packageId(PACKAGE_ID)
              .quantity(1)
              .location("37473")
              .useHourlyPricing(true)
              .prices(getPrices(1640, 2238, 13899, 272, 2142))
              .virtualGuests(guest).build();

      ProductOrderReceipt receipt = api().orderVirtualGuest(order);
      ProductOrder order2 = receipt.getOrderDetails();
      assertEquals(order.getPrices(), order2.getPrices());
      assertNotNull(receipt);
   }

   @Test(groups = "live")
   public void testOrder() {

      VirtualGuest newGuest = VirtualGuest.builder().domain("jclouds.org").hostname(TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder()
              .packageId(PACKAGE_ID)
              .quantity(1)
              .location("37473")
              .useHourlyPricing(true)
              .prices(getPrices(1640, 2238, 13899, 272, 13945))
              .virtualGuests(newGuest).build();

      ProductOrder order2 = api().verifyVirtualGuestOrder(order);
      assertEquals(order.getPrices(), order2.getPrices());
   }

   /**
    * We pass an image id that is mapped to an Ubuntu OS.
    * But the Flex image global identifier we are using is built for CentOS only.
    * In this case, the image identifier overrides the image id declaration.
    *
    * Make sure that the order accepted will in fact provision a CentOS Image.
    */
   @Test
   public void testOrderWithImageTemplateGlobalIdentifierAndIncompatibleImageId() {

      VirtualGuest newGuest = VirtualGuest.builder().domain("jclouds.org").hostname(TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder()
            .packageId(PACKAGE_ID)
            .quantity(1)
            .location("37473")
            .useHourlyPricing(true)
            .prices(getPrices(1640, 2238, 13899, 272, 2142))
            .virtualGuests(newGuest)
            .imageTemplateGlobalIdentifier("5ad7a379-5252-4c19-a02a-1a5882f4fcbf").build();

      ProductOrder order2 = api().verifyVirtualGuestOrder(order);

      ProductItemPrice centOSProductItemPrice = ProductItemPrice.builder().id(13945).build();
      assertTrue(order2.getPrices().contains(centOSProductItemPrice));
      assertEquals(order.getPrices().size(), order2.getPrices().size());

   }

   /**
    * We dont pass an image id.
    * But the Flex image global identifier we are using is built for CentOS only.
    * In this case, the image identifier overrides the image id declaration.
    *
    * Make sure that the order accepted will in fact provision a CentOS Image.
    */
   @Test
   public void testOrderWithImageTemplateGlobalIdentifierAndNoImageId() {

      VirtualGuest newGuest = VirtualGuest.builder().domain("jclouds.org").hostname(TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder()
            .packageId(PACKAGE_ID)
            .quantity(1)
            .location("37473")
            .useHourlyPricing(true)
            .prices(getPrices(1640, 2238, 13899, 272))
            .virtualGuests(newGuest)
            .imageTemplateGlobalIdentifier("5ad7a379-5252-4c19-a02a-1a5882f4fcbf").build();

      ProductOrder order2 = api().verifyVirtualGuestOrder(order);

      ProductItemPrice centOSProductItemPrice = ProductItemPrice.builder().id(13945).build();
      assertTrue(order2.getPrices().contains(centOSProductItemPrice));
      assertEquals(order.getPrices().size() + 1, order2.getPrices().size());

   }

   @Test
   public void testOrderWithImageTemplateGlobalIdentifier() {

      VirtualGuest newGuest = VirtualGuest.builder().domain("jclouds.org").hostname(TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder()
            .packageId(PACKAGE_ID)
            .quantity(1)
            .location("37473")
            .useHourlyPricing(true)
            .prices(getPrices(1640, 2238, 13899, 272, 13945))
            .virtualGuests(newGuest)
            .imageTemplateGlobalIdentifier("5ad7a379-5252-4c19-a02a-1a5882f4fcbf").build();

      ProductOrder order2 = api().verifyVirtualGuestOrder(order);
      assertEquals(order.getPrices(), order2.getPrices());
   }

   @Test
   public void testVerifyOrderWithImageTemplateId() {

      VirtualGuest newGuest = VirtualGuest.builder().domain("jclouds.org").hostname(TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

      ProductOrder order = ProductOrder.builder()
            .packageId(PACKAGE_ID)
            .quantity(1)
            .location("37473")
            .useHourlyPricing(true)
            .prices(getPrices(1640, 2238, 13899, 272, 13945))
            .virtualGuests(newGuest)
            .imageTemplateId("93276").build();

      ProductOrder order2 = api().verifyVirtualGuestOrder(order);
      assertEquals(order.getPrices(), order2.getPrices());

   }
   
   @Test
   public void testVerifyOrderItemId() {

      VirtualGuest newGuest = VirtualGuest.builder().domain("jclouds.org").hostname(TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();
      
      templateBuilder.locationId("3").hardwareId("857,1204,865,186,439").imageId("3839");
      Template template = templateBuilder.build();

      ProductOrder order = ProductOrder.builder()
            .packageId(PACKAGE_ID)
            .quantity(1)
            .location(template.getLocation().getId())
            .useHourlyPricing(true)
            .prices(getPricesFromTemplate(template))
            .virtualGuests(newGuest)
            .imageTemplateId("93276").build();

      ProductOrder order2 = api().verifyVirtualGuestOrder(order);

   }

   private Iterable<ProductItemPrice> defaultPrices;


   @Override
   protected SoftLayerClient create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      defaultPrices = injector.getInstance(Key.get(new TypeLiteral<Iterable<ProductItemPrice>>() {
      }));
      return injector.getInstance(SoftLayerClient.class);
   }

   private VirtualGuestClient api() {
      return api.getVirtualGuestClient();
   }

   private void checkVirtualGuest(VirtualGuest vg) {
      if (vg.getBillingItemId() == -1)
         return;// Quotes and shutting down guests

      assert vg.getAccountId() > 0 : vg;
      assert vg.getCreateDate() != null : vg;
      assert vg.getDomain() != null : vg;
      assert vg.getFullyQualifiedDomainName() != null : vg;
      assert vg.getHostname() != null : vg;
      assert vg.getId() > 0 : vg;
      assert vg.getMaxCpu() > 0 : vg;
      assert vg.getMaxCpuUnits() != null : vg;
      assert vg.getMaxMemory() > 0 : vg;
      assert vg.getStartCpus() > 0 : vg;
      assert vg.getStatusId() >= 0 : vg;
      assert vg.getUuid() != null : vg;
      assert vg.getPrimaryBackendIpAddress() != null : vg;
      assert vg.getPrimaryIpAddress() != null : vg;
   }
   
   protected ImmutableList<ProductItemPrice> getPricesFromTemplate(Template template) {
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


   private ImmutableList<ProductItemPrice> getPrices(Integer... prices) {
      com.google.common.collect.ImmutableList.Builder<ProductItemPrice> result = ImmutableList.builder();
      for (Integer price : prices) {
         ProductItemPrice itemPrice = ProductItemPrice.builder().id(price).build();
         result.add(itemPrice);
      }
      result.addAll(defaultPrices);
      return result.build();
   }
}
