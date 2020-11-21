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
package com.github.apilab.barehttp;

import com.github.apilab.core.Env;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.inject.Singleton;

@dagger.Module
public class ComponentsModule {

  @Provides @IntoMap @StringKey("/") HttpHandler rootHandler() {
    return ctx -> {
      try (var out = ctx.getResponseBody()) {
        out.write("Default response".getBytes(UTF_8));
      }
    };
  }
  
  @Provides
  @Singleton
  public Env env() {
    return new Env();
  }
}
