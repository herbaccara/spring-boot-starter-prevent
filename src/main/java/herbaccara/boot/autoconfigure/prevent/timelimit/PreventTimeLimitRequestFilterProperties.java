package herbaccara.boot.autoconfigure.prevent.timelimit;

import herbaccara.prevent.timelimit.PreventTimeLimitRequestFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@ConfigurationProperties(prefix = "prevent.time-limit")
public class PreventTimeLimitRequestFilterProperties {

    private Integer order = PreventTimeLimitRequestFilter.DEFAULT_FILTER_ORDER;
    private Duration defaultGlobalTimeout = PreventTimeLimitRequestFilter.DEFAULT_GLOBAL_TIMEOUT;
    private Map<String, Duration> globalTimeouts = Collections.emptyMap();
    private Integer errorStatusCode = PreventTimeLimitRequestFilter.DEFAULT_ERROR_STATUS_CODE.value();
    private String errorMessage = PreventTimeLimitRequestFilter.DEFAULT_ERROR_MESSAGE;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public Duration getDefaultGlobalTimeout() {
        return defaultGlobalTimeout;
    }

    public void setDefaultGlobalTimeout(final Duration defaultGlobalTimeout) {
        this.defaultGlobalTimeout = defaultGlobalTimeout;
    }

    public Map<String, Duration> getGlobalTimeouts() {
        return globalTimeouts;
    }

    public void setGlobalTimeouts(final Map<String, Duration> globalTimeouts) {
        this.globalTimeouts = globalTimeouts;
    }

    public Integer getErrorStatusCode() {
        return errorStatusCode;
    }

    public void setErrorStatusCode(final Integer errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
