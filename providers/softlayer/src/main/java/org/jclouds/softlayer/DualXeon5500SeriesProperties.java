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

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PACKAGE_ID;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PRICES;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_ITEMS;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_DISK_CONTROLLER_ID;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_USE_HOURLY_PRICING;

import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author adaml
 *
 */
public class DualXeon5500SeriesProperties implements PropertiesProvider {
	   @Override
	   public Properties sharedProperties() {

	      Properties properties = new Properties();
	      properties.setProperty(PROPERTY_SOFTLAYER_PACKAGE_ID, "42");

	      ImmutableSet.Builder<String> items = ImmutableSet.builder();
	      items.add("15"); // 1 IP Address
	      items.add("49"); // Host Ping: categoryCode: monitoring, notification
	      items.add("51"); // Email and Ticket: categoryCode: notification
	      items.add("52"); // Automated Notification: categoryCode: response
	      items.add("504"); // Reboot / KVM over IP
	      items.add("307"); // Nessus Vulnerability Assessment & Reporting: categoryCode: // vulnerability_scanner
	      items.add("309"); // Unlimited SSL VPN Users & 1 PPTP VPN User per account: categoryCode: // vpn_management
	      ImmutableSet.Builder<String> prices = ImmutableSet.builder();
	      properties.setProperty(PROPERTY_SOFTLAYER_ITEMS, Joiner.on(',').join(items.build()));
	      properties.setProperty(PROPERTY_SOFTLAYER_PRICES, Joiner.on(',').join(prices.build()));
	      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],os64Bit=true,osDescriptionMatches=.*Minimal Install.*");
	      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_HARDWARE_USE_HOURLY_PRICING, "false");
	      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_HARDWARE_DISK_CONTROLLER_ID, "487");
	      return properties;
	   }

	   @Override
	   public Properties customProperties() {
	      Properties properties = new Properties();

	      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY, "" + 10 * 60 * 60 * 1000);
	      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY, "" + 5 * 10 * 60 * 60 * 1000);// five hours
	      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY, "" + 5 * 10 * 60 * 60 * 1000);// five hours
	      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY, "" + 5 * 10 * 60 * 60 * 1000);// five hours
	      return properties;

	   }
}
