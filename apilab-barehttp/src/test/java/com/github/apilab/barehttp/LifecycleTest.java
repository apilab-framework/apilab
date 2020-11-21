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

import com.github.apilab.core.ApplicationLifecycle;
import com.github.apilab.core.ApplicationLifecycleItem;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LifecycleTest {
  ApplicationLifecycle app;
  Set<ApplicationLifecycleItem> services;

  @BeforeEach
  void setup() {
    var dagger = DaggerApplicationComponent.create();
    app = dagger.instance();
    services = dagger.services();
  }

  @Test
  void testInjection() {
    assertThat(services.size(), is(1));
    assertThat(services.iterator().next().getClass(), is(BareHttpLifecycle.class));
  }

  @Test
  void testOpenSockDisabled() throws InterruptedException {
    System.setProperty("API_ENABLE_BAREHTTP", "false");
    app.stop();
    app.start();
    app.start();
    assertThat(availablePort(8000), is(true));

    app.stop();
    assertThat(availablePort(8000), is(true));
  }

  @Test
  void testOpenSockEnabled() throws InterruptedException {
    System.setProperty("API_ENABLE_BAREHTTP", "true");
    app.stop();
    app.start();
    app.start();
    assertThat(availablePort(8000), is(false));

    app.stop();
    assertThat(availablePort(8000), is(true));
  }

  static boolean availablePort(int port) {
    try (var sock = new Socket("127.0.0.1", port)) {
      return false;
    } catch (IOException | RuntimeException e) {
      return true;
    }
  }
}
