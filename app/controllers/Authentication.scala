package controllers

import javax.inject.{Inject, Singleton}

import com.github.t3hnar.bcrypt.Password
import jp.t2v.lab.play2.auth._
import models.db.{AccountRole, Tables}
import models.{Account, Entity, FormData}
import play.api.Logger
import play.api.mvc.Results.Redirect
import play.api.mvc.{Action, Controller, RequestHeader, Result}
import services.db.DBService
import utils.db.TetraoPostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.{ClassTag, classTag}

private[controllers] trait AuthConfigTrait extends AuthConfig {

  /**
    * Reference to the database service to be run
    */
  def database: DBService

  /**
    * A type that is used to identify a user.
    * `String`, `Int`, `Long` and so on.
    */
  type Id = Int

  /**
    * A type that representss a user in your application.
    * `User`, `Account` and so on.
    */
  type User = Entity[Account]

  /**
    * A type that is defined by every action for authorization.
    */
  type Authority = AccountRole.Value

  /**
    * A `ClassTag` is used to retrieve an id from the Cache API.
    * Use something like this:
    */
  val idTag: ClassTag[Id] = classTag[Id]

  /**
    * The session timeout in seconds
    */
  val sessionTimeoutInSeconds: Int = 3600

  /**
    * A function that returns a `User` object from an `Id`.
    * You can alter the procedure to suit your application.
    */
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = {
    database
      .runAsync(Tables.Account.filter(_.id === id).take(1).result.headOption)
      .map(_.map(models.Account(_)))
  }

  /**
    * Where to redirect the user after a successful login.
    */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(routes.RestrictedApplication.messages()))
  }

  /**
    * Where to redirect the user after logging out
    */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(routes.PublicApplication.index()))
  }

  /**
    * If the user is not logged in and tries to access a protected resource then redirect them as follows:
    */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(routes.Authentication.login()))

  /**
    * If authorization failed (usually incorrect password) redirect the user as follows:
    */
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit context: ExecutionContext): Future[Result] = {
    Future.successful(Redirect(routes.Authentication.login()))
  }

  /**
    * A function that determines what `Authority` a user has.
    * You should alter this procedure to suit your application.
    */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    (user.data.role, authority) match {
      case (AccountRole.admin, _) => true
      case (AccountRole.normal, AccountRole.normal) => true
      case _ => false
    }
  }

  /**
    * (Optional)
    * You can custom SessionID Token handler.
    * Default implementation use Cookie.
    */
  override lazy val tokenAccessor = new CookieTokenAccessor(
      cookieSecureOption = false,
      cookieMaxAge = None
  )
}

@Singleton
class Authentication @Inject()(val database: DBService, implicit val webJarAssets: WebJarAssets) extends Controller with AuthConfigTrait with OptionalAuthElement with LoginLogout {

  def prepareLogin() = StackAction { implicit request =>
    if (loggedIn.isDefined) {
      Redirect(routes.RestrictedApplication.messages())
    } else {
      Ok(views.html.login(FormData.login))
    }
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def login() = Action.async { implicit request =>
    FormData.login.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
      account => {
        val q = for {
          row <- Tables.Account.filter(_.email === account.email).take(1)
        } yield (row.id, row.password)
        database.runAsync(q.result.headOption).flatMap {
          case None => {
            Logger.warn(s"Wrong user")
            val form = FormData.login.fill(account).withError("email", "Invalid user")
            Future.successful(BadRequest(views.html.login(form)))
          }
          case Some(user) => {
            if(account.password.isBcrypted(user._2)) {
              Logger.info(s"Login by ${account.email}")
              gotoLoginSucceeded(user._1)
            } else {
              Logger.warn(s"Wrong login credentials!")
              val form = FormData.login.fill(account).withError("password", "Invalid password")
              Future.successful(BadRequest(views.html.login(form)))
            }
          }
        }
      }
    )
  }
}
