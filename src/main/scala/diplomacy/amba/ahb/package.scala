// See LICENSE.SiFive for license details.

package org.chipsalliance.diplomacy.amba

import org.chipsalliance.diplomacy.nodes._
import org.chipsalliance.diplomacy.amba.ahb._
import org.chipsalliance.diplomacy.nodes.SimpleNodeHandle
package object ahb
{
  type AHBSlaveOutwardNode = OutwardNodeHandle[AHBMasterPortParameters, AHBSlavePortParameters, AHBEdgeParameters, AHBSlaveBundle]
  type AHBSlaveInwardNode = InwardNodeHandle[AHBMasterPortParameters, AHBSlavePortParameters, AHBEdgeParameters, AHBSlaveBundle]
  type AHBSlaveNode = SimpleNodeHandle[AHBMasterPortParameters, AHBSlavePortParameters, AHBEdgeParameters, AHBSlaveBundle]
  type AHBMasterOutwardNode = OutwardNodeHandle[AHBMasterPortParameters, AHBSlavePortParameters, AHBEdgeParameters, AHBMasterBundle]
  type AHBMasterInwardNode = InwardNodeHandle[AHBMasterPortParameters, AHBSlavePortParameters, AHBEdgeParameters, AHBMasterBundle]
  type AHBMasterNode = SimpleNodeHandle[AHBMasterPortParameters, AHBSlavePortParameters, AHBEdgeParameters, AHBMasterBundle]
}
