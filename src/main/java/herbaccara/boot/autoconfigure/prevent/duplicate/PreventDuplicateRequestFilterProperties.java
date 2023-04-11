package herbaccara.boot.autoconfigure.prevent.duplicate;

import herbaccara.prevent.duplicate.PreventDuplicateRequestFilter;
import herbaccara.prevent.duplicate.RequestUriType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "prevent.duplicate")
public class PreventDuplicateRequestFilterProperties {

    private Duration timeout = PreventDuplicateRequestFilter.DEFAULT_TIMEOUT;
    private Integer order = PreventDuplicateRequestFilter.DEFAULT_FILTER_ORDER;
    private List<String> urlPatterns = List.of("/*");
    private List<HttpMethod> preventHttpMethods = PreventDuplicateRequestFilter.DEFAULT_PREVENT_HTTP_METHODS;
    private RequestUriType requestUriType = PreventDuplicateRequestFilter.DEFAULT_REQUEST_URI_TYPE;
    private Boolean withQueryString = PreventDuplicateRequestFilter.DEFAULT_WITH_QUERY_STRING;
    private Integer errorStatusCode = PreventDuplicateRequestFilter.DEFAULT_ERROR_STATUS_CODE.value();
    private String errorMessage = PreventDuplicateRequestFilter.DEFAULT_ERROR_MESSAGE;

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(final Duration timeout) {
        this.timeout = timeout;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public List<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(final List<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public List<HttpMethod> getPreventHttpMethods() {
        return preventHttpMethods;
    }

    public void setPreventHttpMethods(final List<HttpMethod> preventHttpMethods) {
        this.preventHttpMethods = preventHttpMethods;
    }

    public RequestUriType getRequestUriType() {
        return requestUriType;
    }

    public void setRequestUriType(final RequestUriType requestUriType) {
        this.requestUriType = requestUriType;
    }

    public Boolean getWithQueryString() {
        return withQueryString;
    }

    public void setWithQueryString(final Boolean withQueryString) {
        this.withQueryString = withQueryString;
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
