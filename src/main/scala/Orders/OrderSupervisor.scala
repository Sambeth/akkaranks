package com.sambeth.akkaranks
package Orders

import akka.NotUsed
import akka.actor.typed.{Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors

object OrderSupervisor {

  def apply(): Behavior[NotUsed] =
    Behaviors.setup[NotUsed] { context =>
      context.log.info("Orders Application has started")

      Behaviors.receiveSignal {
        case (_, PostStop) =>
          context.log.info("Orders Application has stopped")
          Behaviors.stopped
      }
    }
}
