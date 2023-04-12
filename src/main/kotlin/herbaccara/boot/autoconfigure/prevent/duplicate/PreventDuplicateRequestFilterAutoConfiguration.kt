package herbaccara.boot.autoconfigure.prevent.duplicate

import com.fasterxml.jackson.databind.ObjectMapper
import herbaccara.prevent.duplicate.PreventDuplicateRequestFilter
import herbaccara.prevent.duplicate.PreventDuplicateRequestKey
import herbaccara.prevent.duplicate.identity.DefaultIdentityStrategy
import herbaccara.prevent.duplicate.identity.IdentityStrategy
import herbaccara.prevent.duplicate.predicate.PreventDuplicateRequestCaffeinePredicate
import herbaccara.prevent.duplicate.predicate.PreventDuplicateRequestLocalPredicate
import herbaccara.prevent.duplicate.predicate.PreventDuplicateRequestPredicate
import herbaccara.prevent.duplicate.predicate.PreventDuplicateRequestRedisPredicate
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
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer::class)
@EnableConfigurationProperties(PreventDuplicateRequestFilterProperties::class)
class PreventDuplicateRequestFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IdentityStrategy::class)
    fun defaultIdentityStrategy(): IdentityStrategy {
        return DefaultIdentityStrategy()
    }

    @Bean
    fun preventDuplicateRequestFilter(
        requestMappingHandlerMapping: RequestMappingHandlerMapping,
        predicate: PreventDuplicateRequestPredicate,
        identityStrategy: IdentityStrategy,
        properties: PreventDuplicateRequestFilterProperties
    ): FilterRegistrationBean<Filter> {
        val filter = PreventDuplicateRequestFilter(
            requestMappingHandlerMapping,
            predicate,
            identityStrategy,
            properties.preventHttpMethods,
            properties.requestUriType,
            properties.withQueryString,
            HttpStatus.valueOf(properties.errorStatusCode),
            properties.errorMessage
        )

        return FilterRegistrationBean<Filter>().apply {
            setFilter(filter)
            order = properties.order
            addUrlPatterns(*properties.urlPatterns.toTypedArray())
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun preventDuplicateRequestLocalPredicate(properties: PreventDuplicateRequestFilterProperties): PreventDuplicateRequestPredicate {
        return PreventDuplicateRequestLocalPredicate(properties.timeout)
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring", name = ["cache.type"], havingValue = "caffeine")
    fun preventDuplicateRequestCaffeinePredicate(properties: PreventDuplicateRequestFilterProperties): PreventDuplicateRequestPredicate {
        return PreventDuplicateRequestCaffeinePredicate(properties.timeout)
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "spring", name = ["cache.type"], havingValue = "redis")
    class PreventTimeLimitRequestRedisPredicateConfig {

        @Bean
        fun preventDuplicateRequestRedisPredicate(
            @Qualifier("preventDuplicateRequestRedisTemplate")
            redisTemplate: RedisTemplate<PreventDuplicateRequestKey, String>,
            properties: PreventDuplicateRequestFilterProperties
        ): PreventDuplicateRequestPredicate {
            return PreventDuplicateRequestRedisPredicate(properties.timeout, redisTemplate)
        }

        @Bean
        fun preventDuplicateRequestRedisTemplate(
            connectionFactory: RedisConnectionFactory,
            objectMapper: ObjectMapper
        ): RedisTemplate<PreventDuplicateRequestKey, String> {
            return RedisTemplate<PreventDuplicateRequestKey, String>().apply {
                setConnectionFactory(connectionFactory)
                keySerializer = Jackson2JsonRedisSerializer(objectMapper, PreventDuplicateRequestKey::class.java)
                valueSerializer = StringRedisSerializer()
            }
        }
    }
}
