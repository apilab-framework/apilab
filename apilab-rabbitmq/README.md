
## THINGS INJECTED INTO THE CORE MODULE

Healthcheck "rabbitmq". @Named("healthChecks") @StringKey("rabbitmq")

### ENVIRONMENT VARIABLES / SYSTEM PROPERTIES USED BY THIS MODULE

"API_ENABLE_CONSUMERS": set to "true" to enable the registering of queue listeners. Defaults to "false"

"API_RABBITMQ_USERNAME": username for connecting to rabbitmq, defaults to "guest"
"API_RABBITMQ_PASSWORD": password for connecting to rabbitmq, defaults to "guest"
"API_RABBITMQ_HOST": rabbitmq host, defaults to "localhost"
"API_RABBITMQ_PORT": rabbitmq port, defaults to "5672'
