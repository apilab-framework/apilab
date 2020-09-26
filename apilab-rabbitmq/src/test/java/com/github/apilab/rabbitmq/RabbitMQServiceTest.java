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
package com.github.apilab.rabbitmq;

import com.github.apilab.core.Env;
import com.github.apilab.queues.QueueService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Raffaele Ragni
 */
class RabbitMQServiceTest {

  QueueLifecycle service;
  QueueService<String> queueService;

  @BeforeEach
  void startup() {
    queueService = mock(QueueService.class);
    service = new QueueLifecycle();
    service.queueServices = Set.of(queueService);
    service.env = new Env();
  }

  @Test
  void testDoesNotStartIfDisabled() {
    System.setProperty("API_ENABLE_CONSUMERS", "false");

    service.start();
    verify(queueService, times(0)).registerQueueListener();

    service.stop();
    verify(queueService, times(0)).unregisterQueueListener();
  }

  @Test
  void testDoesStartIfEnabled() {
    System.setProperty("API_ENABLE_CONSUMERS", "true");

    service.start();
    verify(queueService).registerQueueListener();

    service.stop();
    verify(queueService).unregisterQueueListener();
  }
}
