<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> remove both of gwt-representer and gwt-dispatch dependencies, add license headers to all new files
/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

<<<<<<< HEAD
package org.apache.hupa.client.ioc;

import org.apache.hupa.client.HupaController;
=======
=======
>>>>>>> remove both of gwt-representer and gwt-dispatch dependencies, add license headers to all new files
package org.apache.hupa.client.ioc;

<<<<<<< HEAD
<<<<<<< HEAD
import org.apache.hupa.client.AppController;
>>>>>>> Change to new mvp framework - first step
=======
import org.apache.hupa.client.evo.AppController;
>>>>>>> Make the evo more clear.
=======
package org.apache.hupa.client.ioc;

import org.apache.hupa.client.AppController;
>>>>>>> Change to new mvp framework - first step
=======
import org.apache.hupa.client.evo.AppController;
>>>>>>> Make the evo more clear.

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * This is the application ginjector.
 * It defines the method our EntryPoint will use to initialize GIN dependecy graph
 * and the GIN module where binding is defined
 */
@GinModules(AppGinModule.class)
public interface AppGinjector extends Ginjector {
<<<<<<< HEAD
<<<<<<< HEAD
//  AppController getAppController();
  HupaController getHupaController();
=======
  AppController getAppController();
>>>>>>> Change to new mvp framework - first step
=======
  AppController getAppController();
>>>>>>> Change to new mvp framework - first step
}
