package org.jclouds.softlayer.compute.functions.product.server;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.compute.functions.product.ProductItems;
import org.jclouds.softlayer.compute.functions.product.ProductItemsToHardware;
import org.jclouds.softlayer.domain.product.ProductItem;

import javax.inject.Singleton;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;

/**
 *
 * Converts a set of product items that belong to the Single Xeon 3200 series (Dedicated Server) package to hardware.
 *
 * Hardware id in case of Single Xeon 3200 series provisioning consists of the following parts:
 *
 * 1. Number of CPU Cores and Speed
 * 2. Amount of Ram
 * 3. Disk Storage
 * 4. Uplink port speed
 * 5. Public Bandwidth
 *
 * All of these hardware configurations are configurable in separate, and are embedded within their own id's.
 *
 * For example:
 *
 * 1613,21003,1267,272,36 --> Single Processor Quad Core Xeon 3230 - 2.60GHz (Kentsfield) - 2 x 4MB cache,8 GB DDR2 667, 500GB SATA II, 10 Mbps Public & Private Networks, Unlimited Bandwidth (10 Mbps Uplink)
 *
 * @author Eli Polonsky
 */
@Singleton
public class SingleXeon3200SeriesProductItemsToHardware extends HardwareServerProductItemsToHardware {

   private static final String SERVER_DISK_CATEGORY_REGEX = "disk[0-9]";
   private static final String FIRST_SERVER_DISK = "disk0";
   private static final String STORAGE_AREA_NETWORK = "SAN";

   private final Pattern serverDiskCategoryRegex;

   public SingleXeon3200SeriesProductItemsToHardware() {
      this.serverDiskCategoryRegex = checkNotNull(Pattern.compile(SERVER_DISK_CATEGORY_REGEX), "serverDiskCategoryRegex");
   }


   @Override
   public Hardware apply(@Nullable Iterable<ProductItem> items) {

      HardwareBuilder hardwareBuilder = new HardwareBuilder();

      ProductItem serverItem = get(filter(items, categoryCode("server")), 0);
      ProductItem ramItem =    get(filter(items, categoryCode("ram")), 0);
      ProductItem volumeItem = get(filter(items, categoryCode("disk0")), 0);
      ProductItem uplinkItem = get(filter(items, categoryCode("port_speed")), 0);
      ProductItem bandwidth = get(filter(items, categoryCode("bandwidth")), 0);

      String hardwareId = ProductItemsToHardware.hardwareId().apply(ImmutableList.of(serverItem, ramItem, volumeItem,
              uplinkItem, bandwidth));

      hardwareBuilder.ids(hardwareId).processor(new Processor(4, 2.6)).ram(ramItem.getCapacity().intValue());

      return hardwareBuilder.volumes(Iterables.transform(filter(items, categoryCodeMatches(serverDiskCategoryRegex)),
              new Function<ProductItem, Volume>() {
                 @Override
                 public Volume apply(ProductItem item) {
                    float volumeSize = ProductItems.capacity().apply(item);
                    return new VolumeImpl(
                            item.getId() + "",
                            item.getDescription().contains(STORAGE_AREA_NETWORK) ? Volume.Type.SAN : Volume.Type.LOCAL,
                            volumeSize, null, categoryCode(FIRST_SERVER_DISK).apply(item), false);
                 }
              })).build();
   }
}
