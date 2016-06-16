package utils.db

import com.github.tminglei.slickpg._
import models.db.AccountRole

trait TetraoPostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgEnumSupport
  with PgRangeSupport
  with PgDate2Support
  with PgHStoreSupport
  with PgSearchSupport
  with PgNetSupport
  with PgLTreeSupport {

  override val api = TetraoAPI

  object TetraoAPI extends API with ArrayImplicits
    with DateTimeImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
    with SearchAssistants {

    implicit val tAccountRole = createEnumJdbcType("account_role", AccountRole)
    implicit val lAccountRole = createEnumListJdbcType("account_role", AccountRole)
    implicit val cAccountRole = createEnumColumnExtensionMethodsBuilder(AccountRole)
    implicit val oAccountRole = createEnumOptionColumnExtensionMethodsBuilder(AccountRole)

    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

}

object TetraoPostgresDriver extends TetraoPostgresDriver
