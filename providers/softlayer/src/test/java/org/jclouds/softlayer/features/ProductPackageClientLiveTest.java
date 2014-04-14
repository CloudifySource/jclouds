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
package org.jclouds.softlayer.features;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.jclouds.softlayer.compute.functions.product.ProductItems;
import org.jclouds.softlayer.domain.Address;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.Region;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductPackage;
import org.jclouds.softlayer.features.account.AccountClient;
import org.jclouds.softlayer.features.product.ProductPackageClient;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.jclouds.softlayer.predicates.ProductItemPredicates.*;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code ProductPackageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ProductPackageClientLiveTest")
public class ProductPackageClientLiveTest extends BaseSoftLayerClientLiveTest {

   /**
    * Name of the package used for ordering virtual guests. For real this is
    * passed in using the property
    * 
    * @{code org.jclouds.softlayer.reference.SoftLayerConstants.
    *        PROPERTY_SOFTLAYER_PACKAGE_ID}
    */
   public static final String CLOUD_SERVER_PACKAGE_NAME = "Cloud Server";

   /**
    * Name of the package used for ordering hourly pricing bare metal instances. For real this is
    * passed in using the property
    *
    * @{code org.jclouds.softlayer.reference.SoftLayerConstants.
    *        PROPERTY_SOFTLAYER_PACKAGE_ID}
    */

   public static final String BARE_METAL_INSTANCE_PACKAGE_NAME = "Bare Metal Instance";

   /**
    * Name of the package used for ordering dedicated Xeon 3200 series servers. For real
    * thisis
    * passed in using the property
    *
    * @{code org.jclouds.softlayer.reference.SoftLayerConstants.
    *        PROPERTY_SOFTLAYER_PACKAGE_ID}
    */

   public static final String SINGLE_XEON_3200_DEDICATED_SERVER_PACKAGE_NAME = "Single Xeon 3200 Series";
   public static final String DUAL_XEON_5500_DEDICATED_SERVER_PACKAGE_NAME = "Dual Xeon 5500 Series (Nehalem)";
   public static final String DUAL_XEON_5500_DEDICATED_SERVER_MULTI_DISK_PACKAGE_NAME = "Specialty Server: Mass Storage: Xeon 5500 (Nehalem) Series";


   @BeforeGroups(groups = { "live" })
   public void setup() {
      super.setup();
      client = api.getProductPackageClient();
      accountClient = api.getAccountClient();

      // This is used several times, so cache to speed up the test.
      cloudServerPackageId = Iterables.find(accountClient.getActivePackages(), named(CLOUD_SERVER_PACKAGE_NAME))
            .getId();
      cloudServerProductPackage = client.getProductPackage(cloudServerPackageId);
   }

   private ProductPackageClient client;
   private AccountClient accountClient;

   private int cloudServerPackageId;
   private ProductPackage cloudServerProductPackage;

   @Test
   public void testGetProductPackage() {
      for (ProductPackage productPackage : accountClient.getReducedActivePackages()) {
         ProductPackage response = client.getProductPackage(productPackage.getId());

         assert null != response;
         assert response.getId() > 0 : response;
         assert response.getName() != null : response;

         assertTrue(response.getItems().size() >= 0);
         for (ProductItem item : response.getItems()) {
            checkProductItem(item);
         }

         for (Datacenter datacenter : response.getDatacenters()) {
            checkDatacenter(datacenter);
         }
      }
   }

   @Test
   public void testDatacentersForCloudLayer() {

      ImmutableSet.Builder<Datacenter> builder = ImmutableSet.builder();
      builder.add(Datacenter.builder().id(18171).name("sea01").longName("Seattle").build());
      builder.add(Datacenter.builder().id(37473).name("wdc01").longName("Washington, DC").build());
      builder.add(Datacenter.builder().id(138124).name("dal05").longName("Dallas 5").build());
      builder.add(Datacenter.builder().id(168642).name("sjc01").longName("San Jose 1").build());
      builder.add(Datacenter.builder().id(224092).name("sng01").longName("Singapore 1").build());
      builder.add(Datacenter.builder().id(265592).name("ams01").longName("Amsterdam 1").build());

      Set<Datacenter> expected = builder.build();

      Set<Datacenter> datacenters = cloudServerProductPackage.getDatacenters();
      assert datacenters.size() == expected.size() : datacenters;
      assertTrue(datacenters.containsAll(expected));

      for (Datacenter dataCenter : datacenters) {
         Address address = dataCenter.getLocationAddress();
         assertNotNull(address);
         checkAddress(address);
      }
   }

   @Test
   public void testGetOneGBRamPrice() {
      Iterable<ProductItem> ramItems = Iterables.filter(cloudServerProductPackage.getItems(),
            Predicates.and(categoryCode("ram"), capacity(1.0f)));

      // capacity is key in GB (1Gb = 1.0f)
      Map<Float, ProductItem> ramToProductItem = Maps.uniqueIndex(ramItems, ProductItems.capacity());

      ProductItemPrice price = ProductItems.price().apply(ramToProductItem.get(1.0f));
      assert Integer.valueOf(1644).equals(price.getId());
   }

   @Test
   public void testGetTwoCPUCoresPrice() {
      // If use ProductItemPredicates.categoryCode("guest_core") get duplicate
      // capacities (units =
      // PRIVATE_CORE and N/A)
      Iterable<ProductItem> cpuItems = Iterables.filter(cloudServerProductPackage.getItems(),
            Predicates.and(units("PRIVATE_CORE"), capacity(2.0f)));

      // number of cores is the key
      Map<Float, ProductItem> coresToProductItem = Maps.uniqueIndex(cpuItems, ProductItems.capacity());

      ProductItemPrice price = ProductItems.price().apply(coresToProductItem.get(2.0f));
      assert Integer.valueOf(1963).equals(price.getId());
   }

   @Test
   public void testGetUbuntuPrice() {
      Iterable<ProductItem> operatingSystems = Iterables.filter(cloudServerProductPackage.getItems(),
            categoryCode("os"));

      Map<String, ProductItem> osToProductItem = Maps.uniqueIndex(operatingSystems, ProductItems.description());

      ProductItemPrice price = ProductItems.price().apply(
            osToProductItem.get("Ubuntu Linux 8 LTS Hardy Heron - Minimal Install (64 bit)"));
      assert Integer.valueOf(1693).equals(price.getId());
   }

   private void checkDatacenter(Datacenter datacenter) {
      assert datacenter.getId() > 0 : datacenter;
      assert datacenter.getName() != null : datacenter;
      assert datacenter.getLongName() != null : datacenter;
      for (Region region : datacenter.getRegions())
         checkRegion(region);
   }

   private void checkRegion(Region region) {
      assert !region.getDescription().isEmpty() : region;
      assert !region.getKeyname().isEmpty() : region;
   }

   private void checkAddress(Address address) {
      assert address.getId() > 0 : address;
      assert address.getCountry() != null : address;
      if (!ImmutableSet.of("SG", "NL").contains(address.getCountry()))
         assert address.getState() != null : address;
   }
}
