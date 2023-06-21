package de.zebrajaeger.phserver.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GpsUtils {
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DECIMAL_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        DECIMAL_FORMAT.setMaximumFractionDigits(6);
    }

    public static String valueToStringWith1mPrecision(double v) {
        return DECIMAL_FORMAT.format(v);
    }
}
