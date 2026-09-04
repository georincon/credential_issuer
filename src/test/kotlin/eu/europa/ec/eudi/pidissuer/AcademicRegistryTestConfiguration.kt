/*
 * Copyright (c) 2023-2026 European Commission
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.europa.ec.eudi.pidissuer

import eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.PadronGraduadosClient
import eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.ProgramaAcademico
import eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.RegistroGraduado
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration(proxyBeanMethods = false)
class AcademicRegistryTestConfiguration {
    @Bean
    @Primary
    fun academicRegistryClient(): PadronGraduadosClient =
        object : PadronGraduadosClient {
            private val programs =
                listOf(
                    ProgramaAcademico(
                        id = 1,
                        codigo = "PRE-001",
                        nombre = "Ingeniería de Sistemas",
                        nivelAcademico = "PREGRADO",
                        modalidad = "PRESENCIAL",
                        facultad = "Ingenierías",
                        unidadAcademica = "Escuela de Sistemas",
                        tituloOtorgado = "Ingeniero de Sistemas",
                        duracionSemestres = 10,
                        creditos = 170,
                    ),
                )

            override suspend fun listarProgramasActivos(): List<ProgramaAcademico> = programs

            override suspend fun listarNivelesAcademicosActivos(): List<String> = listOf("PREGRADO")

            override suspend fun buscarPorUsuario(username: String): RegistroGraduado? = null
        }
}
