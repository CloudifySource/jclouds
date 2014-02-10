package org.jclouds.softlayer;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PACKAGE_ID;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PRICES;

import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

public class SingleXeon2000SeriesProperties implements PropertiesProvider {

	@Override
	public Properties sharedProperties() {

	      Properties properties = new Properties();
	      properties.setProperty(PROPERTY_SOFTLAYER_PACKAGE_ID, "142");

	      ImmutableSet.Builder<String> prices = ImmutableSet.builder();
	      prices.add("15"); // 1 IP Address
	      prices.add("49"); // Host Ping: categoryCode: monitoring, notification
	      prices.add("51"); // Email and Ticket: categoryCode: notification
	      prices.add("52"); // Automated Notification: categoryCode: response
	      prices.add("504"); // Reboot / KVM over IP
	      prices.add("11"); // Disk Controller (Non-RAID)
	      prices.add("307"); // Nessus Vulnerability Assessment & Reporting: categoryCode: // vulnerability_scanner
	      prices.add("309"); // Unlimited SSL VPN Users & 1 PPTP VPN User per account: categoryCode: // vpn_management
	      properties.setProperty(PROPERTY_SOFTLAYER_PRICES, Joiner.on(',').join(prices.build()));
	      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],os64Bit=true,osDescriptionMatches=.*Minimal Install.*");
	      return properties;
	}

	@Override
	public Properties customProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
