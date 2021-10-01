package lum.cah

import cats.effect.IOApp
import lum.cah.gui.*
import squigly.*

object MainClient extends IOApp.Simple:
  def run = Component.mount(Label(_.text ~ "Hi!"))
