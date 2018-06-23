package com.makergenerator.usage

import java.util.UUID

import com.makergenerator.macros.MakerGeneratorMacros._
import org.specs2.mutable.Spec


class MakerGenaratorUsageTest extends Spec {

  "Maker Generator" should {

    "create a maker that sets UUID field" in {

      val personMaker = aMaker[Person]
      val id = UUID.randomUUID
      val person: Person = personMaker.withId(id).make

      person.id must be_===(id)

    }

    "create a maker that sets String field" in {

      val personMaker = aMaker[Person]
      val person: Person = personMaker.withName("Stan").make

      person.name must be_===("Stan")

    }

    "create a maker that sets Int field" in {

      val personMaker = aMaker[Person]
      val person: Person = personMaker.withAge(21).make

      person.age must be_===(21)

    }
  }

}

case class Person(id: UUID, name: String, age: Int)
