
## THINGS INJECTED INTO THE CORE MODULE

Healthcheck "database". @Named("healthChecks") @StringKey("database")

### ENVIRONMENT VARIABLES / SYSTEM PROPERTIES USED BY THIS MODULE

"API_DATABASE_URL": JDBC url, defaults to "jdbc:postgresql://[::1]/postgres"
"API_DATABASE_USERNAME": database user, defaults to "postgres"
"API_DATABASE_PASSWORD": database password, defaults to "postgres"
"API_DATABASE_MAXPOOLSZE": maximum pool of connections, defaults to "100"
"API_ENABLE_MIGRATION": "true" to enable liquibase migrations in the start, defaults to "false"
