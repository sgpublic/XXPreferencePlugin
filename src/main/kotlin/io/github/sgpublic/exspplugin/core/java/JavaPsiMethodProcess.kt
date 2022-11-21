package io.github.sgpublic.exspplugin.core.java

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiType
import com.intellij.psi.impl.light.LightMethodBuilder
import io.github.sgpublic.exsp.annotations.ExSharedPreference
import io.github.sgpublic.exsp.annotations.ExValue
import io.github.sgpublic.exspplugin.base.PsiMethodBuilder
import io.github.sgpublic.exspplugin.base.PsiProcess
import io.github.sgpublic.exspplugin.util.*

class JavaPsiMethodProcess(clazz: PsiClass): PsiProcess<PsiClass, PsiMethod>(clazz) {
    override fun process(): Collection<PsiMethod> {
        val result = mutableListOf<PsiMethod>()

        val annotation = OriginElement.getAnnotation(ExSharedPreference::class.java.canonicalName) ?: return result

        if (OriginElement.hasAnnotation("lombok.Data")) {
            log.warn("DO NOT add @lombok.Data with @ExSharedPreference together! In class: $Name")
            return result
        }

        log.info("Process java class: $Name")

        val Editor = OriginElement.getEditorClass()

        PsiMethodBuilder(OriginElement.manager, JavaLanguage.INSTANCE, "edit")
            .addModifiers(PsiModifier.PUBLIC, PsiModifier.STATIC)
            .setMethodReturnType(Editor.getType())
            .setContainingClass(OriginElement)
            .also {
                it.navigationElement = annotation
            }
            .let {
                result.add(it)
            }

        if (OriginElement.fields.isEmpty()) {
            log.info("@ExSharedPreference class of $Name has empty fields.")
        }

        for (field in OriginElement.fields) {
            if (!field.hasAnnotation(ExValue::class.java.canonicalName) ||
                field.hasModifierProperty(PsiModifier.FINAL)) {
                continue
            }

            if (field.hasAnnotation("lombok.Data")) {
                log.warn("DO NOT add @lombok.Data with @ExSharedPreference together! In class: $Name")
                continue
            } else {
                log.info("Process java field: ${field.name}, in class: $Name")
            }

            PsiMethodBuilder(OriginElement.manager, JavaLanguage.INSTANCE, field.GetterName)
                .addModifiers(PsiModifier.PUBLIC, PsiModifier.STATIC)
                .setMethodReturnType(field.type)
                .setContainingClass(field.containingClass)
                .also {
                    it.navigationElement = field
                }
                .let {
                    result.add(it)
                }

            PsiMethodBuilder(OriginElement.manager, JavaLanguage.INSTANCE, field.SetterName)
                .addModifiers(PsiModifier.PUBLIC, PsiModifier.STATIC)
                .setMethodReturnType(PsiType.VOID)
                .addParameter("value", field.type)
                .setContainingClass(field.containingClass)
                .also {
                    it.navigationElement = field
                }
                .let {
                    result.add(it)
                }
        }

        return result
    }

    override val Name: String = OriginElement.name ?: ""
}