package me.archdev.ctp

import akka.actor.Actor

class SpawnPointActor(spawnName: SpawnName) extends Actor {

  var spawnRate: Byte = 0
  var unitsCounter: Long = 0

  override def receive: Receive = {
    case SupplySpawn(source, unitsCount) =>
      unitsCounter = unitsCounter + unitsCount
      sender() ! UnitsMoved(spawnName, source, unitsCounter)
    case DefendSpawn(attacker, enemyArmy) =>
      val isSpawnCaptured = enemyArmy > unitsCounter
      unitsCounter = Math.abs(unitsCounter - enemyArmy)
      sender() ! FightResult(spawnName, attacker, isSpawnCaptured, unitsCounter)
    case SpawnUnits =>
      unitsCounter = unitsCounter + spawnRate
      sender() ! UnitsSpawned(spawnName, unitsCounter)
    case SetUnitsSpawnRate(rate) =>
      spawnRate = rate
      sender() ! UnitsSpawnRateWasSet(spawnName, spawnRate)
    case GetSpawnStatus =>
      sender() ! SpawnStatus(spawnName, spawnRate, unitsCounter)
    case _ =>
  }

}

