package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class IntegrationBoundaryRulesTest {

    @Test
    fun external_clients_should_be_used_only_by_infrastructure_or_adapters() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().resideInAPackage("..infrastructure..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..infrastructure..",
                "..application..",
                "..domain..",
                "java..",
                "kotlin..",
                "kotlin.jvm..",
                "kotlin.jvm.internal..",
                "kotlin.reflect..",
                "kotlinx..",
                "org.jetbrains.annotations..",
                "org.springframework..",
                "org.springframework.boot..",
                "jakarta..",
                "javax..",
                "com.fasterxml.jackson..",
                "org.slf4j..",
                "io.jsonwebtoken..",
                "io.swagger.v3..",
                "org.springdoc..",
                "org.hibernate.."
            )
        rule.check(classes)
    }
}
