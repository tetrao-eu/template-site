package models.db

// AUTO-GENERATED Slick data model [2016-06-19T16:41:16.325+02:00[Europe/Madrid]]

/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = utils.db.TetraoPostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: utils.db.TetraoPostgresDriver
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Account.schema ++ Message.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Account
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(text)
   *  @param email Database column email SqlType(text)
   *  @param password Database column password SqlType(text)
   *  @param role Database column role SqlType(account_role)
   *  @param createdAt Database column created_at SqlType(timestamptz)
   *  @param updatedAt Database column updated_at SqlType(timestamptz) */
  case class AccountRow(id: Int, name: String, email: String, password: String, role: models.db.AccountRole.Value, createdAt: java.time.OffsetDateTime, updatedAt: java.time.OffsetDateTime)
  /** GetResult implicit for fetching AccountRow objects using plain SQL queries */
  implicit def GetResultAccountRow(implicit e0: GR[Int], e1: GR[String], e2: GR[models.db.AccountRole.Value], e3: GR[java.time.OffsetDateTime]): GR[AccountRow] = GR{
    prs => import prs._
    AccountRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[models.db.AccountRole.Value], <<[java.time.OffsetDateTime], <<[java.time.OffsetDateTime]))
  }
  /** Table description of table account. Objects of this class serve as prototypes for rows in queries. */
  class Account(_tableTag: Tag) extends Table[AccountRow](_tableTag, "account") {
    def * = (id, name, email, password, role, createdAt, updatedAt) <> (AccountRow.tupled, AccountRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(email), Rep.Some(password), Rep.Some(role), Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({r=>import r._; _1.map(_=> AccountRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(text) */
    val name: Rep[String] = column[String]("name")
    /** Database column email SqlType(text) */
    val email: Rep[String] = column[String]("email")
    /** Database column password SqlType(text) */
    val password: Rep[String] = column[String]("password")
    /** Database column role SqlType(account_role) */
    val role: Rep[models.db.AccountRole.Value] = column[models.db.AccountRole.Value]("role")
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz) */
    val updatedAt: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("updated_at")

    /** Uniqueness Index over (email) (database name account_email_key) */
    val index1 = index("account_email_key", email, unique=true)
  }
  /** Collection-like TableQuery object for table Account */
  lazy val Account = new TableQuery(tag => new Account(tag))

  /** Entity class storing rows of table Message
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param content Database column content SqlType(text)
   *  @param tagList Database column tag_list SqlType(_text), Length(2147483647,false)
   *  @param createdAt Database column created_at SqlType(timestamptz)
   *  @param updatedAt Database column updated_at SqlType(timestamptz) */
  case class MessageRow(id: Int, content: String, tagList: List[String], createdAt: java.time.OffsetDateTime, updatedAt: java.time.OffsetDateTime)
  /** GetResult implicit for fetching MessageRow objects using plain SQL queries */
  implicit def GetResultMessageRow(implicit e0: GR[Int], e1: GR[String], e2: GR[List[String]], e3: GR[java.time.OffsetDateTime]): GR[MessageRow] = GR{
    prs => import prs._
    MessageRow.tupled((<<[Int], <<[String], <<[List[String]], <<[java.time.OffsetDateTime], <<[java.time.OffsetDateTime]))
  }
  /** Table description of table message. Objects of this class serve as prototypes for rows in queries. */
  class Message(_tableTag: Tag) extends Table[MessageRow](_tableTag, "message") {
    def * = (id, content, tagList, createdAt, updatedAt) <> (MessageRow.tupled, MessageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(content), Rep.Some(tagList), Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({r=>import r._; _1.map(_=> MessageRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column content SqlType(text) */
    val content: Rep[String] = column[String]("content")
    /** Database column tag_list SqlType(_text), Length(2147483647,false) */
    val tagList: Rep[List[String]] = column[List[String]]("tag_list", O.Length(2147483647,varying=false))
    /** Database column created_at SqlType(timestamptz) */
    val createdAt: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("created_at")
    /** Database column updated_at SqlType(timestamptz) */
    val updatedAt: Rep[java.time.OffsetDateTime] = column[java.time.OffsetDateTime]("updated_at")
  }
  /** Collection-like TableQuery object for table Message */
  lazy val Message = new TableQuery(tag => new Message(tag))
}
