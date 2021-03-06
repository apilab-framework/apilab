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
import com.github.apilab.jdbi.exceptions.MigrationException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.Set;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.immutables.JdbiImmutables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receptive towards:
 * - "jdbiImmutables": Set&lt;Class&lt;?&gt;&gt; - list of classes to be registered for jdbi immutables support automapping.
 *
 * @author Raffaele Ragni
 */
@dagger.Module
public class JdbiModule {

  private static final Logger LOG = LoggerFactory.getLogger(JdbiModule.class);

  @Provides
  @Named("healthChecks")
  @IntoMap
  @StringKey("database")
  public Supplier<Boolean> dbHealthCheck(Jdbi jdbi) {
    return () -> jdbi.withHandle(h -> h.select("select 1").mapTo(String.class).first().equals("1"));
  }

  @Provides
  @Singleton
  public Jdbi jdbi(Env env, @Named("jdbiImmutables") Set<Class<?>> jdbiImmutables) {
    var url = ofNullable(env.get(() -> "API_DATABASE_URL")).orElse("jdbc:postgresql://[::1]/postgres");
    var username = ofNullable(env.get(() -> "API_DATABASE_USERNAME")).orElse("postgres");
    var password = ofNullable(env.get(() -> "API_DATABASE_PASSWORD")).orElse("postgres");
    var maxConnections = ofNullable(env.get(() -> "API_DATABASE_MAXPOOLSZE")).map(Integer::valueOf).orElse(100);

    processMigrations(env, url, username, password);

    var hikDS = createHikariPool(url, username, password, maxConnections);
    var jdbi = Jdbi.create(hikDS);

    registerImmutables(jdbi, jdbiImmutables);

    return jdbi;
  }

  private void processMigrations(Env env, String url, String username, String password) {
    boolean enableMigrations = Optional.ofNullable(env.get(() -> "API_ENABLE_MIGRATION"))
      .map(Boolean::valueOf)
      .orElse(false);
    if (enableMigrations) {
      LOG.info("MIGRATION ENABLED");
      runMigrations("db/changelog.xml", url, username, password);
    }
  }

  public static void runMigrations(String migrationPath, String url, String username, String password) {
    try (var liquibase = new Liquibase(migrationPath, new ClassLoaderResourceAccessor(),
      DatabaseFactory.getInstance().openDatabase(
        url, username, password, null, new FileSystemResourceAccessor()))) {
      liquibase.update(new Contexts(), new LabelExpression());
    } catch (Exception ex) {
      throw new MigrationException(ex.getMessage(), ex);
    }
  }

  private HikariDataSource createHikariPool(String url, String username, String password, Integer maxConnections) {
    var hikConf = new HikariConfig();
    hikConf.setJdbcUrl(url);
    hikConf.setUsername(username);
    hikConf.setPassword(password);
    hikConf.setMaximumPoolSize(maxConnections);
    return new HikariDataSource(hikConf);
  }

  private void registerImmutables(Jdbi jdbi, Set<Class<?>> jdbiImmutables) {
    // Automatically register any class given for jdbi immutable plugin
    // collection named 'jdbiImmutables' in the dependency graph as Set of classes.
    jdbi.getConfig(JdbiImmutables.class)
      .registerImmutable(jdbiImmutables);
  }
}
