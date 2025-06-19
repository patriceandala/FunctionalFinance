package com.patriceandala.finance


import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

import scala.io.circe.generic.auto._

final case class User(id: String, username: String, password: String, balance: Double)
final case class AuthRequest(username: String, password: String)
final case class BalanceResponse(balance: Double)
final case class MoneyRequest(amount: Double)

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    Ref.of[IO, List[User]](List.empty).flatMap { userDb =>
      val service = new UserService(userDb)
      BlazeServerBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(service.routes.orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }
  }
}

class UserService(userDb: Ref[IO, List[User]]) {
  implicit val decoder = jsonOf[IO, AuthRequest]
  implicit val moneyDecoder = jsonOf[IO, MoneyRequest]

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "signup" =>
      for {
        auth <- req.as[AuthRequest]
        id = java.util.UUID.randomUUID().toString
        newUser = User(id, auth.username, auth.password, 0.0)
        _ <- userDb.update(users => newUser :: users)
        res <- Ok(s"User created with ID: $id")
      } yield res

    case req @ POST -> Root / "login" =>
      for {
        auth <- req.as[AuthRequest]
        users <- userDb.get
        maybeUser = users.find(u => u.username == auth.username && u.password == auth.password)
        res <- maybeUser match {
          case Some(user) => Ok(s"Welcome ${user.username}!")
          case None       => Unauthorized("Invalid credentials")
        }
      } yield res

    case GET -> Root / "users" =>
      userDb.get.flatMap(users => Ok(users.map(u => s"${u.id}: ${u.username} (${u.balance})").mkString("\n")))

    case GET -> Root / "balance" / userId =>
      for {
        users <- userDb.get
        maybeUser = users.find(_.id == userId)
        res <- maybeUser match {
          case Some(user) => Ok(BalanceResponse(user.balance))
          case None       => NotFound("User not found")
        }
      } yield res

    case req @ POST -> Root / "deposit" / userId =>
      for {
        money <- req.as[MoneyRequest]
        updated <- userDb.modify { users =>
          users.find(_.id == userId) match {
            case Some(user) =>
              val newUser = user.copy(balance = user.balance + money.amount)
              val updatedList = users.map(u => if (u.id == userId) newUser else u)
              (updatedList, Some(newUser))
            case None => (users, None)
          }
        }
        res <- updated match {
          case Some(user) => Ok(BalanceResponse(user.balance))
          case None       => NotFound("User not found")
        }
      } yield res
  }
}
