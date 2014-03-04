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

package org.jclouds.softlayer.parse.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.OperatingSystem;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.domain.server.HardwareStatus;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Eli Polonsky
 */
@Test(groups = "unit", testName = "ParseHardwareServerRunningTest")
public class ParseHardwareServerRunningTest extends BaseItemParserTest<HardwareServer> {

   @Override
   public String resource() {
      return "/hardware_server_running.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public HardwareServer expected() {
      return HardwareServer.builder()
              .id(153748).accountId(275920).billingItemId(16996905)
              .hostname("node1234245345")
              .domain("me.org").fullyQualifiedDomainName("node1234245345.me.org")
              .primaryBackendIpAddress("10.54.220.130").primaryIpAddress("108.168.224.10")
              .operatingSystem(OperatingSystem.builder().id(2262966)
                      .passwords(Password.builder().id(1923044).username("root").password("QVNa8GN2").build())
                      .build())
              .datacenter(Datacenter.builder().id(168642).name("sjc01").longName("San Jose 1").build())
              .statusId(5)
              .hardwareStatus(new HardwareStatus(HardwareServer.Status.ACTIVE))
              .build();
   }

   protected Injector injector() {
      return Guice.createInjector(new SoftLayerParserModule(), new GsonModule());
   }

}
