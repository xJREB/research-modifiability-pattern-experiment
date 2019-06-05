# ProductSrvFacade

How to start the ProductSrvFacade application
---

1. Run `mvn clean install` to build your application
2. Start application with `java -jar target/ProductSrvFacade-1.0.0.jar server config.yml`
3. To check that your application is running enter url `http://localhost:8040` (port can be adjusted in `config.yml`)

Health Check
---

To see your applications health enter url `http://localhost:8041/healthcheck` (port can be adjusted in `config.yml`)

OpenAPI Endpoint
---

The OpenAPI documentation for all REST resources is available at `http://localhost:8040/openapi.json` or `http://localhost:8040/openapi.yaml`.