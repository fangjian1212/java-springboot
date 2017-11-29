package com.fangjian.framework.utils.self;
/**
 * 获取IP地址类
 */

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.StringTokenizer;


public class IPUtil {
    private static String SERVERIP = null;

    public static String getLocalAddr() {
        if (null == SERVERIP) {
            try {
                SERVERIP = InetAddress.getLocalHost().toString();
            } catch (UnknownHostException ex) {
                SERVERIP = "UnknownHost";
            }
        }
        return SERVERIP;


    }


    /**
	 * 获得客户端Ip.
	 *
	 * @param request
	 * @return
	 */
	public static String getIp(final HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-client-ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		// 多级反向代理
		if (null != ip && !"".equals(ip.trim())) {
			StringTokenizer st = new StringTokenizer(ip, ",");
			if (st.countTokens() > 1) {
				return st.nextToken();
			}
		}
		return ip;
	}





	public static String getClientIP(HttpServletRequest request) {
        // 如果使用了反向代理软件,而且有多级反向代理的话,会通过,进行分隔
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isNotEmpty(ip) && StringUtils.indexOf(ip, ',') != -1) {
            ip = StringUtils.split(ip, ',')[0];
            if (ip.equalsIgnoreCase("unknown")) {
                ip = StringUtils.split(ip, ',')[1];
            }
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            // WebLogic Plug-In Enabled
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-client-ip");
		}
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

	@SuppressWarnings("rawtypes")
	public static String getIp() {
	    String localip = null;// 本地IP，如果没有配置外网IP则返回它
	    String netip = null;// 外网IP
	    try {
	     Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
	     InetAddress ip = null;
	     boolean finded = false;// 是否找到外网IP
	     while (netInterfaces.hasMoreElements() && !finded) {
	      NetworkInterface ni = (NetworkInterface) netInterfaces
	        .nextElement();
	      Enumeration address = ni.getInetAddresses();
	      while (address.hasMoreElements()) {
	       ip = (InetAddress) address.nextElement();
	       if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
	         && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
	        netip = ip.getHostAddress();
	        finded = true;
	        break;
	       } else if (ip.isSiteLocalAddress()
	         && !ip.isLoopbackAddress()
	         && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
	        localip = ip.getHostAddress();
	       }
	      }
	     }
	    } catch (SocketException e) {
	     e.printStackTrace();
	    }
	    if (netip != null && !"".equals(netip)) {
	     return netip;
	    } else {
	     return localip;
	    }
	   }


	public static String getAgent(HttpServletRequest request){
	    String agent = request.getHeader("User-Agent");
	    return agent;
	}


	public static String getUserBrowser(HttpServletRequest request){
	    String Agent = request.getHeader("User-Agent");
        StringTokenizer st = new StringTokenizer(Agent,";");
        st.nextToken();
        String userbrowser ="orther";
        if(st.hasMoreTokens()){
            userbrowser= st.nextToken();
        }

        return userbrowser;
	}

}
