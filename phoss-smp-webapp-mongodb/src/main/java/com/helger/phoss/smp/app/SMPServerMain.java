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
package com.helger.phoss.smp.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for running phoss SMP MongoDB in production environments like Heroku
 * 
 * @author Duncan Wright
 */
public final class SMPServerMain 
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SMPServerMain.class);

  private SMPServerMain() 
  {
    // Utility class
  }

  public static void main(final String[] args) throws Exception 
  {
    // Get port from environment variable (for Heroku) or use default
    String portStr = System.getenv("PORT");
    int port = 8080; // Default port
    if (portStr != null && !portStr.trim().isEmpty()) {
      try {
        port = Integer.parseInt(portStr);
        LOGGER.info("Using port from environment: {}", port);
      } catch (NumberFormatException e) {
        LOGGER.warn("Invalid PORT environment variable: {}, using default port 8080", portStr);
      }
    } else {
      LOGGER.info("No PORT environment variable found, using default port 8080");
    }

    // Create Jetty server
    final Server server = new Server(port);
    
    // Set up web application context - use current JAR as the web application
    final WebAppContext webAppContext = new WebAppContext();
    webAppContext.setContextPath("/");
    
    // Get the path to the currently running JAR file
    String jarPath = SMPServerMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    LOGGER.info("Using JAR file as web application: {}", jarPath);
    webAppContext.setWar(jarPath);
    
    server.setHandler(webAppContext);

    // Start server
    LOGGER.info("Starting phoss SMP server on port {}", port);
    server.start();
    LOGGER.info("phoss SMP server started successfully");
    
    // Wait for server to be stopped
    server.join();
  }
}
