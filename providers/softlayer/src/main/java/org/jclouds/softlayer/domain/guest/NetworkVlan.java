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
 * This class represents the network VLAN setting, which can be set on the primaryNetworkComponent (public IP) 
 * and on the primaryBackendNetworkComponent (private IP) of a virtual guest.
 * It currently contains only the VLAN id.
* @author Noa Kuperberg
*/

public class NetworkVlan {
	public static Builder<?> builder() {
	      return new ConcreteBuilder();
	   }

	   public Builder<?> toBuilder() {
	      return new ConcreteBuilder().fromNetworkVlan(this);
	   }

	   public abstract static class Builder<T extends Builder<T>>  {
	      protected abstract T self();

	      protected int id;

	      /**
	       * @see NetworkVlan#getId()
	       */
	      public T id(int id) {
	         this.id = id;
	         return self();
	      }

	      public NetworkVlan build() {
	         return new NetworkVlan(id);
	      }

	      public T fromNetworkVlan(NetworkVlan in) {
	         return this
	               .id(in.getId());
	      }
	   }

	   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
	      @Override
	      protected ConcreteBuilder self() {
	         return this;
	      }
	   }

	   private final int id;

	   @ConstructorProperties("id")
	   public NetworkVlan(int id) {
	      this.id = checkNotNull(id, "id cannot be null or empty: " + id);
	   }

	   /**
	    * Maps onto {@code VirtualGuest.State}
	    *
	    * @return The key name of a power state.
	    */
	   @Nullable
	   public int getId() {
	      return this.id;
	   }

	   @Override
	   public int hashCode() {
	      return Objects.hashCode(id);
	   }

	   @Override
	   public boolean equals(Object obj) {
	      if (this == obj) return true;
	      if (obj == null || getClass() != obj.getClass()) return false;
	      NetworkVlan that = NetworkVlan.class.cast(obj);
	      return Objects.equal(this.id, that.id);
	   }

	   protected ToStringHelper string() {
	      return Objects.toStringHelper(this)
	            .add("id", id);
	   }

	   @Override
	   public String toString() {
	      return string().toString();
	   }
}
