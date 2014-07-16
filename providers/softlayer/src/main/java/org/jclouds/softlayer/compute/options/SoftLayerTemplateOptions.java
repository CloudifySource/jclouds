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
package org.jclouds.softlayer.compute.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.net.InternetDomainName;

/**
 * Contains options supported by the
 * {@link org.jclouds.compute.ComputeService#createNodesInGroup(String, int, TemplateOptions)} and
 * {@link org.jclouds.compute.ComputeService#createNodesInGroup(String, int, TemplateOptions)}
 * operations on the <em>gogrid</em> provider.
 * 
 * <h2>Usage</h2> The recommended way to instantiate a
 * {@link SoftLayerTemplateOptions} object is to statically import
 * {@code SoftLayerTemplateOptions.*} and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p>
 * 
 * <pre>
 * import static org.jclouds.compute.options.SoftLayerTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 * 
 * @author Adrian Cole
 */
public class SoftLayerTemplateOptions extends TemplateOptions implements Cloneable {

   protected String domainName = "jclouds.org";
   protected String networkVlanId = "";   
   protected boolean privateNetworkOnly = false;
   
   protected int startCpus = -1;
   protected int maxMemory = -1;
   protected String operatingSystemReferenceCode = "";
   protected List<Integer> blockDevicesDiskCapacity = new ArrayList<Integer>();
   protected boolean localDiskFlag = true;
   protected int maxNetworkSpeed = -1;
   protected String postInstallScriptUri = "";   
   

   @Override
   public SoftLayerTemplateOptions clone() {
      SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof SoftLayerTemplateOptions) {
         SoftLayerTemplateOptions eTo = SoftLayerTemplateOptions.class.cast(to);
         eTo.domainName(domainName);
         eTo.networkVlanId(networkVlanId);
         eTo.postInstallScriptUri(postInstallScriptUri);
         eTo.privateNetworkOnly(privateNetworkOnly);
         eTo.startCpus(startCpus);
         eTo.maxMemory(maxMemory);
         eTo.operatingSystemReferenceCode(operatingSystemReferenceCode);
         eTo.blockDevicesDiskCapacity(blockDevicesDiskCapacity);
         eTo.localDiskFlag(localDiskFlag);
         eTo.maxNetworkSpeed(maxNetworkSpeed);
      }
   }

   /**
    * will replace the default domain used when ordering virtual guests. Note
    * this needs to contain a public suffix!
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#orderVirtualGuest
    * @see InternetDomainName#hasPublicSuffix
    */
   public TemplateOptions domainName(String domainName) {
	   
	  if (domainName == null || domainName.trim().length() == 0) {
		   throw new NullPointerException("domainName is null or empty");
	  }

	  checkArgument(InternetDomainName.from(domainName).hasPublicSuffix(), "domainName %s has no public suffix",
            domainName);
      this.domainName = domainName;
      return this;
   }
   
   
   /**
    * will set a network vlan when ordering virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#orderVirtualGuest
    */
   public TemplateOptions networkVlanId(String networkVlanId) {
      
	  if (networkVlanId == null || networkVlanId.trim().length() == 0) {
		   throw new NullPointerException("networkVlanId is null or empty");
	  }
	  
      try {
    	  Integer.valueOf(networkVlanId);
      } catch (NumberFormatException e) {
    	  throw new IllegalArgumentException("networkVlanId " + networkVlanId + " is not a number");
      }

      this.networkVlanId = networkVlanId;
      return this;
   }
   
   
   /**
    * will set a post-install script URI when creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#createVirtualGuest
    */
   public TemplateOptions postInstallScriptUri(String postInstallScriptUri) {
	   
	  if (postInstallScriptUri == null || postInstallScriptUri.trim().length() == 0) {
		   throw new NullPointerException("postInstallScriptUri is null or empty");
	  }
      
      try {
    	  new URL(postInstallScriptUri);
      } catch (Exception e) {
    	  throw new IllegalArgumentException("postInstallScriptUri " + postInstallScriptUri + " is not a valid URI");
      }

      this.postInstallScriptUri = postInstallScriptUri;
      return this;
   }
   
   /**
    * will set the privateNetworkOnly flag when ordering or creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#orderVirtualGuest
    */
   public TemplateOptions privateNetworkOnly(boolean privateNetworkOnly) {
      this.privateNetworkOnly = privateNetworkOnly;
      return this;
   }
   
   /**
    * will set the starting number of Cpus when creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#createVirtualGuest
    */
   public TemplateOptions startCpus(int startCpus) {
	   // TODO : validate number is > 0 and in allowed range
      this.startCpus = startCpus;
      return this;
   }
   
   /**
    * will set the maximum memory (in MB) when creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#createVirtualGuest
    */
   public TemplateOptions maxMemory(int maxMemory) {
	   // TODO : validate number is > 0 and in allowed range
      this.maxMemory = maxMemory;
      return this;
   }
   
   /**
    * will set the operating system Code when creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#createVirtualGuest
    */
   public TemplateOptions operatingSystemReferenceCode(String operatingSystemReferenceCode) {
	   if (operatingSystemReferenceCode == null || operatingSystemReferenceCode.trim().length() == 0) {
		   throw new NullPointerException("operatingSystemReferenceCode is null or empty");
	   }

      this.operatingSystemReferenceCode = operatingSystemReferenceCode;
      return this;
   }
   
   /**
    * will set the blockDevices at the specified order, each with its disk capacity, when creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#createVirtualGuest
    */
   public TemplateOptions blockDevicesDiskCapacity(List blockDevicesDiskCapacity) {
	   if (blockDevicesDiskCapacity != null) {
		   this.blockDevicesDiskCapacity = blockDevicesDiskCapacity;
	   }
      return this;
   }
   
   /**
    * will set the localDiskFlag flag when creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#createVirtualGuest
    */
   public TemplateOptions localDiskFlag(boolean localDiskFlag) {
      this.localDiskFlag = localDiskFlag;
      return this;
   }
   
   /**
    * will set the maximum network speed when creating virtual guests.
    * 
    * @see org.jclouds.softlayer.features.guest.VirtualGuestClient#createVirtualGuest
    */
   public TemplateOptions maxNetworkSpeed(int maxNetworkSpeed) {
	   // TODO : validate number is > 0 and in allowed range
      this.maxNetworkSpeed = maxNetworkSpeed;
      return this;
   }

   public String getDomainName() {
      return domainName;
   }
   
   public String getNetworkVlanId() {
      return networkVlanId;
   }
   
   public String getPostInstallScriptUri() {
      return postInstallScriptUri;
   }
   
   public boolean isPrivateNetworkOnly() {
	   return privateNetworkOnly;
   }
   
   public int getStartCpus() {
	   return startCpus;
   }
   
   public int getMaxMemory() {
	   return maxMemory;
   }
   
   public String getOperatingSystemReferenceCode() {
	   return operatingSystemReferenceCode;
   }
   
   public List<Integer> getBlockDevicesDiskCapacity() {
	   return blockDevicesDiskCapacity;
   }

   public boolean isLocalDiskFlag() {
	   return localDiskFlag;
   }
   
   public int getMaxNetworkSpeed() {
	   return maxNetworkSpeed;
   }

   public static final SoftLayerTemplateOptions NONE = new SoftLayerTemplateOptions();

   public static class Builder {

      /**
       * @see #domainName
       */
      public static SoftLayerTemplateOptions domainName(String domainName) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.domainName(domainName));
      }
      
      /**
       * @see #networkVlanId
       */
      public static SoftLayerTemplateOptions networkVlanId(String networkVlanId) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.networkVlanId(networkVlanId));
      }
      
      /**
       * @see #postInstallScriptUri
       */
      public static SoftLayerTemplateOptions postInstallScriptUri(String postInstallScriptUri) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.postInstallScriptUri(postInstallScriptUri));
      }
      
      /**
       * @see #privateNetworkOnly
       */
      public static SoftLayerTemplateOptions privateNetworkOnly(boolean privateNetworkOnly) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.privateNetworkOnly(privateNetworkOnly));
      }
      
      /**
       * @see #startCpus
       */
      public static SoftLayerTemplateOptions startCpus(int startCpus) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.startCpus(startCpus));
      }
      
      /**
       * @see #maxMemory
       */
      public static SoftLayerTemplateOptions maxMemory(int maxMemory) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.startCpus(maxMemory));
      }
      
      /**
       * @see #operatingSystemReferenceCode
       */
      public static SoftLayerTemplateOptions operatingSystemReferenceCode(String operatingSystemReferenceCode) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.operatingSystemReferenceCode(operatingSystemReferenceCode));
      }
      
      /**
       * @see #blockDevicesDiskCapacity
       */
      public static SoftLayerTemplateOptions blockDevicesDiskCapacity(List<Integer> blockDevicesDiskCapacity) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.blockDevicesDiskCapacity(blockDevicesDiskCapacity));
      }

      /**
       * @see #localDiskFlag
       */
      public static SoftLayerTemplateOptions localDiskFlag(boolean localDiskFlag) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.localDiskFlag(localDiskFlag));
      }
      
      /**
       * @see #maxNetworkSpeed
       */
      public static SoftLayerTemplateOptions maxNetworkSpeed(int maxNetworkSpeed) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.maxNetworkSpeed(maxNetworkSpeed));
      }
      
      
      // methods that only facilitate returning the correct object type

      /**
       * @see TemplateOptions#inboundPorts(int...)
       */
      public static SoftLayerTemplateOptions inboundPorts(int... ports) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.inboundPorts(ports));
      }

      /**
       * @see TemplateOptions#blockOnPort(int, int)
       */
      public static SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.blockOnPort(port, seconds));
      }

      /**
       * @see TemplateOptions#userMetadata(Map)
       */
      public static SoftLayerTemplateOptions userMetadata(Map<String, String> userMetadata) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.userMetadata(userMetadata));
      }

      /**
       * @see TemplateOptions#userMetadata(String, String)
       */
      public static SoftLayerTemplateOptions userMetadata(String key, String value) {
         SoftLayerTemplateOptions options = new SoftLayerTemplateOptions();
         return SoftLayerTemplateOptions.class.cast(options.userMetadata(key, value));
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * @see TemplateOptions#blockOnPort(int, int)
    */
   @Override
   public SoftLayerTemplateOptions blockOnPort(int port, int seconds) {
      return SoftLayerTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * @see TemplateOptions#inboundPorts(int...)
    */
   @Override
   public SoftLayerTemplateOptions inboundPorts(int... ports) {
      return SoftLayerTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * @see TemplateOptions#authorizePublicKey(String)
    */
   @Override
   public SoftLayerTemplateOptions authorizePublicKey(String publicKey) {
      return SoftLayerTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * @see TemplateOptions#installPrivateKey(String)
    */
   @Override
   public SoftLayerTemplateOptions installPrivateKey(String privateKey) {
      return SoftLayerTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return SoftLayerTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SoftLayerTemplateOptions userMetadata(String key, String value) {
      return SoftLayerTemplateOptions.class.cast(super.userMetadata(key, value));
   }

}
