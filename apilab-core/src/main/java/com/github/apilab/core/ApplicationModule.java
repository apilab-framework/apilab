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
package com.github.apilab.core;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import dagger.Provides;
import java.util.ServiceLoader;
import javax.inject.Singleton;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;

/**
 *
 * @author Raffaele Ragni
 */
@dagger.Module
public class ApplicationModule {

  public @Provides @Singleton Gson gson() {
    var builder = new GsonBuilder();
    builder = Converters.registerAll(builder);
    builder = builder.registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory());
    for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
      builder.registerTypeAdapterFactory(factory);
    }
    return builder.create();
  }

}
