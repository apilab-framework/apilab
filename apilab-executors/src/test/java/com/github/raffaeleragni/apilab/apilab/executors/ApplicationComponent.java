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
package com.github.raffaeleragni.apilab.apilab.executors;

import com.github.raffaeleragni.apilab.core.ApplicationInstance;
import javax.inject.Singleton;

/**
 *
 * @author Raffaele Ragni
 */
@dagger.Component(modules = {ExecutorsModule.class, ComponentsModule.class})
@Singleton
public interface ApplicationComponent {
  ApplicationInstance instance();
}
