package me.archdev.ctp

import akka.testkit.{TestActorRef, TestProbe}
import akka.pattern.ask
import me.archdev.DefaultTestSpec

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GameLobbyActorTest extends DefaultTestSpec {

  "Game lobby actor" when {

    "initialized" should {

      "calculate max players count" in new Context {
        lobby.maxPlayersCount should be(4)
      }

    }

    "received ConnectPlayer command" should {

      "add player to game" in new Context {
        (lobbyRef ? ConnectPlayer(player)).thenCheck {
          lobby.players should be(Set(player))
        }
      }

      "save PlayerConnected event in history" in new Context {
        (lobbyRef ? ConnectPlayer(player)).thenCheck {
          expectEvent(PlayerConnected(player))
        }
      }

      "do nothing if maximum count of players reached" in new Context {
        players.map(p => lobbyRef ? ConnectPlayer(p))
          .foldLeft[Future[Any]](Future.successful())((a, b) => a.flatMap(_ => b))
          .thenCheck {
            lobby.players should be(players.dropRight(1))
            expectEvent(players.toSeq.dropRight(1).map(PlayerConnected.apply):_*)
            historyProbe.expectNoMsg(defaultTimeout)
          }
      }

      "do nothing if players locked" in new Context {
        lobby.isPlayersLocked = true
        (lobbyRef ? ConnectPlayer(player)).thenCheck {
          historyProbe.expectNoMsg(defaultTimeout)
        }
      }

    }

    "received DisconnectPlayer command" should {

      "remove player from game" in new Context {
        (lobbyRef ? ConnectPlayer(player))
          .flatMap(_ => lobbyRef ? DisconnectPlayer(player))
          .thenCheck {
            lobby.players should be(Set.empty[Player])
          }
      }

      "save PlayerDisconnected event in history" in new Context {
        (lobbyRef ? ConnectPlayer(player))
          .flatMap(_ => lobbyRef ? DisconnectPlayer(player))
          .thenCheck {
            expectEvent(PlayerConnected(player), PlayerDisconnected(player))
          }
      }

      "do nothing if user not exists in game" in new Context {
        (lobbyRef ? ConnectPlayer(player))
          .flatMap(_ => lobbyRef ? DisconnectPlayer("Wtf"))
          .thenCheck {
            lobby.players should be(Set(player))
            expectEvent(PlayerConnected(player))
            historyProbe.expectNoMsg(defaultTimeout)
          }
      }

      "do nothing if players locked" in new Context {
        (lobbyRef ? ConnectPlayer(player))
          .map(_ => lobby.isPlayersLocked = true)
          .flatMap(_ => lobbyRef ? DisconnectPlayer(player))
          .thenCheck {
            lobby.players should be(Set(player))
            expectEvent(PlayerConnected(player))
            historyProbe.expectNoMsg(defaultTimeout)
          }
      }

    }

    "received LockPlayers command" should {

      "toggle isPlayersLocked on" in new Context {
        (lobbyRef ? LockPlayers).thenCheck(
          lobby.isPlayersLocked should be(true)
        )
      }

      "save PlayersLocked in history" in new Context {
        (lobbyRef ? ConnectPlayer(player))
          .flatMap(_ => lobbyRef ? LockPlayers)
          .thenCheck(
            expectEvent(PlayerConnected(player), PlayersLocked(Set(player)))
          )
      }

    }

  }

  trait Context {
    val players = Set("Amun", "Artanis", "Raynor", "Sara Kerrigan", "Tassadar")
    val player = players.head
    val defaultGameMap: GameMap = Map(
      (0,0) -> SpawnConfig(2, false),
      (1,1) -> SpawnConfig.startPosition(),
      (-1,1) -> SpawnConfig.startPosition(),
      (1,-1) -> SpawnConfig.startPosition(),
      (-1,-1) -> SpawnConfig.startPosition()
    )

    val historyProbe = TestProbe()
    val lobbyRef: TestActorRef[GameLobbyActor] = TestActorRef(new GameLobbyActor(defaultGameMap, historyProbe.testActor))
    val lobby = lobbyRef.underlyingActor

    def expectEvent(gameEvent: GameEvent*) =
      historyProbe.expectMsgAllOf(defaultTimeout, gameEvent.map(SaveEventInHistory.apply):_*)
  }

}
