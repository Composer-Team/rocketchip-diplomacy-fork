// See LICENSE.SiFive for license details.

package org.chipsalliance.diplomacy.amba


import org.chipsalliance.cde.config.Parameters
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy.nodes._

package object axis
{
  type AXISInwardNode = InwardNodeHandle[AXISMasterPortParameters, AXISSlavePortParameters, AXISEdgeParameters, AXISBundle]
  type AXISOutwardNode = OutwardNodeHandle[AXISMasterPortParameters, AXISSlavePortParameters, AXISEdgeParameters, AXISBundle]
  type AXISNode = NodeHandle[AXISMasterPortParameters, AXISSlavePortParameters, AXISEdgeParameters, AXISBundle, AXISMasterPortParameters, AXISSlavePortParameters, AXISEdgeParameters, AXISBundle]
}
