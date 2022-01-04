package com.sambeth.akkaranks
package Orders

import akka.NotUsed
import akka.actor.typed.ActorSystem


object OrderApp extends App {
  ActorSystem[NotUsed](OrderSupervisor(), "order-supervisor")
}
