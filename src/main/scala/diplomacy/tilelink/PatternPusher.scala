// // See LICENSE.SiFive for license details.

// package org.chipsalliance.diplomacy.tilelink

// import chisel3._

// import org.chipsalliance.diplomacy._
// import org.chipsalliance.cde.config._

// trait Pattern {
//   def address: BigInt
//   def size: Int
//   def bits(edge: TLEdgeOut): (Bool, TLBundleA)
//   def dataIn: Option[BigInt] = None
//   require ((address & ((BigInt(1) << size) - 1)) == 0)
// }

// case class WritePattern(address: BigInt, size: Int, data: BigInt) extends Pattern
// {
//   require (0 <= data && data < (BigInt(1) << (8 << size)))
//   def bits(edge: TLEdgeOut) = edge.Put(0.U, UInt(address), UInt(size), UInt(data << (8*(address % edge.manager.beatBytes).toInt)))
// }

// case class ReadPattern(address: BigInt, size: Int) extends Pattern
// {
//   def bits(edge: TLEdgeOut) = edge.Get(0.U, UInt(address), UInt(size))
// }

// case class ReadExpectPattern(address: BigInt, size: Int, data: BigInt) extends Pattern
// {
//   def bits(edge: TLEdgeOut) = edge.Get(0.U, UInt(address), UInt(size))
//   override def dataIn = Some(data)
// }

// class TLPatternPusher(name: String, pattern: Seq[Pattern])(implicit p: Parameters) extends LazyModule
// {
//   val node = TLClientNode(Seq(TLMasterPortParameters.v1(Seq(TLMasterParameters.v1(name = name)))))

//   lazy val module = new Impl
//   class Impl extends LazyModuleImp(this) {
//     val io = IO(new Bundle {
//       val run = Bool(INPUT)
//       val done = Bool(OUTPUT)
//     })

//     val (tl_out, edgeOut) = node.out(0)
//     pattern.foreach { p =>
//       require (p.size <= log2Ceil(edgeOut.manager.beatBytes), "Patterns must fit in a single beat")
//     }

//     val step   = RegInit(UInt(0, width = log2Ceil(pattern.size+1)))
//     val flight = RegInit(false.B)
//     val ready  = RegNext(true.B, false.B)

//     val end = step === UInt(pattern.size)
//     io.done := end && !flight

//     val a = tl_out.a
//     val d = tl_out.d

//     // Expected response?
//     val check  = Vec(pattern.map(p => Bool(p.dataIn.isDefined)))(step) holdUnless a.fire()
//     val expect = Vec(pattern.map(p => UInt(p.dataIn.getOrElse(BigInt(0)))))(step) holdUnless a.fire()
//     assert (!check || !d.fire() || expect === d.bits.data)

//     when (a.fire()) {
//       flight := true.B
//       step := step + 1.U
//     }
//     when (d.fire()) {
//       flight := false.B
//     }

//     val (plegal, pbits) = pattern.map(_.bits(edgeOut)).unzip
//     assert (end || Vec(plegal)(step), s"Pattern pusher ${name} tried to push an illegal request")

//     a.valid := io.run && ready && !end && !flight
//     a.bits  := Vec(pbits)(step)
//     d.ready := true.B

//     // Tie off unused channels
//     tl_out.b.ready := true.B
//     tl_out.c.valid := false.B
//     tl_out.e.valid := false.B
//   }
// }

// object TLPatternPusher
// {
//   def apply(name: String, pattern: Seq[Pattern])(implicit p: Parameters): TLOutwardNode =
//   {
//     val pusher = LazyModule(new TLPatternPusher(name, pattern))
//     pusher.node
//   }
// }
