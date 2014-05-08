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
package org.jclouds.softlayer.predicates;

import com.google.common.base.Predicate;

import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductPackage;

import static com.google.common.base.Preconditions.checkNotNull;

public class ProductPackagePredicates {

   /**
    * Tests if the product package name equals the packageName
    * @param packageName
    * @return true if the name is equal, otherwise false.
    */
   public static Predicate<ProductPackage> named(final String packageName) {
      return new Predicate<ProductPackage>() {
         public boolean apply(ProductPackage productPackage) {
             checkNotNull(productPackage, "productPackage cannot be null");
             return productPackage.getName().equals(packageName);
         }
      };
   }

   /**
    * Tests if the product package id equals the packageId
    * @param packageId
    * @return true if the name is equal, otherwise false.
    */
   public static Predicate<ProductPackage> withId(final int packageId) {
      return new Predicate<ProductPackage>() {
         public boolean apply(ProductPackage productPackage) {
            checkNotNull(productPackage, "productPackage cannot be null");
            return productPackage.getId() == packageId;
         }
      };
   }
   
   /**
    * Tests if the product item id equals the itemId
    * @param itemId
    * @return true if the id is equal, otherwise false.
    */
   public static Predicate<ProductItem> withItemId(final int itemId) {
      return new Predicate<ProductItem>() {
         public boolean apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem cannot be null");
            return productItem.getId() == itemId;
         }
      };
   }
   
   /**
    * Tests if the product item price id equals the priceId
    * @param priceId
    * @return true if the id is equal, otherwise false.
    */
   public static Predicate<ProductItem> withPriceId(final int priceId) {
      return new Predicate<ProductItem>() {
         public boolean apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem cannot be null");
            return productItem.getPrices().iterator().next().getId() == priceId;
         }
      };
   }

}
