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

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;


/**
 * Implements IAndroidSDK, giving access to SDK tools on local host machine.
 */
public class LocalAndroidSdk implements IAndroidSDK {

    private final File sdkPath;

    public LocalAndroidSdk() {
        this(new File(System.getenv(ENV_ANDROID_HOME)));
    }

    public LocalAndroidSdk(File sdkPath) {
    		
        if (!sdkPath.exists()) {
            System.out.println("Environment Variable \"ANDROID_HOME\" is missing or "
                    + "the check the sdk path. " + sdkPath.getAbsolutePath() + "\n Also see http://www.cryse.org/mac-environment-variable/ for setting up ANDROID_HOME VARIABLE IN MAC.");
            System.exit(1);
        }
        this.sdkPath = sdkPath;
        System.out.println("Found ANDROID_HOME as :" + sdkPath);
        assertPathIsDirectory(sdkPath);
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final IAndroidSDK INSTANCE = new LocalAndroidSdk();
    }

    public static IAndroidSDK getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void assertPathIsDirectory(final File path) {
        if (path == null) {
            throw new InvalidSdkException(PARAMETER_MESSAGE);
        }
        if (!path.isDirectory()) {
            throw new InvalidSdkException("Path \"" + path + "\" is not a directory. "
                    + PARAMETER_MESSAGE);
        }
    }

    /* (non-Javadoc)
     * @see com.citrixonline.android.utils.IAndroidSDK#getPathForTool(java.lang.String)
     */
    public String getPathForTool(final String tool) {
    		String buildToolPath = getBuildPath().getAbsolutePath();
    		System.out.println("Android Build Tool Folder is : " + buildToolPath);
        String[] possiblePaths = {
                sdkPath + "/" + PLATFORM_TOOLS_FOLDER_NAME + "/" + tool,
                sdkPath + "/" + PLATFORM_TOOLS_FOLDER_NAME + "/" + tool + ".exe",
                sdkPath + "/" + PLATFORM_TOOLS_FOLDER_NAME + "/" + tool + ".bat",
                sdkPath + "/" + PLATFORM_TOOLS_FOLDER_NAME + "/lib/" + tool,
                sdkPath + "/tools/" + tool,
                sdkPath + "/tools/" + tool + ".exe",
                sdkPath + "/tools/" + tool + ".bat",
                sdkPath + "/tools/lib/" + tool,
                sdkPath + "",
                buildToolPath + "/" + tool,
                buildToolPath + "/" + tool + ".exe"
        };

        for (String possiblePath : possiblePaths) {
            File file = new File(possiblePath);
            if (file.exists() && !file.isDirectory()) {
                return file.getAbsolutePath();
            }
        }

        throw new InvalidSdkException("Could not find tool '" + tool + "'. " + PARAMETER_MESSAGE);
    }

    /* (non-Javadoc)
     * @see com.citrixonline.android.utils.IAndroidSDK#getEmulatorPath()
     */
    public String getEmulatorPath() {
        return getPathForTool("emulator");
    }

    /* (non-Javadoc)
     * @see com.citrixonline.android.utils.IAndroidSDK#getAdbPath()
     */
    public String getAdbPath() {
        return getPathForTool("adb");
    }
    
    /* (non-Javadoc)
     * @see com.citrixonline.android.utils.IAndroidSDK#getAdbPath()
     */
    public String getZipAlignPath() {
        return getPathForTool("zipalign");
    }
    
    public File getBuildPath() {
        File buildToolsFolder = new File(sdkPath, "build-tools");
        if (!buildToolsFolder.exists()) {
            throw new RuntimeException(
                     String.format("%1$s is not a folder and Does not Exist",
                             buildToolsFolder.getAbsolutePath()));
         }
        
        File buildToolPath = lastFolderModified(buildToolsFolder);
        if(!buildToolPath.exists()){
            throw new RuntimeException(
                    String.format("%1$s is not a folder and Does not Exist",
                    		buildToolPath.getAbsolutePath()));
        }
		return buildToolPath;

    }
    
    public File lastFolderModified(File dir) {
        File[] files = dir.listFiles(new FileFilter() {          
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }
}