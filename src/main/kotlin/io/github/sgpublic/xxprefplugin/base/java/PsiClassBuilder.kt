package io.github.sgpublic.xxprefplugin.base.java

import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.*
import com.intellij.psi.impl.light.LightPsiClassBuilder
import java.util.*

interface PsiClassBuilder

open class JavaPsiClassBuilder(
    context: PsiElement, simpleName: String,
    private val mQualifiedName: String,
) : LightPsiClassBuilder(context, simpleName), SyntheticElement, PsiClassBuilder {
    fun addModifiers(vararg modifiers: String): JavaPsiClassBuilder {
        for (modifier in modifiers) {
            modifierList.addModifier(modifier)
        }
        return this
    }

    fun setModifiers(vararg modifiers: String): JavaPsiClassBuilder {
        modifierList.clearModifiers()
        return addModifiers(*modifiers)
    }

    private val constructors: LinkedList<PsiMethod> = LinkedList()
    fun addConstructor(vararg methods: PsiMethod): JavaPsiClassBuilder {
        constructors.addAll(methods)
        return this
    }

    override fun getConstructors(): Array<PsiMethod> {
        return constructors.toTypedArray()
    }

//    private var mContainingFile: PsiFile? = null

    override fun setContainingClass(containingClass: PsiClass?): JavaPsiClassBuilder {
//        mContainingFile = containingClass?.containingFile
        super.setContainingClass(containingClass)
        return this
    }

//    override fun getContainingFile(): PsiFile? {
//        return mContainingFile ?: context?.containingFile
//    }

    fun addParameterType(types: PsiTypeParameterList): JavaPsiClassBuilder {
        for (type in types.typeParameters) {
            addParameterType(type)
        }
        return this
    }

    fun addParameterType(type: PsiTypeParameter): JavaPsiClassBuilder {
        typeParameterList.addParameter(type)
        return this
    }

    override fun getScope(): PsiElement {
        return containingClass?.scope ?: super.getScope()
    }

    override fun getQualifiedName(): String {
        return mQualifiedName
    }

    fun addExtends(type: String): JavaPsiClassBuilder {
        extendsList.addReference(type)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as JavaPsiClassBuilder
        return mQualifiedName == that.mQualifiedName
    }

    override fun hashCode(): Int {
        return mQualifiedName.hashCode()
    }
}