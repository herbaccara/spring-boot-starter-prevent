package herbaccara.prevent.duplicate

import herbaccara.prevent.PreventRequestFilter
import herbaccara.prevent.duplicate.identity.DefaultIdentityStrategy
import herbaccara.prevent.duplicate.identity.IdentityStrategy
import herbaccara.prevent.duplicate.predicate.PreventDuplicateRequestPredicate
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.time.Duration

open class PreventDuplicateRequestFilter @JvmOverloads constructor(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val isDuplicate: PreventDuplicateRequestPredicate,
    private val identityStrategy: IdentityStrategy = DefaultIdentityStrategy(),
    private val preventHttpMethods: List<HttpMethod> = DEFAULT_PREVENT_HTTP_METHODS,
    private val requestUriType: RequestUriType = DEFAULT_REQUEST_URI_TYPE,
    private val withQueryString: Boolean = DEFAULT_WITH_QUERY_STRING,
    private val errorStatusCode: HttpStatus = DEFAULT_ERROR_STATUS_CODE,
    private val errorMessage: String = DEFAULT_ERROR_MESSAGE
) : PreventRequestFilter() {

    companion object {
        const val DEFAULT_FILTER_ORDER = 10

        @JvmField val DEFAULT_TIMEOUT: Duration = Duration.ofSeconds(3)

        @JvmField val DEFAULT_PREVENT_HTTP_METHODS: List<HttpMethod> = listOf(POST, PUT, PATCH, DELETE)

        @JvmField val DEFAULT_REQUEST_URI_TYPE: RequestUriType = RequestUriType.RAW

        const val DEFAULT_WITH_QUERY_STRING: Boolean = false

        @JvmField val DEFAULT_ERROR_STATUS_CODE: HttpStatus = HttpStatus.CONFLICT

        const val DEFAULT_ERROR_MESSAGE: String = "Please try again later."
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val httpMethod = valueOf(request.method)
        if (preventHttpMethods.contains(httpMethod)) {
            val handlerExecutionChain = requestMappingHandlerMapping.getHandler(request)
            if (handlerExecutionChain != null) {
                val handler = handlerExecutionChain.handler
                if (handler is HandlerMethod) {
                    val identity = identityStrategy.apply(request)
                    if (identity.isNullOrBlank().not()) {
                        val uriKey = when (requestUriType) {
                            RequestUriType.RAW -> request.requestURI
                            RequestUriType.HANDLER -> handler.method.let { it.declaringClass.name + "." + it.name }
                        }

                        val key = PreventDuplicateRequestKey(
                            uriKey,
                            if (withQueryString) request.queryString else null,
                            httpMethod.name(),
                            identity!!
                        )

                        if (isDuplicate.test(key)) {
                            response.sendError(errorStatusCode.value(), errorMessage)
                            return
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
