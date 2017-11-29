package com.fangjian.framework.utils.self;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

	public static boolean checkFundTrace(String fundTrace){
		if(null==fundTrace){
			return false;
		}
		Pattern p = Pattern.compile("^\\d{1,4}$");
		return p.matcher(fundTrace).matches();
	}

	/**
	 * 校验登录密码是否包含特殊字符
	 * @param loginPwd
	 * @return
	 */
	public static boolean isPwdContainsSpecialCharacter(String pwd){
		if(null==pwd){
			return false;
		}
		Pattern p = Pattern.compile("^.*[<.>\"'].*");
		return p.matcher(pwd).matches();
	}

	/**
	 * 校验登录密码 ，长度在8-20位，
	 * @param loginPwd
	 * @return
	 */
	public static boolean checkLoginPwd(String loginPwd){
		if(null==loginPwd){
			return false;
		}
		Pattern p = Pattern.compile("^(?![0-9]+$)[a-zA-Z0-9,!,@,#,$,%,^,&,*,?,_,~,(,),\\-,=,+,:,;,/,\\{,\\},\\[,\\],\\\\,\\|,`]{8,20}$");

		return p.matcher(loginPwd).matches()&&loginPwd.length()>=8&&loginPwd.length()<=20;
	}

	/**
	 * 支付密码校验，长度8-20，必须字母数字混合
	 * @param payPwd
	 * @return
	 */
	public static boolean checkPayPwd(String payPwd){
		if(null == payPwd){
			return false;
		}
		Pattern p = Pattern.compile("^(?![a-zA-Z]+$)(?![0-9]+$)[a-zA-Z0-9,!,@,#,$,%,^,&,*,?,_,~,(,),\\-,=,+,:,;,/,\\{,\\},\\[,\\],\\\\,\\|,`]{8,20}$");
		return p.matcher(payPwd).matches();
	}

	//=====================操作员登录帐号
	public static boolean checkLoginName(String loginName){
		if(null==loginName){
			return false;
		}
		Pattern p = Pattern.compile("^[a-zA-Z0-9,_]{5,32}$");

		return p.matcher(loginName).matches() && loginName.length()>=5 && loginName.length()<=32;
	}

	//=====================判断邮件email是否正确格式
	public static boolean checkEmail(String email) {
        boolean flag = false;
         String check = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
         Pattern p = Pattern.compile(check);
        Matcher m = p.matcher(email.trim());
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }



	//=====================判断手机号phone是否正确格式
    public static boolean checkPhone(String phone){
        Pattern p =Pattern.compile("^(13|14|15|18)\\d{9}$");
        Matcher matcher = p.matcher(phone);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }


    public static boolean isContainsChinese(String s){
    	Pattern p =Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = p.matcher(s);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

  //检查字符串是否为数字
    public static boolean isNumber(String str)
       {
        if(StringUtils.isEmpty(str))
            return false;
           Pattern pattern= Pattern.compile("[0-9]*");
           Matcher match=pattern.matcher(str);
           if(match.matches()==false)
           {
                return false;
           }
           else
           {
                return true;
           }
       }


    public static boolean checkPostalCode(String str){
    	if(CheckUtil.isNumber(str) && str.length()==6)
    		return true;
    	return false;
    }

    public static boolean checkStringLength(final String str,int len){
        String fstr=str;
        if(StringUtils.isBlank(fstr)){
            return false;
        }
        try {

            fstr=new String(str.getBytes("gbk"),"iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return (fstr.length()<=len);
    }

    /**
     * 判断字符长度是否在min与max之间
     * @param str
     * @param min
     * @param max
     * @return true/false;
     */
    public static boolean checkLength(String str,int min,int max){
    	if(str != null ){
    		int len = 0;
			try {
				len = str.getBytes("gbk").length;
				return  len >= min && len <= max;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

    	}
    	return false;
    }

    public static boolean checkMaxLength(String str,int max){
    	if(str == null || str.length()==0){
    		return true;
    	}
    	return checkLength(str, 0, max);
    }
    public static boolean checkMinLength(String str,int min){
    	if(min<=0){
    		throw new IllegalArgumentException();
    	}
    	if(str == null || str.length()==0){
    		return false;
    	}

    	return checkLength(str, min, 4000);
    }





    public static void main(String[] args) {
		//System.out.println(CheckUtil.isContainsChinese("d425345阿斯顿飞"));
    	//System.out.print(CheckUtil.checkEmail("a111@staff.com.cn"));
//        System.out.print(CheckUtil.isNumber(""));
//        System.out.print(CheckUtil.checkLoginPwd("aaaaaaaaaaaaaaaaaaaa"));
//        System.out.print(CheckUtil.checkPayPwd("asda1111111111111111"));
   //     System.out.println(CheckUtil.checkLoginPwd("44444444444Z$44444"));
 //       System.out.println(CheckUtil.checkPayPwd("AAAAAAAAAAAAAAADF<"));

        System.out.print(CheckUtil.checkStringLength("上海588",200));

    	//System.out.println(CheckUtil.isNumber("0212341111"));

    }


}
