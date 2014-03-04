package org.jclouds.softlayer;

import java.util.Properties;

/**
 * Interface for specifying default properties in relation to a specific product package
 *
 * @author Eli Polonsky
 */
public interface PropertiesProvider {

   Properties sharedProperties();

   Properties customProperties();
}
