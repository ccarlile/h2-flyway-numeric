package com.github.ccarlile.h2.numeric

import doobie._
import doobie.implicits._

case class TestClass(id: String, value: BigDecimal)

object DoobieTestQueries {
  def query(id: String): Query0[TestClass] =
    sql"select * from test where id=$id".query[TestClass]

  def write(t: TestClass): Update0 =
    sql"insert into test (id, val) values (${t.id}, ${t.value})".update
}
