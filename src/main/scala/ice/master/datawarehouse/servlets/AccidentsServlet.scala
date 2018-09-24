package ice.master.datawarehouse.servlets

import org.scalatra._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

class AccidentsServlet extends ScalatraServlet with CorsSupport {

  /**
    * Adds CORS header to each response in order to prevent cross-origin
    * domain issues.
    */
  after("/*") {
    response.setHeader("Access-Control-Allow-Origin", "*")
  }

  get("/mortal") {
    pretty(render(
      ("mortal_accidents" -> "36") ~ ("total_accidents" -> "100")
    ))
  }


  get("/wounded") {
    pretty(render(
      ("wounded" -> "84") ~ ("total_accidents" -> "100")
    ))
  }
}
