# Estado definitivo - EUDI PID Issuer Piloto UIS

Este repositorio reúne el estado consolidado del piloto UIS a partir del código fuente completo más reciente y los últimos parches aplicados durante la adaptación.

## Cambios consolidados

- Integración del padrón académico UIS con PostgreSQL mediante las tablas de `V1.sql`.
- Consulta de programas académicos activos desde `programas_academicos`.
- Consulta de niveles académicos distintos desde PostgreSQL, ordenados por nivel.
- Selector de nivel académico y programa en la interfaz de generación de credenciales.
- Filtrado cliente de programas según el nivel seleccionado.
- JavaScript del selector externalizado para cumplir la política CSP (`script-src 'self'`).
- Soporte de los dos identificadores Learning Credential configurados:
  - `urn:eu.europa.ec.eudi:learning:credential:1:dc+sd-jwt-compact`
  - `urn:eu.europa.ec.eudi:learning:credential:1:dc+sd-jwt-compact_deferred`
- Validación del `academicProgramId` seleccionado contra los programas activos.
- Inclusión del programa seleccionado en el `issuerState` del ofrecimiento.
- Learning Credential UIS genérica para los niveles TECNICO, TECNOLOGICO, PREGRADO, ESPECIALIZACION, MAESTRIA y DOCTORADO.
- Eliminación de la generación aleatoria de datos académicos en favor de los datos del padrón.
- Correcciones de compilación/formateo aplicadas al proyecto Kotlin/Gradle.
- Esquema H2 exclusivo para pruebas: PostgreSQL `V1.sql` permanece como esquema de producción.

## Producción vs. pruebas

Producción/Docker usa PostgreSQL y `docker-compose/postgresql/schema/V1.sql`.

Las pruebas de integración que arrancan con H2 usan exclusivamente `src/test/resources/schema-h2.sql`.

## Frontend

El recurso JavaScript del selector se encuentra en:

`src/main/resources/public/js/academic-programs.js`

La plantilla lo carga como recurso same-origin:

`/public/js/academic-programs.js`

No debe volver a incorporarse el código del selector como `<script>...</script>` inline mientras la CSP mantenga `script-src 'self'`.
