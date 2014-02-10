package org.jclouds.softlayer.features.server;

import static org.jclouds.softlayer.predicates.ProductPackagePredicates.named;
import static org.testng.Assert.assertEquals;

import java.util.Properties;
import java.util.Random;

import org.jclouds.compute.domain.Template;
import org.jclouds.softlayer.SingleXeon2000SeriesProperties;
import org.jclouds.softlayer.domain.product.ProductItemPrice;
import org.jclouds.softlayer.domain.product.ProductOrder;
import org.jclouds.softlayer.domain.product.ProductPackage;
import org.jclouds.softlayer.domain.server.HardwareServer;
import org.jclouds.softlayer.features.ProductPackageClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class SingleXeon2000SeriesClientLiveTest extends HardwareServerClientLiveTest {
	   private static final String TEST_HOSTNAME_PREFIX = "livetest";

	   @Override
	   protected Properties setupProperties() {
	      Properties properties = super.setupProperties();
	      properties.putAll(new SingleXeon2000SeriesProperties().sharedProperties());
	      return properties;
	   }

	   @Test(groups = "live")
	   public void testVerifyOrderDedicated() {

	      int pkgId = Iterables.find(api.getAccountClient().getReducedActivePackages(),
	              named(ProductPackageClientLiveTest.SINGLE_XEON_2000_DEDICATED_SERVER_PACKAGE_NAME)).getId();

	      HardwareServer server = HardwareServer.builder().domain("jclouds.org").hostname(
	              TEST_HOSTNAME_PREFIX + new Random().nextInt()).build();

	      templateBuilder.locationId("37473").hardwareId("17149,17152,1517,272,125");
	      Template template = templateBuilder.build();

	      ProductOrder order = ProductOrder.builder()
	              .packageId(pkgId)
	              .quantity(1)
	              .location(template.getLocation().getId())
	              .useHourlyPricing(false)
	              .prices(getPrices(template))
	              .hardwareServers(server).build();

	      ProductOrder order2 = api().verifyHardwareServerOrder(order);
	      assertEquals(order.getPrices(), order2.getPrices());
	   }

	   protected Iterable<ProductItemPrice> getPrices(Template template) {
	      ImmutableSet.Builder<ProductItemPrice> result = ImmutableSet.builder();

	      int imageId = Integer.parseInt(template.getImage().getId());
	      result.add(ProductItemPrice.builder().id(imageId).build());

	      Iterable<String> hardwareIds = Splitter.on(",").split(template.getHardware().getId());
	      for (String hardwareId : hardwareIds) {
	         int id = Integer.parseInt(hardwareId);
	         result.add(ProductItemPrice.builder().id(id).build());
	      }
	      result.addAll(defaultPrices);
	      return result.build();
	   }
}
