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
package com.github.apilab.grpc;

import com.github.apilab.core.Env;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.grpc.BindableService;
import javax.inject.Singleton;

/**
 *
 * @author Raffaele Ragni
 */
@dagger.Module
public class ComponentsModule {

  @Provides
  @IntoSet
  public BindableService service(HelloService service) {
    return service;
  }

  @Provides
  @Singleton
  public Env env() {
    return new Env();
  }
}
