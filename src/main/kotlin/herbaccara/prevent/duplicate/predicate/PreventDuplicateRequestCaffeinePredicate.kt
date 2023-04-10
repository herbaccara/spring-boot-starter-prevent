package herbaccara.prevent.duplicate.predicate

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import herbaccara.prevent.duplicate.PreventDuplicateRequestKey
import java.time.Duration

class PreventDuplicateRequestCaffeinePredicate(timeout: Duration) : PreventDuplicateRequestPredicate {

    private val cache: Cache<PreventDuplicateRequestKey, String> = Caffeine.newBuilder()
        .expireAfterWrite(timeout)
        .build()

    override fun test(key: PreventDuplicateRequestKey): Boolean {
        if (cache.getIfPresent(key) != null) {
            return true
        } else {
            cache.put(key, "true")
        }
        return false
    }
}
