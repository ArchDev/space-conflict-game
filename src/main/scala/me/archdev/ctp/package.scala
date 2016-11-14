package me.archdev

package object ctp {

  type SpawnName = String
  type SpawnRate = Int
  type Player = String
  type GameTick = Long

  sealed trait GameEvent
  case class UnitsSpawnRateWasSet(spawnName: String, rate: SpawnRate)
  case class UnitsSpawned(spawnName: SpawnName, spawnUnitsCounter: Long) extends GameEvent
  case class UnitsMoved(target: SpawnName, source: SpawnName, spawnUnitsCounter: Long) extends GameEvent
  case class FightResult(spawnName: SpawnName, attacker: Player, isSpawnCaptured: Boolean, unitsRemains: Long) extends GameEvent
  case class PlayerConnected(player: Player) extends GameEvent
  case class PlayerDisconnected(player: Player) extends GameEvent
  case class PlayersLocked(players: Set[Player]) extends GameEvent

  sealed trait StatusType
  case class SpawnStatus(spawnName: SpawnName, spawnRate: SpawnRate, unitsCount: Long) extends StatusType
  case class GameStatus(spawnStatuses: Map[SpawnName, SpawnStatus]) extends StatusType

  type X = Int
  type Y = Int
  type Coords = (X, Y)
  type GameMap = Map[Coords, SpawnConfig]
  case class SpawnConfig(spawnRate: SpawnRate, isStartPosition: Boolean)
  object SpawnConfig {
    def startPosition(spawnRate: SpawnRate = 1) = SpawnConfig(1, true)
  }

}
