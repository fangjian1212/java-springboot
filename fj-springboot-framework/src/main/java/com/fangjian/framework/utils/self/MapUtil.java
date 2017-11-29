/**
 *  File: MapUtil.java
 *  Description:
 *  Copyright © 2004-2013 hnapay.com . All rights reserved.
 *  Date      Author      Changes
 *  2010-8-20   terry_ma     Create
 *
 */
package com.fangjian.framework.utils.self;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("rawtypes")
public class MapUtil {


	/**
	 * t
	 * @param map
	 * @return key1=value1&key2=value2...
	 */

	public static String map2string(Map map) {

		if (null == map || map.isEmpty()) {
			return null;
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			Iterator iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Object value = map.get(key);
				if (null != value && !(value instanceof Class)
						&& !(value instanceof List)) {
					stringBuilder.append(key).append("=").append(value).append(
							"&");
				}
			}

			return stringBuilder.deleteCharAt(stringBuilder.length() - 1)
					.toString();
		}
	}

	/**
	 *
	 * @param string
	 * @return
	 */
	public static Map string2map(String string) {

		Map<String, String> paraMap = null;
		try {
			if (!StringUtil.isEmpty(string)) {
				paraMap = new HashMap<String, String>();
				String[] paras = string.split("&");
				if (null != paras && paras.length > 0) {
					for (int i = 0; i < paras.length; i++) {
						String tempStr = paras[i];
						int splitIndex = tempStr.indexOf("=");
						paraMap.put(tempStr.substring(0, splitIndex), tempStr
								.substring(splitIndex + 1, tempStr.length()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return paraMap;
	}
}
