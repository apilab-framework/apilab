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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Raffaele Ragni
 */
class JavalinLifecycleTest {

  JavalinLifecycle service;

  @BeforeEach
  void startup() {
    service = new JavalinLifecycle();
    service.javalin = mock(Javalin.class);
    service.env = new Env();
  }

  @Test
  void testDoesNotStartIfDisabled() {
    System.setProperty("API_ENABLE_ENDPOINTS", "false");

    service.start();
    verify(service.javalin, times(0)).start();

    service.stop();
    verify(service.javalin, times(0)).stop();
  }

  @Test
  void testDoesStartIfEnabled() {
    System.setProperty("API_ENABLE_ENDPOINTS", "true");

    service.start();
    verify(service.javalin).start();

    service.stop();
    verify(service.javalin).stop();
  }
}
