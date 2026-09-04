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
package eu.europa.ec.eudi.pidissuer.adapter.out.attestation.learningcredential

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import eu.europa.ec.eudi.pidissuer.adapter.out.attestation.mdl.IsoAlpha2CountryCode
import eu.europa.ec.eudi.pidissuer.adapter.out.attestation.pid.Pid
import eu.europa.ec.eudi.pidissuer.adapter.out.util.randomInstantInThePast
import eu.europa.ec.eudi.pidissuer.domain.HttpsUrl
import eu.europa.ec.eudi.pidissuer.domain.NonBlankString
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

data class Issuer(
    val name: Name,
    val country: IsoAlpha2CountryCode,
    val uri: HttpsUrl,
) {
    typealias Name = NonBlankString
}

typealias FamilyName = NonBlankString
typealias GivenName = NonBlankString
typealias AchievementTitle = NonBlankString
typealias AchievementDescription = NonBlankString

typealias LearningOutcome = NonBlankString
typealias AssessmentGrade = NonBlankString

enum class Language {
    EN,
    JP,

    // ES agregado para el piloto UIS: ningún programa académico de la UIS se
    // dicta en EN/JP. Requiere también mapear este valor en
    // IssueLearningCredential.kt (Language.value).
    ES,
}

typealias LearnerIdentification = NonBlankString
typealias ExpectedStudyTime = NonBlankString

enum class LevelOfLearningExperience {
    Level1,
    Level2,
    Level3,
    Level4,
    Level5,
    Level6,
    Level7,
    Level8,
}

typealias TypesOfQualityAssurance = NonBlankString
typealias PrerequisiteToEnroll = NonBlankString

enum class IntegrationStackabilityOptions {
    Yes,
    No,
}

data class LearningCredential(
    val issuer: Issuer,
    val dateOfIssuance: Instant,
    val dateOfExpiry: Instant? = null,
    val familyName: FamilyName,
    val givenName: GivenName? = null,
    val achievementTitle: AchievementTitle,
    val academicLevel: NonBlankString? = null,
    val academicProgram: NonBlankString? = null,
    val programCode: NonBlankString? = null,
    val degreeTitle: NonBlankString? = null,
    val achievementDescription: AchievementDescription? = null,
    val learningOutcomes: NonEmptyList<LearningOutcome>? = null,
    val assessmentGrade: AssessmentGrade? = null,
    val languagesOfClasses: NonEmptyList<Language>,
    val learnerIdentification: LearnerIdentification,
    val identificationType: NonBlankString? = null,
    val identificationNumber: NonBlankString? = null,
    val expectedStudyTime: ExpectedStudyTime,
    val levelOfLearningExperience: LevelOfLearningExperience,
    val typesOfQualityAssurance: NonEmptyList<TypesOfQualityAssurance>,
    val prerequisitesToEnroll: NonEmptyList<PrerequisiteToEnroll>? = null,
    val integrationStackabilityOptions: IntegrationStackabilityOptions? = null,
) {
    companion object
}

context(random: Random)
fun LearningCredential.Companion.random(pid: Pid): LearningCredential {
    fun effectivity(): Pair<Instant, Instant> {
        val dateOfIssuance = context(Clock.System) { randomInstantInThePast() }
        val inTwoYears = dateOfIssuance + (2 * 365).days
        val inFifteenYears = dateOfIssuance + (15 * 365).days
        val dateOfExpiry =
            Instant.fromEpochSeconds(random.nextLong(inTwoYears.epochSeconds, inFifteenYears.epochSeconds))
        return Pair(dateOfIssuance, dateOfExpiry)
    }

    val (dateOfIssuance, dateOfExpiry) = effectivity()

    return LearningCredential(
        issuer =
            Issuer(
                name = Issuer.Name("Technical University of Munich: Department of Applied Sciences"),
                country = IsoAlpha2CountryCode("DE"),
                uri = HttpsUrl.unsafe("https://university.de/department-of-applied-sciences"),
            ),
        dateOfIssuance = dateOfIssuance,
        dateOfExpiry = dateOfExpiry,
        familyName = FamilyName(pid.familyName.value),
        givenName = GivenName(pid.givenName.value),
        achievementTitle = AchievementTitle("Foundations of Applied AI in Business"),
        achievementDescription =
            AchievementDescription(
                "A comprehensive introductory course on the practical application of " +
                        "Artificial Intelligence models to solve common business problems.",
            ),
        learningOutcomes =
            nonEmptyListOf(
                LearningOutcome("Analyze business processes to identify opportunities for AI implementation"),
                LearningOutcome("Evaluate the suitability of different machine learning models for a given problem"),
            ),
        assessmentGrade = AssessmentGrade("Excellent"),
        languagesOfClasses = nonEmptyListOf(Language.entries.shuffled(random).first()),
        learnerIdentification = LearnerIdentification("0123456"),
        expectedStudyTime = ExpectedStudyTime("12 months"),
        levelOfLearningExperience = LevelOfLearningExperience.entries.shuffled(random).first(),
        typesOfQualityAssurance = nonEmptyListOf(TypesOfQualityAssurance("Institutional Evaluation")),
        prerequisitesToEnroll = nonEmptyListOf(PrerequisiteToEnroll("Familiarity with Python")),
        integrationStackabilityOptions = IntegrationStackabilityOptions.entries.shuffled(random).first(),
    )
}

// Piloto UIS: reemplaza random(pid) como fuente de datos para este emisor.
// A diferencia de random(pid), que inventa un curso al azar solo para
// demostración, esta función recibe el registro REAL de un graduado
// (consultado contra el padrón académico externo, ver
// adapter/out/academicregistry/) y no genera nada al azar: cada campo viene
// del registro o es un valor institucional fijo de la UIS.
fun LearningCredential.Companion.fromUisAcademicRecord(
    registro: eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.RegistroGraduado,
): LearningCredential =
    LearningCredential(
        issuer =
            Issuer(
                name = Issuer.Name("Universidad Industrial de Santander"),
                country = IsoAlpha2CountryCode("CO"),
                uri = HttpsUrl.unsafe("https://www.uis.edu.co"),
            ),
        dateOfIssuance = registro.fechaDeGrado ?: Clock.System.now(),
        dateOfExpiry = null, // un título profesional no expira
        familyName = FamilyName(registro.apellidos),
        givenName = GivenName(registro.nombres),
        achievementTitle = AchievementTitle(registro.programaNombre),
        academicLevel = NonBlankString(registro.nivelAcademico),
        academicProgram = NonBlankString(registro.programaNombre),
        programCode = NonBlankString(registro.programaCodigo),
        degreeTitle = NonBlankString(registro.tituloOtorgado),
        achievementDescription =
            AchievementDescription(
                "Título académico otorgado por la Universidad Industrial de Santander " +
                        "al completar el programa ${registro.programaNombre}.",
            ),
        learningOutcomes = null,
        assessmentGrade = registro.promedioAcumulado?.let { AssessmentGrade(it) },
        languagesOfClasses = nonEmptyListOf(Language.ES),
        learnerIdentification = LearnerIdentification(registro.numeroIdentificacion),
        identificationType = NonBlankString(registro.tipoIdentificacion),
        identificationNumber = NonBlankString(registro.numeroIdentificacion),
        expectedStudyTime = ExpectedStudyTime("${registro.duracionSemestres} semestres / ${registro.creditosPrograma} créditos"),
        levelOfLearningExperience = registro.nivelAcademico.toLearningLevel(),
        typesOfQualityAssurance =
            nonEmptyListOf(TypesOfQualityAssurance("Acreditación de Alta Calidad - CNA Colombia")),
        prerequisitesToEnroll = null,
        integrationStackabilityOptions = null,
    )


private fun String.toLearningLevel(): LevelOfLearningExperience =
    when (uppercase()) {
        "TECNICO", "TECNOLOGICO" -> LevelOfLearningExperience.Level5
        "PREGRADO" -> LevelOfLearningExperience.Level6
        "ESPECIALIZACION", "MAESTRIA" -> LevelOfLearningExperience.Level7
        "DOCTORADO" -> LevelOfLearningExperience.Level8
        else -> LevelOfLearningExperience.Level6
    }
