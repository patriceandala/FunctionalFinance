package com.patriceandala.finance

import cats.implicits._

object Main extends App {
  println("Welcome to FunctionalFinance!")

  val transactions = List(200.0, -100.0, 50.0, -30.0)
  val balance = transactions.combineAll
  println(s"Total balance: $$balance")
}