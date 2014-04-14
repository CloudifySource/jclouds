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

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import java.net.URI;
import java.util.Properties;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for SoftLayer.
 * @author Adrian Cole
 */
public class SoftLayerProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public SoftLayerProviderMetadata() {
      super(builder());
   }

   public SoftLayerProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {

      HardwareServerProperties hardwareServerDefaultProperties = new HardwareServerProperties();
      VirtualGuestProperties virtualGuestDefaultProperties = new VirtualGuestProperties();
      SingleXeon3200SeriesProperties singleXeon3200SeriesDefaultProperties = new
              SingleXeon3200SeriesProperties();
      DualXeon5500SeriesProperties dualXeon5500SeriesProperties = new 
    		  DualXeon5500SeriesProperties();

      Properties properties = new Properties();

      // add all custom properties from all packages to allow for injections
      properties.putAll(hardwareServerDefaultProperties.customProperties());
      properties.putAll(virtualGuestDefaultProperties.customProperties());
      properties.putAll(singleXeon3200SeriesDefaultProperties.customProperties());
      properties.putAll(dualXeon5500SeriesProperties.customProperties());

      // use virtual guest as default provisioning.
      properties.putAll(virtualGuestDefaultProperties.sharedProperties());
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("softlayer")
         .name("SoftLayer")
         .apiMetadata(new SoftLayerApiMetadata())
         .homepage(URI.create("http://www.softlayer.com"))
         .console(URI.create("https://manage.softlayer.com"))
         .iso3166Codes("SG","US-CA","US-TX","US-VA","US-WA","US-TX", "NL", "NSFTW-IL")  // NSFTW-IL is a weird isoCode returned by Softlayer
         .endpoint("https://api.softlayer.com/rest")
         .defaultProperties(SoftLayerProviderMetadata.defaultProperties());
      }

      @Override
      public SoftLayerProviderMetadata build() {
         return new SoftLayerProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
