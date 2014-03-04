/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jclouds.softlayer.compute.functions;

import com.google.common.base.Function;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.softlayer.PackageSpecific;
import org.jclouds.softlayer.compute.functions.guest.VirtualGuestToNodeMetadata;
import org.jclouds.softlayer.compute.functions.server.HardwareServerToNodeMetaData;
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

   private VirtualGuestToNodeMetadata virtualGuestToNodeMetadata;
   private HardwareServerToNodeMetaData hardwareServerToNodeMetaData;

   @Inject
   public SoftLayerNodeToNodeMetaDataFactory(
           VirtualGuestToNodeMetadata virtualGuestToNodeMetadata,
           HardwareServerToNodeMetaData hardwareServerToNodeMetaData) {
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
