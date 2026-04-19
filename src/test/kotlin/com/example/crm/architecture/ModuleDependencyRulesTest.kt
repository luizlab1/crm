package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class ModuleDependencyRulesTest {

    private val importedClasses by lazy { ArchUnitTestHelper.importAppClasses() }

    @Test
    fun infrastructure_should_not_use_generic_third_party_allowlist() {
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
                "org.slf4j..",
                "org.hibernate..",
                "org.springdoc..",
                "jakarta..",
                "javax..",
                "io.jsonwebtoken..",
                "io.swagger.v3..",
                "com.fasterxml.jackson..",
                "org.flywaydb.."
            )
        rule.check(importedClasses)
    }
}
