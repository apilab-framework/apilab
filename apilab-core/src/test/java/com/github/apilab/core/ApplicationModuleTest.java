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

import static com.github.apilab.core.EnumSample.A;
import static com.github.apilab.core.EnumSample.C;
import java.io.IOException;
import java.time.Instant;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 *
 * @author Raffaele Ragni
 */
public class ApplicationModuleTest {
  @Test
  public void testModule() throws IOException, JSONException {
    var gson = new ApplicationModule().gson();

    // Basic serialization sanity check

    var val = gson.fromJson("true", Boolean.class);
    assertThat("value is value", val, is(true));

    // Test serialization/deserialization of the modules added:
    // gson with immutables
    // java 8 date types
    // enums by name

    var data = gson.fromJson(
      "{\"a\": 1, \"b\": \"asd\", \"c\": true, \"e\": \"A\", \"i\":\"2006-06-06T10:00:00Z\"}",
      Data.class);
    assertThat("property a is correct", data.a(), is(1));
    assertThat("property b is correct", data.b(), is("asd"));
    assertThat("property e is correct", data.e(), is(A));
    assertThat("property i is correct", data.i(), is(Instant.parse("2006-06-06T10:00:00Z")));

    data = ImmutableData.builder()
      .a(3)
      .b("bibi")
      .e(C)
      .i(Instant.parse("2001-06-06T10:00:00Z")).build();
    String json = gson.toJson(data);
    String compare = "{\"a\": 3, \"b\": \"bibi\", \"e\": \"C\", \"i\":\"2001-06-06T10:00:00Z\"}";
    JSONAssert.assertEquals(compare, json, true);

    // Test the application lifecycle class

    var instance = DaggerApplicationComponent.create().instance();
    instance.start();
    instance.stop();
  }
}
