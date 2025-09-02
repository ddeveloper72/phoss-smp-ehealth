/*
 * Copyright (C) 2019-2025 Philip Helger and contributors
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.phoss.smp.standalone;

import java.io.File;

import com.helger.photon.jetty.JettyStarter;

/**
 * Run this as an application and your SMP will be up and running on port 90 (to
 * not interfere with anything already running on port 80) of your local
 * machine. Please ensure that you have adopted the DB configuration file.<br>
 * To stop the running Jetty simply invoke the
 * {@link JettyStopSMPSERVER_MONGODB} application in this package. It performs a
 * graceful shutdown of the App Server.
 *
 * @author Philip Helger
 */
public final class RunInJettySMPSERVER_MONGODB
{
  public static void main (final String... args) throws Exception
  {
    // For Heroku deployment, skip the pom.xml check
    final String skipPomCheck = System.getenv("SKIP_POM_CHECK");
    if (skipPomCheck == null || !skipPomCheck.equals("true")) {
      if (!new File ("pom.xml").exists ())
        throw new IllegalStateException ("Please make sure your working directory is the directory containing 'pom.xml'");
    }

    // Get port from environment variable (for Heroku) or use default
    String portStr = System.getenv("PORT");
    int port = 90; // Default port
    if (portStr != null && !portStr.trim().isEmpty()) {
      try {
        port = Integer.parseInt(portStr);
      } catch (NumberFormatException e) {
        System.err.println("Invalid PORT environment variable: " + portStr + ", using default port 90");
      }
    }

    new JettyStarter (RunInJettySMPSERVER_MONGODB.class).setPort (port)
                                                        .setStopPort (x -> x + 1000)
                                                        .setSessionCookieName ("SMPSESSION")
                                                        .run ();
  }
}
