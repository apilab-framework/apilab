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

import java.time.Instant;
import static java.time.Instant.now;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

/**
 *
 * @author Raffaele Ragni
 */
@Value.Immutable
@Gson.TypeAdapters
public interface Data {
  int a();
  String b();
  EnumSample e();
  @Value.Default default Instant i() { return now(); }
}
