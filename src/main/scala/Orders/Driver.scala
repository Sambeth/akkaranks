package com.sambeth.akkaranks
package Orders

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}

import java.util.UUID

object Driver {

  trait Command
  final case class DeliverOrder(order: Order, replyTo: ActorRef[Response]) extends Command
  final case class AssignDriver(order: Order, replyTo: ActorRef[Response]) extends Command

  trait Response
  final case class OrderReceived(order: Order) extends Response
  final case class OrderDelivered(order: Order) extends Response

  def apply(id: UUID, name: String): Behavior[Command] =
    Behaviors.setup[Command](context => new DriverBehaviour(context))

  class DriverBehaviour(context: ActorContext[Command]) extends AbstractBehavior[Command](context) {

    override def onMessage(msg: Command): Behavior[Command] =
      msg match {
        case DeliverOrder(order, replyTo) =>
          replyTo ! OrderDelivered(order)
          this
      }
  }
}
