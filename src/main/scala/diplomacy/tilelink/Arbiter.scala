// See LICENSE.SiFive for license details.

package org.chipsalliance.diplomacy.tilelink

import chisel3._
import chisel3.util._
import chisel3.util.random.LFSR


import org.chipsalliance.cde.config._
import org.chipsalliance.diplomacy.util._
// See LICENSE.SiFive for license details.

import chisel3.util.random.LFSR
import org.chipsalliance.cde.config.Parameters

object TLArbiter
{
  // (valids, select) => readys
  type Policy = (Integer, UInt, Bool) => UInt

  val lowestIndexFirst: Policy = (width, valids, select) => ~(leftOR(valids) << 1)(width-1, 0)

  val highestIndexFirst: Policy = (width, valids, select) => ~((rightOR(valids) >> 1).pad(width))

  val roundRobin: Policy = (width, valids, select) => if (width == 1) 1.U(1.W) else {
    val valid = valids(width-1, 0)
    assert (valid === valids)
    val mask = RegInit(((BigInt(1) << width)-1).U(width-1,0))
    val filter = Cat(valid & ~mask, valid)
    val unready = (rightOR(filter, width*2, width) >> 1) | (mask << width)
    val readys = ~((unready >> width) & unready(width-1, 0))
    when (select && valid.orR) {
      mask := leftOR(readys & valid, width)
    }
    readys(width-1, 0)
  }

  def lowestFromSeq[T <: TLChannel](edge: TLEdge, sink: DecoupledIO[T], sources: Seq[DecoupledIO[T]]): Unit = {
    apply(lowestIndexFirst)(sink, sources.map(s => (edge.numBeats1(s.bits), s)):_*)
  }

  def lowest[T <: TLChannel](edge: TLEdge, sink: DecoupledIO[T], sources: DecoupledIO[T]*): Unit = {
    apply(lowestIndexFirst)(sink, sources.toList.map(s => (edge.numBeats1(s.bits), s)):_*)
  }

  def highest[T <: TLChannel](edge: TLEdge, sink: DecoupledIO[T], sources: DecoupledIO[T]*): Unit = {
    apply(highestIndexFirst)(sink, sources.toList.map(s => (edge.numBeats1(s.bits), s)):_*)
  }

  def robin[T <: TLChannel](edge: TLEdge, sink: DecoupledIO[T], sources: DecoupledIO[T]*): Unit = {
    apply(roundRobin)(sink, sources.toList.map(s => (edge.numBeats1(s.bits), s)):_*)
  }

  def apply[T <: Data](policy: Policy)(sink: DecoupledIO[T], sources: (UInt, DecoupledIO[T])*): Unit = {
    if (sources.isEmpty) {
      sink.bits       := DontCare
    } else if (sources.size == 1) {
      sink :<>= sources.head._2
    } else {
      val pairs = sources.toList
      val beatsIn = pairs.map(_._1)
      val sourcesIn = pairs.map(_._2)

      // The number of beats which remain to be sent
      val beatsLeft = RegInit(0.U)
      val idle = beatsLeft === 0.U
      val latch = idle && sink.ready // winner (if any) claims sink

      // Who wants access to the sink?
      val valids = sourcesIn.map(_.valid)

      // Arbitrate amongst the requests
      val readys = VecInit(policy(valids.size, Cat(valids.reverse), latch).asBools)

      // Which request wins arbitration?
      val winner = VecInit((readys zip valids) map { case (r,v) => r&&v })

      // Confirm the policy works properly
      require (readys.size == valids.size)
      // Never two winners
      val prefixOR = winner.scanLeft(false.B)(_||_).init
      assert((prefixOR zip winner) map { case (p,w) => !p || !w } reduce {_ && _})
      // If there was any request, there is a winner
      assert (!valids.reduce(_||_) || winner.reduce(_||_))

      // Track remaining beats
      val maskedBeats = (winner zip beatsIn) map { case (w,b) => Mux(w, b, 0.U) }

      val initBeats = maskedBeats.reduce(_ | _) // no winner => 0 beats
      beatsLeft := Mux(latch, initBeats, beatsLeft - sink.fire)

      // The one-hot source granted access in the previous cycle
      val state = RegInit(VecInit(Seq.fill(sources.size)(false.B)))
      val muxState = Mux(idle, winner, state)
      state := muxState

      val allowed = Mux(idle, readys, state)
      (sourcesIn zip allowed) foreach { case (s, r) =>
        s.ready := sink.ready && r
      }
      sink.valid := Mux(idle, valids.reduce(_||_), Mux1H(state, valids))
      sink.bits :<= Mux1H(muxState, sourcesIn.map(_.bits))
    }
  }
}