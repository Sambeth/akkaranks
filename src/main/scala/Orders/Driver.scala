package com.sambeth.akkaranks
package Orders

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}

import java.util.UUID

object Driver {

  trait Command
  final case class DeliverOrder(order: Order, to: Facility, replyTo: ActorRef[Response]) extends Command
  final case class GetStatus(replyTo: ActorRef[CurrentStatus]) extends Command

  trait Response
  final case class OrderDelivered(order: Order) extends Response
  final case class CurrentStatus(available: Boolean) extends Response
  private case object DriverAvailable extends Response
  private case object DriverNotAvailable extends Response

  def apply(id: UUID, name: String): Behavior[Command] = {
    Behaviors.setup[Command](context => new DriverBehaviour(context))

    class DriverBehaviour(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {

      override def onMessage(msg: Command): Behavior[Command] =
        msg match {
          case DeliverOrder(order, facility, replyTo) =>
            replyTo ! DriverNotAvailable
            replyTo ! OrderDelivered(order)
            replyTo ! DriverAvailable
            this
        }
    }
  }

}
