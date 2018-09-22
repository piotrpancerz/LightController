package server

import scala.collection.mutable

case class LightList() {
  val lights: mutable.Buffer[Light] = mutable.Buffer[Light]()

  def addLight(): Unit = {
    if(lights.length < 10) {
      lights += Light()
    }
  }
  def getLightById(id: Int): Option[Light] = {
    lights.find(id == _.id)
  }

  def removeLight(id: Int): Unit = {
    val lightOpt = getLightById(id)
    lightOpt match {
      case _: Option[Light] => lights -= lightOpt.get
      case None =>
    }
  }

  def turnOnLight(id: Int): Unit = {
    val lightOpt = getLightById(id)
    lightOpt match {
      case _: Option[Light] => lightOpt.get.turnOn
      case None =>
    }
  }

  def turnOffLight(id: Int): Unit = {
    val lightOpt = getLightById(id)
    lightOpt match {
      case _: Option[Light] => lightOpt.get.turnOff
      case None =>
    }
  }
}
