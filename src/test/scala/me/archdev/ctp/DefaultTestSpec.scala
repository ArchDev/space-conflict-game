package me.archdev.ctp

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import akka.util.{Timeout => AkkaTimeout}

import scala.concurrent.Future
import scala.reflect.ClassTag

abstract class DefaultTestSpec extends TestKit(SharedTestResources.actorSystem) with WordSpecLike with ScalaFutures with Matchers with ActorMatchers {
  private val defaultTimeout = 10 seconds
  implicit val akkaDefaultTimeout: AkkaTimeout = AkkaTimeout(defaultTimeout)
}

trait ActorMatchers extends ScalaFutures with WordSpecLike {

  def matchActorResponse[ActorResponse](result: Future[Any])(action: ActorResponse => Unit)(implicit expectedClass: ClassTag[ActorResponse]): ActorResponse =
    whenReady(result) {
      case validResponse: ActorResponse =>
        action(validResponse)
        validResponse
      case invalidResponse =>
        fail(s"Actor returns invalid result, received: ${invalidResponse.getClass.getSimpleName}, expected: ${expectedClass.runtimeClass.getSimpleName}")
    }

  def checkActorResponse(result: Future[Any])(action: => Unit): Unit =
    whenReady(result)(_ => action)

  implicit class ActorResponseMatcher(result: Future[Any]) {

    def matchResponse[ActorResponse](action: ActorResponse => Unit)(implicit expectedClass: ClassTag[ActorResponse]): ActorResponse =
      matchActorResponse(result)(action)

    def thenCheck(action: => Unit): Unit =
      checkActorResponse(result)(action)

  }

}

object SharedTestResources {
  val actorSystem = ActorSystem("test-actor-system")
}
