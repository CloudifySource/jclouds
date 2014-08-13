/*******************************************************************************
 * Copyright (c) 2014 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.jclouds.softlayer.parsers;

import static com.google.inject.util.Types.newParameterizedType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.softlayer.config.SoftLayerParserModule;
import org.jclouds.softlayer.domain.product.ProductPackage;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

public class ProductPackageParser {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	Injector injector;
	Function<HttpResponse, ProductPackage> parseFunction;

	public ProductPackageParser() {
		injector = Guice.createInjector(new SoftLayerParserModule(), new GsonModule());
		parseFunction = getParseFunction();
	}

	public ProductPackage parse(final File productPackageFile) {
		Payload payload = Payloads.newFilePayload(productPackageFile);
		HttpResponse httpResponse = HttpResponse.builder().statusCode(200).message("ok").payload(payload).build();
		return parseFunction.apply(httpResponse);
	}

	public ProductPackage fromJson(final String productPackageJson) {
		Gson gson = new Gson();
		return gson.fromJson(productPackageJson, ProductPackage.class);
	}

	public String toJson(final ProductPackage productPackage) {
		Gson gson = new Gson();
		return gson.toJson(productPackage);
	}

	@SuppressWarnings("unchecked")
	protected Function<HttpResponse, ProductPackage> getParseFunction() {

		Function<HttpResponse, ProductPackage> parserFunction;
		ParameterizedType parserType = newParameterizedType(ParseJson.class, ProductPackage.class);
		parserFunction = (Function<HttpResponse, ProductPackage>) injector
				.getInstance((Key<? extends Function<HttpResponse, ?>>) Key.get(parserType));
		return parserFunction;
	}

	public ProductPackage loadFromFile(final File productPackageFile) throws IOException {
		String productPackageJson = readFromFile(productPackageFile);
		return fromJson(productPackageJson);
	}
	
	public void writeToFile(final File targetFile, final ProductPackage productPackage) throws IOException {
		String productPackageJson = toJson(productPackage);
		writeToFile(targetFile, productPackageJson);
	}
	
	private synchronized String readFromFile(final File file) throws IOException {

		StringBuffer content = new StringBuffer();

		// if file doesn't exists - throw an exception
		if (!file.exists()) {
			throw new IllegalArgumentException("File " + file.getAbsolutePath() + " doesn't exist");
		}

		FileReader fReader = new FileReader(file.getAbsoluteFile());
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(fReader);
			String line = null;
			while ((line = bReader.readLine()) != null) {
				content.append(line);
				content.append(LINE_SEPARATOR);
			}
		} finally {
			if (bReader != null) {
				bReader.close();	
			}
		}
		
		return content.toString();
	}

	
	public synchronized void writeToFile(final File file, final String content) throws IOException {
		// if file exists - delete it
		if (file.exists()) {
			file.delete();
		}

		file.createNewFile();

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(fw);
			bw.write(content);
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
	}

}
