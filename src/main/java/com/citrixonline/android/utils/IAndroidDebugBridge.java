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

package com.citrixonline.android.utils;

import java.util.Locale;


/**
 * This is a basic interface to run adb commands in local or remote hosts.
 */
public interface IAndroidDebugBridge {
    /**
     * Specifies which type of device to connect or need to be used.
     */
    public enum ANDROIDDEVICETYPE {
        /**
         * USD Device attached that is real phone.
         */
        USB,

        EMULATOR;

        public ANDROIDDEVICETYPE parseString(final String type) {
            if (type.toLowerCase(Locale.US).startsWith("usb")) {
                return ANDROIDDEVICETYPE.USB;
            }
            if (type.toLowerCase(Locale.US).startsWith("emulator")) {
                return ANDROIDDEVICETYPE.EMULATOR;
            }
            return null;
        }
    }

    /**
     * Gets hardware device connected, ignores all emulators.
     * @return {@link String}
     */
    String getDeviceName();

    /**
     * Starts adbserver - runs "adb start-server".
     * @return {@link Boolean}
     */
    boolean startADBServer();

    /**
     * Gets all devices connected to the test machine, runs command "adb devices".
     * @return string array with device serial numbers
     */
    String[] getAllDevicesConnected();

    /**
     * Runs command  "adb shell pm uninstall -k <appFullName>".
     * @param appFullName like com.citrixonline.android.myappname
     * @param deleteDataAndCacheDirectoriesOnDevice , if false -k is option is used and data and
     * cache is not deleted
     * @return {@link Boolean}
     */
    boolean unInstallAPK(String appFullName, Boolean deleteDataAndCacheDirectoriesOnDevice);

    /**
     * Runs command  "adb shell pm list packages".
     * @param appFullName
     * @return {@link Boolean}
     */
    boolean checkAppInstalled(String appFullName);

    /**
     * Runs command "adb install <path_to_apk>".
     * @param apkFileFullPath - Path to APK to install.
     * @return {@link Boolean}
     */
    boolean installAPK(String apkFileFullPath);

    /**
     * Runs command "adb forward tcp:<hostPort> tcp:<devicePort>".
     * @param hostPort like 6100
     * @param devicePort like 7100
     * @return {@link Boolean}
     */
    boolean forwardtcpPort(int hostPort, int devicePort);

    /**
     * Runs command "adb shell kill -9 <pid>".
     * @param pid
     * @return {@link Boolean} true means success, false means failure
     */
    boolean killAndroidProcess(int pid);

    /**
     * Runs command "adb shell am force-stop <packageName>".
     * @param packageName
     * @return {@link Boolean} true means success, false means failure
     */
    boolean forceStopAndroidProcess(String packageName);

    /**
     * @param appname like Omega_D
     * @param maxWaitInSeconds
     * @return {@link Integer}
     */
    int waitAndGetAndroidAppInstanceID(String appname, int maxWaitInSeconds);

    /**
     * Runs command "adb shell ps"  and grep the pid.
     * @param appname
     * @return {@link Integer} if 0 means process does not exist
     */
    int getAndroidAppInstanceID(String appname);

    /**
     * Check device is connected.
     * @return {@link Boolean}
     */
    boolean checkDeviceisConnected();

    /**
     * Push file into Android device.
     * @param localDir  like /Users/JohnDoe/ your local mac or windows dir
     * @param remoteDir like /data/local/bin your remote android device folder
     * @param fileName  like OmegaApp_d
     * @return {@link Boolean}
     */
    boolean push(final String localDir , final String remoteDir, final String fileName);

    /**
     * Ping a remote host.
     * @param host like 10.2.232.31
     * @return {@link Boolean}
     */
    boolean ping(String host);

	void killADBServer();
}