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
package com.github.apilab.rabbitmq;

import com.github.apilab.core.Env;
import com.github.apilab.queues.QueueService;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.apilab.core.ApplicationLifecycleItem;

/**
 *
 * @author Raffaele Ragni
 */
public class QueueLifecycle implements ApplicationLifecycleItem {

  private static final Logger LOG = LoggerFactory.getLogger(QueueLifecycle.class);

  @Inject Env env;
  @Inject ConnectionFactory rabbitConnectionFactory;
  @Inject Set<QueueService> queueServices;

  @Inject
  public QueueLifecycle() {
    ////
  }

  @Override
  public void start() {
    if (enabledConsumers()) {
      LOG.info("## CONSUMERS ENABLED");
      queueServices.stream().forEach(l -> {
        LOG.info("## CONSUMERS Registering {}", l.getClass().getName());
        l.registerQueueListener();
      });
    }
  }

  @Override
  public void stop() {
    if (enabledConsumers()) {
      queueServices.stream().forEach(QueueService::unregisterQueueListener);
    }
  }

  private boolean enabledConsumers() {
    return Optional.ofNullable(env.get(() -> "API_ENABLE_CONSUMERS"))
      .map(Boolean::valueOf)
      .orElse(false);
  }

}
