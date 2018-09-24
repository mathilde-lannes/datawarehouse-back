package ice.master.datawarehouse

import ice.master.datawarehouse.servlets.DatawarehouseServlet
import org.scalatra.test.scalatest._

class DatawarehouseServletTests extends ScalatraFunSuite {

  addServlet(classOf[DatawarehouseServlet], "/*")

  test("GET / on DatawarehouseServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
