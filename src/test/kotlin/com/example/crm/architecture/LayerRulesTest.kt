package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class LayerRulesTest {

    private val importedClasses by lazy { ArchUnitTestHelper.importAppClasses() }

    @Test
    fun controllers_should_only_depend_on_allowed_packages() {
        val rule = classes().that().resideInAPackage("..controller..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..controller..",
                "..service..",
                "..entity..",
                "..dto..",
                "..exception..",
                "..infrastructure.security..",
                "..infrastructure.config..",
                "..infrastructure.web.config..",
                "..application.port.output..",
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
        val rule = noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
        rule.check(importedClasses)
    }

    @Test
    fun repositories_should_not_depend_on_controllers() {
        val rule = noClasses().that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
        rule.check(importedClasses)
    }

    @Test
    fun services_should_not_depend_on_infrastructure_except_ports() {
        val rule = noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
        rule.check(importedClasses)
    }

    @Test
    fun entities_should_not_depend_on_services_or_controllers() {
        val rule = noClasses().that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..")
        rule.check(importedClasses)
    }
}
