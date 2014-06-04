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

import com.google.common.base.Function;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemPrice;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PACKAGE_ID;

/**
 * Converts a set of ProductItems to Hardware.
 * 
 * @author Jason King
 */
@Singleton
public class ProductItemsToHardware implements Function<Iterable<ProductItem>, Hardware> {

   private final Function<Iterable<ProductItem>, Hardware> productItemsToHardware;

   @Inject
   public ProductItemsToHardware(@Named(PROPERTY_SOFTLAYER_PACKAGE_ID) int packageId,
                                 ProductItemsToHardwareFactory productItemsToHardwareFactory) {
      this.productItemsToHardware = productItemsToHardwareFactory.create(packageId);
   }

   @Override
   public Hardware apply(Iterable<ProductItem> items) {
      return productItemsToHardware.apply(items);
   }

   /**
    * Generates a hardwareId based on the priceId's of the items in the list
    *
    * @return comma separated list of price id's
    */
   public static Function<List<ProductItem>, String> hardwareId() {
      return new Function<List<ProductItem>, String>() {
         @Override
         public String apply(List<ProductItem> productItems) {
            StringBuilder builder = new StringBuilder();
            for (ProductItem item : productItems) {
               ProductItemPrice price = ProductItems.price().apply(item);
               builder.append(price.getId()).append(",");
            }
            return builder.toString().substring(0, builder.lastIndexOf(","));
         }
      };
   }
   
   /**
    * Generates a hardwareId based on the priceId's of the items in the list
    *
    * @return comma separated list of price id sets, seporated by ';'
    */
   public static Function<List<ProductItem>, String> allHardwareIdPriceCombinations() {
      return new Function<List<ProductItem>, String>() {
         @Override
         public String apply(List<ProductItem> productItems) {
            String prices = ProductItems.prices().apply(productItems);
            return prices;
         }
      };
   }
   
   /**
    * Generates a hardwareId based on the itemIds
    *
    * @return comma separated list of item ids
    */
   public static Function<List<ProductItem>, String> providerHardwareId() {
      return new Function<List<ProductItem>, String>() {
         @Override
         public String apply(List<ProductItem> productItems) {
            StringBuilder builder = new StringBuilder();
            for (ProductItem item : productItems) {
               builder.append(item.getId()).append(",");
            }
            return builder.toString().substring(0, builder.lastIndexOf(","));
         }
      };
   }
}
