package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class DomainIsolationRulesTest {

    private val importedClasses by lazy { ArchUnitTestHelper.importAppClasses() }

    @Test
    fun domain_should_not_depend_on_infrastructure() {
        val rule = classes().that().resideInAPackage("..domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..domain..",
                "..application..",
                "java..",
                "kotlin..",
                "org.jetbrains.annotations.."
            )
        rule.check(importedClasses)
    }
}
