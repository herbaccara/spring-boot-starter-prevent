package herbaccara.prevent.duplicate

data class PreventDuplicateRequestKey(
    val requestUri: String,
    val queryString: String?,
    val httpMethod: String,
    val identity: String
)
