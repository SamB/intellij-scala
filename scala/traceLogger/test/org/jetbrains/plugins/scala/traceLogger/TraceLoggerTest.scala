package org.jetbrains.plugins.scala.traceLogger

class TraceLoggerTest extends TraceLoggerTestBase {
  override def toString: Data = "[TraceLoggerTest]"

  def test_single_log_msg(): Unit =
    doTest(
      """
        |test
        |""".stripMargin
    ) {
      TraceLogger.log("test")
    }

  def test_variables(): Unit =
    doTest(
      """
        |test (x = 3, x + 3 = 6)
        |""".stripMargin
    ) {
      val x = 3
      TraceLogger.log("test", x, x + 3)
    }

  def test_block(): Unit =
    doTest(
      """
        |a block (a = 10)
        |  test (a = 10, c = 13)
        |  -> 20
        |done (b = 20)
        |""".stripMargin
    ) {
      val a = 10
      val b = TraceLogger.block("a block", a) {
        val c = a + 3
        TraceLogger.log("test", a, c)
        c + 7
      }
      TraceLogger.log("done", b)
    }

  def test_func(): Unit = {
    doTest(
      """
        |org.jetbrains.plugins.scala.traceLogger.TraceLoggerTest.func (this = [TraceLoggerTest], arg = 42, implicitArg = 1.0)
        |  test (x = 3)
        |  -> "blub 42..."
        |""".stripMargin
    ) {
      val x = 3
      def func(arg: Int)(implicit implicitArg: Float): String = TraceLogger.func {
        TraceLogger.log("test", x)
        s"blub $arg..."
      }

      implicit val y: Float = 1.0f
      func(42)
    }
  }

  def test_failing_func(): Unit = {
    doTest(
      """
        |org.jetbrains.plugins.scala.traceLogger.TraceLoggerTest.func (this = [TraceLoggerTest])
        |  test
        |  -> threw [muhahaha]
        |""".stripMargin
    ) {
      def func(): String = TraceLogger.func {
        TraceLogger.log("test")
        throw new TestException("muhahaha")
      }

      func()
    }
  }

  def test_object_func(): Unit = {
    doTest(
      """
        |org.jetbrains.plugins.scala.traceLogger.TraceLoggerTest.funcInObject (x = 5)
        |  -> 8
        |""".stripMargin
    ) {
      TraceLoggerTest.funcInObject(5)
    }
  }

  def test_return(): Unit = {
    doTest(
      """
        |org.jetbrains.plugins.scala.traceLogger.TraceLoggerTest.func (this = [TraceLoggerTest], early = false)
        |  -> "return normal"
        |org.jetbrains.plugins.scala.traceLogger.TraceLoggerTest.func (this = [TraceLoggerTest], early = true)
        |  -> "return early"
        |""".stripMargin
    ) {
      def func(early: Boolean): String = TraceLogger.func {
        if (early) return "return early"
        "return normal"
      }

      func(false)
      func(true)
    }
  }
}

object TraceLoggerTest {
  def funcInObject(x: Int): Int = TraceLogger.func {
    x + 3
  }
}