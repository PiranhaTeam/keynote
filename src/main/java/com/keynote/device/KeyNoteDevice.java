package com.keynote.device;

import com.mc.api.common.GlobalContext;
import com.mc.api.device.Device;
import com.mc.api.device.exception.NoSuchDeviceException;

public class KeyNoteDevice {

	Device device;
	private DeviceType deviceType;
	private GlobalContext context;

	
	public KeyNoteDevice(DeviceType d) {
		super();
		this.deviceType = d;
		try {
			device = Device.get(deviceType.getMCD());
		} catch (NoSuchDeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context = GlobalContext.getGlobalContext();
	}
	
	public void lock(){
		device.lock();
		device.forceDeviceStatus();
		try {
			Thread.sleep(3*1000);
			device.connectAdbTunnel();
			
		} catch (NoSuchDeviceException | InterruptedException e) {
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
		if(context != null)
			context.shutdown();
		else{
			context = GlobalContext.getGlobalContext();
			context.shutdown();
		}
			
	}
}
