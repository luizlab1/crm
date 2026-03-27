package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.ArchRule
import org.junit.jupiter.api.Test

class LayerRulesTest {

    private val importedClasses by lazy { ArchUnitTestHelper.importAppClasses() }

    @Test
    fun controllers_should_only_depend_on_allowed_packages() {
        val rule: ArchRule = classes().that().resideInAPackage("..infrastructure.web.controller..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..infrastructure.web.controller..",
                "..application..",
                "..domain..",
                "..infrastructure.web.dto..",
                "..infrastructure.web.mapper..",
                "..infrastructure.security..",
                "java..",
                "kotlin..",
                "org.jetbrains.annotations..",
                "org.springframework..",
                "jakarta..",
                "io.swagger.v3.oas.annotations.."
            )

        rule.check(importedClasses)
    }

    @Test
    fun services_should_not_depend_on_controllers() {
        val rule = noClasses().that().resideInAPackage("..application..usecase..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure.web.controller..")
        rule.check(importedClasses)
    }

    @Test
    fun repositories_should_not_depend_on_controllers() {
        val rule = noClasses().that().resideInAPackage("..infrastructure.persistence.repository..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure.web.controller..")
        rule.check(importedClasses)

    }

    @Test
    fun application_usecases_should_not_depend_on_infrastructure() {
        val rule = noClasses().that().resideInAPackage("..application..usecase..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
        rule.check(importedClasses)
    }
}
