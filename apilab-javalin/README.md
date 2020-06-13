
## THINGS USED FROM THE CORE MODULE

Map of healthchecks @Named("healthChecks")

### ENVIRONMENT VARIABLES / SYSTEM PROPERTIES USED BY THIS MODULE

- "API_ENABLE_ENDPOINTS": set to "true" to enable the registering of endpoints. Defaults to "false"

- "API_JWT_PUBLIC_KEY": the public key to verify signed tokens, takes precedence on SECRET. Use only if you want to verify tokens. Defaults to: not defined
- "API_JWT_SECRET": the jwt secret used to verify signed tokens. Do not use the variable if you don't want to validate tokens (ex. if a gateway api is already doing it for you), defaults to: not defined

- "JAVALIN_PROMETHEUS_PORT": Plain http port for prometheus metrics, must be different than the web port, defaults to "7080"
- "JAVALIN_HTTP2_PORT": Plain non-secure http port for the web server, defaults to "8080"
- "JAVALIN_HTTPS2_PORT": Secured http port for the web server, defaults to "8443"
- "JAVALIN_HTTPS2_CERT_CLASSPATH": classpath to the certificate for https, defaults to "/keystore.jks", and a default self signed one is provided in the classpath for that, for debugging reasons. Seriously consider using a different one if you publish secure http.
- "JAVALIN_HTTPS2_CERT_PASSWORD": password to the keystore and key, defaults to "password"

