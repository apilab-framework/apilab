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
package com.github.apilab.rest.exceptions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Raffaele Ragni
 */
class ApplicationExceptionTest {

  @Test
  void testExceptions() {
    var t = new RuntimeException();
    var ex = new ServerException("message", t);

    assertThat("message taken", ex.getMessage(), is("message"));
    assertThat("cause taken", ex.getCause(), is(t));

    ex = new NotFoundException("not found message");

    assertThat("status taken", ex.getHttpCode(), is(404));
    assertThat("message taken", ex.getMessage(), is("not found message"));

    ex = new NotAuthenticatedException("not authenticated");

    assertThat("status taken", ex.getHttpCode(), is(401));
    assertThat("message taken", ex.getMessage(), is("not authenticated"));

    ex = new NotAuthorizedException("not authorized");

    assertThat("status taken", ex.getHttpCode(), is(403));
    assertThat("message taken", ex.getMessage(), is("not authorized"));

    ex = new UnprocessableEntityException("unprocessable");

    assertThat("status taken", ex.getHttpCode(), is(422));
    assertThat("message taken", ex.getMessage(), is("unprocessable"));
  }

}
