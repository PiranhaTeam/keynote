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

/*
 * Copyright (C) 2009 Jayway AB
 * Copyright (C) 2007-2008 JVending Masa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citrixonline.piranha.utils;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Original version from Maven Android Plugin, Refactored for automation usage
 * added AFT executor to the factory.
 *
 */
public interface CommandExecutor {
    /**
     * Sets the plexus logger.
     *
     * @param logger the plexus logger
     */
    void setLogger(Logger logger);

    /**
     * Executes the command for the specified executable and list of command options.
     *
     * @param executable the name of the executable (csc, xsd, etc).
     * @param commands   the command options for the compiler/executable
     * @throws ExecutionException if compiler or executable writes anything to the standard error
     * stream or if the process
     *                            returns a process result != 0.
     */
    void executeCommand(String executable, List<String> commands)
            throws ExecutionException;

    /**
     * Executes the command for the specified executable and list of command options.
     *
     * @param executable         the name of the executable (csc, xsd, etc).
     * @param commands           the commands options for the compiler/executable
     * @param failsOnErrorOutput if true, throws an <code>ExecutionException</code> if there the
     * compiler or executable writes anything to the error output stream. By default, this value is
     * true
     * @throws ExecutionException if compiler or executable writes anything to the standard error
     * stream (provided the failsOnErrorOutput is not false) or if the process returns a process
     * result != 0.
     */
    void executeCommand(String executable, List<String> commands, boolean failsOnErrorOutput)
            throws ExecutionException;

    /**
     * Executes the command for the specified executable and list of command options. If the
     * compiler or executable is not within the environmental path, you should use this method to
     * specify the working directory. Always use this method for executables located within the
     * local maven repository.
     *
     * @param executable       the name of the executable (csc, xsd, etc).
     * @param commands         the command options for the compiler/executable
     * @param workingDirectory the directory where the command will be executed
     * @throws ExecutionException if compiler or executable writes anything to the standard error
     * stream (provided the failsOnErrorOutput is not false) or if the process returns a process
     * result != 0.
     */
    void executeCommand(String executable, List<String> commands, File workingDirectory,
            boolean failsOnErrorOutput) throws ExecutionException;

    /**
     * Returns the process result of executing the command. Typically a value of 0 means that the
     * process executed successfully.
     *
     * @return the process result of executing the command
     */
    int getResult();

    /**
     * Get the process id for the executed command.
     *
     * @return {@link Long}
     */
    long getPid();


    /**
     * Returns the standard output from executing the command.
     *
     * @return the standard output from executing the command
     */
    String getStandardOut();

    /**
     * Returns the standard error from executing the command.
     *
     * @return the standard error from executing the command
     */
    String getStandardError();

    /**
     * Provides factory services for creating a default instance of the command executor.
     */
    public static class Factory {

        /**
         * Constructor.
         */
        private Factory() {
        }

        /**
         * Returns a default instance of the command executor.
         *
         * @return a default instance of the command executor
         */
        public static CommandExecutor createLocalCommmandExecutor() {
            return new CommandExecutor() {
                /**
                 * Instance of a plugin logger.
                 */
                private Logger logger = Logger.getLogger(Factory.class);

                /**
                 * Standard Out
                 */
                private StreamConsumer stdOut;

                /**
                 * Standard Error
                 */
                private ErrorStreamConsumer stdErr;

                /**
                 * Process result
                 */
                private int result;

                public void setLogger(final Logger logger) {
                    this.logger = logger;
                }

                long pid;

                private Commandline commandline;

                public void executeCommand(final String executable, final List<String> commands)
                        throws ExecutionException {
                    executeCommand(executable, commands, null, true);
                }

                public void executeCommand(final String executable, final List<String> commands,
                        final boolean failsOnErrorOutput) throws ExecutionException {
                    executeCommand(executable, commands, null, failsOnErrorOutput);
                }

                public void executeCommand(final String executable, List<String> commands,
                        final File workingDirectory, final boolean failsOnErrorOutput)
                                throws ExecutionException {
                    if (commands == null) {
                        commands = new ArrayList<String>();
                    }
                    stdOut = new StreamConsumerImpl();
                    stdErr = new ErrorStreamConsumer();

                    commandline = new Commandline();
                    commandline.setExecutable(executable);
                    commandline.addArguments(commands.toArray(new String[commands.size()]));
                    if (workingDirectory != null && workingDirectory.exists()) {
                        commandline.setWorkingDirectory(workingDirectory.getAbsolutePath());
                    }
                    try {
                        logger.info("LOCALCOMMANDEXECUTOR: Executing command: Commandline = "
                                + commandline);
                        result = CommandLineUtils.executeCommandLine(commandline, stdOut, stdErr);
                        if (logger != null) {
                            logger.debug("LOCALCOMMANDEXECUTOR: Executed command: Commandline = "
                                    + commandline + ", Result = " + result);
                        } else {
                            logger.debug("LOCALCOMMANDEXECUTOR: Executed command: Commandline = "
                                    + commandline + ", Result = " + result);
                        }
                        if ((failsOnErrorOutput && stdErr.hasError()) || result != 0) {
                            throw new ExecutionException(
                                    "LOCALCOMMANDEXECUTOR: Could not execute: Command = "
                                            + commandline.toString() + ", Result = " + result);
                        }
                    } catch (CommandLineException e) {
                        throw new ExecutionException(
                                "LOCALCOMMANDEXECUTOR: Could not execute: Command = "
                                        + commandline.toString() + ", Error message = "
                                        + e.getMessage());
                    }
                    setPid(commandline.getPid());
                }

                public int getResult() {
                    return result;
                }

                public String getStandardOut() {
                    return stdOut.toString();
                }

                public String getStandardError() {
                    return stdErr.toString();
                }


                public void setPid(final long pid) {
                    this.pid = pid;
                }

                public long getPid() {
                    return pid;
                }

                /**
                 * Provides behavior for determining whether the command utility wrote anything to
                 * the Standard Error Stream.
                 * NOTE: I am using this to decide whether to fail the NMaven build. If the compiler
                 * implementation chooses to write warnings to the error stream, then the build
                 * will fail on warnings!!!
                 */
                class ErrorStreamConsumer
                implements StreamConsumer {

                    /**
                     * Is true if there was anything consumed from the stream, otherwise false
                     */
                    private boolean error;

                    /**
                     * Buffer to store the stream
                     */
                    private StringBuffer sbe = new StringBuffer();

                    public ErrorStreamConsumer() {
                        if (logger == null) {
                            System.out.println(
                                    "LOCALCOMMANDEXECUTOR: Error Log not set: "
                                            + "Will not output error logs");
                        }
                        error = false;
                    }

                    public void consumeLine(final String line) {
                        sbe.append(line);
                        if (logger != null) {
                            logger.debug(line);
                        }
                        error = true;
                    }

                    /**
                     * Returns false if the command utility wrote to the Standard Error Stream,
                     * otherwise returns true.
                     *
                     * @return false if the command utility wrote to the Standard Error Stream,
                     * otherwise returns true.
                     */
                    public boolean hasError() {
                        return error;
                    }

                    /**
                     * Returns the error stream
                     *
                     * @return error stream
                     */
                    public String toString() {
                        return sbe.toString();
                    }
                }

                /**
                 * StreamConsumer instance that buffers the entire output
                 */
                class StreamConsumerImpl
                implements StreamConsumer {

                    private DefaultConsumer consumer;

                    private StringBuffer sb = new StringBuffer();

                    public StreamConsumerImpl() {
                        consumer = new DefaultConsumer();
                    }

                    public void consumeLine(final String line) {
                        sb.append(line);
                        if (logger != null) {
                            consumer.consumeLine(line);
                        }
                    }

                    /**
                     * Returns the stream
                     *
                     * @return the stream
                     */
                    public String toString() {
                        return sb.toString();
                    }
                }
            };

        }



    }
}
