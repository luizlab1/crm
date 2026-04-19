package com.example.crm.infrastructure.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfig {

    @Bean
    fun flyway(dataSource: DataSource): Flyway {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .baselineVersion("0")
            .load()
        flyway.migrate()
        return flyway
    }

    @Bean
    fun flywayJpaDependency(): BeanFactoryPostProcessor = BeanFactoryPostProcessor { beanFactory ->
        setOf("entityManagerFactory", "transactionManager").forEach { beanName ->
            runCatching {
                val def = (beanFactory as ConfigurableListableBeanFactory).getBeanDefinition(beanName)
                def.setDependsOn(*(def.dependsOn ?: emptyArray()), "flyway")
            }
        }
    }
}
