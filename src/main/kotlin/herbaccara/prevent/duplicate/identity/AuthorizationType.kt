package herbaccara.prevent.duplicate.identity

enum class AuthorizationType(val prefix: String) {
    BEARER("Bearer "),
    BASIC("Basic ")
}
