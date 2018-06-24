package com.makergenerator.macros

import scala.reflect.macros.whitebox
import scala.language.experimental.macros

object MakerGeneratorMacros {

  def aMaker[T]: Maker[T] = macro MacroImpl.createMakerImpl[T]

}

object MacroImpl {

  def createMakerImpl[T: context.WeakTypeTag](context: whitebox.Context): context.Expr[Maker[T]] = {
    import context.universe._
    val typeParam = weakTypeOf[T]
    val makerClassName = TypeName(s"${typeParam}Maker")

    val fields = typeParam.decls.collectFirst {
      case method: MethodSymbol if method.isPrimaryConstructor ⇒ method
    }.get.paramLists.head

    val initializers = fields.map { field =>
      val fieldName = field.name.toTermName.decodedName.toString
      val initializerName = initializerFieldName(context, fieldName)
      val returnType = field.typeSignature
      val defaultValue = getDefaultValue(context, fieldName, returnType.toString)
      q"var $initializerName: $returnType = $defaultValue"
    }

    val fieldSetters = fields.map { field =>
      val fieldName = field.name.toTermName.decodedName.toString
      val functionName = TermName(s"with${fieldName.capitalize}")
      val paramName = field.name.toTermName
      val returnType: context.universe.Type = field.typeSignature
      val initializerName = initializerFieldName(context, fieldName)
      q"""
        def $functionName($paramName: $returnType) = {
          $initializerName = $paramName
          this
        }
      """
    }

    val constructorParams = fields.map { field =>
      initializerFieldName(context, field.name.toTermName.decodedName.toString)
    }

    val result =
      q"""
         import com.makergenerator.macros.Maker
         class $makerClassName extends Maker[$typeParam] {
           ..$initializers
           ..$fieldSetters
           override def make: $typeParam = ${typeParam.typeSymbol.companion}(..$constructorParams)
        }
        new $makerClassName
      """
    println(showCode(result))
    context.Expr[Maker[T]] {result}
  }

  private def initializerFieldName(context: whitebox.Context, fieldName: String) = {
    import context.universe._
    TermName(s"${fieldName}Val")
  }

  private def getDefaultValue(context: whitebox.Context, fieldName: String, fieldType: String) = {
    import context.universe._
    fieldType match {
      case "String" =>
        val defaultValue = s"default${fieldName.capitalize}"
        q"$defaultValue"
      case "java.util.UUID" => q"UUID.randomUUID"
      case "Int" => q"0"
      case _ => q"null"
    }
  }
}

trait Maker[T] {
  def make: T
}

