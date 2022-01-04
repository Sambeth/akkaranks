package com.sambeth.akkaranks

import akka.NotUsed
import akka.actor.typed.ActorSystem

object OrderApp extends App {
  ActorSystem[NotUsed](OrderSupervisor(), "order-supervisor")
}
