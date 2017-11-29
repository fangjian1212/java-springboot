/**
 *
 */
package com.fangjian.framework.utils.self;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author huhb
 *
 */
public class WebHttpClientUtil {

	public static int POST_METHOD = 1;

	public static int GET_METHOD = 2;

	private static int DEFAULT_CONNECTION_TIMEOUT = 10000;

	private static int DEFAULT_SO_TIMEOUT = 10000;

	public static String commonClient(String url
			, LinkedHashMap<String, String> params
			, int method
			, int conntectTimeOut) throws Exception {
		if(method == POST_METHOD) {
			return postMethod(params, url, conntectTimeOut);
		}

		return "";
	}

	private static String postMethod(Map<String, String> params, String url, int time) throws Exception {
		PostMethod postMethod = new PostMethod(url);
		HttpClient httpClient = new HttpClient();
		if(params != null && params.size() > 0) {
			NameValuePair[] data = new NameValuePair[params.size()];
			int n = 0;
			for(String key : params.keySet()) {
				data[n] = new NameValuePair(key, params.get(key));
				n++;
			}
			postMethod.setRequestBody(data);
		}
		httpClient.getHttpConnectionManager().getParams()
			.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setSoTimeout(DEFAULT_SO_TIMEOUT);
		// 设置utf-8
		postMethod.getParams().setParameter(
			HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
					statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = postMethod.getResponseHeader("location");
				String location = null;
			    if (locationHeader != null) {
			    	location = locationHeader.getValue();
			    	System.out.println("The page was redirected to:" + location);
			    } else {
			    	System.err.println("Location field value is null.");
			    }
			} else if(statusCode == HttpStatus.SC_OK){
				StringBuffer contentBuffer = new StringBuffer();
				InputStream in = postMethod.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in,postMethod.getResponseCharSet()));
				String inputLine = null;
				while((inputLine = reader.readLine()) != null){
					contentBuffer.append(inputLine);
				}
				in.close();
				return contentBuffer.toString();
			}
		} catch (HttpException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			postMethod.releaseConnection();
		}

		return "";
	}

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
//		params.put("merchant", "11111");
//		params.put("orderCode", "11111222");
//		params.put("tranCode", "01");
//
//		params.put("curCode", "10");
//		params.put("orderAmount", "100");
//		params.put("orderTime", "20110822132723");
//		params.put("originMerchant", "121212");
//		params.put("orderNote", "支付");
//		params.put("cardNo", "01");
//		params.put("tranCode", "01");
//
//		String returnStr = WebHttpClientUtil.commonClient("http://10.2.169.137/ecard/api/resumeCard.htm", params, 1, 2000);
//		System.out.println(returnStr);
//		balanceResponseDTO d = JSonUtil.toObject(returnStr, balanceResponseDTO.class);
//		System.out.println(d.getRtnMsg());
//	}

}

