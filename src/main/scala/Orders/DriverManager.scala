package com.sambeth.akkaranks
package Orders

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.util.UUID

object DriverManager {

  trait Command
  final case class RegisterDriver(id: UUID, name: String, replyTo: ActorRef[DriverRegistered]) extends Command
  final case object ListDrivers extends Command
  final case object ListAvailableDrivers extends Command
  private final case class WrappedDriverResponse(response: Driver.Response) extends Command

  trait Response
  final case class DriverRegistered(driver: ActorRef[Driver.Command]) extends Response
  final case class NoDriverAvailable(driver: ActorRef[NotUsed]) extends Response

  def apply(): Behavior[Command] = {

    Behaviors.setup[Command] { context =>
      val driverResponseMapper: ActorRef[Driver.Response] = context.messageAdapter(rsp => WrappedDriverResponse(rsp))

      def driverManagerBehaviour(driverIdToActors: Map[UUID, ActorRef[Driver.Command]],
                                 driverIdToStatus: Map[UUID, String]): Behavior[Command] = {
        import Driver._

        Behaviors.receiveMessage {
          case RegisterDriver(driverId, name, replyTo) =>
            driverIdToActors.get(driverId) match {
              case Some(driverActor) =>
                replyTo ! DriverRegistered(driverActor)
                Behaviors.same
              case None =>
                context.log.info(s"Creating driver actor for $driverId-$name")
                val driverActor = context.spawn(Driver(driverId, name), s"$driverId-$name")
                replyTo ! DriverRegistered(driverActor)
                context.log.info(s"sending reply to $replyTo")
                driverManagerBehaviour(
                  driverIdToActors.updated(driverId, driverActor),
                  driverIdToStatus.updated(driverId, "Available")
                )
            }

          case ListDrivers =>
            context.log.info(s"Here are the registered drivers: $driverIdToActors")
            Behaviors.same

          case ListAvailableDrivers =>
            context.log.info(s"Here are the registered drivers: $driverIdToStatus")
            Behaviors.same
        }
      }

      driverManagerBehaviour(driverIdToActors = Map.empty, driverIdToStatus = Map.empty)
    }
  }
}
