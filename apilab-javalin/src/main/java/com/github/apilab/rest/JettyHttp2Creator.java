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
package com.github.apilab.rest;

import com.github.apilab.core.Env;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.jetty.JettyStatisticsCollector;
import static java.util.Optional.ofNullable;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Creates a http2 server.
 * @author Raffaele Ragni
 */
public class JettyHttp2Creator {

  private static volatile boolean metricsInitialized = false;

  private JettyHttp2Creator() {
  }

  /**
   * Creates a HTTP2 server including configuration for the https with certificate.
   * By default loads "/keystore.jks" with password "password"
   * Ports are: 8443 for https, 8080 for http.
   *
   * This addons library already contains a default "keystore.jks" for "localhost" for debug or internal modes.
   * If your load balancer already deals with correct certificates and protects the api internally,
   * you can use this local certificate internally and still support http/2: it's not worse than using plain http.
   *
   * These addons are made to be used inside containers, so doesn't matter much which port is the default.
   *
   * These variables can be changed through environment variables, defaults are:
   * - JAVALIN_HTTP2_PORT: 8080 (system property is javalinHttp2Port)
   * - JAVALIN_HTTPS2_PORT: 8443 (system property is javalinHttps2Port)
   * - JAVALIN_HTTPS2_CERT_CLASSPATH: "/keystore.jks" (system property is javalinHttps2CertClasspath)
   * - JAVALIN_HTTPS2_CERT_PASSWORD: "password" (system property is getHttps2CertPassword)
   *
   * System properties take over environment variables.
   * @param env the environment container
   * @return Jetty server
   */
  public static Server createHttp2Server(Env env) {
    Server server = new Server();

    setupPrometheusMetrics(server);
    setupPlainHttp(server, env);

    var sslContextFactory = createSSLContextFactory(env);
    var httpsConfig = createHttpsConfig(env);
    HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpsConfig);
    ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
    alpn.setDefaultProtocol("h2");

    // SSL Connection Factory
    SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

    // HTTP/2 Connector
    // Javalin will take care of stopping the server
    ServerConnector http2Connector = new ServerConnector(server, ssl, alpn, h2, new HttpConnectionFactory(httpsConfig)); //NOSONAR
    http2Connector.setPort(getHttps2Port(env));
    server.addConnector(http2Connector);

    return server;
  }

  private static SslContextFactory createSSLContextFactory(Env env) {
    // SSL Context Factory for HTTPS and HTTP/2
    SslContextFactory sslContextFactory = new SslContextFactory.Server();
    // replace with your real keystore
    sslContextFactory.setKeyStorePath(JettyHttp2Creator.class
      .getResource(getHttps2CertClasspath(env)).toExternalForm());
    // replace with your real password
    sslContextFactory.setKeyStorePassword(getHttps2CertPassword(env));
    sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
    sslContextFactory.setProvider("Conscrypt");
    return sslContextFactory;
  }

  private static HttpConfiguration createHttpsConfig(Env env) {
    // HTTP Configuration
    HttpConfiguration config = new HttpConfiguration();
    config.setSendServerVersion(false);
    config.setSecureScheme("https");
    config.setSecurePort(getHttps2Port(env));
    // HTTPS Configuration
    HttpConfiguration httpsConfig = new HttpConfiguration(config);
    httpsConfig.addCustomizer(new SecureRequestCustomizer());
    return httpsConfig;
  }

  private static void setupPlainHttp(Server server, Env env) {
    // Javalin will take care of stopping the server
    ServerConnector connector = new ServerConnector(server); //NOSONAR
    connector.setPort(getHttp2Port(env));
    server.addConnector(connector);
  }

  private static void setupPrometheusMetrics(Server server) {
    StatisticsHandler statisticsHandler = new StatisticsHandler();
    var collector = new JettyStatisticsCollector(statisticsHandler);
    initializeMetrics(collector);
    server.setHandler(statisticsHandler);
  }

  private static synchronized void initializeMetrics(Collector collector) {
    if (!metricsInitialized) {
      var registry = CollectorRegistry.defaultRegistry;
      registry.register(collector);
      metricsInitialized = true;
    }
  }

  private static int getHttp2Port(Env env) {
    return ofNullable(env.get(() -> "JAVALIN_HTTP2_PORT"))
              .map(Integer::valueOf)
              .orElse(8080);
  }

  private static int getHttps2Port(Env env) {
    return ofNullable(env.get(() -> "JAVALIN_HTTPS2_PORT"))
      .map(Integer::valueOf)
      .orElse(8443);
  }

  private static String getHttps2CertClasspath(Env env) {
    return ofNullable(env.get(() -> "JAVALIN_HTTPS2_CERT_CLASSPATH"))
      .orElse("/keystore.jks");
  }

  private static String getHttps2CertPassword(Env env) {
    return ofNullable(env.get(() -> "JAVALIN_HTTPS2_CERT_PASSWORD"))
      .orElse("password");
  }
}
