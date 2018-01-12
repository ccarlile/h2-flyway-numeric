package com.github.ccarlile.h2.numeric

import org.flywaydb.core.Flyway
import org.specs2.mutable._
import doobie.specs2._
import doobie.h2._
import cats.effect._

object TestCase extends Specification with IOChecker {
  val jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
  val dbUserName = "sa"
  val dbPassword = ""

  val id: String = java.util.UUID.randomUUID().toString
  val value: BigDecimal = 1.23

  val testCase = TestClass(id, value)

  def doMigration(jdbc: String, user: String, pass: String): IO[Int] = {
    val flyway = new Flyway
    for {
      _ <- IO(flyway.setDataSource(jdbc,user,pass))
      r <- IO(flyway.migrate())
    } yield r
  }

  val transactor = H2Transactor[IO](
    jdbcUrl,
    dbUserName,
    dbPassword
  ).unsafeRunSync

  doMigration(jdbcUrl, dbUserName, dbPassword).unsafeRunSync

  // reads fine according to JDBC specification
  check(DoobieTestQueries.query(id))

  // fails because metadata reads as DECIMAL while JDBC forbids the BigDecimal -> DECIMAL conversion
  check(DoobieTestQueries.write(testCase))


  /* output of the above test cases:
   [info] + Query0[h2.TestClass] defined at DoobieTestQuery.scala:10
   [info]   
   [info]     select * from test where id=?
   [info]   
   [info]   + SQL Compiles and TypeChecks
   [info]   + P01 String  →  VARCHAR (VARCHAR)
   [info]   + C01 ID  VARCHAR (VARCHAR) NOT NULL  →  String
   [info]   + C02 VAL DECIMAL (DECIMAL) NOT NULL  →  BigDecimal
   [info] + Update0 defined at DoobieTestQuery.scala:13
   [info]   
   [info]     insert into test (id, val) values (?, ?)
   [info]   
   [info]   + SQL Compiles and TypeChecks
   [info]   + P01 String      →  VARCHAR (VARCHAR)
   [error]   x P02 BigDecimal  →  DECIMAL (DECIMAL)
   [error]    BigDecimal is not coercible to DECIMAL (DECIMAL) according to the JDBC
   [error]    specification. Fix this by changing the schema type to NUMERIC, or the
   [error]    Scala type to . (analysisspec.scala:72)
   */

}
