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
package org.jclouds.softlayer.compute.functions.product;

import static org.jclouds.softlayer.compute.functions.product.ProductItems.capacity;
import static org.jclouds.softlayer.compute.functions.product.ProductItems.description;
import static org.jclouds.softlayer.compute.functions.product.ProductItems.item;
import static org.jclouds.softlayer.compute.functions.product.ProductItems.price;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemCategory;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductItem.Builder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests {@code ProductItems}
 *
 * @author Jason King
 */
@Test(groups = "unit")
public class ProductItemsTest {

   private ProductItemCategory category;
   private ProductItemPrice price;
   private ProductItem item;

   @BeforeMethod
   public void setup() {

       category = ProductItemCategory.builder().id(1).categoryCode("category").build();

       price = ProductItemPrice.builder().id(1).build();

       item = ProductItem.builder().id(1)
                                   .capacity(2.0f)
                                   .description("an item")
                                   .prices(price)
                                   .build();
   }

   @Test
   public void testCapacity() {
       assertEquals(capacity().apply(item), 2.0f);
   }
   
   @Test
   public void testItemIdCombinations() {
	   List<ProductItem> items = new ArrayList<ProductItem>();
	   Builder<?> item1 = ProductItem.builder();
	   item1.id(11);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price1 = ProductItemPrice.builder();
	   price1.id(1);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price2 = ProductItemPrice.builder();
	   price2.id(2);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price3 = ProductItemPrice.builder();
	   price3.id(3);
	   item1.prices(price1.build(), price2.build(), price3.build());
	   
	   Builder<?> item2 = ProductItem.builder();
	   item2.id(12);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price4 = ProductItemPrice.builder();
	   price4.id(4);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price5 = ProductItemPrice.builder();
	   price5.id(5);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price6 = ProductItemPrice.builder();
	   price6.id(6);
	   item2.prices(price4.build(), price5.build(), price6.build());
	   
	   Builder<?> item3 = ProductItem.builder();
	   item3.id(13);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price7 = ProductItemPrice.builder();
	   price7.id(7);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price8 = ProductItemPrice.builder();
	   price8.id(8);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price9 = ProductItemPrice.builder();
	   price9.id(9);
	   org.jclouds.softlayer.domain.product.ProductItemPrice.Builder<?> price10 = ProductItemPrice.builder();
	   price10.id(10);
	   item3.prices(price7.build(), price8.build(), price9.build(), price10.build());
	   
	   items.add(item1.build());
	   items.add(item2.build());
	   items.add(item3.build());
	   items.add(item1.build());
	   
	   String apply = ProductItemsToHardware.allHardwareIdPriceCombinations().apply(items);
	   Set<String> itemIds = new HashSet<String>(Arrays.asList(apply.split(";")));
	   Assert.assertTrue(itemIds.contains("1,4,7,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,4,8,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,4,9,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,4,10,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,5,7,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,5,8,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,5,9,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,5,10,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,6,7,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,6,8,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,6,9,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("1,6,10,1"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,4,7,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,4,8,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,4,9,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,4,10,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,5,7,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,5,8,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,5,9,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,5,10,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,6,7,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,6,8,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,6,9,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("2,6,10,2"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,4,7,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,4,8,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,4,9,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,4,10,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,5,7,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,5,8,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,5,9,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,5,10,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,6,7,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,6,8,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,6,9,3"), "Expecting hardware ID combination be contained in hardwares list");
	   Assert.assertTrue(itemIds.contains("3,6,10,3"), "Expecting hardware ID combination be contained in hardwares list");
	   
	   Assert.assertTrue(itemIds.size() == 36, "Expecting 36 different hardware combinations.");
   }

   @Test
   public void testCapacityMissing() {
       ProductItem item = ProductItem.builder().id(1).build();
       assertNull(capacity().apply(item));
   }

   @Test
   public void testDescription() {
       assertEquals(description().apply(item),"an item");
   }

   @Test
   public void testDescriptionMissing() {
       ProductItem item = ProductItem.builder().id(1).build();
       assertNull(description().apply(item));
   }

   @Test
   public void testPrice() {
      assertEquals(price().apply(item),price);
   }

   @Test
   public void testPriceMultiplePrices() {
       ImmutableSet<ProductItemPrice> prices = ImmutableSet.of(price, ProductItemPrice.builder().id(2).build());
       ProductItem item2 = ProductItem.builder().prices(prices).build();
       assertEquals(price().apply(item2),price);
   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testPriceMissing() {
      ProductItem noPriceItem = ProductItem.builder().id(1).build();
      price().apply(noPriceItem);
   }
   
   @Test
   public void testItemCallGetsCategory() {
      ProductItemPrice price = ProductItemPrice.builder().id(1)
                                                         .categories(category)
                                                         .item(item)
                                                         .build();
      ProductItem newItem = item().apply(price);
      assertEquals(newItem.getCategories(), ImmutableSet.of(category));
   }

   @Test
   public void testItemCallNoCategoryOnPrice() {

      ProductItem item1 = item.toBuilder().categories(ImmutableSet.of(category)).build();

      ProductItemPrice price = ProductItemPrice.builder().id(1)
                                                         .item(item1)
                                                         .build();
      ProductItem newItem = item().apply(price);
      assertEquals(newItem.getCategories(), ImmutableSet.of(category));
   }

   @Test
   public void testItemCallCategoryExists() {

      ProductItemCategory category2 = ProductItemCategory.builder()
            .id(12)
            .categoryCode("new category")
            .build();

      ProductItem item1 = item.toBuilder().categories(ImmutableSet.of(category2)).build();

      ProductItemPrice price = ProductItemPrice.builder().id(1)
                                                         .categories(category)
                                                         .item(item1)
                                                         .build();
      ProductItem newItem = item().apply(price);
      assertEquals(newItem.getCategories(), ImmutableSet.of(category2));
   }


}
