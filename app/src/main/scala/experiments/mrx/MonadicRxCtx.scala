package experiments.mrx

import mhtml.{Cancelable, Rx}

class MonadicRxCtx { root =>
  var all = Seq.empty[() => Unit]
  def register(cancelable: Cancelable):Unit = all :+= cancelable.cancelFunction
  def run[T](rx:Rx[T])(effect:T => Unit) = register(Rx.run(rx)(effect))
  def cancel() = {
    all.map(_.apply())
    all = Seq.empty
  }

  def subctx() = new MonadicRxCtx {
    override def register(cancelable: Cancelable): Unit = {
      root.register(cancelable)
      super.register(cancelable)
    }
  }
}
