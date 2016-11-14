package me.archdev.ctp

import akka.actor.Actor

class GameHistoryActor extends Actor {

  var history: Map[GameTick, Seq[GameEvent]] = Map.empty

  override def receive: Receive = {
    case SaveEventInHistory(currentTick, gameEvent) =>
      history = history.updated(
        currentTick,
        history.get(currentTick) match {
          case Some(events) =>
            events :+ gameEvent
          case None =>
            Seq(gameEvent)
        }
      )
    case _ =>
  }

}

sealed trait GameHistoryCommands
case class SaveEventInHistory(tick: GameTick, gameEvent: GameEvent) extends GameHistoryCommands
object SaveEventInHistory {
  def apply(gameEvent: GameEvent): SaveEventInHistory = new SaveEventInHistory(0, gameEvent)
}
