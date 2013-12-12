package org.jclouds.softlayer.compute.functions;

import com.google.common.base.Function;
import com.google.inject.util.Types;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.softlayer.PackageSpecific;
import org.jclouds.softlayer.domain.SoftLayerNode;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is responsible for providing the appropriate transformation function implementation for the given
 * package id.
 *
 * @author Eli Polonsky
 */
@Singleton
public class SoftLayerNodeToNodeMetaDataFactory implements PackageSpecific<Function<SoftLayerNode, NodeMetadata>> {

   private Function<SoftLayerNode, NodeMetadata> virtualGuestToNodeMetadata;
   private Function<SoftLayerNode, NodeMetadata> hardwareServerToNodeMetaData;

   @Inject
   public SoftLayerNodeToNodeMetaDataFactory(
           Function<SoftLayerNode, NodeMetadata> virtualGuestToNodeMetadata,
           Function<SoftLayerNode, NodeMetadata> hardwareServerToNodeMetaData) {
      this.virtualGuestToNodeMetadata = virtualGuestToNodeMetadata;
      this.hardwareServerToNodeMetaData = hardwareServerToNodeMetaData;
   }

   @Override
   public Function<SoftLayerNode, NodeMetadata> create(int packageId) {

      switch (packageId) {
         case 46:
            return virtualGuestToNodeMetadata;
         default:
            return hardwareServerToNodeMetaData;
      }
   }
}
