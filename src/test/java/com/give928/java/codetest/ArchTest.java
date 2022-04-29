package com.give928.java.codetest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.elements.ClassesShouldConjunction;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class ArchTest {
    @Test
    void packageDependencyTest() {
        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.give928.java.codetest");

        /**
         * ..domain.. 패키지에 있는 클래스는 ..study.., ..member.., ..domain..에서 참조 가능
         *
         * ..member.. 패키지에 있는 클래스는 ..study.., ..member.. 에서만 참조 가능
         * (반대로) ..domain.. 패키지는 ..member.. 패키지를 참조하지 못한다.
         *
         * ..study.. 패키지에 있는 클래스는 ..study.. 에서만 참조 가능
         *
         * 순환 참조는 없어야 한다.
         */
        ClassesShouldConjunction domainPackageRule = classes().that().resideInAPackage("..domain..")
                .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..study..", "..member..", "..domain..");
        domainPackageRule.check(javaClasses);

        ClassesShouldConjunction memberPackageRule = noClasses().that().resideInAPackage("..domain..")
                .should().accessClassesThat().resideInAnyPackage("..member");
        memberPackageRule.check(javaClasses);

        ClassesShouldConjunction studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..")
                .should().accessClassesThat().resideInAnyPackage("..study..");
        studyPackageRule.check(javaClasses);

        // javatest 아래 패키지들을 slice 해서 순환참조 확인
        ArchRule freeOfCycles = slices().matching("..codetest.(*)..")
                .should().beFreeOfCycles();
        freeOfCycles.check(javaClasses);
    }
}
