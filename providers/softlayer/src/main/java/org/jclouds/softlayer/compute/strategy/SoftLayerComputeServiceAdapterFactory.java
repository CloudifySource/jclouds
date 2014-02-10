package org.jclouds.softlayer.compute.strategy;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.softlayer.PackageSpecific;
import org.jclouds.softlayer.compute.strategy.guest.SoftLayerVirtualGuestComputeServiceAdapter;
import org.jclouds.softlayer.compute.strategy.server.SoftLayerHardwareServerComputeServiceAdapter;
import org.jclouds.softlayer.compute.strategy.server.SoftLayerSingleXeon2000SeriesComputeServiceAdapter;
import org.jclouds.softlayer.compute.strategy.server.SoftLayerSingleXeon3200SeriesComputeServiceAdapter;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.SoftLayerNode;
import org.jclouds.softlayer.domain.product.ProductItem;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is responsible for providing the appropriate adapter implementation for the given package id.
 *
 * @author Eli Polonsky
 */
@Singleton
public class SoftLayerComputeServiceAdapterFactory implements PackageSpecific<ComputeServiceAdapter<SoftLayerNode,
        Iterable<ProductItem>, ProductItem, Datacenter>>{

   private SoftLayerHardwareServerComputeServiceAdapter hardwareServerComputeServiceAdapter;
   private SoftLayerVirtualGuestComputeServiceAdapter virtualGuestComputeServiceAdapter;
   private SoftLayerSingleXeon3200SeriesComputeServiceAdapter singleXeon3200SeriesComputeServiceAdapter;
private SoftLayerSingleXeon2000SeriesComputeServiceAdapter singleXeon2000SeriesComputeServiceAdapter;

   @Inject
   public SoftLayerComputeServiceAdapterFactory(
           SoftLayerHardwareServerComputeServiceAdapter hardwareServerComputeServiceAdapter,
           SoftLayerVirtualGuestComputeServiceAdapter virtualGuestComputeServiceAdapter,
           SoftLayerSingleXeon3200SeriesComputeServiceAdapter singleXeon3200SeriesComputeServiceAdapter,
           SoftLayerSingleXeon2000SeriesComputeServiceAdapter SingleXeon2000SeriesComputeServiceAdapter) {
      this.hardwareServerComputeServiceAdapter = hardwareServerComputeServiceAdapter;
      this.virtualGuestComputeServiceAdapter = virtualGuestComputeServiceAdapter;
      this.singleXeon3200SeriesComputeServiceAdapter = singleXeon3200SeriesComputeServiceAdapter;
      this.singleXeon2000SeriesComputeServiceAdapter = SingleXeon2000SeriesComputeServiceAdapter;
   }

   @Override
   public ComputeServiceAdapter<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter> create(int packageId) {

      switch (packageId) {
         case 46:
            return virtualGuestComputeServiceAdapter;
         case 50:
            return hardwareServerComputeServiceAdapter;
         case 23:
            return singleXeon3200SeriesComputeServiceAdapter;
         case 142:
        	 return singleXeon2000SeriesComputeServiceAdapter;
         default:
            throw new UnsupportedOperationException("cannot find implementation of "
                    + ComputeServiceAdapter.class + "for package id " + packageId);
      }
   }

}
