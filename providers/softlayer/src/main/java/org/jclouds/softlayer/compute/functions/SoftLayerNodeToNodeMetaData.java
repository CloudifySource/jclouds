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

package org.jclouds.softlayer.compute.functions;

import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_PACKAGE_ID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.domain.SoftLayerNode;

import com.google.common.base.Function;

/**
 *
 * @author Eli Polonsky
 */
@Singleton
public class SoftLayerNodeToNodeMetaData implements Function<SoftLayerNode, NodeMetadata> {

   Function<SoftLayerNode, NodeMetadata> toNodeMetaData;

   @Inject
   public SoftLayerNodeToNodeMetaData(@Named(PROPERTY_SOFTLAYER_PACKAGE_ID) int packageId,
                                      SoftLayerNodeToNodeMetaDataFactory softLayerNodeToNodeMetaDataFactory) {
      this.toNodeMetaData = softLayerNodeToNodeMetaDataFactory.create(packageId);
   }

   @Override
   public NodeMetadata apply(@Nullable SoftLayerNode input) {
      return toNodeMetaData.apply(input);
   }
}
