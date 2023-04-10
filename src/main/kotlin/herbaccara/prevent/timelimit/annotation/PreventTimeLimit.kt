package herbaccara.prevent.timelimit.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PreventTimeLimit(val sec: Long = 30)
