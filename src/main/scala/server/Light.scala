package server

class Light (_id: Int) extends Serializable {
  def id = _id
  var state = true
  var visible = true
  def turnOn = state = true
  def turnOff = state = false
}

object Light {
  var id: Int = 1
  def apply(): Light = {
    val light = new Light(id)
    id = id + 1
    light
  }
}