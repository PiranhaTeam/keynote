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
 * This is exception is called when the SDK path is not valid and is caught as an assertion error
 * by TestNG.
 */
public class InvalidSdkException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6816359484951469194L;

    public InvalidSdkException(final String parameterMessage) {
    	System.out.println(parameterMessage);
    }
}
