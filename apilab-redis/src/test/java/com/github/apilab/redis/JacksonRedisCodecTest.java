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
package com.github.apilab.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Raffaele Ragni
 */
class JacksonRedisCodecTest {

  @Test
  void testKey() {
    var gson = new Gson();
    var codec = new GsonRedisCodec<String>(String.class, gson);

    var result = codec.decodeKey(codec.encodeKey("asd"));

    assertThat("round trip encoding", result, is("asd"));
  }

  @Test
  void testValue() {
    var gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    var codec = new GsonRedisCodec<Map>(Map.class, gson);

    var map = Map.of("k1", "v1", "k2", "v21");

    var result = codec.decodeValue(codec.encodeValue(map));

    assertThat("round trip encoding", result, is(map));
  }

}