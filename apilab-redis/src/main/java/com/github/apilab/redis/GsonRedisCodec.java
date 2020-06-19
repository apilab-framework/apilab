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
import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Simple json codec using gson, for entries in redis.
 * @author Raffaele Ragni
 * @param <T> type of the object in redis
 */
public class GsonRedisCodec<T> implements RedisCodec<String, T> {

  private final Gson gson;
  private final Class<T> clazz;

  public GsonRedisCodec(Class<T> clazz, Gson gson) {
    this.gson = gson;
    this.clazz = clazz;
  }

  @Override
  public String decodeKey(ByteBuffer bytes) {
    return UTF_8.decode(bytes).toString();
  }

  @Override
  public ByteBuffer encodeKey(String key) {
    return UTF_8.encode(key);
  }

  @Override
  public T decodeValue(ByteBuffer bytes) {
    return gson.fromJson(UTF_8.decode(bytes).toString(), clazz);
  }

  @Override
  public ByteBuffer encodeValue(T value) {
    return UTF_8.encode(gson.toJson(value));
  }

}
