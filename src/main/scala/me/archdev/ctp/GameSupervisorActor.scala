package me.archdev.ctp

import akka.actor.{Actor, ActorRef}

class GameSupervisorActor(gameSize: Int, players: Seq[Player]) extends Actor {

  var spawns: Map[SpawnName, ActorRef] = Map.empty
  var spawnsOwners: Map[SpawnName, Option[Player]] = spawns.mapValues(_ => Option.empty[Player])
  var playersOwnership: Map[Player, Set[SpawnName]] = Map(players.map(_ -> Set.empty[SpawnName]):_*)

  override def receive: Receive = {
    case MoveUnits(targetSpawnPoint, units) =>
    case GetGameStatus =>
    case _ =>
  }

}
