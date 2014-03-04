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

package org.jclouds.softlayer.features.server;

import org.jclouds.softlayer.domain.Transaction;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductOrderReceipt;
import org.jclouds.softlayer.domain.server.HardwareServer;

import java.util.Set;

/**
 * Provides synchronous access to HardwareServer.
 * <p/>
 *
 * @see HardwareServerAsyncClient
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Eli Polonsky
 * @deprecated This will be renamed to HardwareServerApi in 1.7.0.
 *
 */
public interface HardwareServerClient {

   /**
    *
    * @param id
    *           id of the hardware server
    * @return hardware server or null if not found
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Hardware_Server"/>
    */
   HardwareServer getHardwareServer(long id);

   /**
    * Cancel the resource or service for a billing Item
    *
    * @param id
    *            The id of the billing item to cancel
    * @return true or false
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Billing_Item/cancelService" />
    */
   boolean cancelService(long id);

   /**
    * Use this method for placing server orders and additional services orders.
    * @param order
    *             Details required to order.
    * @return A receipt for the order
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Product_Order/placeOrder" />
    */
   ProductOrderReceipt orderHardwareServer(ProductOrder order);

   /**
    * Use this method for verifying server orders and additional services orders.
    * @param order
    *             Details required to order.
    * @return The order itself, containing some additional debugging information in case of errors.
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Product_Order/verifyOrder" />
    */
   ProductOrder verifyHardwareServerOrder(ProductOrder order);

   /**
    *
    * @return an account's associated hardware objects.
    * @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Account/getHardware" />
    */
   Set<HardwareServer> listHardwareServers();

   /**
    * Obtain an order container that is ready to be sent to the orderHardwareServer method.
    * This container will include all services that the selected computing instance has.
    * If desired you may remove prices which were returned.
    * @see <a href=" @see <a href="http://sldn.softlayer.com/reference/services/SoftLayer_Product_Order/placeOrder" />
    * @param id
    *          The id of the existing Hardware Server
    * @return
    *          The ProductOrder used to create the HardwareServer or null if not available
    */
   ProductOrder getOrderTemplate(long id);

   /**
    * Use this method to retrieve information about a server's active transaction.
    *
    * @return The active transaction a server currently undertakes.
    * @see <a href=http://sldn.softlayer.com/reference/services/SoftLayer_Hardware_Server/getActiveTransaction"/>
    */
   Transaction getActiveTransaction(long id);


   /**
    * Use this method to retrieve information about a server's last transaction.
    *
    * @return The last completed transaction a server executed.
    * @see <a href=http://sldn.softlayer.com/reference/services/SoftLayer_Hardware_Server/getLastTransaction"/>
    */
   Transaction getLastTransaction(long id);

   /**
    * hard reboot the guest.
    *
    * @param id
    *           id of the virtual guest
    */
   void rebootHardHardwareServer(long id);

   /**
    * Power off a guest
    *
    * @param id
    *           id of the virtual guest
    */
   void powerOffHardwareServer(long id);

   /**
    * Power on a guest
    *
    * @param id
    *           id of the virtual guest
    */
   void powerOnHardwareServer(long id);

}
