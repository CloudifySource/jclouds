package org.jclouds.softlayer;

import javax.inject.Inject;

/**
 * This class is responsible for providing the appropriate properties provider implementation for the given package id.
 *
 * @author Eli Polonsky
 */
public class PropertiesProviderFactory implements PackageSpecific<PropertiesProvider> {

   private SingleXeon3200SeriesProperties singleXeon3200SeriesProperties;
   private SingleXeon2000SeriesProperties singleXeon2000SeriesProperties;
   private HardwareServerProperties hardwareServerProperties;
   private VirtualGuestProperties virtualGuestProperties;

   @Inject
   public PropertiesProviderFactory(SingleXeon3200SeriesProperties singleXeon3200SeriesProperties,
                                    HardwareServerProperties hardwareServerProperties,
                                    VirtualGuestProperties virtualGuestProperties,
                                    SingleXeon2000SeriesProperties singleXeon2000SeriesProperties) {
      this.singleXeon3200SeriesProperties = singleXeon3200SeriesProperties;
      this.singleXeon2000SeriesProperties = singleXeon2000SeriesProperties;
      this.hardwareServerProperties = hardwareServerProperties;
      this.virtualGuestProperties = virtualGuestProperties;
   }

   @Override
   public PropertiesProvider create(int packageId) {

      switch (packageId) {

         case 23:
            return singleXeon3200SeriesProperties;
         case 50:
            return hardwareServerProperties;
         case 46:
            return virtualGuestProperties;
         case 142:
        	 return singleXeon2000SeriesProperties;
         default:
            throw new UnsupportedOperationException("cannot find properties provider for package id " + packageId);
      }

   }

}
