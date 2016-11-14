package me.archdev.ctp

import akka.actor.{Actor, ActorRef}

class GameLobbyActor(gameMap: GameMap, historyActor: ActorRef) extends Actor {

  val maxPlayersCount: Int = gameMap.values.count(_.isStartPosition)

  var isPlayersLocked: Boolean = false
  var players: Set[Player] = Set.empty

  override def receive: Receive = {
    case ConnectPlayer(player) if !isPlayersLocked && players.size < maxPlayersCount =>
      players = players + player
      publishEvent(PlayerConnected(player))
    case DisconnectPlayer(player) if !isPlayersLocked && players.contains(player) =>
      players = players - player
      publishEvent(PlayerDisconnected(player))
    case LockPlayers =>
      isPlayersLocked = true
      publishEvent(PlayersLocked(players))
    case _ =>
      sender() ! ()
  }

  private def publishEvent(gameEvent: GameEvent) = {
    historyActor ! SaveEventInHistory(0, gameEvent)
    sender() ! gameEvent
  }

}

sealed trait GameLobbyCommands
case class ConnectPlayer(player: Player) extends GameLobbyCommands
case class DisconnectPlayer(player: Player) extends GameLobbyCommands
case object LockPlayers extends GameLobbyCommands