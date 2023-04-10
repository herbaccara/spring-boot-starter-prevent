package herbaccara.prevent.timelimit.storage

import herbaccara.prevent.timelimit.PreventTimeLimitRequestKey
import java.time.LocalDateTime

interface PreventTimeLimitRequestStorage {

    fun get(key: PreventTimeLimitRequestKey): LocalDateTime?

    fun put(key: PreventTimeLimitRequestKey, value: LocalDateTime)
}
