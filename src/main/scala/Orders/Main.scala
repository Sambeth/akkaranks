package com.sambeth.akkaranks
package Orders

import akka.NotUsed
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.sambeth.akkaranks.Orders.DriverManager.RegisterDriver

import java.util.UUID

object Main extends App {
  trait Command
  private final case class WrappedDriverManagerResponse(response: DriverManager.Response) extends Command

  def apply(): Behavior[Main.Command] =
    Behaviors.setup { context =>
      val driverManagerResponseMapper: ActorRef[DriverManager.Response] = context.messageAdapter(rsp => WrappedDriverManagerResponse(rsp))

      val orderSupervisor = context.spawn(OrderSupervisor(), "orderSupervisor")
      context.log.info(s"Spawing driver manager: $orderSupervisor")

      val driverManager = context.spawn(DriverManager(), "driverManager")
      context.log.info(s"Spawing driver manager: $driverManager")

      // spawn 3 driver actors
      driverManager ! RegisterDriver(UUID.randomUUID(), "Samuel", driverManagerResponseMapper)
      driverManager ! RegisterDriver(UUID.randomUUID(), "Clifford", driverManagerResponseMapper)
      driverManager ! RegisterDriver(UUID.randomUUID(), "Phyll", driverManagerResponseMapper)


    }
}
