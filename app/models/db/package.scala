package models

package object db {

  object AccountRole extends Enumeration {
    val normal, admin = Value
  }
}
