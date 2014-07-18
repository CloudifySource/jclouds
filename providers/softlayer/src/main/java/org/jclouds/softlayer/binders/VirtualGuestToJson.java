/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
package org.jclouds.softlayer.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.Binder;
import org.jclouds.softlayer.domain.guest.PrimaryBackendNetworkComponent;
import org.jclouds.softlayer.domain.guest.VirtualGuest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Converts a ProductOrder into a json string valid for placing an order via the softlayer api The
 * String is set into the payload of the HttpRequest
 * 
 * @author Noa Kuperberg
 */
public class VirtualGuestToJson implements Binder {

   private Json json;

   @Inject
   public VirtualGuestToJson(Json json) {
      this.json = json;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(input, "templateVirtualGuest");
      VirtualGuest templateVirtualGuest = VirtualGuest.class.cast(input);
      request.setPayload(buildJson(templateVirtualGuest));
      return request;
   }

   /**
    * Builds a Json string suitable for sending to the softlayer api
    * 
    * @param order
    * @return
    */
   String buildJson(VirtualGuest templateVirtualGuest) {

	  Set<NetworkComponent> networkComponents = new HashSet<NetworkComponent>();
 	  NetworkComponent networkComponent = new NetworkComponent(templateVirtualGuest.getMaxNetworkSpeed());
 	  networkComponents.add(networkComponent);
 	  
 	  List<BlockDevice> blockDevices = new ArrayList<BlockDevice>();
 	  List<Integer> blockDevicesDiskCapacity = templateVirtualGuest.getBlockDevicesDiskCapacity();
 	  int deviceIndex = 0;
 	  if (blockDevicesDiskCapacity != null) {
 		 for (Integer diskCapacity : blockDevicesDiskCapacity) {
 	 		  DiskImage diskImage = new DiskImage(diskCapacity);
 	 		  BlockDevice blockDevice = new BlockDevice(String.valueOf(deviceIndex), diskImage);
 	 		  blockDevices.add(blockDevice);
 	 		  
 	 		  deviceIndex++;
 	 		  if (deviceIndex == 1) {	 // deviceIndex cannot be 1, it's reserved for the SWAP disk
 	 			 deviceIndex++;
 	 		  }
 	 	  }
 	  } 	  
 	  
      VirtualGuestCreateObjectParameters createObjectParameters = new VirtualGuestCreateObjectParameters(templateVirtualGuest.getHostname(), templateVirtualGuest.getDomain(),
    		  templateVirtualGuest.getDatacenter().getName(), true /*hourlyBillingFlag*/, templateVirtualGuest.getStartCpus(), templateVirtualGuest.getMaxMemory(),
    		  templateVirtualGuest.getOperatingSystemReferenceCode(), templateVirtualGuest.isPrivateNetworkOnlyFlag(), networkComponents, 
    		  templateVirtualGuest.getPrimaryBackendNetworkComponent(), blockDevices, templateVirtualGuest.isLocalDiskFlag(), templateVirtualGuest.getPostInstallScriptUri());

      return json.toJson(ImmutableMap.of("parameters", ImmutableList.<VirtualGuestCreateObjectParameters> of(createObjectParameters)));
   }

   @SuppressWarnings("unused")
   private static class VirtualGuestCreateObjectParameters {
      private String hostname;
      private String domain;
      private Datacenter datacenter;
      private boolean hourlyBillingFlag;
      private int startCpus;
      private long maxMemory;
      private String operatingSystemReferenceCode;
      private boolean privateNetworkOnlyFlag;
      private Set<NetworkComponent> networkComponents;
      private PrimaryBackendNetworkComponent primaryBackendNetworkComponent;
      private List<BlockDevice> blockDevices;
      private boolean localDiskFlag;
      private String postInstallScriptUri;
    
      
      public VirtualGuestCreateObjectParameters(String hostname, String domain, String datacenterName, boolean hourlyBillingFlag,
    		  int startCpus, long maxMemory, String operatingSystemReferenceCode, boolean privateNetworkOnlyFlag,
    		  Set<NetworkComponent> networkComponents, PrimaryBackendNetworkComponent primaryBackendNetworkComponent, 
    		  List<BlockDevice> blockDevices, boolean localDiskFlag, String postInstallScriptUri) {
    	  
    	  this.hostname = hostname;
    	  this.domain = domain;
    	  this.datacenter = new Datacenter(datacenterName);
    	  this.hourlyBillingFlag = hourlyBillingFlag;
    	  this.startCpus = startCpus;
    	  this.maxMemory = maxMemory;
    	  this.operatingSystemReferenceCode = operatingSystemReferenceCode;
    	  this.privateNetworkOnlyFlag = privateNetworkOnlyFlag;
    	  this.networkComponents = networkComponents;
    	  this.primaryBackendNetworkComponent = primaryBackendNetworkComponent;
    	  this.blockDevices = blockDevices;
    	  this.localDiskFlag = localDiskFlag;
    	  this.postInstallScriptUri = postInstallScriptUri;
      }

   }



   @SuppressWarnings("unused")
   private static class Price {
      private long id;

      public Price(long id) {
         this.id = id;
      }
   }
   
   @SuppressWarnings("unused")
   private static class Datacenter {
      private String name;

      public Datacenter(String name) {
         this.name = name;
      }
   }

   @SuppressWarnings("unused")
   private static class NetworkComponent {
      private long maxSpeed;

      public NetworkComponent(long maxSpeed) {
         this.maxSpeed = maxSpeed;
      }
   }
   
   @SuppressWarnings("unused")
   private static class BlockDevice {
      private String device;
      private DiskImage diskImage;

      public BlockDevice(String device, DiskImage diskImage) {
         this.device = device;
         this.diskImage = diskImage;
      }
   }
   
   @SuppressWarnings("unused")
   private static class DiskImage {
      private int capacity;

      public DiskImage(int capacity) {
         this.capacity = capacity;
      }
   }
   
}
