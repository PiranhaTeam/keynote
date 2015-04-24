package com.keynote.device;

import com.citrixonline.android.utils.IAndroidDebugBridge;
import com.citrixonline.android.utils.IAndroidSDK;
import com.citrixonline.android.utils.LocalADBUtil;
import com.citrixonline.android.utils.LocalAndroidSdk;
import com.mc.api.device.Device;



public class Main {


	
	public static void main(String[] args) {
		
		//Device.unlockAll();
		IAndroidSDK sdk = new LocalAndroidSdk();
		IAndroidDebugBridge adb = new LocalADBUtil();
		adb.killADBServer();
		
		KeyNoteDevice device = new KeyNoteDevice(DeviceType.LG);
		
		device.lock();
		adb = new LocalADBUtil(DeviceType.LG.getadbDeviceID());
		
		
		device.unlock();
	}

}
