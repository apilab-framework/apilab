/*
 * Copyright 2020 r.ragni.
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

import com.github.apilab.core.ApplicationLifecycleItem;
import com.github.apilab.core.Env;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import javax.inject.Inject;

public class BareHttpLifecycle implements ApplicationLifecycleItem {

  @Inject Env env;
  HttpServer server;

  @Inject
  public BareHttpLifecycle() {
    // injected
  }

  @Override
  public void start() {
    if (enabled(env) && server == null) {
      try {
        server = HttpServer.create(new InetSocketAddress(getPort(env)), getBacklog(env));
        server.setExecutor(null);
        server.start(); } catch (IOException ex) { throw new IllegalStateException(ex.getMessage(), ex); }
    }
  }

  @Override
  public void stop() {
    if (enabled(env) && server != null) {
      server.stop(1);
      server = null;
    }
  }

  private static boolean enabled(Env env) {
    return Optional.ofNullable(env.get(() -> "API_ENABLE_BAREHTTP"))
        .map(Boolean::valueOf)
        .orElse(false);
  }

  private static int getPort(Env env) {
    return ofNullable(env.get(() -> "API_BAREHTTP_PORT"))
      .map(Integer::valueOf)
      .orElse(8000);
  }

  private static int getBacklog(Env env) {
    return ofNullable(env.get(() -> "API_BAREHTTP_BACKLOG"))
      .map(Integer::valueOf)
      .orElse(100);
  }
}
