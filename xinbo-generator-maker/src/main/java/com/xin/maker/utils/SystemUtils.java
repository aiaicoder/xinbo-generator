package com.xin.maker.utils;

/**
 * @author 15712
 */
public class SystemUtils {

    public static int checkOs() {
        String os = System.getProperty("os.name").toLowerCase();
        int type;
        if (os.contains("win")) {
            // Windows操作系统
            return type = 1;
        } else {
            // 其他操作系统
            return type = 0;
        }
    }
}

