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
package com.github.apilab.rest;

import com.auth0.jwt.algorithms.Algorithm;
import com.github.apilab.core.ApplicationService;
import com.github.apilab.core.Env;
import com.github.apilab.exceptions.ApplicationException;
import com.github.apilab.rest.auth.ImmutableConfiguration;
import com.github.apilab.rest.auth.JavalinJWTAccessManager;
import com.github.apilab.rest.auth.JavalinJWTFilter;
import com.google.gson.Gson;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.StringKey;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.plugin.openapi.InitialConfigurationCreator;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 *
 * @author Raffaele Ragni
 */
@dagger.Module
public class RESTModule {

  private static final String HEADER_REQUEST_UUID = "X-APP-Request-UUID";
  // This property name will appear as is in log key/value
  private static final String MDC_REQUEST_UUID = "request_uuid";

  private static final Logger LOG = LoggerFactory.getLogger(RESTModule.class);

  @Provides @IntoSet ApplicationService service(RESTService service) {
    return service;
  }

  @Provides
  @Singleton
  @Named("healthChecks")
  @IntoMap
  @StringKey("javalin")
  public Supplier<Boolean> apiHealthCheck() {
    // This is used to fill in the map so that it is not empty
    // when the user uses the api only.
    return () -> true;
  }

  @Provides @Singleton
  public Javalin javalin(
      Env env,
      RESTInitializer initializer,
      Gson gson,
      Set<Endpoint> endpoints,
      @Named("healthChecks") Map<String, Supplier<Boolean>> healthChecks) {

    JavalinJson.setFromJsonMapper(gson::fromJson);
    JavalinJson.setToJsonMapper(gson::toJson);

    var javalin = Javalin.create(config -> {
      config.showJavalinBanner = false;
      config.server(() -> JettyHttp2Creator.createHttp2Server(env));
      config.accessManager(new JavalinJWTAccessManager());
      config.registerPlugin(new OpenApiPlugin(getOpenApiOptions(gson)));
      config.registerPlugin(new JavalinJWTFilter(ImmutableConfiguration.builder()
        .roleMapper(initializer.roleMapper())
        .jwtSecret(createAlgorithmFromEnvironment(env))
        .build()));
      config.registerPlugin(new HealthCheckPlugin(healthChecks, env));
    });

    javalin.exception(ApplicationException.class, (ex, ctx) -> {
      ctx.status(ex.getHttpCode());
      ctx.json(ex.getMessage());
    });

    javalin.get("/", c -> c.redirect("/swagger"));

    // javalin securityscheme type is taken from enum instead of name, making it uppercase.
    // need to convert it to down case or swagger ui will not work with autentication.
    // (HTTP -> http)
    javalin.after("/swagger-docs", c -> {
      String response = c.resultString();
      response = response.replaceAll("\"BearerAuth\":\\{\"type\":\"HTTP\"", "\"BearerAuth\":{\"type\":\"http\"");
      c.result(response);
    });

    boolean enabledEndpoints = Optional.ofNullable(env.get(() -> "API_ENABLE_ENDPOINTS"))
        .map(Boolean::valueOf)
        .orElse(false);

    if (enabledEndpoints) {
      LOG.info("## ENDPOINTS ENABLED");
      endpoints.stream().forEach(e -> {
        LOG.info("## ENDPOINTS Registering {}", e.getClass().getName());
        e.register(javalin);
      });
    }

    // Add to the logging context the key/value of request uuid
    // And also respond in the response header with the request uuid so the users
    // can send them for throubleshooting and finding the requests logs via uuid.
    javalin.before(ctx -> MDC.put(MDC_REQUEST_UUID, UUID.randomUUID().toString()));
    javalin.after(ctx -> {
      String uuid = ofNullable(MDC.get(MDC_REQUEST_UUID)).orElse("");
      ctx.header(HEADER_REQUEST_UUID, uuid);
      MDC.remove(MDC_REQUEST_UUID);
    });
    return javalin;
  }

  // API Docs

  public OpenApiOptions getOpenApiOptions(Gson gson) {

    SecurityScheme securityScheme = new SecurityScheme();
    securityScheme.setType(SecurityScheme.Type.HTTP);
    securityScheme.setScheme("bearer");
    securityScheme.setBearerFormat("JWT");

    var security = new SecurityRequirement();
    security.addList("BearerAuth");
    List<SecurityRequirement> securities = List.of(security);

    InitialConfigurationCreator initialConfigurationCreator = () -> new OpenAPI().info(new Info().version("1.0").description("API").title("API")).schemaRequirement("BearerAuth", securityScheme).security(securities);

    return new OpenApiOptions(initialConfigurationCreator)
      .toJsonMapper(gson::toJson)
      .path("/swagger-docs")
      .ignorePath("/")
      .ignorePath("/status/*")
      .swagger(new SwaggerOptions("/swagger").title("API"));
  }

  private Optional<Algorithm> createAlgorithmFromEnvironment(Env env) {
    // Case 1: plain secret string, using hmac256, from variable API_JWT_SECRET
    // Case 2: rsa pubkey for verify (no priv): the JWT auth plugin only does verification,
    //         if there is a token creation class to be made, will be in the future elsewhere.
    //         API_JWT_PUBLIC_KEY -> used for case 2
    //         [API_JWT_PRIVATE_KEY] -> complementary var to be used in another class later.
    // Which one takes precedence given all env variables set?
    // RSA is more secure so that should come first.
    // Functions have hard coded algs and bits so how to split them (strategy pattern?)
    // Which is the default?

    return Optional
      .ofNullable(env.get(() -> "API_JWT_SECRET"))
      .map(Algorithm::HMAC256);
  }
}
