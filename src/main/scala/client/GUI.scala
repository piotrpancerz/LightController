package client

import java.io.{DataInputStream, DataOutputStream, ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Button, CheckBox, Slider}
import scalafx.scene.image.{Image, ImageView}
import server.LightList

import scala.collection.mutable

object GUI extends JFXApp {
  var lightList: LightList = getRequest()
  var checked: mutable.Map[Int, Boolean] = scala.collection.mutable.Map((lightList.lights.map(_.id).zip(Array.fill(lightList.lights.length)(false)).toList): _*)
  stage = new JFXApp.PrimaryStage {
    scene = new Scene(700, 250) {
      val aButton = new Button("Add light")
      aButton.layoutX = 50
      aButton.layoutY = 20
      aButton.prefWidth = 150
      val rButton = new Button("Remove checked")
      rButton.layoutX = 50
      rButton.layoutY = 70
      rButton.prefWidth = 150
      val onButton = new Button("Turn On checked")
      onButton.layoutX = 50
      onButton.layoutY = 120
      onButton.prefWidth = 150
      val offButton = new Button("Turn Off checked")
      offButton.layoutX = 50
      offButton.layoutY = 170
      offButton.prefWidth = 150
      val checkBoxes = Array.fill(10)(new CheckBox())
      val imageViews = Array.fill(10)(new ImageView(new Image("file:on.png",20,20,true,true)))
      checkBoxes.indices.foreach( i => {
        checkBoxes(i).visible = false
        checkBoxes(i).layoutX = 300+((i%5)*75)
        checkBoxes(i).layoutY = if(i<=4) 30 else 130
      })
      imageViews.indices.foreach(i => {
        imageViews(i).visible = false
        imageViews(i).layoutX = 300+((i%5)*75)
        imageViews(i).layoutY = if(i<=4) 55 else 155
      })
      updateGUI(checkBoxes, imageViews)

      aButton.onAction = () => {
        addRequest()
        updateGUI(checkBoxes, imageViews)
      }

      rButton.onAction = () => {
        val lightsToRemove = checked.filter(_._2 == true).unzip( a => (a._1,a._2))
        deleteRequest(lightsToRemove._1.toList)
        updateGUI(checkBoxes, imageViews)
      }

      onButton.onAction = () => {
        val lightsToTurnOn = checked.filter(_._2 == true).unzip( a => (a._1,a._2))
        turnOnRequest(lightsToTurnOn._1.toList)
        updateGUI(checkBoxes, imageViews)
      }

      offButton.onAction = () => {
        val lightsToTurnOff = checked.filter(_._2 == true).unzip( a => (a._1,a._2))
        turnOffRequest(lightsToTurnOff._1.toList)
        updateGUI(checkBoxes, imageViews)
      }

      checkBoxes.indices.foreach( i => checkBoxes(i).onAction = () => {
        if(checkBoxes(i).selected())
          checked(lightList.lights(i).id) = true
        else
          checked(lightList.lights(i).id) = false
      })

      content = List(aButton,rButton,onButton,offButton)
      checkBoxes.foreach(content += _)
      imageViews.foreach(content += _)
    }
  }

  def getRequest(): LightList = {
    val socket = new Socket(InetAddress.getByName("localhost"), 9000)
    val getRequest = GetRequest()
    val streamIn = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    val streamOut = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    streamOut.writeObject(getRequest)
    streamOut.flush()
    lightList = streamIn.readObject().asInstanceOf[LightList]
    socket.close()
    lightList
  }

  def addRequest(): Unit = {
    val socket = new Socket(InetAddress.getByName("localhost"), 9000)
    val addRequest = AddRequest()
    val streamIn = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    val streamOut = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    streamOut.writeObject(addRequest)
    streamOut.flush()
    lightList = streamIn.readObject().asInstanceOf[LightList]
    socket.close()
  }

  def deleteRequest(ids: List[Int]): Unit = {
    val socket = new Socket(InetAddress.getByName("localhost"), 9000)
    val deleteRequest = DeleteRequest(ids)
    val streamIn = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    val streamOut = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    streamOut.writeObject(deleteRequest)
    streamOut.flush()
    lightList = streamIn.readObject().asInstanceOf[LightList]
    socket.close()
  }

  def turnOnRequest(ids: List[Int]): Unit = {
    val socket = new Socket(InetAddress.getByName("localhost"), 9000)
    val turnOnRequest = TurnOnRequest(ids)
    val streamIn = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    val streamOut = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    streamOut.writeObject(turnOnRequest)
    streamOut.flush()
    lightList = streamIn.readObject().asInstanceOf[LightList]
    socket.close()
  }

  def turnOffRequest(ids: List[Int]): Unit = {
    val socket = new Socket(InetAddress.getByName("localhost"), 9000)
    val turnOffRequest = TurnOffRequest(ids)
    val streamIn = new ObjectInputStream(new DataInputStream(socket.getInputStream()))
    val streamOut = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()))
    streamOut.writeObject(turnOffRequest)
    streamOut.flush()
    lightList = streamIn.readObject().asInstanceOf[LightList]
    socket.close()
  }

  def updateGUI(checkBoxes: Array[CheckBox], imageViews : Array[ImageView]): Unit = {
    for(i <- 0 until lightList.lights.length){
      checkBoxes(i).text = lightList.lights(i).id.toString
      checkBoxes(i).selected = false
      checkBoxes(i).visible = true
      imageViews(i).image = if(lightList.lights(i).state){
        new Image("file:on.png",20,20,true,true)
      } else {
        new Image("file:off.png",20,20,true,true)
      }
      imageViews(i).visible = true
    }
    for(i <- lightList.lights.length until 10){
      checkBoxes(i).selected = false
      checkBoxes(i).visible = false
      imageViews(i).visible = false
    }
    checked.keys.foreach(x => checked(x) = false)
  }
}
