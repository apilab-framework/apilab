/*
 * Copyright 2019 Raffaele Ragni.
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
package com.github.apilabframework.apilab.prometheus;

import com.github.apilab.core.ApplicationLifecycleItem;
import com.github.apilab.core.Env;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import static java.util.Optional.ofNullable;
import javax.inject.Inject;

/**
 *
 * @author Raffaele Ragni
 */
public class PrometheusLifecycle implements ApplicationLifecycleItem {

  HTTPServer metricServer;
  @Inject Env env;

  @Inject
  public PrometheusLifecycle() {
    ///
  }

  /**
   * Starts the metrics server.
   * Port is set via env PROMETHEUS_PORT.
   */
  @Override
  public void start() {
    ensureServerIsStopped();
    try { metricServer = new HTTPServer(getPrometheusPort(env)); } catch (IOException ex) { throw new IllegalStateException(ex.getMessage(), ex); }
  }

  /**
   * Stops the metrics server, but only if it was started already.
   */
  @Override
  public void stop() {
    ensureServerIsStopped();
    metricServer = null;
  }

  private void ensureServerIsStopped() {
    if (metricServer != null) {
      metricServer.stop();
    }
  }

  private static int getPrometheusPort(Env env) {
    return ofNullable(env.get(() -> "API_PROMETHEUS_PORT"))
              .map(Integer::valueOf)
              .orElse(7080);
  }

}
