package org.chipsalliance.diplomacy.bundlebridge

import chisel3._
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy.nodes._

case class BundleBridgeEphemeralNode[T <: Data](
)(
  implicit valName: ValName)
    extends EphemeralNode(new BundleBridgeImp[T])()
