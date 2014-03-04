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
package org.jclouds.softlayer.reference;

/**
 * Configuration properties and constants used in SoftLayer connections.
 * 
 * @author Adrian Cole
 */
public interface SoftLayerConstants {

   /**
    * package id of the requested package to work with.
    */
   public static final String PROPERTY_SOFTLAYER_PACKAGE_ID = "jclouds.softlayer.package-id";

   /**
    * pattern where last group matches core speed
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX = "jclouds.softlayer.virtualguest.cpu-regex";

   /**
    * Default Boot Disk type (SAN, LOCAL)
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE = "jclouds.softlayer.virtualguest.disk0-type";

   /**
    * number of milliseconds to wait for an order to arrive on the api.
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY = "jclouds.softlayer.virtualguest.login-delay";

   /**
    * standard prices for all new guests.
    */
   public static final String PROPERTY_SOFTLAYER_PRICES = "jclouds.softlayer.virtualguest.prices";

   /**
    * number of milliseconds to wait for an order to be empty of transactions.
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_ENDED_DELAY = "jclouds.softlayer.virtualguest.active-transactions-ended-delay";

   /**
    * number of milliseconds to wait for an order to start its transactions.
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_STARTED_DELAY = "jclouds.softlayer.virtualguest.active-transactions-started-delay";

   /**
    * number of milliseconds to wait for a server order to accept login credentials.
    */
   public static final String PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY = "jclouds.softlayer.server.login-delay";

   /**
    * number of milliseconds to wait for a server order to finish its transactions.
    */
   public static final String PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY = "jclouds.softlayer.server.active-transactions-ended-delay";

   /**
    * number of milliseconds to wait for a server order to start its transactions.
    */
   public static final String PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY = "jclouds.softlayer.server.active-transactions-started-delay";

   /**
    * number of milliseconds to wait for a server order to be approved.
    */
   public static final String PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY = "jclouds.softlayer.server.order-approved-delay";

   /**
    * Transactions logger for reporting order transactions.
    */
   public static final String TRANSACTION_LOGGER = "jclouds.softlayer.transaction";
}
