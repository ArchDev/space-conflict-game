package me.archdev.ctp

import akka.actor.{Actor, ActorRef}

class GameSupervisorActor(gameMap: GameMap) extends Actor {

  var currentTick: GameTick = 0
  var playersOwnership: Map[Player, Set[SpawnName]] = Map.empty

  var spawns: Map[SpawnName, ActorRef] = Map.empty
  var spawnsCoords: Map[Coords, SpawnName] = Map.empty
  var spawnsOwners: Map[SpawnName, Option[Player]] = Map.empty

  override def receive: Receive = {
    case _ =>
      sender() ! ()
  }

}

sealed trait GameCommands
case class MoveUnits(targetSpawnPoint: SpawnName, unitsCount: Option[Long] = None) extends GameCommands
case object GetGameStatus extends GameCommands
