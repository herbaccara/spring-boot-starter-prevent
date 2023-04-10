package herbaccara.boot.autoconfigure.prevent

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
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@AutoConfiguration
@EnableConfigurationProperties(PreventRequestFilterProperties::class)
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
        properties: PreventRequestFilterProperties
    ): FilterRegistrationBean<Filter> {
        val duplicateProperties = properties.duplicate

        val filter = PreventDuplicateRequestFilter(
            requestMappingHandlerMapping,
            predicate,
            identityStrategy,
            duplicateProperties.preventHttpMethods,
            duplicateProperties.requestUriType,
            duplicateProperties.withQueryString,
            HttpStatus.valueOf(duplicateProperties.errorStatusCode),
            duplicateProperties.errorMessage
        )

        return FilterRegistrationBean<Filter>().apply {
            setFilter(filter)
            order = duplicateProperties.order
            addUrlPatterns(*duplicateProperties.urlPatterns.toTypedArray())
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun preventDuplicateRequestLocalPredicate(properties: PreventRequestFilterProperties): PreventDuplicateRequestPredicate {
        return PreventDuplicateRequestLocalPredicate(properties.duplicate.timeout)
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring", name = ["cache.type"], havingValue = "caffeine")
    fun preventDuplicateRequestCaffeinePredicate(properties: PreventRequestFilterProperties): PreventDuplicateRequestPredicate {
        return PreventDuplicateRequestCaffeinePredicate(properties.duplicate.timeout)
    }

    @AutoConfiguration
    @ConditionalOnProperty(prefix = "spring", name = ["cache.type"], havingValue = "redis")
    class PreventTimeLimitRequestRedisPredicateConfig {

        @Bean
        fun preventDuplicateRequestRedisPredicate(
            @Qualifier("preventDuplicateRequestRedisTemplate")
            redisTemplate: RedisTemplate<PreventDuplicateRequestKey, String>,
            properties: PreventRequestFilterProperties
        ): PreventDuplicateRequestPredicate {
            return PreventDuplicateRequestRedisPredicate(properties.duplicate.timeout, redisTemplate)
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
