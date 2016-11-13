package me.archdev

package object ctp {

  type SpawnName = String
  type Player = String

  sealed trait GameCommands
  case class MoveUnits(targetSpawnPoint: SpawnName, unitsCount: Option[Long] = None) extends GameCommands
  case object GetGameStatus extends GameCommands

  sealed trait SpawnCommands
  case object SpawnUnits extends SpawnCommands
  case object GetSpawnStatus extends SpawnCommands
  case class SetUnitsSpawnRate(spawnRate: Byte) extends SpawnCommands
  case class SupplySpawn(source: SpawnName, unitsCount: Long) extends SpawnCommands
  case class DefendSpawn(attacker: Player, unitsCount: Long) extends SpawnCommands

  sealed trait GameEvents
  case class UnitsSpawnRateWasSet(spawnName: String, rate: Byte)
  case class UnitsSpawned(spawnName: SpawnName, spawnUnitsCounter: Long) extends GameEvents
  case class UnitsMoved(target: SpawnName, source: SpawnName, spawnUnitsCounter: Long) extends GameEvents
  case class FightResult(spawnName: SpawnName, attacker: Player, isSpawnCaptured: Boolean, unitsRemains: Long) extends GameEvents

  sealed trait StatusType
  case class SpawnStatus(spawnName: SpawnName, spawnRate: Byte, unitsCount: Long) extends StatusType
  case class GameStatus(spawnStatuses: Map[SpawnName, SpawnStatus]) extends StatusType

}
