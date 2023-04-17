package com.example

import cats.effect.{IO, IOApp, ExitCode}
import io.quartz.QuartzH2Server
import io.quartz.QuartzH2Client
import io.quartz.http2.routes.{HttpRouteIO, Routes}
import io.quartz.http2.model.{Headers, Method, ContentType, Request, Response}
import io.quartz.http2._
import io.quartz.http2.model.Method._
import io.quartz.http2.model.ContentType.JSON

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import fs2.{Stream, Chunk}

import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.quartz.MyLogger._

import java.util.concurrent.ConcurrentHashMap

import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala
import cats.implicits._
import cats.syntax.all._
import ch.qos.logback.classic.Level

//To re-generate slef-signed cert use.
//keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
//in your browser: https://localhost:8443  ( click vist website, you need to accept slef-cigned cert first time )

//case class Device(id: Int, model: String)
//case class User(name: String, devices: Seq[Device])

object Main extends IOApp {

  val CHAT_GPT_TOKEN = "YOUR_TOKEN"
  val TIMEOUT_MS     = 60000

  val connectionTbl =
    ConcurrentHashMap[Long, Http2ClientConnection](100).asScala

  val ctx = QuartzH2Client.buildSSLContext("TLSv1.3", null, null, true)

  case class ChatGPTMessage(role: String, content: String)
  case class ChatGPTAPIRequest(model: String, temperature: Float, messages: Array[ChatGPTMessage])

  given JsonValueCodec[ChatGPTAPIRequest] = JsonCodecMaker.make

  val R: HttpRouteIO =
    ///////////////////////////////////////
    case req @ POST -> Root / "token" =>
      for {
        text    <- req.body.map(String(_))
        connOpt <- IO(connectionTbl.get(req.connId))
        _ <- IO
          .raiseError(new Exception("Cannot connect to openai"))
          .whenA(connOpt.isDefined == false)

        request <- IO(
          ChatGPTAPIRequest(
            "gpt-3.5-turbo",
            0.7,
            messages = Array(ChatGPTMessage("user", s"translate from English to Ukranian: '$text'"))
          )
        )

        response <- connOpt.get.doPost(
          "/v1/chat/completions",
          fs2.Stream.emits(writeToArray(request)),
          Headers().contentType(
            ContentType.JSON
          ) + ("Authorization" -> s"Bearer $CHAT_GPT_TOKEN")
        )
        output <- response.bodyAsText

      } yield (Response.Ok().contentType(ContentType.JSON).asText(output))

  def onDisconnect(id: Long) = for {
    _ <- IO(connectionTbl.get(id).map(c => c.close()))
    _ <- IO(connectionTbl.remove(id))
    _ <- Logger[IO].info(
      s"HttpRouteIO: https://api.openai.com closed for connection Id = $id"
    )
  } yield ()

  def onConnect(id: Long) = for {
    c <- QuartzH2Client.open(
      "https://api.openai.com",
      TIMEOUT_MS,
      ctx = ctx,
      incomingWindowSize = 184590
    )
    _ <- IO(connectionTbl.put(id, c))
    _ <- Logger[IO].info(s"HttpRouteIO: https://api.openai.com open for connection Id = $id")
  } yield ()

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO(QuartzH2Server.setLoggingLevel(Level.DEBUG))
        .whenA(args.find(_ == "--debug").isDefined)
      _ <- IO(QuartzH2Server.setLoggingLevel(Level.ERROR))
        .whenA(args.find(_ == "--error").isDefined)
      _   <- IO(QuartzH2Server.setLoggingLevel(Level.OFF)).whenA(args.find(_ == "--off").isDefined)
      ctx <- QuartzH2Server.buildSSLContext("TLS", "keystore.jks", "password")
      exitCode <- new QuartzH2Server(
        "localhost",
        8443,
        TIMEOUT_MS,
        ctx,
        onConnect = onConnect,
        onDisconnect = onDisconnect
      )
        .startIO(R, sync = false)

    } yield (exitCode)
}
