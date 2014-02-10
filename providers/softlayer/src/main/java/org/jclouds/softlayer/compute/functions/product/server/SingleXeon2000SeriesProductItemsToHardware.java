package org.jclouds.softlayer.compute.functions.product.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;

import java.util.regex.Pattern;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.softlayer.compute.functions.product.ProductItems;
import org.jclouds.softlayer.compute.functions.product.ProductItemsToHardware;
import org.jclouds.softlayer.domain.product.ProductItem;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * 
 * @author adaml
 *
 */
public class SingleXeon2000SeriesProductItemsToHardware extends HardwareServerProductItemsToHardware {

	private static final String SERVER_DISK_CATEGORY_REGEX = "disk[0-9]";
	private static final String FIRST_SERVER_DISK = "disk0";
	private static final String STORAGE_AREA_NETWORK = "SAN";

	private final Pattern serverDiskCategoryRegex;

	public SingleXeon2000SeriesProductItemsToHardware() {
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
