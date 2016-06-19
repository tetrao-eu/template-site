package controllers

import java.time.OffsetDateTime
import javax.inject.{Inject, Singleton}

import com.github.t3hnar.bcrypt.Password
import jp.t2v.lab.play2.auth.AuthElement
import models.db.{AccountRole, Tables}
import models.{FormData, FormDataAccount, Message}
import play.api.Logger
import play.api.mvc.Controller
import services.db.DBService
import utils.db.TetraoPostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class RestrictedApplication @Inject()(val database: DBService, implicit val webJarAssets: WebJarAssets)
  extends Controller with AuthConfigTrait with AuthElement {

  def messages() = AsyncStack(AuthorityKey -> AccountRole.normal) { implicit request =>
    database.runAsync(Tables.Message.sortBy(_.id).result).map { rowSeq =>
      val messageSeq = rowSeq.map(Message(_))
      Ok(views.html.messages(loggedIn, messageSeq, FormData.addMessage))
    }
  }

  def deleteMessage(id:Int) = AsyncStack(AuthorityKey -> AccountRole.normal) { implicit request =>
    database.runAsync(Tables.Message.filter(_.id === id).delete).map {_ =>
      Logger.info(s"Deleted message#$id by ${loggedIn.data.email}" )
      Redirect(routes.RestrictedApplication.messages())
    }
  }

  def addMessage() = AsyncStack(AuthorityKey -> AccountRole.normal) { implicit request =>
    FormData.addMessage.bindFromRequest.fold(
      formWithErrors => Future.successful(Redirect(routes.RestrictedApplication.messages())),
      message => {
        database.runAsync((Tables.Message returning Tables.Message.map(_.id)) += message.toRow()).map { id =>
          Logger.info(s"Inserted message#$id by ${loggedIn.data.email}")
          Redirect(routes.RestrictedApplication.messages())
        }
      }
    )
  }

  def account() = StackAction(AuthorityKey -> AccountRole.normal) { implicit request =>
    val form = FormData.updateAccount.fill(FormDataAccount(loggedIn.data.name, loggedIn.data.email, "", ""))
    Ok(views.html.account(loggedIn, form, insert = false))
  }

  def updateAccount() = AsyncStack(AuthorityKey -> AccountRole.normal) { implicit request =>
    FormData.updateAccount.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.account(loggedIn, formWithErrors, insert = false))),
      accountFormData => {
        if(accountFormData.password.nonEmpty && accountFormData.password != accountFormData.passwordAgain) {
          val form = FormData.addAccount.fill(accountFormData).withError("passwordAgain", "Passwords don't match")
          Future.successful(BadRequest(views.html.account(loggedIn, form, insert = false)))
        } else {
          val now = OffsetDateTime.now()
          val update = if(accountFormData.password.nonEmpty) {
            //update also password
            val bcryptedPassword = accountFormData.password.bcrypt
            val q = for {
              row <- Tables.Account if row.id === loggedIn.id
            } yield (row.name, row.email, row.password, row.updatedAt)
            q.update((accountFormData.name, accountFormData.email, bcryptedPassword, now))
          } else {
            val q = for {
              row <- Tables.Account if row.id === loggedIn.id
            } yield (row.name, row.email, row.updatedAt)
            q.update((accountFormData.name, accountFormData.email, now))
          }
          database.runAsync(update).map { _ =>
            Logger.info(s"Updated account of ${loggedIn.data.email}")
            Redirect(routes.RestrictedApplication.messages())
          }
        }
      }
    )
  }

  def newAccount() = StackAction(AuthorityKey -> AccountRole.admin) { implicit request =>
    Ok(views.html.account(loggedIn, FormData.addAccount, insert = true))
  }

  def addAccount() = AsyncStack(AuthorityKey -> AccountRole.admin) { implicit request =>
    FormData.addAccount.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.account(loggedIn, formWithErrors, insert = true))),
      accountFormData => {
        if(accountFormData.password.nonEmpty && accountFormData.password == accountFormData.passwordAgain) {
          val now = OffsetDateTime.now()
          val row = Tables.AccountRow(
            id = -1,
            name = accountFormData.name,
            email = accountFormData.email,
            password = accountFormData.password.bcrypt,
            role = AccountRole.normal,
            updatedAt = now,
            createdAt = now
          )
          database.runAsync((Tables.Account returning Tables.Account.map(_.id)) += row).map { id =>
            Logger.info(s"Inserted account#$id by ${loggedIn.data.email}")
            Redirect(routes.RestrictedApplication.messages())
          }
        } else {
          val form = FormData.addAccount.fill(accountFormData).withError("passwordAgain", "Passwords don't match")
          Future.successful(BadRequest(views.html.account(loggedIn, form, insert = true)))
        }
      }
    )
  }
}
