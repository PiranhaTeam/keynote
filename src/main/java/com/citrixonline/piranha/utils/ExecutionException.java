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

package com.citrixonline.piranha.utils;


/**
 * This is exception is called whenever a command to remote host is failed, in the end
 * a assertion Error is thrown which is caught by testNg.
 */
public class ExecutionException extends Exception {
    /**
     * Generated serial version UID.
     */
    static final long serialVersionUID = -7843278034782074384L;

    /**
     * Constructs an <code>ExecutionException</code>  with no exception message.
     */
    public ExecutionException(final String host) {
        super();
//        PiranhaUtils.commandFailed(new PiranhaException(host, "Execution Exception"));
    }

    /**
     * Constructs an <code>ExecutionException</code> with the specified exception message.
     *
     * @param message the exception message
     */
    public ExecutionException(final String host, final String message) {
        super(message);
//        PiranhaUtils.commandFailed(new PiranhaException(host, message));
    }

    /**
     * Constructs an <code>ExecutionException</code> with the specified exception message and cause
     * of the exception.
     *
     * @param message the exception message
     * @param cause   the cause of the exception
     */
    public ExecutionException(final String host, final String message, final Throwable cause) {
        super(message, cause);
 //       PiranhaUtils.commandFailed(new PiranhaException(host , message, cause));
    }

}

