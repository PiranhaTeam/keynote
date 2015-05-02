package com.keynote.device;

public enum DeviceType {
	

	LG("LG" , 7846 , "01b9565913d00ca1"),
	SAMSUNG("SAMSUNG" , 8968 , "233d9801");
	
	private String displayName;
	private int mcd;
	private String adbDeviceID;

	private DeviceType(String displayName , int MCD , String adbDeviceID) {
	      this.displayName = displayName;
	      this.mcd = MCD;
	      this.adbDeviceID = adbDeviceID;
	 }

	 	public String getDisplayName() {
	      return displayName;
	    }

		public int getMCD() {
			return mcd;	
		}
		
		public String getadbDeviceID() {
			return adbDeviceID;	
		}

		public boolean equalsIgnoreCase(final String value){
		    return this.getDisplayName().equalsIgnoreCase(value);
		}
}
