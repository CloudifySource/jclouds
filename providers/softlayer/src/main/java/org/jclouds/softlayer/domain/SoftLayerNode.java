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

package org.jclouds.softlayer.domain;

import java.beans.ConstructorProperties;

/**
 * Base class for all Soft Layer nodes.
 *
 * CCI - {@link org.jclouds.softlayer.domain.guest.VirtualGuest}
 *
 *       Virtual Guests are all types of visualization methods supported by softlayer.
 *
 * BMI - {@link org.jclouds.softlayer.domain.server.HardwareServer}
 *
 *       Bare Metal Instances are all types of physical servers supported by softlayer.
 *
 * @author Eli Polonsky
 *
 */
public abstract class SoftLayerNode {

   protected final int accountId;
   protected final String domain;
   protected final String fullyQualifiedDomainName;
   protected final String hostname;
   protected final int id;
   protected final String primaryBackendIpAddress;
   protected final String primaryIpAddress;
   protected final int billingItemId;
   protected final OperatingSystem operatingSystem;
   protected final Datacenter datacenter;
   protected final String notes;

   /**
    * @return A node's associated account id
    */
   public int getAccountId() {
      return accountId;
   }

   /**
    * @return A node's domain name
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return A name reflecting the hostname and domain of the node.
    */
   public String getFullyQualifiedDomainName() {
      return fullyQualifiedDomainName;
   }

   /**
    * @return A node's hostname
    */
   public String getHostname() {
      return hostname;
   }

   /**
    * @return Unique ID for a node.
    */
   public int getId() {
      return id;
   }

   /**
    * @return private ip address
    */
   public String getPrimaryBackendIpAddress() {
      return primaryBackendIpAddress;
   }

   /**
    * @return public ip address
    */
   public String getPrimaryIpAddress() {
      return primaryIpAddress;
   }

   /**
    * @return The billing item for a node.o
    */
   public int getBillingItemId() {
      return billingItemId;
   }

   /**
    * @return A node's operating system.
    */
   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
   }

   /**
    * @return The node's datacenter
    */
   public Datacenter getDatacenter() {
      return datacenter;
   }

   /**
    * @return A small note about a cloud instance to use at your discretion.
    */
   public String getNotes() {
      return notes;
   }

   public static class BillingItem {
      private final int id;

      @ConstructorProperties("id")
      public BillingItem(int id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return "[id=" + id + "]";
      }
   }

   public SoftLayerNode(int accountId, String domain, String fullyQualifiedDomainName,
                        String hostname, int id, String notes, String primaryBackendIpAddress,
                        String primaryIpAddress, BillingItem billingItem, OperatingSystem operatingSystem, Datacenter datacenter) {
      this.accountId = accountId;
      this.domain = domain;
      this.fullyQualifiedDomainName = fullyQualifiedDomainName;
      this.hostname = hostname;
      this.id = id;
      this.notes = notes;
      this.primaryBackendIpAddress = primaryBackendIpAddress;
      this.primaryIpAddress = primaryIpAddress;
      this.billingItemId = billingItem == null ? -1 : billingItem.id;
      this.operatingSystem = operatingSystem;
      this.datacenter = datacenter;

   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int accountId;
      protected String domain;
      protected String fullyQualifiedDomainName;
      protected String hostname;
      protected int id;
      protected String notes;
      protected String primaryBackendIpAddress;
      protected String primaryIpAddress;
      protected int billingItemId;
      protected OperatingSystem operatingSystem;
      protected Datacenter datacenter;

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getAccountId()
       */
      public T accountId(int accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getFullyQualifiedDomainName()
       */
      public T fullyQualifiedDomainName(String fullyQualifiedDomainName) {
         this.fullyQualifiedDomainName = fullyQualifiedDomainName;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getHostname()
       */
      public T hostname(String hostname) {
         this.hostname = hostname;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getNotes()
       */
      public T notes(String notes) {
         this.notes = notes;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getPrimaryBackendIpAddress()
       */
      public T primaryBackendIpAddress(String primaryBackendIpAddress) {
         this.primaryBackendIpAddress = primaryBackendIpAddress;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getPrimaryIpAddress()
       */
      public T primaryIpAddress(String primaryIpAddress) {
         this.primaryIpAddress = primaryIpAddress;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getBillingItemId()
       */
      public T billingItemId(int billingItemId) {
         this.billingItemId = billingItemId;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getOperatingSystem()
       */
      public T operatingSystem(OperatingSystem operatingSystem) {
         this.operatingSystem = operatingSystem;
         return self();
      }

      /**
       * @see org.jclouds.softlayer.domain.SoftLayerNode#getDatacenter()
       */
      public T datacenter(Datacenter datacenter) {
         this.datacenter = datacenter;
         return self();
      }
   }
}
