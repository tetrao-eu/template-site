package controllers

import javax.inject.{Inject, Singleton}

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api.mvc.Controller
import services.db.DBService

@Singleton
class PublicApplication @Inject()(val database: DBService, implicit val webJarAssets: WebJarAssets)
  extends Controller with AuthConfigTrait with OptionalAuthElement {

  def index() = StackAction { implicit request =>
    Ok(views.html.index(loggedIn))
  }
}
