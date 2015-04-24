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

/**
 * Basic interface class to give access to Android SDK tools such Emulator, ADB etc.
 *
 */
public interface IAndroidSDK {
    /**
     * The <code>ANDROID_HOME</code> environment variable name.
     */
    String ENV_ANDROID_HOME = "ANDROID_HOME";

    /**
     * folder name for the sdk sub folder that contains the platform tools.
     */
    String PLATFORM_TOOLS_FOLDER_NAME = "platform-tools";

    String PARAMETER_MESSAGE = " Please set a valid Android SDK directory path by setting"
            + "environment variable  " + ENV_ANDROID_HOME + ".";

    /**
     * Returns the complete path for a tool, based on this SDK.
     *
     * @param tool which tool, for example <code>adb</code> or <code>dx.jar</code>.
     * @return the complete path as a <code>String</code>, including the tool's filename.
     */
    String getPathForTool(String tool);

    /**
     * Get the emulator path.
     *
     * @return {@link String}
     */
    String getEmulatorPath();

    /**
     * Get the android debug tool path (adb).
     *
     * @return {@link String}
     */
    String getAdbPath();
    
    String getZipAlignPath();
}