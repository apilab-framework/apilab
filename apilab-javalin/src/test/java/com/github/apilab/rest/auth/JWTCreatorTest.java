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
package com.github.apilab.rest.auth;

import com.auth0.jwt.algorithms.Algorithm;
import static com.github.apilab.rest.auth.Roles.ADMIN;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Raffaele Ragni
 */
class JWTCreatorTest {

  private static final AuthConfiguration BASE_CONFIG = ImmutableAuthConfiguration.builder()
      .roleMapper(Roles::valueOf)
      .build();
  private static final AuthConfiguration EXTENDED_CONFIG = ImmutableAuthConfiguration.builder()
      .roleMapper(Roles::valueOf)
      .jwtSecret(Optional.of(Algorithm.HMAC256("secret")))
      .jwtRolesProperty("Roles")
      .build();

  @Test
  void testWrongConfig() {
    assertThrows(IllegalArgumentException.class, () -> {
      new JavalinJWTCreator(BASE_CONFIG);
    });
  }

  @Test
  void testSimpleToken() {
    var creator = new JavalinJWTCreator(EXTENDED_CONFIG);
    var token = creator.create(Set.of(ADMIN));
    assertThat("token is correct", token, is("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJSb2xlcyI6WyJBRE1JTiJdfQ.TYDeoIGD7wjJj63XWhBTto0PdYBRu5NJrFS4geDv9zA"));
  }

  @Test
  void testSimpleTokenAdditionalClaims() {
    var creator = new JavalinJWTCreator(EXTENDED_CONFIG);
    var token = creator.create(Set.of(ADMIN), () -> Map.of("test", "value", "test2", "value2"));
    assertThat("token is correct", token, is("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0ZXN0MiI6InZhbHVlMiIsInRlc3QiOiJ2YWx1ZSIsIlJvbGVzIjpbIkFETUlOIl19.R6asOqiW-4eJSXmlgg9BFWKpcP08bOrpNEyNPT1Z7jo"));
  }

}
