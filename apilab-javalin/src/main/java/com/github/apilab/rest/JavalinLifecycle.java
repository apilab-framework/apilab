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
package com.github.apilab.rest;

import com.github.apilab.core.Env;
import io.javalin.Javalin;
import java.util.Optional;
import javax.inject.Inject;
import com.github.apilab.core.ApplicationLifecycleItem;

/**
 *
 * @author Raffaele Ragni
 */
public class JavalinLifecycle implements ApplicationLifecycleItem {

  @Inject
  public JavalinLifecycle() {
    ////
  }

  @Inject Javalin javalin;
  @Inject Env env;

  @Override
  public void start() {
    if (!enabled()) {
      return;
    }
    javalin.start();
  }

  @Override
  public void stop() {
    if (!enabled()) {
      return;
    }
    javalin.stop();
  }

  private boolean enabled() {
    return Optional.ofNullable(env.get(() -> "API_ENABLE_ENDPOINTS"))
       .map(Boolean::valueOf)
       .orElse(false);
  }

}
