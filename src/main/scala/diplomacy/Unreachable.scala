// See LICENSE.SiFive for license details.

package org.chipsalliance.diplomacy

case object Unreachable {
  def apply(): Nothing = throw new AssertionError("unreachable code")
}