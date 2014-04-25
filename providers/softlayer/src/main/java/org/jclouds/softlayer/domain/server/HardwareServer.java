/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.jclouds.softlayer.domain.server;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.SoftLayerNode;

import java.beans.ConstructorProperties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The hardware server data type contains general information relating to a single SoftLayer server.
 *
 * @author Eli Polonsky
 * @see <a href=
"http://sldn.softlayer.com/reference/datatypes/SoftLayer_Hardware"
/>

 */
public class HardwareServer extends SoftLayerNode {

   /**
    * These statuses come from the hardwareStatus field. i.e.
    * http://sldn.softlayer.com/reference/datatypes/SoftLayer_Hardware_Status
    */
   public static enum Status {
      ACTIVE,
      DEPLOY,
      DEPLOY2,
      MACWAIT,
      RECLAIM,
      UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHardwareServer(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends SoftLayerNode.Builder<T> {
      protected abstract T self();

      protected boolean privateNetworkOnly;
      protected int hardwareStatusId;
      protected HardwareStatus hardwareStatus;

      /**
       * @see HardwareServer#isPrivateNetworkOnly()
       */
      public T privateNetworkOnly(boolean privateNetworkOnly) {
         this.privateNetworkOnly = privateNetworkOnly;
         return self();
      }

      /**
       * @see HardwareServer#getHardwareStatusId()
       */
      public T statusId(int statusId) {
         this.hardwareStatusId = statusId;
         return self();
      }

      /**
       * @see HardwareServer#getHardwareStatus() ()
       */
      public T hardwareStatus(HardwareStatus hardwareStatus) {
         this.hardwareStatus = hardwareStatus;
         return self();
      }

      public HardwareServer build() {
         return new HardwareServer(accountId, domain, fullyQualifiedDomainName, hostname,
                 id, notes, privateNetworkOnly, hardwareStatusId, primaryBackendIpAddress,
                 primaryIpAddress, new BillingItem(billingItemId),
                 operatingSystem, datacenter, hardwareStatus);
      }

      public T fromHardwareServer(HardwareServer in) {
         return this
                 .accountId(in.getAccountId())
                 .domain(in.getDomain())
                 .fullyQualifiedDomainName(in.getFullyQualifiedDomainName())
                 .hostname(in.getHostname())
                 .id(in.getId())
                 .notes(in.getNotes())
                 .privateNetworkOnly(in.isPrivateNetworkOnly())
                 .statusId(in.getHardwareStatusId())
                 .primaryBackendIpAddress(in.getPrimaryBackendIpAddress())
                 .primaryIpAddress(in.getPrimaryIpAddress())
                 .billingItemId(in.getBillingItemId())
                 .operatingSystem(in.getOperatingSystem())
                 .datacenter(in.getDatacenter())
                 .hardwareStatus(in.getHardwareStatus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final boolean privateNetworkOnly;
   private final int hardwareStatusId;
   private final HardwareStatus hardwareStatus;

   @ConstructorProperties({
           "accountId", "domain", "fullyQualifiedDomainName", "hostname", "id", "notes", "privateNetworkOnlyFlag", "hardwareStatusId", "primaryBackendIpAddress", "primaryIpAddress", "billingItem", "operatingSystem", "datacenter", "hardwareStatus"
   })
   protected HardwareServer(int accountId, @Nullable String domain,
                          @Nullable String fullyQualifiedDomainName, @Nullable String hostname, int id,
                          @Nullable String notes, @Nullable boolean privateNetworkOnly, @Nullable int hardwareStatusId, @Nullable String primaryBackendIpAddress,
                          @Nullable String primaryIpAddress, @Nullable BillingItem billingItem,
                          @Nullable OperatingSystem operatingSystem, @Nullable Datacenter datacenter, @Nullable HardwareStatus hardwareStatus) {
      super(accountId, domain, fullyQualifiedDomainName, hostname, id, notes, primaryBackendIpAddress, primaryIpAddress,
              billingItem, operatingSystem, datacenter);
      this.privateNetworkOnly = privateNetworkOnly;
      this.hardwareStatusId = hardwareStatusId;
      this.hardwareStatus = hardwareStatus;
   }

   /**
    * @return Whether the computing instance only has access to the private network.
    */
   @Nullable
   public boolean isPrivateNetworkOnly() {
      return this.privateNetworkOnly;
   }

   /**
    * @return A computing instances status ID
    */
   @Nullable
   public int getHardwareStatusId() {
      return this.hardwareStatusId;
   }

   /**
    * @return The current power state of a virtual guest.
    */
   @Nullable
   public HardwareStatus getHardwareStatus() {
      return this.hardwareStatus;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accountId, domain, fullyQualifiedDomainName, hostname, id, notes, privateNetworkOnly, hardwareStatusId, primaryBackendIpAddress, primaryIpAddress, billingItemId, operatingSystem, datacenter, hardwareStatus);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      HardwareServer that = HardwareServer.class.cast(obj);
      return Objects.equal(this.accountId, that.accountId)
              && Objects.equal(this.domain, that.domain)
              && Objects.equal(this.fullyQualifiedDomainName, that.fullyQualifiedDomainName)
              && Objects.equal(this.hostname, that.hostname)
              && Objects.equal(this.id, that.id)
              && Objects.equal(this.notes, that.notes)
              && Objects.equal(this.privateNetworkOnly, that.privateNetworkOnly)
              && Objects.equal(this.hardwareStatusId, that.hardwareStatusId)
              && Objects.equal(this.primaryBackendIpAddress, that.primaryBackendIpAddress)
              && Objects.equal(this.primaryIpAddress, that.primaryIpAddress)
              && Objects.equal(this.billingItemId, that.billingItemId)
              && Objects.equal(this.operatingSystem, that.operatingSystem)
              && Objects.equal(this.datacenter, that.datacenter)
              && Objects.equal(this.hardwareStatus, that.hardwareStatus);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
              .add("accountId", accountId).add("domain", domain).add("fullyQualifiedDomainName", fullyQualifiedDomainName).add("hostname", hostname).add("id", id).add("notes", notes).add("privateNetworkOnly", privateNetworkOnly).add("hardwareStatusId", hardwareStatusId).add("primaryBackendIpAddress", primaryBackendIpAddress).add("primaryIpAddress", primaryIpAddress).add("billingItemId", billingItemId).add("operatingSystem", operatingSystem).add("datacenter", datacenter).add("hardwareStatus", hardwareStatus);
   }

   @Override
   public String toString() {
      return string().toString();
   }


}
