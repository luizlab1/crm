package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class NamingAndAnnotationRulesTest {

    private val importedClasses by lazy { ArchUnitTestHelper.importAppClasses() }

    @Test
    fun controller_name_should_imply_controller_package() {
        val rule = classes().that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..infrastructure.web.controller..")
        rule.check(importedClasses)
    }

    @Test
    fun application_usecaseimpl_should_be_annotated_with_service() {
        val rule = classes().that().resideInAPackage("..application..usecase..")
            .and().haveSimpleNameEndingWith("UseCaseImpl")
            .should().beAnnotatedWith(org.springframework.stereotype.Service::class.java)
        rule.check(importedClasses)
    }

    @Test
    fun repository_name_should_imply_repository_package() {
        val rule = classes().that().haveSimpleNameEndingWith("Repository")
            .should().resideInAnyPackage(
                "..infrastructure.persistence.repository..",
                "..domain.repository.."
            )
        rule.check(importedClasses)
    }
}
