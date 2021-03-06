/*
 * Copyright 2020 Raffaele Ragni.
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
package com.github.apilab.core;

import java.util.Set;
import javax.inject.Inject;

/**
 * Manages startup and tear down of all the instances injected in the application life cycle.
 * This is mainly used by the apilab modules to determine a start and stop context of the
 * application.
 * @author Raffaele Ragni
 */
public class ApplicationLifecycle {

  @Inject Set<ApplicationLifecycleItem> services;

  @Inject
  public ApplicationLifecycle() {
    ///
  }

  public void start() {
    services.forEach(ApplicationLifecycleItem::start);
  }

  public void stop() {
    services.forEach(ApplicationLifecycleItem::stop);
  }
}
