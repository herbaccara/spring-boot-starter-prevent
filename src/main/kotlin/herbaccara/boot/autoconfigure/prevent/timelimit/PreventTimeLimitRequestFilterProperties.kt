package herbaccara.boot.autoconfigure.prevent.timelimit

import herbaccara.prevent.timelimit.PreventTimeLimitRequestFilter
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "prevent.time-limit")
data class PreventTimeLimitRequestFilterProperties(
    val order: Int = PreventTimeLimitRequestFilter.DEFAULT_FILTER_ORDER,
    val defaultGlobalTimeout: Duration = PreventTimeLimitRequestFilter.DEFAULT_GLOBAL_TIMEOUT,
    val globalTimeouts: Map<String, Duration> = emptyMap(),
    val errorStatusCode: Int = PreventTimeLimitRequestFilter.DEFAULT_ERROR_STATUS_CODE.value(),
    val errorMessage: String = PreventTimeLimitRequestFilter.DEFAULT_ERROR_MESSAGE
)
