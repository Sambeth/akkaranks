package com.sambeth.akkaranks
package Orders

import akka.NotUsed
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors

import java.util.UUID

object OrderSupervisor {
  trait Command
  private final case class WrappedDriverManagerResponse(response: DriverManager.Response) extends Command

  def apply(): Behavior[DriverManager.Response] = {
    import DriverManager._

    Behaviors.setup[Command] { context =>
      val driverManagerResponseMapper: ActorRef[DriverManager.Response] = context.messageAdapter(rsp => WrappedDriverManagerResponse(rsp))

      context.log.info("Orders Application has started")
      // spawn driver manager actor
      val driverManager = context.spawn(DriverManager(), "Order-Manager")

      // spawn 3 driver actors
      driverManager ! RegisterDriver(UUID.randomUUID(), "Samuel", driverManagerResponseMapper)
      driverManager ! RegisterDriver(UUID.randomUUID(), "Clifford", driverManagerResponseMapper)
      driverManager ! RegisterDriver(UUID.randomUUID(), "Phyll", driverManagerResponseMapper)

      Behaviors.receiveMessage[Command] {
        case wrapped: WrappedDriverManagerResponse =>
          wrapped.response match {
            case DriverRegistered(driver) =>
              context.log.info(s"Driver registered: ${driver.ref}")
              Behaviors.same
          }
      }

      Behaviors.receiveSignal {
        case (_, PostStop) =>
          context.log.info("Orders Application has stopped")
          Behaviors.stopped
      }
    }
  }
}
