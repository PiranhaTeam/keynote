package com.keynote.device;

import com.mc.api.device.Device;
import com.mc.api.device.exception.NoSuchDeviceException;

public class KeyNoteDevice {

	Device device;
	private DeviceType deviceType;

	
	public KeyNoteDevice(DeviceType d) {
		super();
		this.deviceType = d;
		try {
			device = Device.get(deviceType.getMCD());
		} catch (NoSuchDeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void lock(){
		device.lock();
		try {
			device.connectAdbTunnel();
		} catch (NoSuchDeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unlock(){
		device.unlock();
		try {
			device.terminateAdbTunnel();
		} catch (NoSuchDeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
