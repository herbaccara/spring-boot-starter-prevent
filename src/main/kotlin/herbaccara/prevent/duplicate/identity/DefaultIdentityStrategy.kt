package herbaccara.prevent.duplicate.identity

import herbaccara.prevent.duplicate.identity.IdentityType.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

class DefaultIdentityStrategy @JvmOverloads constructor(
    private val identityTypes: List<IdentityType> = listOf(
        AUTHORIZATION_BEARER,
        AUTHORIZATION_BASIC,
        CLIENT_IP,
        SESSION_ID
    )
) : IdentityStrategy {

    override fun apply(request: HttpServletRequest): String? {
        for (type in identityTypes) {
            val identity = identity(request, type)
            if (identity.isNullOrBlank().not()) {
                return identity
            }
        }
        return null
    }

    protected fun identity(request: HttpServletRequest, identityType: IdentityType): String? {
        return when (identityType) {
            AUTHORIZATION_BEARER -> authorization(request, AuthorizationType.BEARER)
            AUTHORIZATION_BASIC -> authorization(request, AuthorizationType.BASIC)
            CLIENT_IP -> clientIp(request)
            SESSION_ID -> sessionId(request)
        }
    }

    protected fun sessionId(request: HttpServletRequest): String? = request.session.id

    protected fun authorization(request: HttpServletRequest, authorizationType: AuthorizationType): String? {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorization.isNullOrBlank().not()) {
            val prefix = authorizationType.prefix
            val trim = authorization.trim()
            if (trim.startsWith(prefix)) {
                return trim.substring(prefix.length)
            }
        }
        return null
    }

    protected fun clientIp(request: HttpServletRequest): String? {
        val headerNames = listOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP",
            "X-RealIP",
            "REMOTE_ADDR"
        )
        for (headerName in headerNames) {
            val value = request.getHeader(headerName)
            if (value.isNullOrBlank().not() && !"unknown".equals(value, ignoreCase = true)) {
                return value
            }
        }
        return request.remoteAddr
    }
}
