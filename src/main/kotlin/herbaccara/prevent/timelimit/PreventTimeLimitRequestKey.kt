package herbaccara.prevent.timelimit

data class PreventTimeLimitRequestKey(
    val scope: Scope,
    val ip: String,
    val mac: String,
    val identity: String
)
