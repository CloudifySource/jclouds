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
package org.jclouds.softlayer.features.guest;

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
import org.testng.annotations.Test;

import java.io.IOException;

import static org.jclouds.reflect.Reflection2.method;
/**
 * Tests annotation parsing of {@code VirtualGuestAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VirtualGuestAsyncClientTest extends BaseSoftLayerAsyncClientTest<VirtualGuestAsyncClient> {

   public void testListVirtualGuests() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "listVirtualGuests");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(
            httpRequest,
      		"GET https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests.json?objectMask=virtualGuests.accountId%3BvirtualGuests.domain%3BvirtualGuests.fullyQualifiedDomainName%3BvirtualGuests.hostname%3BvirtualGuests.id%3BvirtualGuests.primaryIpAddress%3BvirtualGuests.primaryBackendIpAddress%3BvirtualGuests.status%3BvirtualGuests.powerState%3BvirtualGuests.billingItem HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(
            httpRequest,
  				"GET https://api.softlayer.com/rest/v3/SoftLayer_Account/VirtualGuests.json?objectMask=virtualGuests.accountId%3BvirtualGuests.domain%3BvirtualGuests.fullyQualifiedDomainName%3BvirtualGuests.hostname%3BvirtualGuests.id%3BvirtualGuests.primaryIpAddress%3BvirtualGuests.primaryBackendIpAddress%3BvirtualGuests.status%3BvirtualGuests.powerState%3BvirtualGuests.billingItem HTTP/1.1");
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

   public void testGetVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "getVirtualGuest", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(
            httpRequest,
    		  "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234.json?objectMask=accountId%3BcreateDate%3BdedicatedAccountHostOnlyFlag%3Bdomain%3BfullyQualifiedDomainName%3Bhostname%3Bid%3BlastPowerStateId%3BlastVerifiedDate%3BmaxCpu%3BmaxCpuUnits%3BmaxMemory%3BmetricPollDate%3BmodifyDate%3BstartCpus%3BstatusId%3Buuid%3BglobalIdentifier%3BmanagedResourceFlag%3BprimaryIpAddress%3BprimaryBackendIpAddress%3Bstatus%3BpowerState%3BnetworkVlans%3BoperatingSystem.passwords%3Bdatacenter%3BbillingItem%3BprivateNetworkOnlyFlag HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testRebootHardVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "rebootHardVirtualGuest", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/rebootHard.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPowerOffVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "powerOffVirtualGuest", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/powerOff.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPowerOnVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "powerOnVirtualGuest", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/powerOn.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testPauseVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "pauseVirtualGuest", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/pause.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testResumeVirtualGuest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "resumeVirtualGuest", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
            "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/resume.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetActiveTransaction() {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "getActiveTransaction", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
              "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/getActiveTransaction.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetLastTransaction() {
      Invokable<?, ?> method = method(VirtualGuestAsyncClient.class, "getLastTransaction", long.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(1234));

      assertRequestLineEquals(httpRequest,
              "GET https://api.softlayer.com/rest/v3/SoftLayer_Virtual_Guest/1234/getLastTransaction.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
