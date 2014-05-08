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
package org.jclouds.softlayer.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.withId;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.withItemId;
import static org.jclouds.softlayer.predicates.ProductPackagePredicates.withPriceId;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_ITEMS;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PACKAGE_ID;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PRICES;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.softlayer.PropertiesProviderFactory;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.SoftLayerNodeToNodeMetaData;
import org.jclouds.softlayer.compute.functions.datacenter.DatacenterToLocation;
import org.jclouds.softlayer.compute.functions.product.ProductItemToImage;
import org.jclouds.softlayer.compute.functions.product.ProductItemsToHardware;
import org.jclouds.softlayer.compute.internal.SoftLayerTemplateBuilderImpl;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.compute.strategy.SoftLayerComputeServiceAdapter;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.SoftLayerNode;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductPackage;
import org.jclouds.softlayer.features.account.AccountClient;
import org.jclouds.softlayer.features.product.ProductPackageClient;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class SoftLayerComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter>>() {
      }).to(SoftLayerComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<SoftLayerNode, NodeMetadata>>() {
      }).to(SoftLayerNodeToNodeMetaData.class);
      bind(new TypeLiteral<Function<ProductItem, org.jclouds.compute.domain.Image>>() {
      }).to(ProductItemToImage.class);
      bind(new TypeLiteral<Function<Iterable<ProductItem>, org.jclouds.compute.domain.Hardware>>() {
      }).to(ProductItemsToHardware.class);
      bind(new TypeLiteral<Function<Datacenter, Location>>() {
      }).to(DatacenterToLocation.class);
      bind(TemplateOptions.class).to(SoftLayerTemplateOptions.class);
      bind(TemplateBuilder.class).to(SoftLayerTemplateBuilderImpl.class);
      // to have the compute service adapter override default locations
      install(new LocationsFromComputeServiceAdapterModule<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter>(){});
   }

   /**
    * Many requests need the same productPackage, which is in this case the package for virtual
    * guests. We may at some point need to make an annotation qualifying it as such. ex. @VirtualGuest
    */
   @Provides
   @Singleton
   @Memoized
   public Supplier<ProductPackage> getProductPackage(AtomicReference<AuthorizationException> authException,
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final SoftLayerClient client,
            @Named(PROPERTY_SOFTLAYER_PACKAGE_ID) final int packageId) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
               new Supplier<ProductPackage>() {
                  @Override
                  public ProductPackage get() {
                     AccountClient accountClient = client.getAccountClient();
                     ProductPackageClient productPackageClient = client.getProductPackageClient();
                     ProductPackage p = find(accountClient.getReducedActivePackages(), withId(packageId));
                     return productPackageClient.getProductPackage(p.getId());
                  }
                  
                  @Override
                  public String toString() {
                     return Objects.toStringHelper(client).add("method", "accountClient.getActivePackages")
                                                          .add("method", "productPackageClient.getProductPackage").toString();
                  }
               }, seconds, TimeUnit.SECONDS);
   }

   @Inject
   @Provides
   @Singleton
   public Iterable<ProductItemPrice> prices(@Memoized Supplier<ProductPackage> productPackageSupplier,
		   						PropertiesProviderFactory defaultPropertiesFactory,
                                @Named(PROPERTY_SOFTLAYER_PACKAGE_ID) final int packageId) {
	  
	  final Set<ProductItem> productItems = productPackageSupplier.get().getItems();
	  
	  String prices =
              (String) defaultPropertiesFactory.create(packageId).sharedProperties().get(PROPERTY_SOFTLAYER_PRICES);
      
      Iterable<ProductItemPrice> transformedPrices = new ArrayList<ProductItemPrice>();
      if (!isNull(prices)) {
    	  transformedPrices = Iterables.transform(Splitter.on(',').split(checkNotNull(prices, "prices")),
    			  new Function<String, ProductItemPrice>() {
    		  @Override
    		  public ProductItemPrice apply(String arg0) {
    			  ProductItem item = find(productItems, withPriceId(Integer.parseInt(arg0)), null);
    			  if (item == null) {
    				  throw new NoSuchElementException("Item with pre-determined price ID:" + arg0 
    						  + " Does not exist in package:" + packageId);
    			  }
    			  return ProductItemPrice.builder().id(item.getPrices().iterator().next().getId()).build();
    		  }
    	  });
      }
      
      String items = 
    		  (String) defaultPropertiesFactory.create(packageId).sharedProperties().get(PROPERTY_SOFTLAYER_ITEMS);
      Iterable<ProductItemPrice> transformedItems = new ArrayList<ProductItemPrice>();
      if (!isNull(items)) {
    	  transformedItems = Iterables.transform(Splitter.on(',').split(checkNotNull(items, "items")),
    			  new Function<String, ProductItemPrice>() {
    		  @Override
    		  public ProductItemPrice apply(String arg0) {
    			  ProductItem item = find(productItems, withItemId(Integer.parseInt(arg0)), null);
    			  if (item == null) {
    				  throw new NoSuchElementException("Item with pre-determined item ID:" + arg0 
    						  + " Does not exist in package:" + packageId);
    			  }
    			  return ProductItemPrice.builder().id(item.getPrices().iterator().next().getId()).build();
    		  }
    	  });
      }
      
      return Iterables.concat(transformedItems, transformedPrices);
   }

   private boolean isNull(String string) {
	   return string == null || string.equals("");
   }
}
