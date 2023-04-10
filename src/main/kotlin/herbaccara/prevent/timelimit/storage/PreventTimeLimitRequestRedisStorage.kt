package herbaccara.prevent.timelimit.storage

import herbaccara.prevent.timelimit.PreventTimeLimitRequestKey
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDateTime

class PreventTimeLimitRequestRedisStorage(
    private val redisTemplate: RedisTemplate<String, Any>
) : PreventTimeLimitRequestStorage {

    companion object {
        private const val REDIS_HASH_KEY = "PreventTimeLimitRequest"
    }

    override fun get(key: PreventTimeLimitRequestKey): LocalDateTime? {
        val operations: HashOperations<String, Any, Any> = redisTemplate.opsForHash()
        return operations.get(REDIS_HASH_KEY, key) as LocalDateTime?
    }

    override fun put(key: PreventTimeLimitRequestKey, value: LocalDateTime) {
        val operations: HashOperations<String, Any, Any> = redisTemplate.opsForHash()
        operations.put(REDIS_HASH_KEY, key, value)
    }
}
