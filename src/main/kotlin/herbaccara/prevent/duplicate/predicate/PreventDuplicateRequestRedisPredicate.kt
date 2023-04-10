package herbaccara.prevent.duplicate.predicate

import herbaccara.prevent.duplicate.PreventDuplicateRequestKey
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

class PreventDuplicateRequestRedisPredicate(
    private val timeout: Duration,
    private val redisTemplate: RedisTemplate<PreventDuplicateRequestKey, String>
) : PreventDuplicateRequestPredicate {

    override fun test(key: PreventDuplicateRequestKey): Boolean {
        if (redisTemplate.opsForValue().get(key) != null) {
            return true
        } else {
            redisTemplate.opsForValue().set(key, "true", timeout)
        }
        return false
    }
}
