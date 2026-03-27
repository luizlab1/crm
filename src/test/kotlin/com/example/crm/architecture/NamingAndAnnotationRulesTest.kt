package com.example.crm.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class NamingAndAnnotationRulesTest {

    @Test
    fun controllers_name_and_package_should_match() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..infrastructure.web.controller..")
        rule.check(classes)
    }

    @Test
    fun services_name_and_annotation_should_match() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().haveSimpleNameEndingWith("UseCaseImpl").or().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(org.springframework.stereotype.Service::class.java)
        rule.check(classes)
    }

    @Test
    fun repositories_name_and_package_should_match() {
        val classes = ArchUnitTestHelper.importAppClasses()
        val rule = classes().that().haveSimpleNameEndingWith("Repository")
            .should().resideInAnyPackage(
                "..infrastructure.persistence.repository..",
                "..domain.repository.."
            )
        rule.check(classes)
    }
}
