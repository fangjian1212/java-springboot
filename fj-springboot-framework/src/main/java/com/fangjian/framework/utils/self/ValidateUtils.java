package com.fangjian.framework.utils.self;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {
	private static final Map<String, Pattern> patternMap = new HashMap<String, Pattern>();

	public static final boolean isValidEmail(String email) {
		String regex = "^\\w+([-+.]+\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		return matches(email, regex);
	}

	public static final boolean isValidMobile(String mobile) {
		String regex = "^1\\d{10}$";

		return matches(mobile, regex);
	}

	public static boolean isValidateByRegex(String regexString, String regex) {
		return matches(regexString, regex);
	}
	public static final boolean isValidMinusAmount(String amt) {
		String regex = "^(-)?(0|[1-9]\\d*)$|^(-)?(0|[1-9]\\d*)\\.(\\d{1}|\\d{2})$";
		return matches(amt, regex);
	}
	public static boolean isTwoDateGreaterXYear(String fromDate, String toDate,int x) {
		int nextXYear = Integer.parseInt(fromDate.trim().substring(0,4)) + x;
		String nextXDate = "" + nextXYear+fromDate.trim().substring(4);
		if(toDate.trim().compareTo(nextXDate)>=0){
			return true;
		}else{
			return false;
		}
	}
	public static final boolean isValidIDCard(String sid) {
		// String regex = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
		// return matches(cardId, regex);
		sid = sid.toUpperCase();
		sid.trim();
		if (sid.length() == 15) {
			if(sid.contains("X")){
				return false;
			}
			String sid0 = sid.substring(0, 6) + "19" + sid.substring(6, 15);
			return idcheckup(sid0);
		} else if (sid.length() == 18) {
			checkdate(sid);
			return checkdigit(sid);
		} else
			return false;
	}



	public static final boolean isValidPhone(String phone) {
		String regex = "^((\\(\\d{2,3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{6,7}(\\-\\d{1,4})?$";
		return matches(phone, regex);
	}

	public static final boolean isValidInteger(String str) {
		String regex = "^[-\\+]?\\d+$";
		return matches(str, regex);
	}


	public static final boolean isValidZip(String zip) {
		String regex = "^[1-9]\\d{5}$";
		return matches(zip, regex);
	}

	public static final boolean isValidChinese(String chinese) {
		String regex = "^[\\u4e00-\\u9fa5]+$";
		return matches(chinese, regex);
	}

	public static final boolean isValidEnglish(String english) {
		return matches(english, "^[A-Za-z]+$");
	}

	public static final boolean isValidNumber(String num) {
		return matches(num, "^\\d+$");
	}

	public static final boolean isValidCharacter(String character) {
		// return matches(character,"^\\w+$");
		return matches(character, "^[A-Za-z0-9-]+$");
	}

	public static final boolean isValidDate(String yyyy_mm_dd) {
		String regex = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		return matches(yyyy_mm_dd, regex);
	}

	public static final boolean isValidYearMonth(String yyyymm){
		String regex = "20\\d[0-9](0[1-9]|1[012])";
		return matches(yyyymm,regex);
	}
	public static final boolean isValidTime(String time) {
		String regex = "^\\d{1,2}\\d{1,2}\\d{1,2}$";
		return matches(time, regex);
	}

	public static final boolean isValidDateTime(String dt) {
		String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
		return matches(dt, regex);
	}

	public static final boolean isValidAmount(String amt) {
		String regex = "^(0|[1-9]\\d*)$|^(0|[1-9]\\d*)\\.(\\d{1,2})$";
		return matches(amt, regex);
	}

	public static final boolean matches(String str, String regex) {
		if ( null == str){
			return false;
		}
		Pattern p = getPattern(regex);
		Matcher m = p.matcher(str);
		boolean b = m.matches();
		return b;
	}

	private static final Pattern getPattern(String regex){
		Pattern p = patternMap.get(regex);
		if ( null == p){
			p = Pattern.compile(regex);
			patternMap.put(regex, p);
		}
		return p;
	}

	public static boolean idcheckup(String s) {
		StringBuffer sad = new StringBuffer(s);
		int[] idcd = new int[18];
		for (int i = 1; i < 18; i++) {
			int j = 17 - i;
			idcd[i - 1] = Integer.parseInt(sad.substring(j, j + 1));
		}

		int[] w = { 2, 4, 8, 5, 10, 9, 7, 3, 6, 1, 2, 4, 8, 5, 10, 9, 7 };
//		char[] as = { '1', '0', 'x', '9', '8', '7', '6', '5', '4', '3', '2' };
		int sum = 0;
		for (int i = 0; i < 17; i++) {
			sum = sum + idcd[i] * w[i];
		}
		sum = sum % 11;

		int y = Integer.parseInt(sad.substring(6, 10));
		int m = Integer.parseInt(sad.substring(10, 12));
		int d = Integer.parseInt(sad.substring(12, 14));
		if (y < 1900 || m < 1 || m > 12 || d < 1 || d > 31
				|| ((m == 4 || m == 6 || m == 9 || m == 11) && d > 30)
				|| (m == 2 && ((y % 4 > 0 && d > 28) || d > 29))) {
			return false;
		} else
			return true;
	}

	public static boolean checkdate(String s) {
		String sad = new String(s);
		int y = Integer.parseInt(sad.substring(6, 10));
		int m = Integer.parseInt(sad.substring(10, 12));
		int d = Integer.parseInt(sad.substring(12, 14));
		if (y < 1900 || m < 1 || m > 12 || d < 1 || d > 31
				|| ((m == 4 || m == 6 || m == 9 || m == 11) && d > 30)
				|| (m == 2 && ((y % 4 > 0 && d > 28) || d > 29)))
			return false;
		else
			return true;
	}

	public static boolean checkdigit(String s) {

		StringBuffer sad = new StringBuffer(s);
		int[] idcd = new int[18];
		for (int i = 1; i < 18; i++) {
			int j = 17 - i;
			idcd[i - 1] = Integer.parseInt(sad.substring(j, j + 1));
		}
		int[] w = { 2, 4, 8, 5, 10, 9, 7, 3, 6, 1, 2, 4, 8, 5, 10, 9, 7 };
		char[] as = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

		int sum = 0;
		for (int i = 0; i < 17; i++) {
			sum = sum + idcd[i] * w[i];
		}
		sum = sum % 11;
		char c = sad.charAt(17);
		if (c == as[sum])
			return true;
		else
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

	public static void main(String[] args) {
		// String sid = "340121198006147612";
		// String sid = "44010219711023435x";
		// boolean flag = isValidCardId(sid);
		// if(flag)
		// System.out.println("The id is valid!");
		// else
		// System.out.println("the id is invalid!");

		System.out.println("abcd="+isValidYearMonth("200913"));
		System.out.println("email="+isValidEmail("luo-qingfeng@126.com"));

	}

}
