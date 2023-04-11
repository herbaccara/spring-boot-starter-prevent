// package herbaccara.boot.autoconfigure.prevent.duplicate
//
// import herbaccara.prevent.duplicate.PreventDuplicateRequestFilter
// import herbaccara.prevent.duplicate.RequestUriType
// import org.springframework.boot.context.properties.ConfigurationProperties
// import org.springframework.http.HttpMethod
// import java.time.Duration
//
// @ConfigurationProperties(prefix = "prevent.duplicate")
// data class PreventDuplicateRequestFilterProperties(
//    val timeout: Duration = PreventDuplicateRequestFilter.DEFAULT_TIMEOUT,
//    val order: Int = PreventDuplicateRequestFilter.DEFAULT_FILTER_ORDER,
//    val urlPatterns: List<String> = listOf("/*"),
//    val preventHttpMethods: List<HttpMethod> = PreventDuplicateRequestFilter.DEFAULT_PREVENT_HTTP_METHODS,
//    val requestUriType: RequestUriType = PreventDuplicateRequestFilter.DEFAULT_REQUEST_URI_TYPE,
//    val withQueryString: Boolean = PreventDuplicateRequestFilter.DEFAULT_WITH_QUERY_STRING,
//    val errorStatusCode: Int = PreventDuplicateRequestFilter.DEFAULT_ERROR_STATUS_CODE.value(),
//    val errorMessage: String = PreventDuplicateRequestFilter.DEFAULT_ERROR_MESSAGE
// )
