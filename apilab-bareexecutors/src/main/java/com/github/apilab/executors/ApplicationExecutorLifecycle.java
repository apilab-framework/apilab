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
package com.github.apilab.executors;

import com.github.apilab.core.Env;
import java.util.Optional;
import javax.inject.Inject;
import com.github.apilab.core.ApplicationLifecycleItem;

/**
 *
 * @author Raffaele Ragni
 */
public class ApplicationExecutorLifecycle implements ApplicationLifecycleItem {

  @Inject Env env;
  @Inject ApplicationScheduler applicationScheduler;

  @Inject
  public ApplicationExecutorLifecycle() {
    ////
  }

  @Override
  public void start() {
    if (enableExecutors()) {
      applicationScheduler.start();
    }
  }

  @Override
  public void stop() {
    if (enableExecutors()) {
      applicationScheduler.stop();
    }
  }

  public boolean enableExecutors() {
    return Optional.ofNullable(env.get(() -> "API_ENABLE_SCHEDULED"))
      .map(Boolean::valueOf)
      .orElse(false);
  }

}
