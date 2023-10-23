/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.serialization;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor.ioc.Container;

/**
 * A set of deserializers. Returns null if no serializer is capable of coping with the required media type.
 *
 * @author Lucas Cavalcanti
 * @author Ricardo Nakamura
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultDeserializers implements Deserializers {

	private final Map<String, Class<? extends Deserializer>> deserializers = new HashMap<>();

	@Override
	public Deserializer deserializerFor(String contentType, Container container) {
		if (deserializers.containsKey(contentType)) {
			return container.instanceFor(deserializers.get(contentType));
		}
		return subpathDeserializerFor(contentType, container);
	}

	private Deserializer subpathDeserializerFor(String contentType, Container container) {
		if(contentType.contains("/")) {
			String newType = removeChar(contentType, "/");
			if (deserializers.containsKey(newType)) {
				return container.instanceFor(deserializers.get(newType));
			}
		}
		return subpathDeserializerForPlus(contentType, container);
	}

	private Deserializer subpathDeserializerForPlus(String contentType, Container container) {
		if(contentType.contains("+")) {
			String newType = removeChar(contentType, "+");
			if (deserializers.containsKey(newType)) {
				return container.instanceFor(deserializers.get(newType));
			}
		}
		return null;
	}

	private static String removeChar(String type, String by) {
		return type.substring(type.lastIndexOf(by)+1);
	}

	@Override
	public void register(Class<? extends Deserializer> type) {
		Deserializes deserializes = type.getAnnotation(Deserializes.class);
		checkArgument(deserializes != null, "You must annotate your deserializers with @Deserializes");

		for (String contentType : deserializes.value()) {
			deserializers.put(contentType, type);
		}
	}
}
