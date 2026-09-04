package eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry

import kotlin.time.Instant

data class ProgramaAcademico(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val nivelAcademico: String,
    val modalidad: String,
    val facultad: String?,
    val unidadAcademica: String?,
    val tituloOtorgado: String,
    val duracionSemestres: Int,
    val creditos: Int,
)

data class RegistroGraduado(
    val nombres: String,
    val apellidos: String,
    val tipoIdentificacion: String,
    val numeroIdentificacion: String,
    val programaId: Long,
    val programaCodigo: String,
    val programaNombre: String,
    val nivelAcademico: String,
    val modalidad: String,
    val facultad: String?,
    val unidadAcademica: String?,
    val tituloOtorgado: String,
    val duracionSemestres: Int,
    val creditosPrograma: Int,
    val fechaIngreso: java.time.LocalDate?,
    val fechaDeGrado: Instant?,
    val promedioAcumulado: String?,
    val creditosAprobados: Int?,
    val semestresCursados: Int?,
    val estadoAcademico: String,
)

interface PadronGraduadosClient {
    suspend fun listarProgramasActivos(): List<ProgramaAcademico>
    suspend fun listarNivelesAcademicosActivos(): List<String>
    suspend fun buscarPorUsuario(username: String): RegistroGraduado?
}
