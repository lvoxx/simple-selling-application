package com.shitcode.demo1.helper;

import java.text.DecimalFormat;

public abstract class BitSizeConverter {
    public static long BYTE = 1L;
    public static long KB = BYTE * 1000;
    public static long MB = KB * 1000;
    public static long GB = MB * 1000;
    public static long TB = GB * 1000;
    public static long PB = TB * 1000;
    public static long EB = PB * 1000;

    private static DecimalFormat DEC_FORMAT = new DecimalFormat("#.##");

    public static String format(long size) {
        if (size < 0)
            throw new IllegalArgumentException("Invalid file size: " + size);
        if (size >= EB)
            return formatSize(size, EB, "EB");
        if (size >= PB)
            return formatSize(size, PB, "PB");
        if (size >= TB)
            return formatSize(size, TB, "TB");
        if (size >= GB)
            return formatSize(size, GB, "GB");
        if (size >= MB)
            return formatSize(size, MB, "MB");
        if (size >= KB)
            return formatSize(size, KB, "KB");
        return formatSize(size, BYTE, "Bytes");
    }

    
    public static long format(String size) {
        long result = 0;
        String value = size.replaceAll("[^0-9]", "");
        String unit = size.replaceAll("[0-9]", "").trim().toUpperCase();
        if (value.length() == 0)
            throw new IllegalArgumentException("Invalid size: " + size);
        try {
            result = Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        switch (unit) {
            case "EB":
                result *= EB;
                break;
            case "PB":
                result *= PB;
                break;
            case "TB":
                result *= TB;
                break;
            case "GB":
                result *= GB;
                break;
            case "MB":
                result *= MB;
                break;
            case "KB":
                result *= KB;
                break;
            default:
                // Byte
        }
        return result;
    }


    private static String formatSize(long size, long divider, String unitName) {
        return DEC_FORMAT.format((double) size / divider) + " " + unitName;
    }

}
