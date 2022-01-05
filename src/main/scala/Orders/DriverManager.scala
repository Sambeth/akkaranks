package com.sambeth.akkaranks
package Orders

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.util.UUID

object DriverManager {

  trait Command
  final case class CreateDriver(id: UUID, name: String) extends Command
  private case object RequestAvailableDriver extends Command
  private final case class WrappedDriverResponse(response: Driver.Response) extends Command

  trait Response
  final case class DriverAvailable(driver: ActorRef[Nothing]) extends Response

  def apply(): Behavior[Command] = {
    Behaviors.setup[Command] { context =>
      val driverResponseMapper: ActorRef[Driver.Response] = context.messageAdapter(rsp => WrappedDriverResponse(rsp))

      def driverManagerBehaviour(availableDrivers: List[ActorRef[Driver.Command]] = List.empty): Behavior[Command] = {
        Behaviors.receiveMessage {
          case CreateDriver(id, name) =>
            val driver = context.spawn(Driver(id, name), s"$id-$name")
            driverManagerBehaviour(driver :: availableDrivers)
        }
      }
    }
  }

}
