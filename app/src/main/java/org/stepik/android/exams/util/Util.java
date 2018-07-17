package org.stepik.android.exams.util;

import android.os.Build;

import java.util.Random;

public class Util {

    public static int getRandomNumberBetween(final int lower, final int upper) {
        return (int) (Math.random() * (upper - lower)) + lower;
    }

    private static final String ALLOWED_SYMBOLS = "ABCDEFGHIJKLMNOPQRESTUVWXYZabcdefgghiklmnopqrstuvwxyz1234567890_";

    public static String randomString(final int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            builder.append(ALLOWED_SYMBOLS.charAt(random.nextInt(ALLOWED_SYMBOLS.length())));
        }

        return builder.toString();
    }

    public static boolean isLowAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
