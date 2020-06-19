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
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Raffaele Ragni
 */
public class ExecutorServiceTest {

  ApplicationExecutorLifecycle service;

  // BeforeEach just doesn't work, have no time to deal with people's libraries while maintaining my own.
  public void startup() {
    service = new ApplicationExecutorLifecycle();
    service.applicationScheduler = mock(ApplicationScheduler.class);
    service.env = new Env();
  }

  @Test
  public void testDoesNotStartIfDisabled() {
    startup();
    System.setProperty("API_ENABLE_SCHEDULED", "false");

    service.start();
    verify(service.applicationScheduler, times(0)).start();

    service.stop();
    verify(service.applicationScheduler, times(0)).stop();
  }

  @Test
  public void testDoesStartIfEnabled() {
    startup();
    System.setProperty("API_ENABLE_SCHEDULED", "true");

    service.start();
    verify(service.applicationScheduler).start();

    service.stop();
    verify(service.applicationScheduler).stop();
  }
}
