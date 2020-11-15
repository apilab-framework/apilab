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
package com.github.apilab.grpc;

import app.grpc.v1.GreeterGrpc;
import app.grpc.v1.Hello;
import io.grpc.stub.StreamObserver;
import javax.inject.Inject;

public class HelloService extends GreeterGrpc.GreeterImplBase {

  @Inject
  public HelloService() {
    // Injection
  }

  @Override
  public void sayHello(Hello.HelloRequest request, StreamObserver<Hello.HelloReply> responseObserver) {
    responseObserver.onNext(Hello.HelloReply.newBuilder().setMessage(request.getName()).build());
    responseObserver.onCompleted();
  }

}
