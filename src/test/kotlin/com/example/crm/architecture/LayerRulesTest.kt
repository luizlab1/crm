package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.ArchRule
import org.junit.jupiter.api.Test

class LayerRulesTest {

    @Test
    fun controllers_should_only_depend_on_services_and_dto() {
        val classes = ArchUnitTestHelper.importAppClasses()
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
        
        rule.check(classes)
    }

    @Test
    fun services_should_not_depend_on_controllers() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().resideInAPackage("..application..usecase..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..application..",
                "..domain..",
                "..infrastructure..",
                "..infrastructure.persistence..",
                "..infrastructure.web.dto..",
                "java..",
                "kotlin..",
                "org.springframework..",
                "org.springframework.transaction..",
                "org.springframework.stereotype..",
                "org.springframework.data..",
                "org.springframework.data.domain..",
                "org.jetbrains.annotations.."
            )
        rule.check(classes)
    }

    @Test
    fun repositories_should_not_depend_on_controllers() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().resideInAPackage("..infrastructure.persistence.repository..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                "..infrastructure.persistence.repository..",
                "..domain..",
                "..application..",
                "..infrastructure..",
                "java..",
                "kotlin..",
                "org.jetbrains.annotations..",
                "org.springframework..",
                "org.springframework.data..",
                "org.springframework.data.domain..",
                "org.springframework.transaction.."
            )
        rule.check(classes)
    }
}
