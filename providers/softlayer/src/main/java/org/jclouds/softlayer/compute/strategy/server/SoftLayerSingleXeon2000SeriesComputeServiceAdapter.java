package org.jclouds.softlayer.compute.strategy.server;

import static com.google.common.collect.Iterables.filter;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY;
import static org.jclouds.softlayer.reference.SoftLayerConstants.PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.collect.Memoized;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.domain.product.ProductItem;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductPackage;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

public class SoftLayerSingleXeon2000SeriesComputeServiceAdapter extends SoftLayerHardwareServerComputeServiceAdapter {

	@Inject
	public SoftLayerSingleXeon2000SeriesComputeServiceAdapter(
			SoftLayerClient client,
			HardwareServerHasLoginDetailsPresent serverHasLoginDetailsPresent,
			HardwareServerHasNoRunningTransactions serverHasNoActiveTransactionsTester,
			HardwareServerStartedTransactions serverHasActiveTransactionsTester,
			HardwareProductOrderApprovedAndServerIsPresent hardwareProductOrderApprovedAndServerIsPresent,
			@Memoized Supplier<ProductPackage> productPackageSupplier,
			Iterable<ProductItemPrice> prices, 
			@Named(PROPERTY_SOFTLAYER_SERVER_LOGIN_DETAILS_DELAY) long serverLoginDelay,
			@Named(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_ENDED_DELAY) long activeTransactionsEndedDelay,
			@Named(PROPERTY_SOFTLAYER_SERVER_ACTIVE_TRANSACTIONS_STARTED_DELAY) long activeTransactionsStartedDelay, 
			@Named(PROPERTY_SOFTLAYER_SERVER_HARDWARE_ORDER_APPROVED_DELAY) long hardwareApprovedDelay) {
		super(client, serverHasLoginDetailsPresent,
				serverHasNoActiveTransactionsTester, serverHasActiveTransactionsTester,
				hardwareProductOrderApprovedAndServerIsPresent, productPackageSupplier,
				prices, serverLoginDelay, activeTransactionsEndedDelay,
				activeTransactionsStartedDelay, hardwareApprovedDelay);
	}

	@Override
	public Iterable<Iterable<ProductItem>> listHardwareProfiles() {
		ProductPackage productPackage = productPackageSupplier.get();
		Set<ProductItem> items = productPackage.getItems();
		ImmutableSet.Builder<Iterable<ProductItem>> result = ImmutableSet.builder();
		for (ProductItem cpuItem : filter(items, categoryCode("server"))) {
			for (ProductItem ramItem : filter(items, categoryCode("ram"))) {
				for (ProductItem firsDiskItem : filter(items, categoryCode("disk0"))) {
					for (ProductItem uplinkItem : filter(items, categoryCode("port_speed"))) {
						for (ProductItem bandwidth : filter(items, categoryCode("bandwidth"))) {
							ImmutableSet<ProductItem> hardware = ImmutableSet.of(cpuItem, ramItem,
									firsDiskItem, uplinkItem, bandwidth);
							result.add(hardware);
						}
					}
				}
			}
		}
		return result.build();
	}

}
