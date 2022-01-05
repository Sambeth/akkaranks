package com.sambeth.akkaranks
package Orders

import java.util.UUID

case class Order(id: UUID, item: String, quantity: Int)
