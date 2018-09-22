package server

import java.io.{DataInputStream, DataOutputStream, ObjectInputStream, ObjectOutputStream}
import java.net.ServerSocket

import client._

object Main extends App {
  val lights = LightList()
  val s = new ServerSocket(9000)
  while (true) {
    val socket = s.accept()
    val streamOut = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    val streamIn = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    streamIn.readObject() match {
      case _: AddRequest =>
        lights.addLight()
      case request: DeleteRequest =>
        println(request.ids)
        request.ids.foreach( id => lights.removeLight(id))
      case request: TurnOnRequest =>
        request.ids.foreach( id => lights.turnOnLight(id))
      case request: TurnOffRequest =>
        request.ids.foreach( id => lights.turnOffLight(id))
      case _: GetRequest =>
      case _ =>
    }
    streamOut.writeObject(lights)
    streamOut.flush()
    socket.close()
  }
}
