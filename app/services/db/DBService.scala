package services.db

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.{DBIOAction, NoStream}
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@ImplementedBy(classOf[DBServiceImpl])
trait DBService {
  def runAsync[R](a: DBIOAction[R, NoStream, Nothing]): Future[R]

  def run[R](a: DBIOAction[R, NoStream, Nothing]): R
}

@Singleton
class DBServiceImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends DBService {
  private val db = dbConfigProvider.get[JdbcProfile].db

  def runAsync[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = {
    db.run(a)
  }

  def run[R](a: DBIOAction[R, NoStream, Nothing]): R = {
    Await.result(runAsync(a), Duration.Inf)
  }
}
