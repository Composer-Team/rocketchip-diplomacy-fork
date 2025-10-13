package org.chipsalliance.diplomacy.bundlebridge


import chisel3._
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy.nodes._
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy.bundlebridge._

case class BundleBridgeNexusNode[T <: Data](
  default:             Option[() => T] = None,
  inputRequiresOutput: Boolean = false
) // when false, connecting a source does not mandate connecting a sink
(
  implicit valName:    ValName)
    extends NexusNode(new BundleBridgeImp[T])(
      dFn = seq => seq.headOption.getOrElse(BundleBridgeParams(default)),
      uFn = seq => seq.headOption.getOrElse(BundleBridgeParams(None)),
      inputRequiresOutput = inputRequiresOutput,
      outputRequiresInput = !default.isDefined
    )
