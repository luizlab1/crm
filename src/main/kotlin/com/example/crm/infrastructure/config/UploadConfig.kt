package com.example.crm.infrastructure.config

import com.example.crm.application.port.output.FileTypeRule
import com.example.crm.application.port.output.UploadSettings
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Locale

@Configuration
@EnableConfigurationProperties(UploadProperties::class)
class UploadConfig {

    @Bean
    fun uploadSettings(props: UploadProperties): UploadSettings {
        val defaults = toRule(props.defaults)
        val rules = props.rules.mapValues { toRule(it.value) }
        return UploadSettings(
            minQuality = props.minQuality,
            maxQuality = props.maxQuality,
            defaults = defaults,
            rules = rules
        )
    }

    private fun toRule(p: UploadProperties.RuleProperties): FileTypeRule = FileTypeRule(
        allowedExtensions = p.allowedExtensions.map { it.lowercase(Locale.ROOT) }.toSet(),
        maxSizeBytes = p.maxSizeBytes,
        maxWidth = p.maxWidth,
        maxHeight = p.maxHeight
    )
}
