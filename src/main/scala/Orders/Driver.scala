package com.sambeth.akkaranks
package Orders

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}

object Driver {

  trait Command
  final case class DeliverOrder(order: Order, to: Facility, replyTo: ActorRef[OrderDelivered]) extends Command
  final case class GetStatus(replyTo: ActorRef[CurrentStatus]) extends Command

  trait Response
  final case class OrderDelivered(order: Order) extends Response
  final case class CurrentStatus(available: Boolean) extends Response

  def apply(): Behavior[Command] = {
    Behaviors.setup[Command](context => new DriverBehaviour(context))

    class DriverBehaviour(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {
      private var availability: Boolean = true

      override def onMessage(msg: Command): Behavior[Command] =
        msg match {
          case DeliverOrder(order, facility, replyTo) =>
            availability = false
            replyTo ! OrderDelivered(order)
            this

          case GetStatus(replyTo) =>
            replyTo ! CurrentStatus(available = availability)
            this
        }
    }
  }

}
