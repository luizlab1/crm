package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class IntegrationBoundaryRulesTest {

    private val importedClasses by lazy { ArchUnitTestHelper.importAppClasses() }

    @Test
    fun infrastructure_should_only_depend_on_explicit_internal_and_external_packages() {
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
                "org.hibernate..",
                "org.flywaydb.."
            )
        rule.check(importedClasses)
    }
}
