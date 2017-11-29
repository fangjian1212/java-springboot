package com.fangjian.framework.utils.self;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zengjin
 * @date 2010-8-4
 *
 * @author ddr
 * modify 2013-9-13 修改cookies有时取不到的bug
 * 修改所有方法均为静态方法
 * @param
 */
public class CookieUtil {

	private static final int maxAge=60*60*24*365;

	public static void setCookie(HttpServletResponse response,String cookieKey,String cookieValue){
		Cookie newcookie;
		newcookie = new Cookie(cookieKey,cookieValue);
		newcookie.setMaxAge(maxAge);
		newcookie.setSecure(false);
		response.addCookie(newcookie);
	}
	public static void setCookieFile(HttpServletResponse response,String cookieKey,String cookieValue){
		Cookie newcookie;
		newcookie = new Cookie(cookieKey,cookieValue);
		newcookie.setMaxAge(maxAge);
		response.addCookie(newcookie);
	}

	public static String getCookie(HttpServletRequest request,String cookieKey){
		Cookie[] cookies = request.getCookies();

			if(cookies!=null)
			{
			   for (int i = 0; i < cookies.length; i++)
			    {
			       Cookie c = cookies[i];
			       if(c.getName().equalsIgnoreCase(cookieKey))
			       {
			          return c.getValue();
			        }

			    }
			 }

		return null;
	}

	public static void clearCookie(HttpServletRequest request,HttpServletResponse response){
		 Cookie[]cookies = request.getCookies();
	    try {
	    	if(cookies!=null && cookies.length>0){
	        for(int i=0; i<cookies.length; i++) {
	            cookies[i].setMaxAge(0);
	            response.addCookie(cookies[i]);
	        }
	       }
	    } catch(Exception ex) {
	    	ex.printStackTrace();
	    }
	}

	public static void removeCookieValue(HttpServletRequest request,HttpServletResponse response,String key){
		 Cookie[]cookies = request.getCookies();
	    try {
	    	if(cookies!=null && cookies.length>0){
	        for(int i=0; i<cookies.length; i++) {
	        	Cookie c = cookies[i];
		       if(c.getName().equalsIgnoreCase(key)){
		    	   cookies[i].setMaxAge(0);
		            response.addCookie(cookies[i]);
		       }
	        }
	       }
	    } catch(Exception ex) {
	    	ex.printStackTrace();
	    }
	}
}
