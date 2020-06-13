
## THINGS INJECTED INTO THE CORE MODULE

Healtcheck for "redis". @Named("healthChecks") @StringKey("redis")


### ENVIRONMENT VARIABLES / SYSTEM PROPERTIES USED BY THIS MODULE

- "API_REDIS_URL": URL of redis instance. No authentication supported at the moment. Defaults to "redis://localhost"
