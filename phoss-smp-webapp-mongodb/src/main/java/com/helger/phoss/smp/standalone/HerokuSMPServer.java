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

import com.helger.commons.string.StringHelper;
import com.helger.photon.jetty.JettyStarter;

/**
 * Heroku-compatible SMP server that reads port from environment variable
 * 
 * @author Duncan Wright
 */
public final class HerokuSMPServer
{
  public static void main (final String... args) throws Exception
  {
    // Get port from environment variable (Heroku sets this)
    String portStr = System.getenv("PORT");
    if (StringHelper.hasNoText(portStr)) {
      portStr = "8080"; // Default port
    }
    final int port = Integer.parseInt(portStr);
    
    // Start the Jetty server
    new JettyStarter (HerokuSMPServer.class).setPort (port)
                                           .setStopPort (x -> x + 1000)  
                                           .setSessionCookieName ("SMPSESSION")
                                           .run ();
  }
}
