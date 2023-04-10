package herbaccara.boot.autoconfigure.prevent

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnablePreventDuplicateRequestFilter
@EnablePreventTimeLimitRequestFilter
annotation class EnablePreventRequestFilter
