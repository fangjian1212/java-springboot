/**
 *  File: MessageConvertFactory.java
 *  Description:
 *  Copyright © 2004-2013 hnapay.com . All rights reserved.
 *  Date      Author      Changes
 *  2010-7-26   Terry_ma    Create
 *
 */
package com.fangjian.framework.utils.self;

import org.springframework.context.MessageSource;


/**
 *
 */
public class MessageConvertFactory {

	private static MessageSource messageSource;

	public static String getMessage(String messageId) {

		if (null == messageSource) {
			return null;
		}
		return messageSource.getMessage(messageId, null, null);
	}

	public static String getMessage(String messageId, String[] args) {

		if (null == messageSource) {
			return null;
		}
		return messageSource.getMessage(messageId, args, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		MessageConvertFactory.messageSource = messageSource;
	}

}
