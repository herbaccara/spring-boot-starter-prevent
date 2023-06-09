package herbaccara.boot.autoconfigure.prevent.timelimit

import org.springframework.context.annotation.Import
import java.lang.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(PreventTimeLimitRequestFilterAutoConfiguration::class)
annotation class EnablePreventTimeLimitRequestFilter
