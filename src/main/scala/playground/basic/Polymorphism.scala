// Scalaのポリモーフィズム

package playground.basic

object Polymorphism extends App {
  // Index
  MethodOverloading()
  Inheritance()
  PimpMyLibraryPattern()
  TypeClassAndDependentMethodTypes()
  MagnetPattern()

  // Method Overloading
  object MethodOverloading {
    case class CreateCommand()
    case class UpdateCommand()

    case class CreateResult(createOk: Boolean)
    case class UpdateResult(updateOk: Boolean)

    class CommandHandler {
      def handle(createCommand: CreateCommand): CreateResult = CreateResult(true)
      def handle(updateCommand: UpdateCommand): UpdateResult = UpdateResult(true)
    }

    def apply() = {
      val handler = new CommandHandler()
      val result1: CreateResult = handler.handle(CreateCommand())
      val result2: UpdateResult = handler.handle(UpdateCommand())
      println(result1)
      println(result2)
    }
  }

  // 継承
  object Inheritance {
    case class CreateCommand()
    case class UpdateCommand()

    case class CreateResult(createOk: Boolean)
    case class UpdateResult(updateOk: Boolean)

    trait CommandHandler[C, R] {
      def handle(command: C): R
    }

    class CreateCommandHandler extends CommandHandler[CreateCommand, CreateResult] {
      override def handle(command: CreateCommand): CreateResult = CreateResult(true)
    }

    class UpdateCommandHandler extends CommandHandler[UpdateCommand, UpdateResult] {
      override def handle(command: UpdateCommand): UpdateResult = UpdateResult(true)
    }

    def apply() = {
      val handler1 = new CreateCommandHandler()
      val handler2 = new UpdateCommandHandler()
      val result1: CreateResult = handler1.handle(CreateCommand())
      val result2: UpdateResult = handler2.handle(UpdateCommand())
      println(result1)
      println(result2)
    }
  }

  // Pimp my Library Pattern
  object PimpMyLibraryPattern {
    case class CreateCommand()
    case class UpdateCommand()

    case class CreateResult(createOk: Boolean)
    case class UpdateResult(updateOk: Boolean)

    class CommandHandler

    implicit class CreateCommandHandler(handler: CommandHandler) {
      def handle(command: CreateCommand): CreateResult = CreateResult(true)
    }

    implicit class UpdateCommandHandler(handler: CommandHandler) {
      def handle(command: UpdateCommand): UpdateResult = UpdateResult(true)
    }

    def apply() = {
      val handler = new CommandHandler()
      val result1: CreateResult = handler.handle(CreateCommand())
      val result2: UpdateResult = handler.handle(UpdateCommand())
      println(result1)
      println(result2)
    }
  }

  // Type Class And Dependent Method Types
  object TypeClassAndDependentMethodTypes {
    case class CreateCommand()
    case class UpdateCommand()

    case class CreateResult(createOk: Boolean)
    case class UpdateResult(updateOk: Boolean)

    trait Command[A, R] {
      type Result = R
      def apply(command: A): Result
    }

    class CommandHandler {
      def handle[A, R](command: A)(implicit handler: Command[A, R]): handler.Result = handler(command)
    }

    implicit val createCommand = new Command[CreateCommand, CreateResult] {
      def apply(command: CreateCommand): Result = CreateResult(true)
    }

    implicit val updateCommand = new Command[UpdateCommand, UpdateResult] {
      def apply(command: UpdateCommand): Result = UpdateResult(true)
    }

    def apply() = {
      val handler = new CommandHandler()
      val result1: CreateResult = handler.handle(CreateCommand())
      val result2: UpdateResult = handler.handle(UpdateCommand())
      println(result1)
      println(result2)
    }
  }

  // Magnet Pattern
  object MagnetPattern {
    import scala.language.implicitConversions

    case class CreateCommand()
    case class UpdateCommand()

    case class CreateResult(createOk: Boolean)
    case class UpdateResult(updateOk: Boolean)

    trait CommandMagnet[R] {
      type Result = R
      def apply(): Result
    }

    implicit def createCommandHandler(createCommand: CreateCommand): CommandMagnet[CreateResult] = new CommandMagnet[CreateResult] {
      def apply(): Result = CreateResult(true)
    }

    implicit def updateCommandHandler(updateCommand: UpdateCommand): CommandMagnet[UpdateResult] = new CommandMagnet[UpdateResult] {
      def apply(): Result = UpdateResult(true)
    }

    class CommandHandler {
      def handle[T](magnet: CommandMagnet[T]): magnet.Result = magnet()
    }

    def apply() = {
      val handler = new CommandHandler()
      val result1: CreateResult = handler.handle(CreateCommand())
      val result2: UpdateResult = handler.handle(UpdateCommand())
      println(result1)
      println(result2)
    }
  }
}

