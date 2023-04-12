package herbaccara.boot.autoconfigure.prevent.timelimit

import com.fasterxml.jackson.databind.ObjectMapper
import herbaccara.prevent.timelimit.PreventTimeLimitRequestFilter
import herbaccara.prevent.timelimit.PreventTimeLimitRequestKey
import herbaccara.prevent.timelimit.annotation.GlobalPreventTimeLimit
import herbaccara.prevent.timelimit.annotation.PreventTimeLimit
import herbaccara.prevent.timelimit.storage.PreventTimeLimitRequestLocalStorage
import herbaccara.prevent.timelimit.storage.PreventTimeLimitRequestRedisStorage
import herbaccara.prevent.timelimit.storage.PreventTimeLimitRequestStorage
import jakarta.servlet.Filter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.time.LocalDateTime

@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer::class)
@EnableConfigurationProperties(PreventTimeLimitRequestFilterProperties::class)
class PreventTimeLimitRequestFilterAutoConfiguration {

    private fun patternsConditionUrls(mappingInfo: RequestMappingInfo): Set<String> {
        return mappingInfo.patternsCondition?.patterns ?: emptySet()
    }

    private fun pathPatternsConditionUrls(mappingInfo: RequestMappingInfo): Set<String> {
        return mappingInfo.pathPatternsCondition?.patterns?.map { it.patternString }?.toSet() ?: emptySet()
    }

    @Bean
    fun preventTimeLimitRequestFilter(
        requestMappingHandlerMapping: RequestMappingHandlerMapping,
        storage: PreventTimeLimitRequestStorage,
        properties: PreventTimeLimitRequestFilterProperties
    ): FilterRegistrationBean<Filter> {
        val urlPatterns = requestMappingHandlerMapping.handlerMethods
            .flatMap { (mappingInfo, handlerMethod) ->
                val preventTimeLimit = handlerMethod.getMethodAnnotation(PreventTimeLimit::class.java)
                val globalPreventTimeLimit = handlerMethod.getMethodAnnotation(GlobalPreventTimeLimit::class.java)

                val strings = mutableSetOf<String>()
                if ((globalPreventTimeLimit != null && globalPreventTimeLimit.value.isBlank().not()) || (preventTimeLimit != null && preventTimeLimit.sec > 0)) {
                    strings.addAll(pathPatternsConditionUrls(mappingInfo))
                    strings.addAll(patternsConditionUrls(mappingInfo))
                }
                strings
            }
            .toSet()

        val defaultGlobalTimeout = properties.defaultGlobalTimeout
        val globalTimeouts = properties.globalTimeouts

        val filter = PreventTimeLimitRequestFilter(
            requestMappingHandlerMapping,
            storage,
            { globalTimeouts[it] ?: defaultGlobalTimeout },
            HttpStatus.valueOf(properties.errorStatusCode),
            properties.errorMessage
        )

        return FilterRegistrationBean<Filter>().apply {
            setFilter(filter)
            order = properties.order
            setUrlPatterns(urlPatterns)
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun preventTimeLimitRequestStorage(): PreventTimeLimitRequestStorage {
        return PreventTimeLimitRequestLocalStorage()
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "spring", name = ["cache.type"], havingValue = "redis")
    class PreventTimeLimitRequestRedisStorageConfig {

        @Bean
        fun preventTimeLimitRequestRedisStorage(
            @Qualifier("preventTimeLimitRequestRedisTemplate") redisTemplate: RedisTemplate<String, Any>
        ): PreventTimeLimitRequestStorage {
            return PreventTimeLimitRequestRedisStorage(redisTemplate)
        }

        @Bean
        fun preventTimeLimitRequestRedisTemplate(
            connectionFactory: RedisConnectionFactory,
            objectMapper: ObjectMapper
        ): RedisTemplate<String, Any> {
            return RedisTemplate<String, Any>().apply {
                setConnectionFactory(connectionFactory)
                hashKeySerializer = Jackson2JsonRedisSerializer(objectMapper, PreventTimeLimitRequestKey::class.java)
                hashValueSerializer = Jackson2JsonRedisSerializer(objectMapper, LocalDateTime::class.java)
            }
        }
    }
}
