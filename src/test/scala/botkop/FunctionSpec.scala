package botkop

import botkop.autograd.Variable
import botkop.{numsca => ns}
import org.scalatest.{FlatSpec, Matchers}

class FunctionSpec extends FlatSpec with Matchers {

  "Function" should "transpose" in {
    val v = Variable(ns.randn(4, 6))
    val w = Variable(ns.randn(3, 2))

    val vt = v.t()
    vt.shape shouldBe List(6, 4)

    vt.backward(w)
    v.grad.get.shape shouldBe List(2, 3)
  }

  it should "do a dot product" in {
    val x = Variable(ns.arange(12).reshape(3, 4))
    val y = Variable(ns.arange(8).reshape(4, 2))
    val z = x.dot(y)
    z.shape shouldBe List(3, 2)

    println(z)

    val g = Variable(ns.arange(6).reshape(z.shape.toArray))
    z.backward(g)

    println(x.grad.get.data)
    println(y.grad.get.data)

    x.grad.get.data.data shouldBe Array( //
        1, 3, 5, //
        7, 3, 13, //
        23, 33, 5, //
        23, 41, 59)
    x.grad.get.shape shouldBe List(3, 4)

    y.grad.get.data.data shouldBe Array( //
        40, 52, //
        46, 61, //
        52, 70, //
        58, 79)
    y.grad.get.shape shouldBe List(4, 2)
  }

  it should "do an affine operation" in {

    val inputFeatures = 4
    val outputFeatures = 3
    val numSamples = 16

    def makeVariable(shape: Int*): Variable =
      Variable(ns.arange(shape.product).reshape(shape: _*))

    val w = makeVariable(outputFeatures, inputFeatures)
    val b = makeVariable(1, outputFeatures)

    val x = makeVariable(numSamples, inputFeatures)

    val d = x.dot(w.t())
    d.shape shouldBe List(numSamples, outputFeatures)

    val y = d + b
    y.shape shouldBe d.shape

    val dy = makeVariable(y.shape: _*)
    y.backward(dy)


    println(w.grad.get.shape)
    println(b.grad.get)

    println(ns.sum(b.grad.get.data, axis=0))

  }

}
