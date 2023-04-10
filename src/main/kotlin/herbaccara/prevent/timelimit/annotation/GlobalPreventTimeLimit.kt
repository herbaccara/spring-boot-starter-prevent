package herbaccara.prevent.timelimit.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GlobalPreventTimeLimit(val value: String)
