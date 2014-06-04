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

package org.jclouds.softlayer.compute.strategy.guest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.itemId;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.matches;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.priceId;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_FLEX_IMAGE_GLOBAL_IDENTIFIER;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_FLEX_IMAGE_ID;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_ENDED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_STARTED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY;
import static org.jclouds.util.Predicates2.retry;

import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.compute.functions.product.ProductItemToImage;
import org.jclouds.softlayer.compute.options.SoftLayerTemplateOptions;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.Password;
import org.jclouds.softlayer.domain.SoftLayerNode;
import org.jclouds.softlayer.domain.Transaction;
import org.jclouds.softlayer.domain.guest.VirtualGuest;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductOrderReceipt;
import org.jclouds.softlayer.domain.product.ProductPackage;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.reference.SoftLayerConstants;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * defines the connection between the {@link org.jclouds.softlayer.SoftLayerClient#getVirtualGuestClient} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 *
 */
public class SoftLayerVirtualGuestComputeServiceAdapter implements
        ComputeServiceAdapter<SoftLayerNode, Iterable<ProductItem>, ProductItem, Datacenter> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final SoftLayerClient client;

   private final Supplier<ProductPackage> productPackageSupplier;
   private final Predicate<VirtualGuest> loginDetailsTester;
   private final Predicate<VirtualGuest> guestHasNoActiveTransactionsTester;
   private final Predicate<VirtualGuest> guestHasActiveTransactionsTester;
   private final long guestLoginDelay;
   private final Pattern cpuPattern;
   private final long transactionsEndedDelay;
   private final long transactionsStartedDelay;
   private final Pattern disk0Type;
   private final Iterable<ProductItemPrice> prices;
   private final String imageTemplateId;
   private final String imageTemplateGlobalIdentifier;
   
   private Pattern serverDiskCategoryRegex;
   private String externalDisksId;
   private static final String SERVER_DISK_CATEGORY_REGEX = "guest_disk[1-5]";

   @Inject
   public SoftLayerVirtualGuestComputeServiceAdapter(SoftLayerClient client,
                                         VirtualGuestHasLoginDetailsPresent virtualGuestHasLoginDetailsPresent,
                                         VirtualGuestHasNoRunningTransactions guestHasNoActiveTransactionsTester,
                                         VirtualGuestStartedTransactions guestHasActiveTransactionsTester,
                                         @Memoized Supplier<ProductPackage> productPackageSupplier, Iterable<ProductItemPrice> prices,
                                         @Named(PROPERTY_SOFTLAYER_FLEX_IMAGE_ID) String imageTemplateId,
                                         @Named(PROPERTY_SOFTLAYER_FLEX_IMAGE_GLOBAL_IDENTIFIER) String imageTemplateGlobalIdentifier,
                                         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_CPU_REGEX) String cpuRegex,
                                         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_DISK0_TYPE) String disk0Type,
                                         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_LOGIN_DETAILS_DELAY) long guestLoginDelay,
                                         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_ENDED_DELAY) long transactionsEndedDelay,
                                         @Named(PROPERTY_SOFTLAYER_VIRTUALGUEST_ACTIVE_TRANSACTIONS_STARTED_DELAY) long transactionsStartedDelay,
                                         @Named(PROPERTY_SOFTLAYER_EXTERNAL_DISKS_IDS) String multipleDisksHardwareId) {
      this.client = checkNotNull(client, "client");
      this.guestLoginDelay = guestLoginDelay;
      this.transactionsStartedDelay = transactionsStartedDelay;
      this.transactionsEndedDelay = transactionsEndedDelay;
      this.productPackageSupplier = checkNotNull(productPackageSupplier, "productPackageSupplier");
      checkArgument(guestLoginDelay > 500, "guestOrderDelay must be in milliseconds and greater than 500");
      this.loginDetailsTester = retry(virtualGuestHasLoginDetailsPresent, guestLoginDelay);
      this.cpuPattern = Pattern.compile(checkNotNull(cpuRegex, "cpuRegex"));
      this.prices = checkNotNull(prices, "prices");
      this.disk0Type = Pattern.compile(".*" + checkNotNull(disk0Type, "disk0Type") + ".*" + "|.*SAN.*");
      this.guestHasNoActiveTransactionsTester = retry(guestHasNoActiveTransactionsTester, transactionsEndedDelay, 100, 1000);
      this.guestHasActiveTransactionsTester = retry(guestHasActiveTransactionsTester, transactionsStartedDelay, 100, 1000);
      this.imageTemplateId = imageTemplateId;
      this.imageTemplateGlobalIdentifier = imageTemplateGlobalIdentifier;
      this.serverDiskCategoryRegex = checkNotNull(Pattern.compile(SERVER_DISK_CATEGORY_REGEX), "serverDiskCategoryRegex");
		this.externalDisksId = multipleDisksHardwareId;
   }

   @Override
   public NodeAndInitialCredentials<SoftLayerNode> createNodeWithGroupEncodedIntoName(String group, String name,
                                                                                     Template template) {
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");
      checkArgument(template.getOptions().getClass().isAssignableFrom(SoftLayerTemplateOptions.class),
              "options class %s should have been assignable from SoftLayerTemplateOptions", template.getOptions()
              .getClass());

      String domainName = template.getOptions().as(SoftLayerTemplateOptions.class).getDomainName();

      VirtualGuest newGuest = VirtualGuest.builder().domain(domainName).hostname(name).build();
      
      String validGuestHardwarePriceIds = getValidPriceCombination(template, newGuest);

      ProductOrder order = ProductOrder.builder().packageId(productPackageSupplier.get().getId())
              .location(template.getLocation().getId()).quantity(1).useHourlyPricing(true).prices(getPrices(template, validGuestHardwarePriceIds))
              .virtualGuests(newGuest)
              .imageTemplateGlobalIdentifier(imageTemplateGlobalIdentifier)
              .imageTemplateId(imageTemplateId).build();

      logger.debug(">> ordering new virtualGuest domain(%s) hostname(%s)", domainName, name);
      ProductOrderReceipt productOrderReceipt = client.getVirtualGuestClient().orderVirtualGuest(order);
      VirtualGuest result = get(productOrderReceipt.getOrderDetails().getVirtualGuests(), 0);
      logger.trace("<< virtualGuest(%s)", result.getId());

      logger.debug(">> awaiting for transactions on guest(%s) to start", result.getHostname());
      boolean guestHasStartedTransactions = guestHasActiveTransactionsTester.apply(result);
      logger.debug(">> virtualGuest(%s) has started transactions(%s)", result.getId(), guestHasStartedTransactions);

      checkState(guestHasStartedTransactions, "order for host %s did not start its transactions within %sms", result,
              Long.toString(transactionsStartedDelay));

      logger.debug(">> awaiting for transactions on guest (%s) to complete", result.getId());
      boolean noMoreTransactions = guestHasNoActiveTransactionsTester.apply(result);
      logger.debug(">> virtualGuest(%s) completed transactions(%s)", result.getId(), noMoreTransactions);

      checkState(noMoreTransactions, "order for host %s did not finish its transactions within %sms", result,
              Long.toString(transactionsEndedDelay));

      logger.debug(">> awaiting login details for virtualGuest(%s)", result.getId());
      boolean orderInSystem = loginDetailsTester.apply(result);
      logger.trace("<< virtualGuest(%s) complete(%s)", result.getId(), orderInSystem);

      checkState(orderInSystem, "order for guest %s doesn't have login details within %sms", result,
              Long.toString(guestLoginDelay));
      result = client.getVirtualGuestClient().getVirtualGuest(result.getId());

      Password pw = get(result.getOperatingSystem().getPasswords(), 0);
      return new NodeAndInitialCredentials<SoftLayerNode>(result, result.getId() + "", LoginCredentials.builder().user(pw.getUsername()).password(
              pw.getPassword()).build());
   }

   private String getValidPriceCombination(Template template, VirtualGuest newGuest) {
	   String allPrices = template.getHardware().getId();
	   String[] allPricesArray = allPrices.split(";");
	   NoSuchElementException lastExeption = new NoSuchElementException();
	   for (String pricesId : allPricesArray) {
		      ProductOrder order = ProductOrder.builder().packageId(productPackageSupplier.get().getId())
		              .location(template.getLocation().getId()).quantity(1).useHourlyPricing(true).prices(getPrices(template, pricesId))
		              .virtualGuests(newGuest)
		              .imageTemplateGlobalIdentifier(imageTemplateGlobalIdentifier)
		              .imageTemplateId(imageTemplateId).build();
		   try {
			   ProductOrder guestProductOrderReceipt = client.getVirtualGuestClient().verifyVirtualGuestOrder(order);
			   return pricesId;
		   } catch (Exception e) {
			   logger.info("Failed verifying hardware price ID " + pricesId + ". Retrying with alternative price id."
					   + " message is " + e.getMessage());
			   lastExeption = new NoSuchElementException(e.getMessage());
		   }
	   }
	   throw lastExeption;
   }

private ImmutableList<ProductItemPrice> getPrices(Template template, String validHardwareIds) {
	      com.google.common.collect.ImmutableList.Builder<ProductItemPrice> result = ImmutableList.builder();

	      int imageId = Integer.parseInt(template.getImage().getId());
	      result.add(ProductItemPrice.builder().id(imageId).build());

	      Iterable<String> hardwareIds = Splitter.on(",").split(validHardwareIds);
	      for (String hardwareId : hardwareIds) {
	         int id = Integer.parseInt(hardwareId);
	         result.add(ProductItemPrice.builder().id(id).build());
	      }
	      result.addAll(prices);
	      return result.build();
	   }

   @Override
   public Iterable<Iterable<ProductItem>> listHardwareProfiles() {
      ProductPackage productPackage = productPackageSupplier.get();
      Set<ProductItem> items = productPackage.getItems();
      // either default, or passed by named property.
      final ImmutableList<ProductItem> disks = getExternalDisks(items);
      
      ImmutableSet.Builder<Iterable<ProductItem>> result = ImmutableSet.builder();
      for (ProductItem cpuItem : filter(items, matches(cpuPattern))) {
    	  for (ProductItem ramItem : filter(items, categoryCode("ram"))) {
    		  for (ProductItem sanItem : filter(items, and(matches(disk0Type), categoryCode("guest_disk0")))) {
    			  for (ProductItem uplinkItem : filter(items, categoryCode("port_speed"))) {
    				  for (ProductItem bandwidth : filter(items, categoryCode("bandwidth"))) {
    					  Builder<ProductItem> hardwareBuilder = ImmutableList.builder();
    					  hardwareBuilder.add(cpuItem, ramItem,sanItem, uplinkItem, bandwidth);
    					  hardwareBuilder.addAll(disks);
    					  result.add(hardwareBuilder.build());
    				  }
    			  }
    		  }
    	  }
      }
      return result.build();
   }
   
	private ImmutableList<ProductItem> getExternalDisks(Set<ProductItem> items) {
		
		Builder<ProductItem> disksBuilder = ImmutableList.builder();
		
		if (!this.externalDisksId.equals("")) {
			Iterable<ProductItem> allDiskItems = filter(items, categoryCodeMatches(serverDiskCategoryRegex));
			ImmutableList<ProductItem> diskItemsList = listDisksByPrice(allDiskItems);
			
			if (diskItemsList == null) {
				diskItemsList = listDisksByItemId(allDiskItems);
			}
			
			if (diskItemsList == null) {
				throw new NoSuchElementException("Failed listing hardwares having additional disk items with ids: " 
						+ this.externalDisksId + ". disk items could not be resolved by item or price id.");
			}
			
			disksBuilder.addAll(diskItemsList);
		}

		return disksBuilder.build();
	}

	private ImmutableList<ProductItem> listDisksByPrice(
			final Iterable<ProductItem> diskItems) {
		
		Builder<ProductItem> diskList = ImmutableList.builder();
		Iterable<String> pricesId = Splitter.on(",").split(this.externalDisksId);
		
		for (String priceId : pricesId) {
			//price IDs are unique. expecting one result.
			Iterable<ProductItem> diskByPriceId = filter(diskItems, priceId(priceId));
			
			if (!diskByPriceId.iterator().hasNext()) {
				logger.debug("Additional disk with price id " + priceId + " was not found."
						+ " Assuming item id is used.");
				return null;
			}
			
			diskList.add(get(diskByPriceId, 0));
		}
		
		return diskList.build();
	}
	
	private ImmutableList<ProductItem> listDisksByItemId(
			Iterable<ProductItem> diskItems) {
		
		Builder<ProductItem> diskList = ImmutableList.builder();
		Iterable<String> itemsId = Splitter.on(",").split(this.externalDisksId);;
		
		for (String priceId : itemsId) {
			//item IDs are unique. expecting one result.
			Iterable<ProductItem> diskByItemId = filter(diskItems, itemId(priceId));
			
			if (!diskByItemId.iterator().hasNext()) {
				logger.warn("Additional disk with item id " + priceId + " was not found.");
				return null;
			}
			
			diskList.add(get(diskByItemId, 0));
		}
		
		return diskList.build();
	}

	@Override
	public Iterable<ProductItem> listImages() {
		ImmutableList.Builder<ProductItem> result = ImmutableList.builder();
		Iterable<ProductItem> images = filter(productPackageSupplier.get().getItems(), categoryCode("os"));
		for (ProductItem productItem : images) {
			if (productItem.getPrices().size() > 1) {
				// we divide image items that have more then one price per image into separate items. 
				for (ProductItemPrice price : productItem.getPrices()) {
					ProductItem newItem = ProductItem.builder().id(productItem.getId())
							.description(productItem.getDescription())
							.units(productItem.getUnits())
							.capacity(productItem.getCapacity())
							.prices(price)
							.categories(productItem.getCategories()).build();
					result.add(newItem);
				}
			} else {
				result.add(productItem);
			}
		}
		return result.build();
	}

   // cheat until we have a getProductItem command
   @Override
   public ProductItem getImage(final String id) {
      return find(listImages(), new Predicate<ProductItem>(){

         @Override
         public boolean apply(ProductItem input) {
            return ProductItemToImage.imageId().apply(input).equals(id)
            		|| ProductItemToImage.imageItemId().apply(input).equals(id);
         }

      }, null);
   }

   @Override
   public Iterable<SoftLayerNode> listNodes() {

      Set<VirtualGuest> unfiltered = client.getVirtualGuestClient().listVirtualGuests();

      return filter(new HashSet<SoftLayerNode>(unfiltered), new Predicate<SoftLayerNode>() {

         @Override
         public boolean apply(SoftLayerNode arg0) {
            boolean hasBillingItem = arg0.getBillingItemId() != -1;
            if (hasBillingItem)
               return true;
            logger.trace("server invalid, as it has no billing item %s", arg0);
            return false;
         }

      });
   }

   @Override
   public Iterable<SoftLayerNode> listNodesByIds(final Iterable<String> ids) {

      return filter(listNodes(), new Predicate<SoftLayerNode>() {

         @Override
         public boolean apply(SoftLayerNode server) {
            return contains(ids, server.getId());
         }
      });
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      return productPackageSupplier.get().getDatacenters();
   }

   @Override
   public VirtualGuest getNode(String id) {
      long serverId = Long.parseLong(id);
      return client.getVirtualGuestClient().getVirtualGuest(serverId);
   }

   @Override
   public void destroyNode(String id) {
      VirtualGuest guest = getNode(id);
      if (guest == null)
         return;

      if (guest.getBillingItemId() == -1)
         throw new IllegalStateException(String.format("no billing item for guest(%s) so we cannot cancel the order",
                 id));

      logger.debug(">> canceling service for guest(%s) billingItem(%s)", id, guest.getBillingItemId());
      client.getVirtualGuestClient().cancelService(guest.getBillingItemId());

      logger.debug(">> awaiting for transactions on guest(%s) to start", guest.getHostname());
      boolean guestHasStartedTransactions = guestHasActiveTransactionsTester.apply(guest);
      logger.debug(">> virtualGuest(%s) has started transactions(%s)", guest.getId(), guestHasStartedTransactions);

      checkState(guestHasStartedTransactions, "order for host %s did not start its transactions within %sms", guest,
              Long.toString(transactionsStartedDelay));

      logger.debug(">> awaiting transactions for hardwareServer(%s)", guest.getId());
      boolean noMoreTransactions = guestHasNoActiveTransactionsTester.apply(guest);
      logger.debug(">> hardwareServer(%s) complete(%s)", guest.getId(), noMoreTransactions);
   }

   @Override
   public void rebootNode(String id) {
      client.getVirtualGuestClient().rebootHardVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void resumeNode(String id) {
      client.getVirtualGuestClient().resumeVirtualGuest(Long.parseLong(id));
   }

   @Override
   public void suspendNode(String id) {
      client.getVirtualGuestClient().pauseVirtualGuest(Long.parseLong(id));
   }

   public static class VirtualGuestHasLoginDetailsPresent implements Predicate<VirtualGuest> {
      private final SoftLayerClient client;

      @Inject
      public VirtualGuestHasLoginDetailsPresent(SoftLayerClient client) {
         this.client = checkNotNull(client, "client was null");
      }

      @Override
      public boolean apply(VirtualGuest guest) {
         checkNotNull(guest, "virtual guest was null");

         VirtualGuest newGuest = client.getVirtualGuestClient().getVirtualGuest(guest.getId());
         boolean hasBackendIp = newGuest.getPrimaryBackendIpAddress() != null;
         boolean hasPrimaryIp = newGuest.getPrimaryIpAddress() != null;
         boolean hasPasswords = newGuest.getOperatingSystem() != null
                 && newGuest.getOperatingSystem().getPasswords().size() > 0;

         return hasBackendIp && hasPrimaryIp && hasPasswords;
      }
   }

   public static class VirtualGuestHasNoRunningTransactions implements Predicate<VirtualGuest> {

      private Map<VirtualGuest, Transaction> lastTransactionPerGuest = Maps.newConcurrentMap();

      private final SoftLayerClient client;

      @Resource
      @Named(SoftLayerConstants.TRANSACTION_LOGGER)
      protected Logger logger = Logger.NULL;

      @Inject
      public VirtualGuestHasNoRunningTransactions(SoftLayerClient client) {
         this.client = checkNotNull(client, "client was null");
      }

      @Override
      public boolean apply(@Nullable VirtualGuest guest) {
         Transaction activeTransaction = client.getVirtualGuestClient().getActiveTransaction(guest.getId());
         if (activeTransaction != null) {
            Transaction previous = lastTransactionPerGuest.get(guest);
            if (previous != null && !previous.getName().equals(activeTransaction.getName())) {
               logger.info("Successfully completed transaction %s in %s seconds.", previous.getName(),
                       previous.getElapsedSeconds());
               logger.info("Current transaction is %s. Average completion time is %s minutes.",
                       activeTransaction.getName(), activeTransaction.getAverageDuration());
            }

            lastTransactionPerGuest.put(guest, activeTransaction);
            return false;
         }
         logger.info("Successfully completed all transactions for host %s", guest.getHostname());
         lastTransactionPerGuest.remove(guest);
         return true;
      }
   }

   public static class VirtualGuestStartedTransactions implements Predicate<VirtualGuest> {

      private final SoftLayerClient client;

      @Resource
      @Named(SoftLayerConstants.TRANSACTION_LOGGER)
      protected Logger logger = Logger.NULL;

      @Inject
      public VirtualGuestStartedTransactions(SoftLayerClient client) {
         this.client = checkNotNull(client, "client was null");
      }

      @Override
      public boolean apply(@Nullable VirtualGuest guest) {
         boolean result = client.getVirtualGuestClient().getActiveTransaction(guest.getId()) != null;
         if (!result) {
            logger.trace(">> guest(%s) has not started any transactions yet", guest.getHostname());
         }
         return result;
      }
   }

public void validateOrder(Template template, VirtualGuest virtualGuest) {
	getValidPriceCombination(template, virtualGuest);
	
}

}
