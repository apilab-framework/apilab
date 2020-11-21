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
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.NettyServerBuilder;
import static java.util.Optional.ofNullable;
import java.util.Set;
import static me.dinowernli.grpc.prometheus.Configuration.cheapMetricsOnly;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@dagger.Module
public class GRPCModule {
  private static final Logger LOG = LoggerFactory.getLogger(GRPCModule.class);

  private static final MonitoringServerInterceptor INTERCEPTOR = MonitoringServerInterceptor.create(cheapMetricsOnly());

  @Provides @IntoSet ApplicationLifecycleItem lifecycle(GRPCLifecycleItem service) {
    return service;
  }

  @Provides Server server(
    Env env,
    Set<BindableService> services) {

    var builder = NettyServerBuilder.forPort(getPort(env));

    for (BindableService service: services) {
      LOG.info("## ADDING GRPC SERVICE: {}", service.getClass().getName());
      builder.addService(ServerInterceptors.intercept(service, INTERCEPTOR));
    }

    return builder.build();
  }

  private static int getPort(Env env) {
    return ofNullable(env.get(() -> "GRPC_PORT"))
      .map(Integer::valueOf)
      .orElse(9090);
  }
}
