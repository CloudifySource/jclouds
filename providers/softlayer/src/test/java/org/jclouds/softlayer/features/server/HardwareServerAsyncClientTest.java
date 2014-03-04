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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.softlayer.features.BaseSoftLayerAsyncClientTest;
import org.jclouds.softlayer.features.guest.VirtualGuestAsyncClient;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.jclouds.reflect.Reflection2.method;

/**
 * Tests annotation parsing of {@code HardwareServerAsyncClient}
 * 
 * @author Eli Polonsky
 */
@Test(groups = "unit")
public class HardwareServerAsyncClientTest extends BaseSoftLayerAsyncClientTest<HardwareServerAsyncClient> {

   public void testListHardwareServers() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(HardwareServerAsyncClient.class, "listHardwareServers");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Account/getHardware?objectMask=hardware.hardwareStatus%3Bhardware.operatingSystem.passwords%3Bhardware.datacenter%3Bhardware.billingItem HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Account/getHardware?objectMask=hardware.hardwareStatus%3Bhardware.operatingSystem.passwords%3Bhardware.datacenter%3Bhardware.billingItem HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/json\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetHardwareServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(HardwareServerAsyncClient.class, "getHardwareServer", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(
            httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Hardware_Server/1234.json?objectMask=hardwareStatus%3BoperatingSystem.passwords%3Bdatacenter%3BbillingItem HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testRebootHardHardwareServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(HardwareServerAsyncClient.class, "rebootHardHardwareServer", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Hardware_Server/1234/rebootHard.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPowerOffHardwareServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(HardwareServerAsyncClient.class, "powerOffHardwareServer", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Hardware_Server/1234/powerOff.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPowerOnHardwareServer() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(HardwareServerAsyncClient.class, "powerOnHardwareServer", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Hardware_Server/1234/powerOn.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetActiveTransaction() {
      Invokable<?, ?> method = method(HardwareServerAsyncClient.class, "getActiveTransaction", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
              "GET https://api.softlayer.com/rest/v3/SoftLayer_Hardware_Server/1234/getActiveTransaction.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetLastTransaction() {
      Invokable<?, ?> method = method(HardwareServerAsyncClient.class, "getLastTransaction", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
              "GET https://api.softlayer.com/rest/v3/SoftLayer_Hardware_Server/1234/getLastTransaction.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
