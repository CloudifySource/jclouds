/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.jclouds.softlayer.compute.functions.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.SoftLayerNode;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.server.HardwareServer;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Eli Polonsky
 */
@Singleton
public class HardwareServerToNodeMetaData implements Function<SoftLayerNode, NodeMetadata>  {

   public static final Map<HardwareServer.Status, NodeMetadata.Status> serverStatusToNodeStatus = ImmutableMap
           .<HardwareServer.Status, NodeMetadata.Status> builder()
           .put(HardwareServer.Status.DEPLOY, NodeMetadata.Status.PENDING)
           .put(HardwareServer.Status.DEPLOY2, NodeMetadata.Status.PENDING)
           .put(HardwareServer.Status.MACWAIT, NodeMetadata.Status.PENDING)
           .put(HardwareServer.Status.RECLAIM, NodeMetadata.Status.SUSPENDED)
           .put(HardwareServer.Status.ACTIVE, NodeMetadata.Status.RUNNING)
           .put(HardwareServer.Status.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build();

   private final Supplier<Set<? extends Location>> locations;
   private final GetHardwareForHardwareServer hardware;
   private final GetImageForHardwareServer images;
   private final GroupNamingConvention nodeNamingConvention;

   @Inject
   HardwareServerToNodeMetaData(@Memoized Supplier<Set<? extends Location>> locations,
                                GetHardwareForHardwareServer hardware, GetImageForHardwareServer images,
                                GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
      this.hardware = hardware;
      this.images = images;
   }

   @Override
   public NodeMetadata apply(SoftLayerNode from) {

      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getHostname());
      builder.hostname(from.getHostname());
      if (from.getDatacenter() != null)
         builder.location(from(locations.get()).firstMatch(
                 LocationPredicates.idEquals(from.getDatacenter().getId() + "")).orNull());
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.getHostname()));

      Image image = images.getImage((HardwareServer) from);
      if (image != null) {
         builder.imageId(image.getId());
         builder.operatingSystem(image.getOperatingSystem());
         builder.hardware(hardware.getHardware((HardwareServer) from));
      }
      builder.status(serverStatusToNodeStatus.get(((HardwareServer)from).getHardwareStatus().getStatus()));

      // These are null for 'bad' guest orders in the HALTED state.
      if (from.getPrimaryIpAddress() != null)
         builder.publicAddresses(ImmutableSet.<String> of(from.getPrimaryIpAddress()));
      if (from.getPrimaryBackendIpAddress() != null)
         builder.privateAddresses(ImmutableSet.<String> of(from.getPrimaryBackendIpAddress()));
      return builder.build();
   }

   @Singleton
   public static class GetHardwareForHardwareServer {

      private final SoftLayerClient client;
      private final Function<Iterable<ProductItem>, Hardware> productItemsToHardware;

      @Inject
      public GetHardwareForHardwareServer(SoftLayerClient client,
                                        Function<Iterable<ProductItem>, Hardware> productItemsToHardware) {
         this.client = checkNotNull(client, "client");
         this.productItemsToHardware = checkNotNull(productItemsToHardware, "productItemsToHardware");

      }

      public Hardware getHardware(HardwareServer server) {
    	  //TODO(adaml): The get method is not available in BMI
    	  return null;
      }
   }

   @Singleton
   public static class GetImageForHardwareServer {

      private SoftLayerClient client;

      @Inject
      public GetImageForHardwareServer(SoftLayerClient client) {
         this.client = client;
      }
      
 	 //getOrderTemplate api call is invalid for BM servers and will always return null.
      public Image getImage(HardwareServer server) {
    	  return null;
      }
   }
}
