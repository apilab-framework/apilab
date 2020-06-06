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
package com.github.apilab.rest.testmodules;

import com.github.apilab.core.Env;
import com.github.apilab.rest.Endpoint;
import com.github.apilab.rest.GetEndpointSample;
import com.github.apilab.rest.ImmutableRESTInitializer;
import com.github.apilab.rest.RESTInitializer;
import com.google.gson.Gson;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.StringKey;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 *
 * @author Raffaele Ragni
 */
@dagger.Module
public class ComponentsModule {

  @Provides
  @IntoSet
  public Endpoint endpoints() {
    return new GetEndpointSample();
  }

  @Provides
  @Singleton
  public Gson gson() {
    return new Gson();
  }

  @Provides
  @Singleton
  @Named("healthChecks")
  @IntoMap
  @StringKey("value")
  public Supplier<Boolean> value() {
    return () -> true;
  }

  @Provides
  @Singleton
  public Env env() {
    return new Env();
  }

  @Provides
  @Singleton
  public RESTInitializer initializer() {
    return ImmutableRESTInitializer.builder().build();
  }
}
