package com.keynote.device;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.tools.ant.types.Commandline;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;

import com.citrixonline.android.utils.IAndroidDebugBridge;
import com.citrixonline.android.utils.IAndroidSDK;
import com.citrixonline.android.utils.LocalADBUtil;
import com.citrixonline.android.utils.LocalAndroidSdk;
import com.mc.api.device.Device;



public class Main {

	
	static HelpFormatter formatter = new HelpFormatter();
	static CommandLineParser parser = new BasicParser();
	static Options options = new Options();
	static DeviceType model;
	private static KeyNoteDevice device;
	
	public static void main(String[] args) throws InterruptedException {
				
	parseArgs(args);
		
	createADBTerminal(model);

	createShutDownHook();
	while(true){
		 Thread.sleep(1000);
	}
	}

	private static void parseArgs(String[] args) {
		options.addOption("d", "device" , true, "can be 'LG' or 'SAMSUNG'");

		try {
			CommandLine line = parser.parse(options, args);
	        	        
	        if( line.hasOption( "d" ) ) {
	        		model = DeviceType.valueOf(line.getOptionValue("d"));
	        		System.out.println( "Model : " + model.getDisplayName() );
	        } else {
				printHelp();
			}
	        
		} catch (ParseException exp) {
			System.out.println("Unexpected exception : " + exp.getMessage());
			printHelp();
		}
	}


	private static void createADBTerminal(DeviceType deviceModel) {
		IAndroidDebugBridge adb = new LocalADBUtil();
		adb.killADBServer();
		
		device = new KeyNoteDevice(deviceModel);
		
		device.lock();
		
		
		adb = new LocalADBUtil(deviceModel.getadbDeviceID());
//		System.out.println("Device Connected: " + adb.getAllDevicesConnected().toString());
		List<String> devices = Arrays.asList(adb.getAllDevicesConnected());
		if(devices.contains(deviceModel.getadbDeviceID())){
			System.out.println("--------------------------------------------");
			System.out.println(String.format("%s Device with id '%s' has been successfully acquired. " , deviceModel.getDisplayName(), deviceModel.getadbDeviceID()));
			System.out.println("--------------------------------------------");
		} else {
			System.out.println(String.format("%s Device with id '%s' = FAILED TO ACQUIRE :(  " , deviceModel.getDisplayName(), deviceModel.getadbDeviceID()));	
		}
	}
	
	private static void printHelp(){
		formatter.printHelp( "keynote", options );	
		System.exit(1);
	}
	
	private static void createShutDownHook()
	{
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	        	
	        		device.unlock();
	            System.out.println();
	            System.out.println("Thanks for using the application");
	            System.out.println("Exiting...");

	        }
	    }));
	}

}
