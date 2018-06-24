# maker-generator
Using Scala macros to generate test data 'makers'

For example, given the following case class
``` scala
case class Person(id: UUID, name: String, age: Int)
```

The macro will generate an instance of `Maker[Person]` as follows
``` scala
  class PersonMaker extends Maker[Person] {
    var idVal: java.util.UUID = UUID.randomUUID
    var nameVal: String = "defaultName"
    var ageVal: Int = 0
    def withId(id: java.util.UUID) = {
      idVal = id
      this
    }
    def withName(name: String) = {
      nameVal = name
      this
    }
    def withAge(age: Int) = {
      ageVal = age
      this
    }
    override def make: com.makergenerator.usage.Person = Person(idVal, nameVal, ageVal)
  }
```

The 'maker' can then be used as follows
``` scala
val personMaker = aMaker[Person]
val person = personMaker.withName("Joe").withAge(32).make
```

It is still in POC stage, and for supports only field types `String`, `Int` and `UUID`
