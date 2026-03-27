package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class DomainIsolationRulesTest {

    @Test
    fun domain_should_not_depend_on_infrastructure() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().resideInAPackage("..domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..domain..",
                "..application..",
                "java..",
                "kotlin..",
                "org.jetbrains.annotations.."
            )
        rule.check(classes)
    }
}
