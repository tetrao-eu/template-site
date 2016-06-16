package models

import play.api.data.{Mapping, Form}
import play.api.data.Forms._

case class FormDataLogin(email: String, password: String)

case class FormDataAccount(name:String, email: String, password: String, passwordAgain:String)

object FormData {

  val login = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(FormDataLogin.apply)(FormDataLogin.unapply)
  )

  val addMessage = Form(
    mapping(
      "content" -> nonEmptyText,
      "tags" -> text
    )(Message.formApply)(Message.formUnapply)
  )

  private[this] def accountForm(passwordMapping:Mapping[String]) = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email,
      "password" -> passwordMapping,
      "passwordAgain" -> passwordMapping
    )(FormDataAccount.apply)(FormDataAccount.unapply)
  )

  val updateAccount = accountForm(text)

  val addAccount = accountForm(nonEmptyText)
}
