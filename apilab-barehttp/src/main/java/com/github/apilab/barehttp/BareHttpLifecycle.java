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
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BareHttpLifecycle implements ApplicationLifecycleItem {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationLifecycleItem.class);
  
  @Inject Env env;
  @Inject Map<String, HttpHandler> handlers;
  HttpServer server;

  @Inject
  public BareHttpLifecycle() {
    // injected
  }

  @Override
  public void start() {
    if (enabled(env) && server == null) {
      var port = getPort(env);
      var backlog = getBacklog(env);
      LOG.info("## STARTING BARE HTTP SERVER FROM SUN, port: {}, backlog/threads: {}", port, backlog);
      try {
        server = HttpServer.create(new InetSocketAddress(port), backlog);
        handlers.entrySet().forEach(e -> {
          LOG.info("## REGISTERING BARE HTTP SERVER HANDLER: '{}'", e.getKey());
          server.createContext(e.getKey(), e.getValue());
        });
        server.setExecutor(Executors.newFixedThreadPool(backlog));
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
