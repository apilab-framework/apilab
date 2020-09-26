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
package com.github.apilab.jdbi;

import com.github.apilab.core.Env;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Raffaele Ragni
 */
class JdbiModuleTest {

  @Test
  void testModule() {
    var env = new Env();
    System.setProperty("API_DATABASE_URL", "jdbc:h2:mem:test");
    System.setProperty("API_DATABASE_USERNAME", "sa");
    System.setProperty("API_DATABASE_PASSWORD", "");
    System.setProperty("API_DATABASE_MAXPOOLSZE", "1");
    System.setProperty("API_ENABLE_MIGRATION", "false");
    new JdbiModule().jdbi(env, emptySet());

    System.setProperty("API_ENABLE_MIGRATION", "true");
    var jdbi = new JdbiModule().jdbi(env, emptySet());

    assertThrows(RuntimeException.class, () -> {
      JdbiModule.runMigrations(
        "wrong path does not exist!",
        "jdbc:h2:mem:test2", "sa", "");
    });

    var result = new JdbiModule().dbHealthCheck(jdbi).get();
    assertThat("Healthcheck is ok", result, is(true));
  }

}
