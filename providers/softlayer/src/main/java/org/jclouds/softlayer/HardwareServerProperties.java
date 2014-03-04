package org.jclouds.softlayer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import javax.inject.Singleton;
import java.util.Properties;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PACKAGE_ID;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PRICES;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY;

/**
 * Default Properties for provisioning hardware servers on softlayer.
 *
 * @author Eli Polonsky
 */
@Singleton
public class HardwareServerProperties implements PropertiesProvider {

   @Override
   public Properties sharedProperties() {

      Properties properties = new Properties();
      properties.setProperty(PROPERTY_SOFTLAYER_PACKAGE_ID, "50");

      ImmutableSet.Builder<String> prices = ImmutableSet.builder();
      prices.add("21"); // 1 IP Address
      prices.add("55"); // Host Ping: categoryCode: monitoring, notification
      prices.add("57"); // Email and Ticket: categoryCode: notification
      prices.add("58"); // Automated Notification: categoryCode: response
      prices.add("1800"); // 0 GB Bandwidth: categoryCode: bandwidth
      prices.add("905"); // Reboot / Remote Console: categoryCode: remote_management
      prices.add("418"); // Nessus Vulnerability Assessment & Reporting: categoryCode: // vulnerability_scanner
      prices.add("420"); // Unlimited SSL VPN Users & 1 PPTP VPN User per account: categoryCode: // vpn_management
      properties.setProperty(PROPERTY_SOFTLAYER_PRICES, Joiner.on(',').join(prices.build()));
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],os64Bit=true,osDescriptionMatches=.*Minimal Install.*");
      return properties;
   }

   @Override
   public Properties customProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY, "" + 10 * 60 * 60 * 1000);
      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY, "" + 10 * 60 * 60 * 1000);
      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY, "" + 10 * 60 * 60 * 1000);
      properties.setProperty(PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY, "" + 10 * 60 * 60 * 1000);
      return properties;
   }
}
