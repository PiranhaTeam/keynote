package com.keynote.device;

import com.mc.api.device.Device;



public class Main {


	
	public static void main(String[] args) {
		
		Device.unlockAll();
		KeyNoteDevice device = new KeyNoteDevice(DeviceType.LG);
		device.lock();


	}

}
