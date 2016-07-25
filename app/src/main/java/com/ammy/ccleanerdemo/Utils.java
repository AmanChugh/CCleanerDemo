package com.ammy.ccleanerdemo;

/**
 * Created by amandeepsingh on 22/7/16.
 */
public class Utils {

    public static String byteToReadable(long bytes) {
        String[] types = {"kb", "Mb", "GB", "TB", "PB", "EB"};

        int unit = 1000;
        if (bytes < unit) return bytes + "bytes";
        int exp = (int) (Math.log(bytes) / Math.log(unit));

        return String.format("%.1f ", bytes / Math.pow(unit, exp)) + types[exp - 1];
    }
}
