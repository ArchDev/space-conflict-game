package me.archdev.ctp

import akka.pattern.ask
import akka.testkit.TestActorRef
import me.archdev.DefaultTestSpec

class SpawnPointActorTest extends DefaultTestSpec {

  "Spawn point actor" when {

    "received SupplySpawn command" should {

      "increment unitsCounter on unitsCount" in new Context {
        (spawnRef ? SupplySpawn(extraSpawnName, 10)).thenCheck {
          spawn.unitsCounter should be(10)
        }
      }

      "return UnitsMoved event" in new Context {
        spawn.unitsCounter = 100
        (spawnRef ? SupplySpawn(extraSpawnName, 10))
          .matchResponse[UnitsMoved](_ should be(UnitsMoved(spawnName, extraSpawnName, 110)))
      }

    }

    "received DefendSpawn command" should {

      "decrement unitsCounter on unitsCount" in new Context {
        spawn.unitsCounter = 100
        (spawnRef ? DefendSpawn(attacker, 10)).thenCheck {
          spawn.unitsCounter should be(90)
        }
      }

      "leave units counter always positive" in new Context {
        (spawnRef ? DefendSpawn(attacker, 10)).thenCheck {
          spawn.unitsCounter should be(10)
        }
      }

      "return failed FightResult if spawn had more or equals count of units that enemy" in new Context {
        spawn.unitsCounter = 10
        (spawnRef ? DefendSpawn(attacker, 10))
          .matchResponse[FightResult](_ should be(FightResult(spawnName, attacker, false, 0)))
      }

      "return successful FightResult if spawn had less units that enemy" in new Context {
        (spawnRef ? DefendSpawn(attacker, 10))
          .matchResponse[FightResult](_ should be(FightResult(spawnName, attacker, true, 10)))
      }

    }

    "received SpawnUnits command" should {

      "increment unitsCounter on spawnRate" in new Context {
        spawn.spawnRate = 10
        (spawnRef ? SpawnUnits).thenCheck {
          spawn.unitsCounter should be(10)
        }
      }

      "return UnitsSpawned event" in new Context {
        spawn.spawnRate = 1
        spawn.unitsCounter = 10
        (spawnRef ? SpawnUnits)
          .matchResponse[UnitsSpawned](_ should be(UnitsSpawned(spawnName, 11)))
      }

    }

    "received SetUnitsSpawnRate command" should {

      "set spawnRate" in new Context {
        (spawnRef ? SetUnitsSpawnRate(10)).thenCheck {
          spawn.spawnRate should be(10)
        }
      }

      "return UnitsSpawnRateWasSet event" in new Context {
        (spawnRef ? SetUnitsSpawnRate(10))
          .matchResponse[UnitsSpawnRateWasSet](_ should be(UnitsSpawnRateWasSet(spawnName, 10)))
      }

    }

    "received GetSpawnStatus command" should {

      "return SpawnStatus event" in new Context {
        spawn.spawnRate = 10
        spawn.unitsCounter = 100
        (spawnRef ? GetSpawnStatus)
            .matchResponse[SpawnStatus](_ should be(SpawnStatus(spawnName, 10, 100)))
      }

    }

  }

  trait Context {
    val attacker: Player = "Amun"
    val spawnName: SpawnName = "Adun"
    val extraSpawnName: SpawnName = "Shakuras"
    val spawnRef: TestActorRef[SpawnPointActor] = TestActorRef(new SpawnPointActor(spawnName))
    val spawn = spawnRef.underlyingActor
  }

}
