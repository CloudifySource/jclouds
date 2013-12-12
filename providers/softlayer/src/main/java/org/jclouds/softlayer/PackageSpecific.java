package org.jclouds.softlayer;

/**
 * Interface for package specific implementations.
 * Each module or class that is package specific (i.e virtual guest, bare metal instances,
 * dedicated server) should implement this interface and return the correct implementation depending on the package id.
 *
 * @see PropertiesProviderFactory
 *
 *
 * @author Eli Polonsky
 */
public interface PackageSpecific<T> {

   T create(int packageId);
}
