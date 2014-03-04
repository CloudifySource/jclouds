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
package org.jclouds.softlayer.features.server;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.Fallbacks;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.softlayer.binders.ProductOrderToJson;
import org.jclouds.softlayer.domain.Transaction;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductOrderReceipt;
import org.jclouds.softlayer.domain.server.HardwareServer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Provides asynchronous access to HardwareServer via their REST API.
 * <p/>
 * 
 * @see HardwareServerClient
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Eli Polonsky
 * @deprecated Async interfaces will be removed in 1.7.0
 */
@Deprecated
@RequestFilters(BasicAuthentication.class)
@Path("/v{jclouds.api-version}")
public interface HardwareServerAsyncClient {

   public static String SERVER_MASK = "hardwareStatus;operatingSystem.passwords;datacenter;billingItem";
   public static String LIST_HARDWARE_MASK = "hardware.hardwareStatus;hardware.operatingSystem.passwords;hardware.datacenter;hardware.billingItem";

   /**
    * @see HardwareServerClient#getHardwareServer
    */
   @GET
   @Path("/SoftLayer_Hardware_Server/{id}.json")
   @QueryParams(keys = "objectMask", values = SERVER_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ListenableFuture<HardwareServer> getHardwareServer(@PathParam("id") long id);

   /**
    * @see HardwareServerClient#cancelService
    */
   @GET
   @Path("/SoftLayer_Billing_Item/{id}/cancelService.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> cancelService(@PathParam("id") long id);

   /**
    * @see HardwareServerClient#orderHardwareServer
    */
   @POST
   @Path("/SoftLayer_Product_Order/placeOrder.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ListenableFuture<ProductOrderReceipt> orderHardwareServer(@BinderParam(ProductOrderToJson.class) ProductOrder order);

   /**
    * @see org.jclouds.softlayer.features.server.HardwareServerClient#verifyHardwareServerOrder
    */
   @POST
   @Path("/SoftLayer_Product_Order/verifyOrder.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ListenableFuture<ProductOrder> verifyHardwareServerOrder(@BinderParam(ProductOrderToJson.class)ProductOrder order);

   /**
    * @see HardwareServerClient#listHardwareServers()
    */
   @GET
   @Path("/SoftLayer_Account/getHardware")
   @QueryParams(keys = "objectMask", values = LIST_HARDWARE_MASK)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<HardwareServer>> listHardwareServers();

   /**
    * @see org.jclouds.softlayer.features.server.HardwareServerClient#rebootHardHardwareServer
    */
   @GET
   @Path("/SoftLayer_Hardware_Server/{id}/rebootHard.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   ListenableFuture<Void> rebootHardHardwareServer(@PathParam("id") long id);

   /**
    * @see org.jclouds.softlayer.features.server.HardwareServerClient#powerOffHardwareServer
    */
   @GET
   @Path("/SoftLayer_Hardware_Server/{id}/powerOff.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   ListenableFuture<Void> powerOffHardwareServer(@PathParam("id") long id);

   /**
    * @see org.jclouds.softlayer.features.server.HardwareServerClient#powerOnHardwareServer
    */
   @GET
   @Path("/SoftLayer_Hardware_Server/{id}/powerOn.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   ListenableFuture<Void> powerOnHardwareServer(@PathParam("id") long id);


   /**
    * Throws an Internal Server Error if called on bad orders (mapped to HttpResponseException)
    * @see org.jclouds.softlayer.features.server.HardwareServerClient#getOrderTemplate
    * @throws org.jclouds.http.HttpResponseException if called with a 'bad' order.
    */
   @GET
   @Path("SoftLayer_Hardware_Server/{id}/getOrderTemplate/MONTHLY.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ListenableFuture<ProductOrder> getOrderTemplate(@PathParam("id") long id);

   /**
    * @see org.jclouds.softlayer.features.server.HardwareServerClient#getActiveTransaction
    */
   @GET
   @Path("/SoftLayer_Hardware_Server/{id}/getActiveTransaction.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ListenableFuture<Transaction> getActiveTransaction(@PathParam("id") long id);

   /**
    * @see org.jclouds.softlayer.features.server.HardwareServerClient#getLastTransaction
    */
   @GET
   @Path("/SoftLayer_Hardware_Server/{id}/getLastTransaction.json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ListenableFuture<Transaction> getLastTransaction(@PathParam("id") long id);

}
