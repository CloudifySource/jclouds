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
package org.jclouds.softlayer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import javax.inject.Singleton;
import java.util.Properties;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.*;

/**
 * Default Properties for provisioning virtual guest on softlayer.
 *
 * @author Eli Polonsky
 */
@Singleton
public class VirtualGuestProperties implements PropertiesProvider {

   @Override
   public Properties sharedProperties() {

      Properties properties = new Properties();
      properties.setProperty(PROPERTY_SOFTLAYER_PACKAGE_ID, "46");
      properties.setProperty(PROPERTY_SOFTLAYER_PACKAGE_SESSION_INTERVAL_SEC, "86400");	// defaults to once a day
      properties.setProperty(PROPERTY_SOFTLAYER_PACKAGE_CACHE_FILE_PATH, "C:/gs-files/product_package_46.json");	// defaults value
      properties.setProperty(PROPERTY_SOFTLAYER_PACKAGE_CACHE_TTL_SEC, "18000");	// defaults to 5 hours
      ImmutableSet.Builder<String> items = ImmutableSet.builder();
      ImmutableSet.Builder<String> prices = ImmutableSet.builder();
      items.add("15"); // 1 IP Address
      items.add("49"); // Host Ping: categoryCode: monitoring, notification
      items.add("51"); // Email and Ticket: categoryCode: notification
      items.add("52"); // Automated Notification: categoryCode: response
      //items.add("439"); // 0 GB Bandwidth: categoryCode: bandwidth
      items.add("503"); // Reboot / Remote Console: categoryCode: remote_management
      items.add("307"); // Nessus Vulnerability Assessment & Reporting: categoryCode: // vulnerability_scanner
      items.add("309"); // Unlimited SSL VPN Users & 1 PPTP VPN User per account: categoryCode: // vpn_management
      properties.setProperty(PROPERTY_SOFTLAYER_PRICES, Joiner.on(',').join(prices.build()));
      properties.setProperty(PROPERTY_SOFTLAYER_ITEMS, Joiner.on(',').join(items.build()));
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],os64Bit=true,osDescriptionMatches=.*Minimal Install.*");
      properties.setProperty(PROPERTY_SOFTLAYER_FLEX_IMAGE_ID, "");
      properties.setProperty(PROPERTY_SOFTLAYER_FLEX_IMAGE_GLOBAL_IDENTIFIER, "");
      
      return properties;
   }

   @Override
   public Properties customProperties() {

      Properties properties = new Properties();

      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY, "" + 60 * 60 * 1000);
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_ENDED_DELAY, "" + 60 * 60 * 1000);
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_STARTED_DELAY, "" + 60 * 60 * 1000);
      // ex: for private (ex. don't share hardware) use "Private [0-9]+ x ([.0-9]+) GHz Core[s]?"
      // ex: for private and public use ".*[0-9]+ x ([.0-9]+) GHz Core[s]?"
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX, "[0-9]+ x ([0-9.]+) GHz Core[s]?");
      // SAN or LOCAL
      properties.setProperty(PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE, "LOCAL");
      properties.setProperty(PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS, "");

      return properties;

   }
}
