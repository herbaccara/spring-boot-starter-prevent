package herbaccara.boot.autoconfigure.prevent

import herbaccara.boot.autoconfigure.prevent.duplicate.EnablePreventDuplicateRequestFilter
import herbaccara.boot.autoconfigure.prevent.timelimit.EnablePreventTimeLimitRequestFilter

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@EnablePreventDuplicateRequestFilter
@EnablePreventTimeLimitRequestFilter
annotation class EnablePreventRequestFilter
