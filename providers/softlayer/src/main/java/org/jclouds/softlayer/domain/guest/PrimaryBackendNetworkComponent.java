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
package org.jclouds.softlayer.domain.guest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * This object represents the PrimaryBackendNetworkComponent of a virtual guest.
 * It currently contains only the Network VLAN setting, which here sets the VLAN of the private IP.
* @author Noa Kuperberg
*/
public class PrimaryBackendNetworkComponent {


	   public static Builder<?> builder() {
	      return new ConcreteBuilder();
	   }

	   public Builder<?> toBuilder() {
	      return new ConcreteBuilder().fromPrimaryBackendNetworkComponent(this);
	   }

	   public abstract static class Builder<T extends Builder<T>>  {
	      protected abstract T self();

	      protected NetworkVlan networkVlan;

	      /**
	       * @see PrimaryBackendNetworkComponent#getNetworkVlan()
	       */
	      public T networkVlan(NetworkVlan networkVlan) {
	         this.networkVlan = networkVlan;
	         return self();
	      }

	      public PrimaryBackendNetworkComponent build() {
	         return new PrimaryBackendNetworkComponent(networkVlan);
	      }

	      public T fromPrimaryBackendNetworkComponent(PrimaryBackendNetworkComponent in) {
	         return this
	               .networkVlan(in.getNetworkVlan());
	      }
	   }

	   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
	      @Override
	      protected ConcreteBuilder self() {
	         return this;
	      }
	   }

	   private final NetworkVlan networkVlan;

	   @ConstructorProperties("networkVlan")
	   public PrimaryBackendNetworkComponent(NetworkVlan networkVlan) {
	      this.networkVlan = checkNotNull(networkVlan,"networkVlan cannot be null or empty: " + networkVlan);
	   }

	   /**
	    * Maps onto {@code NetworkVlan}
	    *
	    * @return The networkVlan of a PrimaryBackendNetworkComponent.
	    */
	   @Nullable
	   public NetworkVlan getNetworkVlan() {
	      return this.networkVlan;
	   }

	   @Override
	   public int hashCode() {
	      return Objects.hashCode(networkVlan);
	   }

	   @Override
	   public boolean equals(Object obj) {
	      if (this == obj) return true;
	      if (obj == null || getClass() != obj.getClass()) return false;
	      PrimaryBackendNetworkComponent that = PrimaryBackendNetworkComponent.class.cast(obj);
	      return Objects.equal(this.networkVlan, that.networkVlan);
	   }

	   protected ToStringHelper string() {
	      return Objects.toStringHelper(this)
	            .add("networkVlan", networkVlan);
	   }

	   @Override
	   public String toString() {
	      return string().toString();
	   }
}
