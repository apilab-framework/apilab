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
package com.github.apilab.prometheus;

import com.github.apilab.core.Env;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Raffaele Ragni
 */
class PrometheusTest {

  @Test
  void testMetrics() {

    var lifecycle = new PrometheusLifecycle();
    lifecycle.env = new Env();

    System.setProperty("API_ENABLE_PROMETHEUS", "false");

    lifecycle.start();
    lifecycle.stop();
    lifecycle.start();
    assertThat("metrics server is not started", lifecycle.metricServer, is(nullValue()));


    System.setProperty("API_ENABLE_PROMETHEUS", "true");

    lifecycle.stop();
    lifecycle.start();
    assertThat("metrics server is in the end started", lifecycle.metricServer, is(not(nullValue())));

    lifecycle.stop();

    assertThat("metrics server is in the end stopped", lifecycle.metricServer, is(nullValue()));

    lifecycle.start();
    lifecycle.start();
    assertThat("metrics server is in the end started", lifecycle.metricServer, is(not(nullValue())));

    lifecycle.stop();

    assertThat("metrics server is in the end stopped", lifecycle.metricServer, is(nullValue()));
  }
}
