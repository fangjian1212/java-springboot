package com.fangjian.xj.service.common.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class ConfigUtils {
    private static Properties properties = new Properties();
    private static String[] propNames = new String[]{"common.properties"};

    static {
        String path = ClassLoader.getSystemResource("").getPath();
        for (int i = 0; i < propNames.length; i++) {
            File file = new File(path + propNames[i]);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString;
                while ((tempString = reader.readLine()) != null) {
                    String[] pro = tempString.split("=");
                    if (pro != null && pro.length == 2) {
                        properties.setProperty(pro[0], pro[1]);
                    }
                }
                reader.close();
            } catch (IOException e) {
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
    }

    public static String get(String name) {
        if (name == null) {
            return null;
        }
        String value = properties.getProperty(name);
        return value;
    }

    public static String get(String name, String def) {
        String val = get(name);
        return val != null ? val : def;
    }

    public static int getInt(String name) {
        String val = get(name);
        return val == null ? 0 : Integer.parseInt(val);
    }

    public static int getInt(String name, int def) {
        String val = get(name);
        return val != null ? Integer.parseInt(val) : def;
    }

    public static long getLong(String name) {
        String val = get(name);
        return val == null ? 0l : Long.parseLong(val);
    }

    public static long getLong(String name, long def) {
        String val = get(name);
        return val != null ? Long.parseLong(val) : def;
    }

    public static boolean getBool(String name) {
        String val = get(name);
        return val == null ? false : Boolean.parseBoolean(val);
    }

    public static boolean getBool(String name, boolean def) {
        String val = get(name);
        return val != null ? Boolean.parseBoolean(val) : def;
    }

    public static float getFloat(String name) {
        String val = get(name);
        return val == null ? 0f : Float.parseFloat(val);
    }

    public static float getFloat(String name, float def) {
        String val = get(name);
        return val != null ? Float.parseFloat(val) : def;
    }

    public static double getDouble(String name) {
        String val = get(name);
        return val == null ? 0 : Double.parseDouble(val);
    }

    public static double getDouble(String name, double def) {
        String val = get(name);
        return val != null ? Double.parseDouble(val) : def;
    }

}
