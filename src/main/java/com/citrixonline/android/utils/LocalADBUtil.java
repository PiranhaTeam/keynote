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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citrixonline.piranha.utils.CommandExecutor;
import com.citrixonline.piranha.utils.ExecutionException;

/**
 * Implements @link {@link IAndroidDebugBridge} to handle ADB commands on the local machine.
 *
 */
public class LocalADBUtil implements IAndroidDebugBridge {

    Logger    logger = Logger.getLogger(LocalADBUtil.class);

    String device;

    public LocalADBUtil() { }

    public LocalADBUtil(final String serialNumber) {
        super();
        this.device = serialNumber;
    }

    /**
     * use "usb" for usb connected device, or char "emulator" to connect to emulator.
     * @param devicetype - supports usb, emulator
     */
    public LocalADBUtil(final ANDROIDDEVICETYPE devicetype) {
        super();
        this.device = devicetype.toString();
    }

    protected void commandFailed(final CommandExecutor executor, final String message) {
        logger.error(executor.getStandardOut());
        logger.error(executor.getStandardError());
        System.out.println(message);
    }

    private boolean executeADBCommand(final String command) {
        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);
        List<String> commands = new ArrayList<String>();
        commands.add(command);
        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands);
            return true;
        } catch (ExecutionException e) {
            commandFailed(executor, "Start ADBServer Command Failed");
            return false;
        }
    }

    /**
     * Gets the name of the Android device attached to the local machine.
     * @return {@link String} - name of the attached device.
     */
    public String getDeviceName() {
        String[] devices = getAllDevicesConnected();
        for (int i = 0; i < devices.length; i++) {
            String name = devices[i];
            if (!name.contains("emulator")) {
                return name;
            }
        }
        System.out.println("Could not find a device attached. Emulators are not supported");
        // this only for now and G2M Android testing as G2M does not run on
        // emulators due to VOIP arm instruction incompatibility.
        return null;
    }

    /**
     * Starts the ADB server.
     * @return Boolean - true if successful.
     */
    public boolean startADBServer() {
        return executeADBCommand("start-server");
    }
    
    public void killADBServer() {
        executeADBCommand("kill-server");
    }

    /**
     * Gets a list of all the devices attached.
     * @return Array of {@link String} listing all the attached devices.
     */
    public String[] getAllDevicesConnected() {
        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);
        List<String> commands = new ArrayList<String>();
        commands.add("devices");
        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands);
        } catch (ExecutionException e) {
            commandFailed(executor, "getAllDevicesConnected Command Failed");
            return null;
        }

        String result = executor.getStandardOut();
        result = result.replace("List of devices attached ", "");
        result = result.replace("device", "");
        result = result.replace("offline", "");
        String[] split = result.split("\\s");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }
        return split;
    }

    /**
     * Installs the specified APK file in device/emulator.
     * @return True is installation was successful, else false.
     */
    public boolean installAPK(final String apkFile) {
        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);
        List<String> commands = new ArrayList<String>();
        addDeviceParameter(commands);
        commands.add("install");
        commands.add("-r");
        commands.add(apkFile);
        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands, false);
            final String standardOut = executor.getStandardOut();
            if (standardOut != null && standardOut.contains("Failure")) {
                System.out.println("Error deploying " + apkFile
                        + " to device. You might want to undeploy the apk first \n" + standardOut);
                
            }
        } catch (ExecutionException e) {
            commandFailed(executor, "installAPK Command Failed");
            return false;
        }
        String result = executor.getStandardOut();
        if (!result.contains("Success")) {
        		System.out.println("Failed to install apk : " + apkFile);
            return false;
        }
        return true;
    }

    /**
     * Un-installs the specified APK in device/emulator.
     * @return True if un-install was successful.
     */
    public boolean unInstallAPK(final String appFullName,
            final Boolean deleteDataAndCacheDirectoriesOnDevice) {
        if (!checkAppInstalled(appFullName)) {
            return true;
        }
        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);
        List<String> commands = new ArrayList<String>();
        addDeviceParameter(commands);
        commands.add("shell");
        commands.add("pm");
        commands.add("uninstall");
        if (!deleteDataAndCacheDirectoriesOnDevice) {
            commands.add("-k"); // ('-k' means keep the data and cache
            // directories)
        }
        commands.add(appFullName);
        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands, false);
        } catch (ExecutionException e) {
            commandFailed(executor, "unInstallAPK Command Failed");
            return false;
        }
        String result = executor.getStandardOut();
        if (!result.contains("Success")) {
 //           PiranhaUtils.commandFailed("Failed to uninstall application : "
 //                   + appFullName);
            return false;
        }
        return true;
    }

    /**
     * Launches the specified application name on the device/emulator.
     * @param packageName - Package name for the application.
     * @param appName - Application name to launch.
     * @return True if successful, else false.
     */
    public boolean launchApkApplication(final String packageName, final String appName,
            final String commandLine) {

        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);

        // Launch an app install via an apk file.  For example:
        //    am start -a android.intent.action.MAIN -n
        // com.citrixonline.test/com.citrixonline.test.AndroidAFTAppActivity -e host <ip>
        // -version <aft version>
        List<String> commands = new ArrayList<String>();
        addDeviceParameter(commands);
        commands.add("shell");
        commands.add("am");
        commands.add("start");
        commands.add("-a");
        commands.add("android.intent.action.MAIN");
        commands.add("-n");
        commands.add(packageName + "/" + packageName + "." + appName);

        if (!commandLine.isEmpty()) {
            commands.add(commandLine);
        }

        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands, false);
        } catch (ExecutionException e) {
            commandFailed(executor, "launchApplication Command Failed");
            return false;
        }

        return true;
    }

    /**
     * Launches the binary executable.
     * @param executable
     * @return {@link Boolean}
     */
    public boolean launchNativeBinary(final String executable) {
        CommandExecutor executor = CommandExecutor.Factory.createLocalCommmandExecutor();
        executor.setLogger(logger);

        List<String> commands;
        commands = new ArrayList<String>();
        commands.add(LocalAndroidSdk.getInstance().getAdbPath());
        addDeviceParameter(commands);
        commands.add("shell");
        commands.add(executable);

        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands, false);
        } catch (ExecutionException e) {
            commandFailed(executor, "launchApplication Command Failed");
            return false;
        }
        return true;
    }

    /**
     * Checks to verify if the application is installed on the device/emulator.
     * @param appFullName - Application name to verify.
     * @return True if specified application was found to be installed, else false.
     */
    public boolean checkAppInstalled(final String appFullName) {
        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);
        List<String> commands = new ArrayList<String>();
        addDeviceParameter(commands);
        commands.add("shell");
        commands.add("pm");
        commands.add("list");
        commands.add("packages");
        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands);
        } catch (ExecutionException e) {
            commandFailed(executor, "checkAppInstalled Command Failed");
            return false;
        }
        String result = executor.getStandardOut();
        String[] packages = result.split("package:");
        for (int i = 0; i < packages.length; i++) {
            if (packages[i].equalsIgnoreCase(appFullName)) {
                return true;
            }
        }
        logger.debug(result);
        return false;
    }

    /**
     * Checks if a specific device should be used, and adds any relevant
     * parameter(s) to the parameters list.
     * @param commands the parameters to be used with the {@code adb} command
     */
    protected void addDeviceParameter(final List<String> commands) {
        if (StringUtils.isNotBlank(device)) {
            if ("usb".equals(device)) {
                commands.add("-d");
            } else if ("emulator".equals(device)) {
                commands.add("-e");
            } else {
                commands.add("-s");
                commands.add(device);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.citrixonline.android.utils.IADB#forwardtcpPort(int, int)
     */
    public boolean forwardtcpPort(final int hostPort, final int devicePort) {
        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);
        List<String> commands = new ArrayList<String>();
        commands.add("forward");
        commands.add("tcp:" + hostPort);
        commands.add("tcp:" + devicePort);
        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands);
            return true;
        } catch (ExecutionException e) {
            commandFailed(executor, "forwardtcpPort Command Failed");
            return false;
        }
    }

    /**
     * Kills the Android process specified by it's process id.
     * @param pid - process ID
     * @return True if successful, else false.
     */
    public boolean killAndroidProcess(final int pid) {
        return executeADBCommand("kill-server");
    }

    /* (non-Javadoc)
     * @see com.citrixonline.android.utils.AndroidDebugBridge#forceStoplAndroidProcess(int)
     */
    public boolean forceStopAndroidProcess(final String packageName) {
        if (determineAndroidOS() > 4.0) {
            executeADBCommand("force-stop " + packageName);
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.citrixonline.android.utils.AndroidDebugBridge#determineAndroidOS(int)
     */
    public float determineAndroidOS() {
        CommandExecutor executor = CommandExecutor.Factory
                .createLocalCommmandExecutor();
        executor.setLogger(logger);

        List<String> commands = new ArrayList<String>();
        addDeviceParameter(commands);
        commands.add("shell");
        commands.add("getprop");
        commands.add("ro.build.version.release");

        try {
            executor.executeCommand(LocalAndroidSdk.getInstance().getAdbPath(),
                    commands);
        } catch (ExecutionException e) {
            commandFailed(executor, "determineAndroidOS Command Failed");
            return (float) 0.0;
        }

        String result = executor.getStandardOut().trim();

        logger.debug("determineAndroidOS of device successfully: " + result);

        return Float.parseFloat(result);
    }

    public int getAndroidAppInstanceID(final String appname) {
//        PiranhaUtils.commandFailed("method not implemented");
        return 0;
    }

    public boolean checkDeviceisConnected() {
//        PiranhaUtils.commandFailed("method not implemented");
        return false;
    }

    public boolean push(final String localDir, final String remoteDir, final String fileName) {
//        PiranhaUtils.commandFailed("method not implemented");
        return false;
    }

    public boolean ping(final String host) {
//        PiranhaUtils.commandFailed("method not implemented");
        return false;
    }

    public int waitAndGetAndroidAppInstanceID(final String appname, final int MAXWaitinSeconds) {
        // TODO Auto-generated method stub
        return 0;
    }

    //    /**
    //     * adb shell am instrument -w -e className
    //     * com.citrixonline.android.presentation.UIControllerActivity -e pkgName
    //     * com.citrixonline.android.gotomeeting
    //     * com.citrixonline.android.AutomationService
    //     * /com.citrixonline.android.AutomationService.Piranha
    //     *
    //     * @param className
    //     * @param pkgName
    //     * @param timeout
    //     * @param unit
    //     * @param androidPort
    //     * @return
    //     */
    //    public void startPiranhaInstrumentation(final String className,
    //            final String pkgName, int androidPort, Long timeout, TimeUnit unit) {
    //
    //        unInstallAPK(AutomationServiceLauncher.AUTOMATIONSERVICEPKGNAME, true);
    //
    //        installAPK(new File(AutomationServiceLauncher.getServerAPKPathFromResource()));
    //
    //        // Open port for communication with pirnaha Java Client (adb forward tcp:7100
    // tcp:6100)
    //        forwardtcpPort(androidPort , AutomationServiceLauncher.AUTOMATIONSERVICEPORT);
    //
    //
    //        piranhaAndroidAutomationTask.runAsyncTask(this, className, pkgName, timeout, unit);
    //
    //        // wait for 1 second to make sure automationservice gets started
    //        try {
    //            Thread.sleep(1000);
    //        } catch (InterruptedException e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //    }
    //
    //
    //    public void stopPiranhaInstrumentation() {
    //        unInstallAPK(AutomationServiceLauncher.AUTOMATIONSERVICEPKGNAME, true);
    //        piranhaAndroidAutomationTask.terminateTask();
    //    }
}
