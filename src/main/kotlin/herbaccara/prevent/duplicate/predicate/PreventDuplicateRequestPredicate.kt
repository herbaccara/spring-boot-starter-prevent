package herbaccara.prevent.duplicate.predicate

import herbaccara.prevent.duplicate.PreventDuplicateRequestKey
import java.util.function.Predicate

interface PreventDuplicateRequestPredicate : Predicate<PreventDuplicateRequestKey>
