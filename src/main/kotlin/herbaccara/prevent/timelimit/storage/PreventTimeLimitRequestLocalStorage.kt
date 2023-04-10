package herbaccara.prevent.timelimit.storage

import herbaccara.prevent.timelimit.PreventTimeLimitRequestKey
import java.time.LocalDateTime

class PreventTimeLimitRequestLocalStorage : PreventTimeLimitRequestStorage {

    private val map = mutableMapOf<PreventTimeLimitRequestKey, LocalDateTime>()

    override fun get(key: PreventTimeLimitRequestKey): LocalDateTime? = map[key]

    override fun put(key: PreventTimeLimitRequestKey, value: LocalDateTime) {
        map[key] = value
    }
}
