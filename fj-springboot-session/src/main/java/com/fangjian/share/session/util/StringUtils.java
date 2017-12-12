/*
 */

package com.fangjian.share.session.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Random RANDOM = new Random();

    private static char[] NUMBERS_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static char[] LETTERS_CHARS = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static char[] NUMBERS_LETTERS_CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static final Pattern EMOJI = Pattern.compile(
            "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    /**
     * 将表格中的NULL转为空字符
     *
     * @param str 输入字符
     * @return 输出字符
     */
    public static String toString(Object str) {
        if (str == null) {
            str = "";
        } else if (str == "null")
            str = "";
        else {
            str = str.toString().trim();
        }
        return str.toString();
    }


    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str.trim())) ? true : false;
    }

    /**
     * 验证是否为yyyy-MM-dd日期格式
     *
     * @param str
     * @return
     * @author baobao
     */
    public static boolean isDateValid(String str) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = (Date) formatter.parse(str);
            return str.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证是否为整形数字类型
     *
     * @param value
     * @return
     * @author baobao
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    public static String random(int count, char[] chars) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("生成字符串长度" + count + "小于0");
        }

        char[] buffer = new char[count];

        while (count-- != 0) {
            char ch = chars[RANDOM.nextInt(chars.length)];
            buffer[count] = ch;
        }
        return new String(buffer);
    }

    public static String randomNumbers(int count) {
        return random(count, NUMBERS_CHARS);
    }

    public static String randomLetters(int count) {
        return random(count, LETTERS_CHARS);
    }

    public static String randomNumbersAndLetters(int count) {
        return random(count, NUMBERS_LETTERS_CHARS);
    }

    public static boolean isAbsoluteUrl(String url) {
        final Pattern ABSOLUTE_URL = Pattern.compile("\\A[a-z0-9.+-]+://.*", Pattern.CASE_INSENSITIVE);

        return ABSOLUTE_URL.matcher(url).matches();
    }

    /**
     * 过滤emoji字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        if (isEmpty(source)) {
            return source;
        }
        Matcher matcher = EMOJI.matcher(source);
        return matcher.replaceAll("");
    }

    public static String[] splitToStringArray(String str, String delimiters) {
        if (str == null) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.trim();
            if (token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * 将字符串集合转拼接成单个字符串
     *
     * @param list      字符串集合
     * @param delimiter 分隔符
     * @return 拼接后的字符串
     */
    public static String toDelimitedString(List<String> list, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
            String str = iterator.next();
            builder.append(str);
            if (iterator.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

}
