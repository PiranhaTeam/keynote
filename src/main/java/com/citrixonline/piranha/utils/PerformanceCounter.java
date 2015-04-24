/*
 * Copyright (c) Citrix Online LLC
 * All Rights Reserved Worldwide.
 *
 * THIS PROGRAM IS CONFIDENTIAL AND PROPRIETARY TO CITRIX ONLINE
 * AND CONSTITUTES A VALUABLE TRADE SECRET.  Any unauthorized use,
 * reproduction, modification, or disclosure of this program is
 * strictly prohibited.  Any use of this program by an authorized
 * licensee is strictly subject to the terms and conditions,
 * including confidentiality obligations, set forth in the applicable
 * License and Co-Branding Agreement between Citrix Online LLC and
 * the licensee.
 */

package com.citrixonline.piranha.utils;


public class PerformanceCounter {

    private static long startTime;
    private static long stopTime;

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void stop() {
        stopTime = System.currentTimeMillis();
    }

    /**
     * very expensive call takes 5-8 ms, don't use it often.
     */
    public static void logElapsedTimeinMilliSeconds() {
        String msg = Long.toString(System.currentTimeMillis() - startTime) + "(MS)";
        System.out.println(String.format("Time Taken: %s", msg));
    }

    public static String getElapsedTimeinMilliSeconds() {
        return Long.toString(stopTime - startTime) + "(Ms)";
    }

    public static String getElapsedTimeinSeconds() {
        return Long.toString((stopTime - startTime) / 1000) + "(s)";
    }

    public static int safeLongToInt(final long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(l
                    + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    /**
     * Hidden default constructor.
     */
    private PerformanceCounter() { }
}
