package models

import java.time.OffsetDateTime

import models.db.{AccountRole, Tables}

case class Entity[T](id:Int, data:T)

case class Account(name: String, email: String, role: AccountRole.Value) {
  def isAdmin: Boolean = role == AccountRole.admin
}

object Account {
  def apply(row: Tables.AccountRow): Entity[Account] = {
    Entity(
      id = row.id,
      data = Account(
        name = row.name,
        email = row.email,
        role = row.role
      )
    )
  }
}

case class Message(content:String, tagSet:Set[String]) {
  def toRow() = {
    val now = OffsetDateTime.now()
    Tables.MessageRow(
      id = -1,
      content = content,
      tagList = tagSet.toList,
      createdAt = now,
      updatedAt = now
    )
  }
}

object Message {
  def apply(row: Tables.MessageRow): Entity[Message] = {
    Entity(
      id = row.id,
      data = Message(
        content = row.content,
        tagSet = row.tagList.toSet
      )
    )
  }

  def formApply(content:String, tags:String):Message = {
    Message(
      content = content.trim,
      tagSet = tags.split(",").map(_.trim).filterNot(_.isEmpty).toSet
    )
  }

  def formUnapply(m:Message):Option[(String, String)] = {
    Some((m.content, m.tagSet.mkString(",")))
  }
}

