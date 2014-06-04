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
package org.jclouds.softlayer.compute.strategy;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.strategy.guest.SoftLayerVirtualGuestComputeServiceAdapter;
import org.jclouds.softlayer.compute.strategy.server.SoftLayerHardwareServerComputeServiceAdapter;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.SoftLayerNode;
import org.jclouds.softlayer.domain.guest.VirtualGuest;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.server.HardwareServer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PACKAGE_ID;

/**
 * defines the connection between the {@link SoftLayerClient} implementation and
 * the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class SoftLayerComputeServiceAdapter implements
      ComputeServiceAdapter<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter> {

   private final ComputeServiceAdapter<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter> adapter;

   @Inject
   public SoftLayerComputeServiceAdapter(@Named(PROPERTY_SOFTLAYER_PACKAGE_ID) int packageId,
         SoftLayerComputeServiceAdapterFactory softLayerComputeServiceAdapterFactory) {
      this.adapter = softLayerComputeServiceAdapterFactory.create(packageId);
   }

   @Override
   public NodeAndInitialCredentials<SoftLayerNode> createNodeWithGroupEncodedIntoName(String group, String name,
         Template template) {
      return adapter.createNodeWithGroupEncodedIntoName(group, name, template);
   }
   
   public void validateOrder(Template template, SoftLayerNode newServer) {
	   if (adapter instanceof SoftLayerHardwareServerComputeServiceAdapter) {
		   ((SoftLayerHardwareServerComputeServiceAdapter) adapter).validateOrder(template, (HardwareServer)newServer);
	   } else if (adapter instanceof SoftLayerVirtualGuestComputeServiceAdapter) {
		   ((SoftLayerVirtualGuestComputeServiceAdapter) adapter).validateOrder(template, (VirtualGuest)newServer);
	   } else {
		   throw new UnsupportedOperationException("adapter of type " + adapter.getClass().getName() + " does not support validation");
	   }
   }

   @Override
   public Iterable<Iterable<ProductItem>> listHardwareProfiles() {
      return adapter.listHardwareProfiles();
   }

   @Override
   public Iterable<ProductItem> listImages() {
      return adapter.listImages();
   }
   
   // cheat until we have a getProductItem command
   @Override
   public ProductItem getImage(final String id) {
      return adapter.getImage(id);
   }
   
   @Override
   public Iterable<SoftLayerNode> listNodes() {
      return adapter.listNodes();
   }

   @Override
   public Iterable<SoftLayerNode> listNodesByIds(final Iterable<String> ids) {
      return adapter.listNodesByIds(ids);
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      return adapter.listLocations();
   }

   @Override
   public SoftLayerNode getNode(String id) {
      return adapter.getNode(id);
   }

   @Override
   public void destroyNode(String id) {
      adapter.destroyNode(id);
   }

   @Override
   public void rebootNode(String id) {
      adapter.rebootNode(id);
   }

   @Override
   public void resumeNode(String id) {
      adapter.resumeNode(id);
   }

   @Override
   public void suspendNode(String id) {
      adapter.suspendNode(id);
   }
}
