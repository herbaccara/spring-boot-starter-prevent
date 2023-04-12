package herbaccara.prevent.timelimit

import herbaccara.prevent.PreventRequestFilter
import herbaccara.prevent.timelimit.annotation.GlobalPreventTimeLimit
import herbaccara.prevent.timelimit.annotation.PreventTimeLimit
import herbaccara.prevent.timelimit.storage.PreventTimeLimitRequestStorage
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Function

class PreventTimeLimitRequestFilter(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val storage: PreventTimeLimitRequestStorage,
    private val globalTimeouts: Function<String, Duration>,
    private val errorStatusCode: HttpStatus = DEFAULT_ERROR_STATUS_CODE,
    private val errorMessage: String = DEFAULT_ERROR_MESSAGE
) : PreventRequestFilter() {

    companion object {
        const val DEFAULT_FILTER_ORDER: Int = 20

        @JvmField val DEFAULT_GLOBAL_TIMEOUT: Duration = Duration.ofSeconds(30)

        @JvmField val DEFAULT_ERROR_STATUS_CODE: HttpStatus = HttpStatus.CONFLICT

        const val DEFAULT_ERROR_MESSAGE: String = "Please try again in %,d seconds."
    }

    private val ip: String
    private val mac: String

    init {
        val localHost: InetAddress = InetAddress.getLocalHost()
        ip = localHost.hostAddress
        mac = NetworkInterface.getByInetAddress(localHost).hardwareAddress
            .joinToString("-") { String.format("%02X", it) }
    }

    private fun isDuplicate(scope: Scope, identity: String, sec: Long): Pair<Boolean, Long> {
        val now = LocalDateTime.now()

        val key = PreventTimeLimitRequestKey(scope, ip, mac, identity)
        val lastAccessDateTime = storage.get(key)
        if (lastAccessDateTime != null) {
            val between: Long = ChronoUnit.SECONDS.between(lastAccessDateTime, now)
            if (between < sec) {
                return true to (sec - between)
            }
        }

        storage.put(key, now)
        return false to 0
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val handlerExecutionChain = requestMappingHandlerMapping.getHandler(request)
        if (handlerExecutionChain != null) {
            val handler = handlerExecutionChain.handler
            if (handler is HandlerMethod) {
                // local 과 global 설정이 동시에 있다면 local 부터 처리 한다.
                val preventTimeLimit = handler.getMethodAnnotation(PreventTimeLimit::class.java)
                if (preventTimeLimit != null) {
                    val identity = handler.method.let { it.declaringClass.name + "." + it.name }
                    val sec = preventTimeLimit.sec

                    val (duplicate, remainTime) = isDuplicate(Scope.LOCAL, identity, sec)
                    if (duplicate) {
                        response.sendError(errorStatusCode.value(), String.format(errorMessage, remainTime))
                        return
                    }
                }

                val globalPreventTimeLimit = handler.getMethodAnnotation(GlobalPreventTimeLimit::class.java)
                if (globalPreventTimeLimit != null) {
                    val identity: String = globalPreventTimeLimit.value
                    val sec = globalTimeouts.apply(identity).toSeconds()

                    val (duplicate, remainTime) = isDuplicate(Scope.GLOBAL, identity, sec)
                    if (duplicate) {
                        response.sendError(errorStatusCode.value(), String.format(errorMessage, remainTime))
                        return
                    }
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
