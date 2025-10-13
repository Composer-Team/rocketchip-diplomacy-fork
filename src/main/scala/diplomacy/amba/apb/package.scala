// See LICENSE.SiFive for license details.

package org.chipsalliance.diplomacy.amba

import org.chipsalliance.diplomacy.nodes._
import org.chipsalliance.diplomacy._
import org.chipsalliance.diplomacy.util._

package object apb
{
  type APBOutwardNode = OutwardNodeHandle[APBMasterPortParameters, APBSlavePortParameters, APBEdgeParameters, APBBundle]
  type APBInwardNode = InwardNodeHandle[APBMasterPortParameters, APBSlavePortParameters, APBEdgeParameters, APBBundle]
  type APBNode = SimpleNodeHandle[APBMasterPortParameters, APBSlavePortParameters, APBEdgeParameters, APBBundle]
}
