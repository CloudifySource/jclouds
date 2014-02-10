package org.jclouds.softlayer.compute.functions.product;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.softlayer.PackageSpecific;
import org.jclouds.softlayer.compute.functions.product.guest.VirtualGuestProductItemsToHardware;
import org.jclouds.softlayer.compute.functions.product.server.HardwareServerProductItemsToHardware;
import org.jclouds.softlayer.compute.functions.product.server.SingleXeon2000SeriesProductItemsToHardware;
import org.jclouds.softlayer.compute.functions.product.server.SingleXeon3200SeriesProductItemsToHardware;
import org.jclouds.softlayer.domain.product.ProductItem;

import com.google.common.base.Function;

/**
 * This class is responsible for creating the proper product items to hardware conversion,
 * depending on the package id.
 *
 * @author Eli Polonsky
 */
@Singleton
public class ProductItemsToHardwareFactory implements PackageSpecific<Function<Iterable<ProductItem>, Hardware>> {

   private VirtualGuestProductItemsToHardware virtualGuestProductItemsToHardware;
   private HardwareServerProductItemsToHardware hardwareServerProductItemsToHardware;
   private SingleXeon3200SeriesProductItemsToHardware singleXeon3200SeriesProductItemsToHardware;
   private SingleXeon2000SeriesProductItemsToHardware singleXeon2000SeriesProductItemsToHardware;

   @Inject
   public ProductItemsToHardwareFactory(VirtualGuestProductItemsToHardware virtualGuestProductItemsToHardware,
                                        HardwareServerProductItemsToHardware hardwareServerProductItemsToHardware,
                                        SingleXeon3200SeriesProductItemsToHardware singleXeon3200SeriesProductItemsToHardware,
                                        SingleXeon2000SeriesProductItemsToHardware singleXeon2000SeriesProductItemsToHardware) {
      this.virtualGuestProductItemsToHardware = virtualGuestProductItemsToHardware;
      this.hardwareServerProductItemsToHardware = hardwareServerProductItemsToHardware;
      this.singleXeon3200SeriesProductItemsToHardware = singleXeon3200SeriesProductItemsToHardware;
      this.singleXeon2000SeriesProductItemsToHardware = singleXeon2000SeriesProductItemsToHardware;
   }

   @Override
   public Function<Iterable<ProductItem>, Hardware> create(int packageId) {

      switch (packageId) {

         case 46:
            return virtualGuestProductItemsToHardware;
         case 50:
            return hardwareServerProductItemsToHardware;
         case 23:
            return singleXeon3200SeriesProductItemsToHardware;
         case 142:
        	 return singleXeon2000SeriesProductItemsToHardware;
         default:
            throw new UnsupportedOperationException("cannot find implementation for package id " + packageId);

      }

   }
}
