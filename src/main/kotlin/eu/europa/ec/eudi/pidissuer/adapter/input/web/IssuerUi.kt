package eu.europa.ec.eudi.pidissuer.adapter.input.web

import arrow.core.raise.effect
import arrow.core.raise.fold
import com.eygraber.uri.Uri
import com.eygraber.uri.Url
import eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.PadronGraduadosClient
import eu.europa.ec.eudi.pidissuer.appendPath
import eu.europa.ec.eudi.pidissuer.domain.*
import eu.europa.ec.eudi.pidissuer.port.input.CreateCredentialsOffer
import eu.europa.ec.eudi.pidissuer.port.out.qr.Dimensions
import eu.europa.ec.eudi.pidissuer.port.out.qr.Format
import eu.europa.ec.eudi.pidissuer.port.out.qr.GenerateQqCode
import eu.europa.ec.eudi.pidissuer.port.out.qr.Pixels
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*
import kotlin.io.encoding.Base64

class IssuerUi(
    private val metadata: CredentialIssuerMetaData,
    private val createCredentialsOffer: CreateCredentialsOffer,
    private val generateQrCode: GenerateQqCode,
    private val academicRegistry: PadronGraduadosClient,
) {
    val router: RouterFunction<ServerResponse> =
        coRouter {
            (GET("") or GET("/")) {
                ServerResponse.status(HttpStatus.TEMPORARY_REDIRECT)
                    .renderAndAwait("redirect:$GENERATE_CREDENTIALS_OFFER")
            }
            GET(GENERATE_CREDENTIALS_OFFER, contentType(MediaType.ALL) and accept(MediaType.TEXT_HTML)) {
                handleDisplayGenerateCredentialsOfferForm()
            }
            POST(
                GENERATE_CREDENTIALS_OFFER,
                contentType(MediaType.APPLICATION_FORM_URLENCODED) and accept(MediaType.TEXT_HTML),
                ::handleGenerateCredentialsOffer,
            )
        }

    private suspend fun handleDisplayGenerateCredentialsOfferForm(): ServerResponse {
        val programs = academicRegistry.listarProgramasActivos()
        val academicLevels = academicRegistry.listarNivelesAcademicosActivos()
        val credentialIds = setOf(
            "urn:eu.europa.ec.eudi:learning:credential:1:dc+sd-jwt-compact",
            "urn:eu.europa.ec.eudi:learning:credential:1:dc+sd-jwt-compact_deferred",
        )
        val usefulLinks = createUsefulLinks(metadata.id, metadata.authorizationServers[0])
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).renderAndAwait(
            "generate-credentials-offer-form",
            mapOf(
                "credentialConfigurationIds" to credentialIds,
                "nivelesAcademicos" to academicLevels,
                "programasAcademicos" to programs,
                "credentialsOfferUri" to createCredentialsOffer.defaultCredentialOfferUri.toString(),
                "openid4VciVersion" to OpenId4VciSpec.VERSION,
                "usefulLinks" to usefulLinks,
            ),
        )
    }

    private suspend fun handleGenerateCredentialsOffer(request: ServerRequest): ServerResponse {
        val createCredentialOfferRequest = request.createCredentialOfferRequest()
        // The program must exist and be active. The authoritative academic record
        // is still resolved later from the authenticated Wallet/Keycloak identity.
        val programs = academicRegistry.listarProgramasActivos()
        val selectedProgram = ensureProgramExists(createCredentialOfferRequest.academicProgramId, programs)
        val selectedCredentialId = createCredentialOfferRequest.credentialConfigurationIds.firstOrNull()?.value
        val usefulLinks = createUsefulLinks(metadata.id, metadata.authorizationServers[0])

        return effect {
            createCredentialsOffer(createCredentialOfferRequest)
        }.fold(
            transform = { credentialsOfferUri ->
                context(generateQrCode) {
                    credentialsOfferUri.credentialOfferSuccessResponse(selectedProgram, selectedCredentialId, usefulLinks)
                }
            },
            recover = { error ->
                log.warn("Unable to generate Credentials Offer. Error: {}", error)
                error.credentialOfferErrorResponse()
            },
        )
    }

    private fun createUsefulLinks(credentialIssuer: CredentialIssuerId, authorizationServer: HttpsUrl): Map<String, String> {
        fun HttpsUrl.wellKnown(path: String): HttpsUrl =
            HttpsUrl.unsafe(value.buildUpon().path(null).appendPath(".well-known").appendPath(path).apply {
                value.pathSegments.filterNot { it.isBlank() }.forEach { appendPath(it) }
            }.build().toString())
        return mapOf(
            "credential_issuer_metadata" to credentialIssuer.wellKnown("openid-credential-issuer").externalForm,
            "protected_resource_metadata" to credentialIssuer.wellKnown("oauth-protected-resource").externalForm,
            "authorization_server_metadata" to authorizationServer.wellKnown("oauth-authorization-server").externalForm,
            "sdjwt_vc_issuer_metadata" to credentialIssuer.wellKnown("jwt-vc-issuer").externalForm,
            "learning_credential_sdjwt_vc_type_metadata" to credentialIssuer.appendPath("/type-metadata/urn:eu.europa.ec.eudi:learning:credential:1").externalForm,
        )
    }

    companion object {
        const val GENERATE_CREDENTIALS_OFFER = "/issuer/credentialsOffer/generate"
        private val log = LoggerFactory.getLogger(IssuerUi::class.java)
    }
}

private fun ensureProgramExists(
    programId: Long?,
    programs: List<eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.ProgramaAcademico>,
): eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.ProgramaAcademico {
    require(programId != null) { "Debe seleccionar un programa académico" }
    return programs.firstOrNull { it.id == programId }
        ?: error("El programa académico seleccionado no existe o está inactivo")
}

private suspend fun ServerRequest.createCredentialOfferRequest(): CreateCredentialsOffer.Request {
    val formData = awaitFormData()
    val credentialIds = formData["credentialIds"].orEmpty().map(::CredentialConfigurationId).toSet()
    val credentialsOfferUri = formData["credentialsOfferUri"]?.firstOrNull { it.isNotBlank() }
    val academicProgramId = formData["academicProgramId"]?.firstOrNull()?.toLongOrNull()
    return CreateCredentialsOffer.Request(credentialIds, credentialsOfferUri, academicProgramId)
}

context(generateQrCode: GenerateQqCode)
private suspend fun Uri.credentialOfferSuccessResponse(
    selectedProgram: eu.europa.ec.eudi.pidissuer.adapter.out.academicregistry.ProgramaAcademico,
    selectedCredentialId: String?,
    usefulLinks: Map<String, String>,
): ServerResponse {
    val uri = this@credentialOfferSuccessResponse
    val qrCode = generateQrCode(uri, Format.PNG, Dimensions(Pixels(360u), Pixels(360u)))
    return ServerResponse.ok().contentType(MediaType.TEXT_HTML).renderAndAwait(
        "display-credentials-offer",
        mapOf(
            "uri" to uri.toString(),
            "qrCode" to Base64.encode(qrCode),
            "qrCodeMediaType" to "image/png",
            "programaAcademico" to selectedProgram,
            "credentialConfigurationId" to (selectedCredentialId ?: ""),
            "usefulLinks" to usefulLinks,
            "openid4VciVersion" to OpenId4VciSpec.VERSION,
        ),
    )
}

private suspend fun CreateCredentialsOffer.Error.credentialOfferErrorResponse(): ServerResponse =
    ServerResponse.badRequest().contentType(MediaType.TEXT_HTML).renderAndAwait(
        "generate-credentials-offer-error",
        mapOf("error" to this::class.java.canonicalName, "openid4VciVersion" to OpenId4VciSpec.VERSION),
    )
