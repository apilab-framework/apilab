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
package com.github.apilab.grpc;

import com.github.apilab.core.ApplicationLifecycleItem;
import com.github.apilab.core.Env;
import io.grpc.Server;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GRPCLifecycleItem implements ApplicationLifecycleItem {

  private static final Logger LOG = LoggerFactory.getLogger(GRPCLifecycleItem.class);

  @Inject Server server;
  @Inject Env env;

  @Inject
  public GRPCLifecycleItem() {
    // Injection
  }

  @Override
  public void start() {
    try {
      if (enabled(env)) {
        LOG.info("## GRPC ENABLED");
        server.start();
      }
    } catch (IOException ex) {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }

  @Override
  public void stop() {
    if (enabled(env))
      server.shutdown();
  }

  private static boolean enabled(Env env) {
    return Optional.ofNullable(env.get(() -> "API_ENABLE_GRPC"))
        .map(Boolean::valueOf)
        .orElse(false);
  }
}
