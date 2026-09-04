CAMBIOS PILOTO UIS - eudi-srv-pid-issuer

Esta carpeta contiene los archivos modificados para:
1. Crear programa_academico y graduado automáticamente en PostgreSQL.
2. Cargar los 4 programas iniciales.
3. Consultar programas desde PostgreSQL.
4. Mostrar Nivel académico -> Programa de forma dependiente en Thymeleaf.
5. Eliminar Modalidades y el bloque de Formato.
6. Mostrar únicamente los dos credential_configuration_id del Learning Credential:
   - urn:eu.europa.ec.eudi:learning:credential:1:dc+sd-jwt-compact
   - urn:eu.europa.ec.eudi:learning:credential:1:dc+sd-jwt-compact_deferred
7. Habilitar el botón solo cuando se seleccionen nivel, programa y tipo de ofrecimiento.
8. Pasar el programa seleccionado como issuer_state del Credential Offer.
9. Obtener el registro académico autorizado desde graduado + programa_academico.
10. Añadir claims académicos al SD-JWT VC: academic_level, academic_program, program_code, degree_title.

IMPORTANTE SOBRE LA BASE EXISTENTE:
El script V2.sql está en docker-compose/postgresql/schema y será ejecutado automáticamente por PostgreSQL SOLO al inicializar un volumen de datos nuevo.
Si el volumen 'postgres' ya existe, docker compose up NO vuelve a ejecutar V2.sql.
Para el piloto desde cero:
  docker compose down -v
  docker compose up -d

Esto borra la base de datos del volumen Docker actual. Si hay datos que conservar, NO usar -v; ejecutar V2.sql manualmente con psql.

IMPORTANTE SOBRE AUTENTICACION:
El proyecto existente ya autentica la emisión mediante OAuth/Keycloak y valida la prueba criptográfica de la Wallet en el credential endpoint.
Estos cambios NO inventan un flujo OpenID4VP nuevo. Los datos académicos emitidos proceden de PostgreSQL y se relacionan con authorizationContext.username.
El issuer_state se incluye en el Credential Offer para transportar la selección del programa, pero para bindearla de forma obligatoria al token OAuth en un flujo cross-device todavía se requiere persistir el estado y/o configurar Keycloak para devolverlo como claim. No presentar esa parte como implementada hasta hacer esa integración.
