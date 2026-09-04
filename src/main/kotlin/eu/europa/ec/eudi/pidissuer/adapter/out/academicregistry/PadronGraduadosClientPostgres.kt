package eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import kotlin.time.toKotlinInstant

class PadronGraduadosClientPostgres(private val db: DatabaseClient) : PadronGraduadosClient {
    override suspend fun listarNivelesAcademicosActivos(): List<String> =
        db.sql(
            """
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
            """.trimIndent(),
        )
            .map { row, _ -> row.get("nivel_academico", String::class.java)!! }
            .all()
            .collectList()
            .awaitSingle()

    override suspend fun listarProgramasActivos(): List<ProgramaAcademico> =
        db.sql(
            """
            SELECT id_programa, codigo, nombre, nivel_academico, modalidad,
                   facultad, unidad_academica, titulo_otorgado,
                   duracion_semestres, creditos
            FROM programas_academicos
            WHERE activo = TRUE
            ORDER BY CASE nivel_academico
                WHEN 'TECNICO' THEN 1
                WHEN 'TECNOLOGICO' THEN 2
                WHEN 'PREGRADO' THEN 3
                WHEN 'ESPECIALIZACION' THEN 4
                WHEN 'MAESTRIA' THEN 5
                WHEN 'DOCTORADO' THEN 6
                ELSE 99
            END, nombre
            """.trimIndent(),
        )
            .map { row, _ ->
                ProgramaAcademico(
                    id = row.get("id_programa", java.lang.Long::class.java)!!.toLong(),
                    codigo = row.get("codigo", String::class.java)!!,
                    nombre = row.get("nombre", String::class.java)!!,
                    nivelAcademico = row.get("nivel_academico", String::class.java)!!,
                    modalidad = row.get("modalidad", String::class.java)!!,
                    facultad = row.get("facultad", String::class.java),
                    unidadAcademica = row.get("unidad_academica", String::class.java),
                    tituloOtorgado = row.get("titulo_otorgado", String::class.java)!!,
                    duracionSemestres = row.get("duracion_semestres", Integer::class.java)!!.toInt(),
                    creditos = row.get("creditos", Integer::class.java)!!.toInt(),
                )
            }.all().collectList().awaitSingle()

    override suspend fun buscarPorUsuario(username: String): RegistroGraduado? =
        db.sql(
            """
            SELECT e.nombres,
                   e.apellidos,
                   e.tipo_identificacion,
                   e.numero_identificacion,
                   h.programa_id,
                   p.codigo AS programa_codigo,
                   p.nombre AS programa_nombre,
                   p.nivel_academico,
                   p.modalidad,
                   p.facultad,
                   p.unidad_academica,
                   p.titulo_otorgado,
                   p.duracion_semestres,
                   p.creditos AS creditos_programa,
                   h.fecha_ingreso,
                   h.fecha_de_grado,
                   h.promedio_acumulado,
                   h.creditos_aprobados,
                   h.semestres_cursados,
                   h.estado_academico
            FROM historial_estudiantes h
            JOIN datos_estudiantes e ON e.id_estudiante = h.estudiante_id
            JOIN programas_academicos p ON p.id_programa = h.programa_id
            WHERE e.usuario_autenticacion = :username
              AND h.estado_academico = 'GRADUADO'
              AND p.activo = TRUE
            ORDER BY h.fecha_de_grado DESC NULLS LAST
            LIMIT 1
            """.trimIndent(),
        )
            .bind("username", username)
            .map { row, _ ->
                RegistroGraduado(
                    nombres = row.get("nombres", String::class.java)!!,
                    apellidos = row.get("apellidos", String::class.java)!!,
                    tipoIdentificacion = row.get("tipo_identificacion", String::class.java)!!,
                    numeroIdentificacion = row.get("numero_identificacion", String::class.java)!!,
                    programaId = row.get("programa_id", java.lang.Long::class.java)!!.toLong(),
                    programaCodigo = row.get("programa_codigo", String::class.java)!!,
                    programaNombre = row.get("programa_nombre", String::class.java)!!,
                    nivelAcademico = row.get("nivel_academico", String::class.java)!!,
                    modalidad = row.get("modalidad", String::class.java)!!,
                    facultad = row.get("facultad", String::class.java),
                    unidadAcademica = row.get("unidad_academica", String::class.java),
                    tituloOtorgado = row.get("titulo_otorgado", String::class.java)!!,
                    duracionSemestres = row.get("duracion_semestres", Integer::class.java)!!.toInt(),
                    creditosPrograma = row.get("creditos_programa", Integer::class.java)!!.toInt(),
                    fechaIngreso = row.get("fecha_ingreso", java.time.LocalDate::class.java),
                    fechaDeGrado = row.get("fecha_de_grado", java.time.LocalDate::class.java)
                        ?.atStartOfDay(java.time.ZoneOffset.UTC)
                        ?.toInstant()
                        ?.toKotlinInstant(),
                    promedioAcumulado = row.get("promedio_acumulado", String::class.java),
                    creditosAprobados = row.get("creditos_aprobados", Integer::class.java)?.toInt(),
                    semestresCursados = row.get("semestres_cursados", Integer::class.java)?.toInt(),
                    estadoAcademico = row.get("estado_academico", String::class.java)!!,
                )
            }.all().awaitFirstOrNull()
}
