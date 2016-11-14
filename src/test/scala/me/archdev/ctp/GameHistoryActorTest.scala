package me.archdev.ctp

import akka.testkit.TestActorRef
import me.archdev.DefaultTestSpec

class GameHistoryActorTest extends DefaultTestSpec {

  "Game history actor" when {

    "received SaveEventInHistory command" should {

      "save game event to history" in new Context {
        historyRef ! SaveEventInHistory(PlayerConnected("1"))
        historyRef ! SaveEventInHistory(PlayerConnected("2"))
        historyRef ! SaveEventInHistory(PlayerConnected("3"))

        history.history(0) should be(Seq(PlayerConnected("1"), PlayerConnected("2"), PlayerConnected("3")))
      }

      "save game event to history by ticks" in new Context {
        historyRef ! SaveEventInHistory(1, PlayerConnected("1"))
        historyRef ! SaveEventInHistory(2, PlayerConnected("2"))
        historyRef ! SaveEventInHistory(3, PlayerConnected("3"))

        history.history(1) should be(Seq(PlayerConnected("1")))
        history.history(2) should be(Seq(PlayerConnected("2")))
        history.history(3) should be(Seq(PlayerConnected("3")))
      }

    }

  }

  trait Context {
    val historyRef: TestActorRef[GameHistoryActor] = TestActorRef[GameHistoryActor]
    val history = historyRef.underlyingActor
  }

}
