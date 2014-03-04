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
package org.jclouds.softlayer;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.softlayer.features.account.AccountClient;
import org.jclouds.softlayer.features.datacenter.DatacenterClient;
import org.jclouds.softlayer.features.guest.VirtualGuestClient;
import org.jclouds.softlayer.features.product.ProductPackageClient;
import org.jclouds.softlayer.features.server.HardwareServerClient;

import java.io.Closeable;

/**
 * Provides synchronous access to SoftLayer.
 * <p/>
 * 
 * @see SoftLayerAsyncClient
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole
 */
public interface SoftLayerClient extends Closeable {

   /**
    * Provides synchronous access to VirtualGuest features.
    */
   @Delegate
   VirtualGuestClient getVirtualGuestClient();

   /**
    * Provides synchronous access to Datacenter features.
    */
   @Delegate
   DatacenterClient getDatacenterClient();

   /**
    * Provides synchronous access to ProductPackage features.
    */
   @Delegate
   ProductPackageClient getProductPackageClient();

   /**
    * Provides synchronous access to Account features.
    */
   @Delegate
   AccountClient getAccountClient();

   /**
    * Provides synchronous access to Hardware features.
    */
   @Delegate
   HardwareServerClient getHardwareServerClient();

}
