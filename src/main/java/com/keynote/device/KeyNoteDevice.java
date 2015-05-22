package com.keynote.device;

import com.mc.api.common.GlobalContext;
import com.mc.api.device.Device;
import com.mc.api.device.exception.DeviceExecutionException;
import com.mc.api.device.exception.NoSuchDeviceException;
import com.mc.api.device.helper.*;
import com.mc.obmodel.KeyDevicePattern;

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

	public void wakeup() throws DeviceExecutionException {
		final String WAKE_UP_KEY = "__WakeUp__";
		final com.mc.obmodel.DeviceType deviceType = device.getDataObject().getDeviceType();

		// Find a key mapping with wake up sequence. Once it is found,
		// set key mode it was found in
		String keyMode = null;

		for (KeyDevicePattern pattern : deviceType.getKeyDevicePatterns()) {
			if (WAKE_UP_KEY.equals(pattern.getKeyVirtualID())) {
				keyMode = pattern.getKeyModeID();
				break;
			}
		}

		if (keyMode != null) {

			KeysHelper keysHelper = new KeysHelper();
			keysHelper.setHoldTime(100);
			keysHelper.setDelayTime(100);
			device.sendKeys(
					"[" + WAKE_UP_KEY + "]", KeyMode.fromString(keyMode), keysHelper); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}
}
