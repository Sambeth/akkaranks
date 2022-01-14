package com.sambeth.akkaranks
package Orders

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors

import java.util.UUID

object OrderSupervisor {

  def apply(): Behavior[OrderSupervisor.Command] = {
    import DriverManager._

    Behaviors.setup[OrderSupervisor.Command] { context =>

      def active(): Behavior[OrderSupervisor.Command] = {
        Behaviors.receiveMessage[OrderSupervisor.Command] {
          case WrappedDriverManagerResponse(response) =>
            response match {
              case DriverRegistered(driver) =>
                context.log.info("Something is happening herey")
                context.log.info(s"Driver registered: ${driver.ref}")
                Behaviors.same
              case _ =>
                context.log.info("Nothing was matched")
                Behaviors.same
            }
        }

        Behaviors.receiveSignal[OrderSupervisor.Command] {
          case (_, PostStop) =>
            context.log.info("Orders Application has stopped")
            Behaviors.stopped
        }
      }

      active()
    }
  }
}
