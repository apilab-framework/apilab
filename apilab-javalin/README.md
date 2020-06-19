
## THINGS USED FROM THE CORE MODULE

Map of healthchecks @Named("healthChecks")

### ENVIRONMENT VARIABLES / SYSTEM PROPERTIES USED BY THIS MODULE

- "API_ENABLE_ENDPOINTS": set to "true" to enable the registering of endpoints. Defaults to "false"

- "API_JWT_SECRET": the jwt secret used to verify signed tokens. Not used if AuthConfiguration overrides it manually.

- "JAVALIN_HTTP2_PORT": Plain non-secure http port for the web server, defaults to "8080"
- "JAVALIN_HTTPS2_PORT": Secured http port for the web server, defaults to "8443"
- "JAVALIN_HTTPS2_CERT_CLASSPATH": classpath to the certificate for https, defaults to "/keystore.jks", and a default self signed one is provided in the classpath for that, for debugging reasons. Seriously consider using a different one if you publish secure http.
- "JAVALIN_HTTPS2_CERT_PASSWORD": password to the keystore and key, defaults to "password"

