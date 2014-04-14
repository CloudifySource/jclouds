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
package org.jclouds.softlayer.compute.internal;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.NullEqualToIsParentOrIsGrandparentOfCurrentLocation;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.domain.internal.TemplateImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author adaml
 *
 */
public class SoftLayerTemplateBuilderImpl extends TemplateBuilderImpl {

	@Inject
	protected SoftLayerTemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
			@Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> hardwares,
			Supplier<Location> defaultLocation2, @Named("DEFAULT") Provider<TemplateOptions> optionsProvider,
			@Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider) {
		super(locations, images, hardwares, defaultLocation2, optionsProvider, defaultTemplateProvider);
	}

	private final Predicate<Hardware> hardwareIdPredicate = new Predicate<Hardware>() {
		@Override
		public boolean apply(Hardware input) {
			boolean returnVal = true;
			if (hardwareId != null) {
				if (!input.getId().startsWith(hardwareId)) {
					returnVal = false;
				}
//				ImmutableSet<String> hardwareIds = ImmutableSet.copyOf(Splitter.on(",").split(hardwareId));
//				ImmutableSet<String> inputHardwareIds = ImmutableSet.copyOf(Splitter.on(",").split(input.getId()));
//				if (!inputHardwareIds.containsAll(hardwareIds)) {
//					returnVal = false;
//				}
				// match our input params so that the later predicates pass.
				if (returnVal) {
					fromHardware(input);
				}
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "hardwareId(" + hardwareId + ")";
		}
	};

	private final Predicate<Hardware> providerHardwareIdPredicate = new Predicate<Hardware>() {
		@Override
		public boolean apply(Hardware input) {
			boolean returnVal = true;
			if (hardwareId != null) {
				if (!input.getProviderId().startsWith(hardwareId)) {
					returnVal = false;
				}
				//				ImmutableSet<String> hardwareIds = ImmutableSet.copyOf(Splitter.on(",").split(hardwareId));
				//				ImmutableSet<String> inputHardwareIds = ImmutableSet.copyOf(Splitter.on(",").split(input.getProviderId()));
				//				if (!inputHardwareIds.containsAll(hardwareIds)) {
				//					returnVal = false;
				//				}
				// match our input params so that the later predicates pass.
				if (returnVal) {
					fromHardware(input);
				}
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "providerHardwareId(" + hardwareId + ")";
		}
	};

	private final Predicate<Image> imageVersionPredicate = new Predicate<Image>() {
		@Override
		public boolean apply(Image input) {
			boolean returnVal = true;
			if (imageVersion != null) {
				if (input.getVersion() == null)
					returnVal = false;
				else
					returnVal = input.getVersion().contains(imageVersion) || input.getVersion().matches(imageVersion);
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "imageVersion(" + imageVersion + ")";
		}
	};

	private final Predicate<Image> imageNamePredicate = new Predicate<Image>() {
		@Override
		public boolean apply(Image input) {
			boolean returnVal = true;
			if (imageName != null) {
				if (input.getName() == null)
					returnVal = false;
				else
					returnVal = input.getName().equals(imageName) || input.getName().contains(imageName)
					|| input.getName().matches(imageName);
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "imageName(" + imageName + ")";
		}
	};

	private final Predicate<Image> imageDescriptionPredicate = new Predicate<Image>() {
		@Override
		public boolean apply(Image input) {
			boolean returnVal = true;
			if (imageDescription != null) {
				if (input.getDescription() == null)
					returnVal = false;
				else
					returnVal = input.getDescription().equals(imageDescription)
					|| input.getDescription().contains(imageDescription)
					|| input.getDescription().matches(imageDescription);
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "imageDescription(" + imageDescription + ")";
		}
	};

	private final Predicate<OperatingSystem> osFamilyPredicate = new Predicate<OperatingSystem>() {

		@Override
		public boolean apply(OperatingSystem input) {
			boolean returnVal = true;
			if (osFamily != null)
				returnVal = osFamily.equals(input.getFamily());
			return returnVal;
		}

		@Override
		public String toString() {
			return "osFamily(" + osFamily + ")";
		}
	};

	private final Predicate<OperatingSystem> osNamePredicate = new Predicate<OperatingSystem>() {
		@Override
		public boolean apply(OperatingSystem input) {
			boolean returnVal = true;
			if (osName != null) {
				if (input.getName() == null)
					returnVal = false;
				else
					returnVal = input.getName().contains(osName) || input.getName().matches(osName);
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "osName(" + osName + ")";
		}
	};

	private final Predicate<OperatingSystem> osDescriptionPredicate = new Predicate<OperatingSystem>() {
		@Override
		public boolean apply(OperatingSystem input) {
			boolean returnVal = true;
			if (osDescription != null) {
				if (input.getDescription() == null)
					returnVal = false;
				else
					returnVal = input.getDescription().contains(osDescription)
					|| input.getDescription().matches(osDescription);
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "osDescription(" + osDescription + ")";
		}
	};

	private final Predicate<OperatingSystem> osVersionPredicate = new Predicate<OperatingSystem>() {
		@Override
		public boolean apply(OperatingSystem input) {
			boolean returnVal = true;
			if (osVersion != null) {
				if (input.getVersion() == null)
					returnVal = false;
				else
					returnVal = input.getVersion().contains(osVersion) || input.getVersion().matches(osVersion);
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "osVersion(" + osVersion + ")";
		}
	};

	private final Predicate<OperatingSystem> os64BitPredicate = new Predicate<OperatingSystem>() {
		@Override
		public boolean apply(OperatingSystem input) {
			boolean returnVal = true;
			if (os64Bit != null) {
				if (os64Bit)
					return input.is64Bit();
				else
					return !input.is64Bit();
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "os64Bit(" + os64Bit + ")";
		}
	};

	private final Predicate<OperatingSystem> osArchPredicate = new Predicate<OperatingSystem>() {
		@Override
		public boolean apply(OperatingSystem input) {
			boolean returnVal = true;
			if (osArch != null) {
				if (input.getArch() == null)
					returnVal = false;
				else
					returnVal = input.getArch().contains(osArch) || input.getArch().matches(osArch);
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "osArch(" + osArch + ")";
		}
	};


	private final Predicate<ComputeMetadata> locationPredicate = new NullEqualToIsParentOrIsGrandparentOfCurrentLocation(new Supplier<Location>(){

		@Override
		public Location get() {
			return location;
		}

	});

	private final Predicate<Image> idPredicate = new Predicate<Image>() {
		@Override
		public boolean apply(Image input) {
			boolean returnVal = true;
			if (imageId != null) {
				returnVal = imageId.equals(input.getId());
				// match our input params so that the later predicates pass.
				if (returnVal) {
					fromImage(input);
				}
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "imageId(" + imageId + ")";
		}
	};
	
	private final Predicate<Image> providerImageIdPredicate = new Predicate<Image>() {
		@Override
		public boolean apply(Image input) {
			boolean returnVal = true;
			if (imageId != null) {
				returnVal = imageId.equals(input.getProviderId());
				// match our input params so that the later predicates pass.
				if (returnVal) {
					fromImage(input);
				}
			}
			return returnVal;
		}

		@Override
		public String toString() {
			return "providerImageId(" + imageId + ")";
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Template build() {
		if (nothingChangedExceptOptions()) {
			TemplateBuilder defaultTemplate = defaultTemplateProvider.get();
			if (options != null)
				defaultTemplate.options(options);
			return defaultTemplate.build();
		}

		if (options == null)
			options = optionsProvider.get();
		logger.debug(">> searching params(%s)", this);
		Set<? extends Image> images = getImages();
		checkState(images.size() > 0, "no images present!");
		Set<? extends Hardware> hardwaresToSearch = hardwares.get();
		checkState(hardwaresToSearch.size() > 0, "no hardware profiles present!");

		Image image = null;
		if (imageId != null) {
			image = findImageWithId(images);
			if (currentLocationWiderThan(image.getLocation()))
				this.location = image.getLocation();
		}

		Hardware hardware = null;
		if (hardwareId != null) {
			hardware = findHardwareWithId(hardwaresToSearch);
			if (currentLocationWiderThan(hardware.getLocation()))
				this.location = hardware.getLocation();
		}

		// if the user hasn't specified a location id, or an image or hardware
		// with location, let's search scoped to the implicit one
		if (location == null)
			location = defaultLocation.get();

		if (image == null) {
			Iterable<? extends Image> supportedImages = findSupportedImages(images);
			if (hardware == null)
				hardware = resolveHardware(hardwaresToSearch, supportedImages);
			image = resolveImage(hardware, supportedImages);
		} else {
			if (hardware == null)
				hardware = resolveHardware(hardwaresToSearch, ImmutableSet.of(image));
		}

		logger.debug("<<   matched image(%s) hardware(%s) location(%s)", image.getId(), hardware.getId(),
				location.getId());
		return new TemplateImpl(image, hardware, location, options);
	}

	private Image findImageWithId(Set<? extends Image> images) {
		Image image;
		// TODO: switch to GetImageStrategy in version 1.5
		image = tryFind(images, idPredicate).orNull();
		if (image == null) {
			image = tryFind(images, providerImageIdPredicate).orNull();
		}
		if (image == null)
			throwNoSuchElementExceptionAfterLoggingImageIds(format("%s, %s not found", idPredicate, providerImageIdPredicate), images);
		return image;
	}

	private boolean currentLocationWiderThan(Location location) {
		return this.location == null || (location != null && this.location.getScope().compareTo(location.getScope()) < 0);
	}

	private Iterable<? extends Image> findSupportedImages(Set<? extends Image> images) {
		Predicate<Image> imagePredicate = buildImagePredicate();
		Iterable<? extends Image> supportedImages = filter(images, imagePredicate);
		if (size(supportedImages) == 0) {
			throw throwNoSuchElementExceptionAfterLoggingImageIds(
					format("no image matched predicate: %s", imagePredicate), images);
		}
		return supportedImages;
	}

	@VisibleForTesting
	boolean nothingChangedExceptOptions() {
		return osFamily == null && location == null && imageId == null && hardwareId == null && hypervisor == null
				&& osName == null && imagePredicate == null && osDescription == null && imageVersion == null
				&& osVersion == null && osArch == null && os64Bit == null && imageName == null && imageDescription == null
				&& minCores == 0 && minRam == 0 && minDisk == 0 && !biggest && !fastest;
	}


	private Predicate<Image> buildImagePredicate() {
		List<Predicate<Image>> predicates = newArrayList();
		if (location != null)
			predicates.add(new Predicate<Image>() {

				@Override
				public boolean apply(Image input) {
					return locationPredicate.apply(input);
				}

				@Override
				public String toString() {
					return locationPredicate.toString();
				}
			});

		final List<Predicate<OperatingSystem>> osPredicates = newArrayList();
		if (osFamily != null)
			osPredicates.add(osFamilyPredicate);
		if (osName != null)
			osPredicates.add(osNamePredicate);
		if (osDescription != null)
			osPredicates.add(osDescriptionPredicate);
		if (osVersion != null)
			osPredicates.add(osVersionPredicate);
		if (os64Bit != null)
			osPredicates.add(os64BitPredicate);
		if (osArch != null)
			osPredicates.add(osArchPredicate);
		if (osPredicates.size() > 0)
			predicates.add(new Predicate<Image>() {

				@Override
				public boolean apply(Image input) {
					return and(osPredicates).apply(input.getOperatingSystem());
				}

				@Override
				public String toString() {
					return and(osPredicates).toString();
				}

			});
		if (imageVersion != null)
			predicates.add(imageVersionPredicate);
		if (imageName != null)
			predicates.add(imageNamePredicate);
		if (imageDescription != null)
			predicates.add(imageDescriptionPredicate);
		if (imagePredicate != null)
			predicates.add(imagePredicate);

		// looks verbose, but explicit <Image> type needed for this to compile
		// properly
		Predicate<Image> imagePredicate = predicates.size() == 1 ? Iterables.<Predicate<Image>> get(predicates, 0)
				: Predicates.<Image> and(predicates);
		return imagePredicate;
	}

	private Hardware findHardwareWithId(Set<? extends Hardware> hardwaresToSearch) {
		Hardware hardware;
		// TODO: switch to GetHardwareStrategy in version 1.5
		hardware = tryFind(hardwaresToSearch, hardwareIdPredicate).orNull();
		if (hardware == null)
			hardware = tryFind(hardwaresToSearch, providerHardwareIdPredicate).orNull();
		if (hardware == null) {
			throw throwNoSuchElementExceptionAfterLoggingHardwareIds(format("%s not found", hardwareIdPredicate),
					hardwaresToSearch);
		}
		return hardware;
	}

}
