/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.jclouds.softlayer.domain.product;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.domain.guest.ReducedVirtualGuest;
import org.jclouds.softlayer.domain.server.HardwareServer;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Class ReducedProductOrder
 *
 * @author Noa Kuperberg
 */
public class ReducedProductOrder {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromReducedProductOrder(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int packageId;
      protected String location;
      protected List<ProductItemPrice> prices = ImmutableList.of();
      protected Set<ReducedVirtualGuest> virtualGuests = ImmutableSet.of();
      protected Set<HardwareServer> hardwareServers = ImmutableSet.of();
      protected int quantity;
      protected boolean useHourlyPricing;
      protected String imageTemplateGlobalIdentifier;
      protected String imageTemplateId;


      /**
       * @see ReducedProductOrder#getPackageId()
       */
      public T packageId(int packageId) {
         this.packageId = packageId;
         return self();
      }

      /**
       * @see ReducedProductOrder#getLocation()
       */
      public T location(String location) {
         this.location = location;
         return self();
      }

      /**
       * @see ReducedProductOrder#getPrices()
       */
      public T prices(ImmutableList<ProductItemPrice> immutableList) {
         this.prices = ImmutableList.copyOf(checkNotNull(immutableList, "prices"));
         return self();
      }

      public T prices(ProductItemPrice... in) {
         return prices(ImmutableList.copyOf(in));
      }

      /**
       * @see ReducedProductOrder#getReducedVirtualGuests()
       */
      public T virtualGuests(Set<ReducedVirtualGuest> ReducedVirtualGuests) {
         this.virtualGuests = ImmutableSet.copyOf(checkNotNull(virtualGuests, "virtualGuests"));
         return self();
      }

      public T virtualGuests(ReducedVirtualGuest... in) {
         return virtualGuests(ImmutableSet.copyOf(in));
      }

      /**
       * @see ReducedProductOrder#getHardwareServers() () ()
       */
      public T hardwareServers(Set<org.jclouds.softlayer.domain.server.HardwareServer> hardwareServers) {
         this.hardwareServers = ImmutableSet.copyOf(checkNotNull(hardwareServers, "hardwareServers"));
         return self();
      }

      public T hardwareServers(org.jclouds.softlayer.domain.server.HardwareServer... in) {
         return hardwareServers(ImmutableSet.copyOf(in));
      }

      /**
       * @see ReducedProductOrder#getQuantity()
       */
      public T quantity(int quantity) {
         this.quantity = quantity;
         return self();
      }

      /**
       * @see ReducedProductOrder#getUseHourlyPricing()
       */
      public T useHourlyPricing(boolean useHourlyPricing) {
         this.useHourlyPricing = useHourlyPricing;
         return self();
      }

      public T imageTemplateGlobalIdentifier(String imageTemplateGlobalIdentifier) {
         this.imageTemplateGlobalIdentifier = imageTemplateGlobalIdentifier;
         return self();
      }

      public T imageTemplateId(String imageTemplateId) {
         this.imageTemplateId = imageTemplateId;
         return self();
      }

      public ReducedProductOrder build() {
         return new ReducedProductOrder(packageId, location, prices, virtualGuests, hardwareServers, quantity, useHourlyPricing, imageTemplateGlobalIdentifier, imageTemplateId);
      }

      public T fromReducedProductOrder(ReducedProductOrder in) {
         return this
               .packageId(in.getPackageId())
               .location(in.getLocation())
               .prices(in.getPrices())
               .virtualGuests(in.getVirtualGuests())
               .quantity(in.getQuantity())
               .useHourlyPricing(in.getUseHourlyPricing())
               .imageTemplateGlobalIdentifier(in.getImageTemplateGlobalIdentifier())
               .imageTemplateId(in.getImageTemplateId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int packageId;
   private final String location;
   private final ImmutableList<ProductItemPrice> prices;
   private final Set<ReducedVirtualGuest> virtualGuests;
   private final Set<HardwareServer> hardwareServers;
   private final int quantity;
   private final boolean useHourlyPricing;
   private final String imageTemplateGlobalIdentifier;
   private final String imageTemplateId;

   @ConstructorProperties({
      "packageId", "location", "prices", "virtualGuests", "hardware", "quantity", "useHourlyPricing", "imageTemplateGlobalIdentifier", "imageTemplateId"
   })
   protected ReducedProductOrder(int packageId, @Nullable String location, @Nullable List<ProductItemPrice> prices, @Nullable Set<ReducedVirtualGuest> virtualGuests,
                          @Nullable Set<HardwareServer> hardwareServers,
                          int quantity, boolean useHourlyPricing, String imageTemplateGlobalIdentifier, String imageTemplateId) {
      this.packageId = packageId;
      this.location = location;
      this.prices = prices == null ? ImmutableList.<ProductItemPrice>of() : ImmutableList.copyOf(prices);
      this.virtualGuests = virtualGuests == null ? ImmutableSet.<ReducedVirtualGuest>of() : ImmutableSet.copyOf(virtualGuests);
      this.hardwareServers = hardwareServers == null ? ImmutableSet.<HardwareServer>of() : ImmutableSet.copyOf(hardwareServers);;
      this.quantity = quantity;
      this.useHourlyPricing = useHourlyPricing;
      this.imageTemplateGlobalIdentifier = imageTemplateGlobalIdentifier;
      this.imageTemplateId = imageTemplateId;
   }

   /**
    * @return The package id of an order. This is required.
    */
   public int getPackageId() {
      return this.packageId;
   }

   /**
    * @return The region keyname or specific location keyname where the order should be provisioned.
    */
   @Nullable
   public String getLocation() {
      return this.location;
   }

   /**
    * Gets the item prices in this order.
    * All that is required to be present is the prices ID
    *
    * @return the prices.
    */
   public ImmutableList<ProductItemPrice> getPrices() {
      return this.prices;
   }

   /**
    * Gets the virtual guests in this order.
    *
    * @return the the virtual guests.
    */
   public Set<ReducedVirtualGuest> getVirtualGuests() {
      return this.virtualGuests;
   }

   /**
    * Gets the hardwareServers servers in this order.
    *
    * @return the the virtual guests.
    */
   public Set<HardwareServer> getHardwareServers() {
      return this.hardwareServers;
   }

   public int getQuantity() {
      return this.quantity;
   }

   public boolean getUseHourlyPricing() {
      return this.useHourlyPricing;
   }

   public String getImageTemplateGlobalIdentifier() {
      return this.imageTemplateGlobalIdentifier;
   }

   public String getImageTemplateId() {
      return this.imageTemplateId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(packageId, location, prices, virtualGuests, hardwareServers, quantity, useHourlyPricing);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ReducedProductOrder that = ReducedProductOrder.class.cast(obj);
      return Objects.equal(this.packageId, that.packageId)
            && Objects.equal(this.location, that.location)
            && Objects.equal(this.prices, that.prices)
            && Objects.equal(this.virtualGuests, that.virtualGuests)
            && Objects.equal(this.hardwareServers, that.hardwareServers)
            && Objects.equal(this.quantity, that.quantity)
            && Objects.equal(this.useHourlyPricing, that.useHourlyPricing)
            && Objects.equal(this.imageTemplateGlobalIdentifier, that.imageTemplateGlobalIdentifier)
            && Objects.equal(this.imageTemplateId, that.imageTemplateId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("packageId", packageId).add("location", location).add("prices", prices)
            .add("virtualGuests", virtualGuests).add("hardwareServers", hardwareServers)
            .add("quantity", quantity).add("useHourlyPricing", useHourlyPricing).add("imageTemplateId", imageTemplateId)
            .add("imageTemplateGlobalIdentifier", imageTemplateGlobalIdentifier);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
