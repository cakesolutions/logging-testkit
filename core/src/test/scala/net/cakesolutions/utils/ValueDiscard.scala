// Copyright 2016-2018 Cake Solutions Limited

package net.cakesolutions.utils

object ValueDiscard {
  def apply[T](value: => T): Unit = {
    val _ = value
  }
}
