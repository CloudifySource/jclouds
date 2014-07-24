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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItem.Builder;
import org.jclouds.softlayer.domain.product.ProductItemCategory;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.predicates.ProductItemPredicates;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class ProductItems {

   /**
    * Creates a function to get the capacity from a product item.
    */
   public static Function<ProductItem, Float> capacity() {
      return new Function<ProductItem, Float>() {
         @Override
         public Float apply(ProductItem productItem) {
            return productItem.getCapacity();
         }
      };
   }

   /**
    * Creates a function to get the description from a product item.
    */
   public static Function<ProductItem, String> description() {
      return new Function<ProductItem, String>() {
         @Override
         public String apply(ProductItem productItem) {
            return productItem.getDescription();
         }
      };
   }

   /**
    * Creates a function to get the ProductItemPrice for the ProductItem. Currently returns the
    * first prices. This will need to be changed if more than one prices is returned.
    */
   public static Function<ProductItem, ProductItemPrice> price() {
      return new Function<ProductItem, ProductItemPrice>() {
         @Override
         public ProductItemPrice apply(ProductItem productItem) {
            if (productItem.getPrices().isEmpty())
               throw new NoSuchElementException("ProductItem has no prices:" + productItem);
            return Iterables.get(productItem.getPrices(), 0);
         }
      };
   }
   
   /**
    * Creates a function to get the ProductItemPrice for the ProductItem. 
    */
   public static Function<List<ProductItem>, String> prices() {
      return new Function<List<ProductItem>, String>() {
         @Override
         public String apply(List<ProductItem> productItems) {
        	 List<String[]> allPricesAsList = new ArrayList<String[]>();
        	 List<ProductItem> uniqueItems = getUniqueItems(productItems);
        	 for (int i = 0; i < uniqueItems.size(); i++) {
        		 allPricesAsList.add(getPricesAsString(uniqueItems.get(i)));
			}
        	 String[][] sets = new String[uniqueItems.size()][uniqueItems.size()];
        	 for (int i = 0; i < sets.length; i++) {
				sets[i] = allPricesAsList.get(i);
			}
        	 Set<String> reducedPricesSet = new LinkedHashSet<String>();
        	 getAllReducedCombinations(sets, 0, "", reducedPricesSet);
        	 
        	 String pricesFinal = getProductItemToPriceMapping(productItems, reducedPricesSet);
        	 
        	 return pricesFinal;
         }
         
         private String getProductItemToPriceMapping(List<ProductItem> productItems,
        		 Set<String> uniqueItems) {
        	 StringBuilder sb = new StringBuilder();
        	 Set<String> allPricesSet = new LinkedHashSet<String>();
        	 for (String uniqueIds : uniqueItems) {
        		 final String finalPriceIds;
        		 // append two marker chars.
        		 String itemIdTemplate = "," + ProductItemsToHardware.providerHardwareId().apply(productItems) + ",";
        		 for (String id : uniqueIds.split(",")) {
        			 ProductItem productItem = get(filter(productItems, ProductItemPredicates.priceId(id)), 0);
        			 // replace according to marker char.
        			 itemIdTemplate = itemIdTemplate.replaceAll("," + Integer.toString(productItem.getId()) + ",", "," + id + ",");
        			 itemIdTemplate = itemIdTemplate.replaceAll("," + Integer.toString(productItem.getId()) + ",", "," + id + ",");
        		 }
        		 
        		 finalPriceIds = itemIdTemplate.substring(0, itemIdTemplate.length() - 1).replaceFirst(",", "");
        		 if (!allPricesSet.contains(finalPriceIds)) {
        			 allPricesSet.add(finalPriceIds);
        		 }
        	 }
        	 for (String ids : allPricesSet) {
        		 sb.append(ids).append(";");
        	 }
        	 return sb.toString().substring(0, sb.toString().length() - 1);
         }

		private List<ProductItem> getUniqueItems(List<ProductItem> items) {
        	 LinkedHashSet<ProductItem> ids = new LinkedHashSet<ProductItem>();
        	 for (ProductItem pi : items) {
        		 if (!ids.contains(pi)) {
        			 ids.add(pi);
        		 }
			}
        	 return new ArrayList<ProductItem>(ids);
         }
         
         private String[] getPricesAsString(ProductItem productItem) {
			List<String> allPrices = new ArrayList<String>();
			for (ProductItemPrice price : productItem.getPrices()) {
				allPrices.add(Integer.toString(price.getId()));
			}
			return allPrices.toArray(new String[allPrices.size()]);
		}
         
         private void getAllReducedCombinations(String[][] sets, int n, String prefix, Set<String> reducedPricesSet){
             if(n >= sets.length){
//            	 System.out.println((prefix.substring(0,prefix.length()-1)));
            	 reducedPricesSet.add((prefix.substring(0,prefix.length()-1)));
                 return;
             }
             for(String s : sets[n]){
                 getAllReducedCombinations(sets, n+1, prefix+s+",", reducedPricesSet);
             }
         }
      };
   }

   /**
    * Creates a function to get the ProductItem for the ProductItemPrice. Copies the category
    * information from the prices to the item if necessary The ProductItemPrices must have
    * ProductItems.
    */
   public static Function<ProductItemPrice, ProductItem> item() {
      return new Function<ProductItemPrice, ProductItem>() {
         @Override
         public ProductItem apply(ProductItemPrice productItemPrice) {
            Set<ProductItemCategory> categories = productItemPrice.getCategories();
            ProductItem item = productItemPrice.getItem();
            ProductItem.Builder builder = productItemPrice.getItem().toBuilder();
            if (item.getCategories().size() == 0 && categories.size() != 0) {
               builder.categories(categories);
            }

            return builder.build();
         }
      };
   }
}
