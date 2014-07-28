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
package org.jclouds.softlayer.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedList;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.guest.PrimaryBackendNetworkComponent;
import org.jclouds.softlayer.domain.guest.VirtualGuest;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.server.HardwareServer;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Converts a ProductOrder into a json string valid for placing an order via the softlayer api The
 * String is set into the payload of the HttpRequest
 * 
 * @author Jason King, Noa Kuperberg
 */
public class ProductOrderToJson implements Binder {

   private Json json;

   @Inject
   public ProductOrderToJson(Json json) {
      this.json = json;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(input, "order");
      ProductOrder order = ProductOrder.class.cast(input);
      request.setPayload(buildJson(order));
      return request;
   }

   /**
    * Builds a Json string suitable for sending to the softlayer api
    * 
    * @param order
    * @return
    */
   String buildJson(ProductOrder order) {

      Iterable<Price> prices = Iterables.transform(order.getPrices(), new Function<ProductItemPrice, Price>() {
         @Override
         public Price apply(ProductItemPrice productItemPrice) {
            return new Price(productItemPrice.getId());
         }
      });

      Iterable<VirtualGuestServerProperties> virtualHosts = Iterables.transform(order.getVirtualGuests(),
               new Function<VirtualGuest, VirtualGuestServerProperties>() {
                  @Override
                  public VirtualGuestServerProperties apply(VirtualGuest virtualGuest) {
                     return new VirtualGuestServerProperties(virtualGuest.getHostname(), virtualGuest.getDomain(), 
                    		 virtualGuest.isPrivateNetworkOnlyFlag(), virtualGuest.getPrimaryBackendNetworkComponent());
                  }
               });

      Iterable<HostnameAndDomain> hardwareServers = Iterables.transform(order.getHardwareServers(),
              new Function<HardwareServer, HostnameAndDomain>() {
                 @Override
                 public HostnameAndDomain apply(HardwareServer hardwareServer) {
                    return new HostnameAndDomain(hardwareServer.getHostname(), hardwareServer.getDomain());
                 }
              });

      OrderData data = new OrderData(order.getPackageId(), order.getLocation(), Lists.newLinkedList(prices), Sets
               .newLinkedHashSet(virtualHosts), Sets.newLinkedHashSet(hardwareServers), order.getQuantity(), order.getUseHourlyPricing(), order.getImageTemplateGlobalIdentifier(), order.getImageTemplateId());

      return json.toJson(ImmutableMap.of("parameters", ImmutableList.<OrderData> of(data)));
   }

   @SuppressWarnings("unused")
   private static class OrderData {
      private String complexType = "SoftLayer_Container_Product_Order";
      private long packageId = -1;
      private String location;
      private LinkedList<Price> prices;
      private Set<VirtualGuestServerProperties> virtualGuests;
      private Set<HostnameAndDomain> hardware;
      private long quantity;
      private boolean useHourlyPricing;
      private String imageTemplateGlobalIdentifier;
      private String imageTemplateId;

      public OrderData(long packageId, String location, LinkedList<Price> linkedList, Set<VirtualGuestServerProperties> virtualGuests,
                       Set<HostnameAndDomain> hardwareServers, long quantity, boolean useHourlyPricing, String imageTemplateGlobalIdentifier, String imageTemplateId) {
         this.packageId = packageId;
         this.location = location;
         this.prices = linkedList;
         this.virtualGuests = virtualGuests;
         this.hardware = hardwareServers;
         this.quantity = quantity;
         this.useHourlyPricing = useHourlyPricing;
         this.imageTemplateGlobalIdentifier = imageTemplateGlobalIdentifier;
         this.imageTemplateId = imageTemplateId;
      }

   }

   @SuppressWarnings("unused")
   private static class HostnameAndDomain {
      private String hostname;
      private String domain;

      public HostnameAndDomain(String hostname, String domain) {
         this.hostname = hostname;
         this.domain = domain;
      }

   }
   
   @SuppressWarnings("unused")
   private static class VirtualGuestServerProperties {
      private String hostname;
      private String domain;
      private boolean privateNetworkOnlyFlag;
      private PrimaryBackendNetworkComponent primaryBackendNetworkComponent;

      public VirtualGuestServerProperties(String hostname, String domain, boolean privateNetworkOnlyFlag, 
    		  PrimaryBackendNetworkComponent primaryBackendNetworkComponent) {
         this.hostname = hostname;
         this.domain = domain;
         this.privateNetworkOnlyFlag = privateNetworkOnlyFlag;
         this.primaryBackendNetworkComponent = primaryBackendNetworkComponent;
      }

   }


   @SuppressWarnings("unused")
   private static class Price {
      private long id;

      public Price(long id) {
         this.id = id;
      }
   }

}
