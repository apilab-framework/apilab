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
package com.github.apilab.rest;

import com.github.apilab.core.GSONModule;
import com.github.apilab.core.Env;
import com.github.apilab.rest.auth.ImmutableAuthConfiguration;
import com.github.apilab.rest.exceptions.UnprocessableEntityException;
import com.github.apilab.rest.testmodules.DaggerApplicationComponent;
import io.javalin.Javalin;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Raffaele Ragni
 */
class JavalinModuleTest {

  @Test
  void testInjectedLifecycle() {
    var services = DaggerApplicationComponent.create().services();

    assertThat("Service is loaded", services, hasItem(isA(JavalinLifecycle.class)));
  }

  @Test
  void testJavalin() throws IOException {
    var config = new JavalinModule();
    var env = new Env();
    System.setProperty("API_ENABLE_ENDPOINTS", "false");

    config.javalin(env,
      ImmutableAuthConfiguration.builder().build(),
      new GSONModule().gson(),
      Set.of(new GetEndpointSample()),
      Map.of("test", (Supplier<Boolean>) () -> true));

    System.setProperty("API_ENABLE_ENDPOINTS", "true");

    Javalin javalin = config.javalin(env,
      ImmutableAuthConfiguration.builder().build(),
      new GSONModule().gson(),
      Set.of(new GetEndpointSample()),
      Map.of("test", (Supplier<Boolean>) () -> true));

    javalin.get("/testbad", c -> {throw new UnprocessableEntityException("");});

    javalin.start();

    try {
      OkHttpClient client = new OkHttpClient();

      Request request = new Request.Builder()
        .url("http://localhost:8080/swagger-docs")
        .build();
      Response response = client.newCall(request).execute();

      assertThat("Response code is 200", response.code(), is(200));

      request = new Request.Builder()
        .url("http://localhost:8080/testbad")
        .build();
      response = client.newCall(request).execute();

      assertThat("Response code is 422", response.code(), is(422));
    }
    finally {
      javalin.stop();
    }
  }

  @Test
  void testInitializerDefaultValue() {
    var initializer = ImmutableAuthConfiguration.builder().build();
    var fn = initializer.roleMapper();
    assertThat("Mapping string straight away", fn.apply("test").toString(), is("test"));
  }

}
