package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class ModuleDependencyRulesTest {

    @Test
    fun modules_should_not_depend_on_each_other_unless_whitelisted() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().resideInAPackage("..infrastructure..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..infrastructure..",
                "..application..",
                "..domain..",
                "java..",
                "kotlin..",
                "org..",
                "jakarta..",
                "io..",
                "com.fasterxml.."
            )
        rule.check(classes)
    }
}
