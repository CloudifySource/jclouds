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
package org.jclouds.softlayer.compute.functions.server;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.datacenter.DatacenterToLocationTest;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.parse.server.ParseHardwareServerRunningTest;
import org.testng.annotations.Test;

import java.util.Set;

import static org.easymock.EasyMock.createNiceMock;
import static org.testng.Assert.assertEquals;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "HardwareServerToNodeMetadataTest")
public class HardwareServerToNodeMetadataTest {

   GroupNamingConvention.Factory namingConvention = Guice.createInjector().getInstance(GroupNamingConvention.Factory.class);

   @Test
   public void testApplyWhereHardwareServerIsRunning() {

      // notice if we've already parsed this properly here, we can rely on it.
      HardwareServer server = new ParseHardwareServerRunningTest().expected();

      // setup so that we have an expected Location to be parsed from the guest.
      Location expectedLocation = DatacenterToLocationTest.function.apply(server.getDatacenter());
      Supplier<Set<? extends Location>> locationSupplier = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(expectedLocation));

      HardwareServerToNodeMetaData parser = new HardwareServerToNodeMetaData(locationSupplier,
            new GetHardwareForHardwareServerMock(), new GetImageForHardwareServerMock(), namingConvention);

      NodeMetadata node = parser.apply(server);

      assertEquals(
            node,
            new NodeMetadataBuilder().ids("153748").name("node1234245345").hostname("node1234245345")
                  .location(expectedLocation).status(Status.RUNNING)
                  .publicAddresses(ImmutableSet.of("108.168.224.10"))
                  .privateAddresses(ImmutableSet.of("10.54.220.130"))
                  .hardware(new GetHardwareForHardwareServerMock().getHardware(server))
                  .imageId(new GetImageForHardwareServerMock().getImage(server).getId())
                  .operatingSystem(new GetImageForHardwareServerMock().getImage(server).getOperatingSystem()).build());

   }

   private static class GetHardwareForHardwareServerMock extends HardwareServerToNodeMetaData.GetHardwareForHardwareServer {

      @SuppressWarnings("unchecked")
      public GetHardwareForHardwareServerMock() {
         super(createNiceMock(SoftLayerClient.class), createNiceMock(Function.class));
      }

      @Override
      public Hardware getHardware(HardwareServer server) {
         return new HardwareBuilder().ids("mocked hardware").build();
      }
   }

   private static class GetImageForHardwareServerMock extends HardwareServerToNodeMetaData.GetImageForHardwareServer {

      public GetImageForHardwareServerMock() {
         super(null);
      }

      @Override
      public Image getImage(HardwareServer server) {
         return new ImageBuilder().ids("123").description("mocked image")
               .operatingSystem(OperatingSystem.builder().description("foo os").build())
               .status(Image.Status.AVAILABLE).build();
      }
   }
}
