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
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;

import java.util.ArrayList;
import java.util.Arrays;
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
   public static void main(String[] args) {
	   
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
	   System.out.println(apply);
	   System.out.println("hold");
	   
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
