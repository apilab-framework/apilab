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
package com.github.apilab.barehttp;

import com.github.apilab.core.ApplicationLifecycleItem;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@dagger.Module
public class BareHttpModule {
  @Provides @IntoSet ApplicationLifecycleItem lifecycle(BareHttpLifecycle service) {
    return service;
  }
}
