package org.chipsalliance.diplomacy.bundlebridge

import chisel3._
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy.nodes._
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy.bundlebridge._

case class BundleBridgeIdentityNode[T <: Data]()(
  implicit valName: ValName)
    extends IdentityNode(new BundleBridgeImp[T])()
