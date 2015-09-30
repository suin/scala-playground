// Eitherをforでつなげる方法
package playground.basic

object EitherChain extends App {
  def countDown(input: Int): Either[Throwable, Int] = {
    if (input <= 0) Left(new Error("input must be greater than zero"))
    else Right(input - 1)
  }

  def countDownWithNestedMatchers(input: Int): Either[Throwable, Int] = {
    countDown(input) match {
      case Left(err) => Left(err)
      case Right(count) =>
        countDown(count) match {
          case Left(err) => Left(err)
          case Right(count) =>
            countDown(count)
        }
    }
  }

  def countDownWithFlatMap(input: Int): Either[Throwable, Int] = {
    countDown(input).right.flatMap { count =>
      countDown(count).right.flatMap { count =>
        countDown(count)
      }
    }
  }

  def countDownWithFor(input: Int): Either[Throwable, Int] = {
    for {
      count <- countDown(input).right
      count <- countDown(count).right
      count <- countDown(count).right
    } yield count
  }

  List(
    countDownWithNestedMatchers _,
    countDownWithFlatMap _,
    countDownWithFor _
  ).foreach { countDown =>
    assume(countDown(3) == Right(0))
  }

  List(
    countDownWithNestedMatchers _,
    countDownWithFlatMap _,
    countDownWithFor _
  ).foreach { countDown =>
    assume(countDown(2).isLeft)
  }
}
