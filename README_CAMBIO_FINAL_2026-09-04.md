# Consolidación final UIS – 2026-09-04

Se consolidó el repositorio completo del PID Issuer piloto UIS con los cambios aplicados durante esta sesión.

## Corrección crítica aplicada

`PadronGraduadosClientPostgres.kt` usa una subconsulta para obtener los niveles académicos distintos y ordenarlos con `CASE`. Esto evita el error PostgreSQL `42P10` causado por `SELECT DISTINCT ... ORDER BY CASE ...`.

Consulta resultante:

```sql
SELECT n.nivel_academico
FROM (
    SELECT DISTINCT nivel_academico
    FROM programas_academicos
    WHERE activo = TRUE
) n
ORDER BY CASE n.nivel_academico
    WHEN 'TECNICO' THEN 1
    WHEN 'TECNOLOGICO' THEN 2
    WHEN 'PREGRADO' THEN 3
    WHEN 'ESPECIALIZACION' THEN 4
    WHEN 'MAESTRIA' THEN 5
    WHEN 'DOCTORADO' THEN 6
    ELSE 99
END
```

## UI

El selector Nivel académico → Programa utiliza JavaScript externo:

`src/main/resources/public/js/academic-programs.js`

La plantilla carga el recurso mediante:

`@{/public/js/academic-programs.js}`

El JavaScript inline del selector fue eliminado para respetar la CSP.
