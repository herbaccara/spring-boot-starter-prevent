package herbaccara.prevent.duplicate.predicate

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import herbaccara.prevent.duplicate.PreventDuplicateRequestKey
import java.time.Duration

class PreventDuplicateRequestLocalPredicate(timeout: Duration) : PreventDuplicateRequestPredicate {

    private val cache: Cache<PreventDuplicateRequestKey, String> = CacheBuilder.newBuilder()
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
