package ice.master.datawarehouse.servlets

import ice.master.datawarehouse.Database
import org.scalatra._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.mongodb.scala.Document

class DatawarehouseServlet extends ScalatraServlet with CorsSupport {

    /**
     * Adds CORS header to each response in order to prevent cross-origin
     * domain issues.
     */
    after("/*") {
        response.setHeader("Access-Control-Allow-Origin", "*")
    }

    get("/") {
        val database: Database = new Database()
        
        database.recreate()
    }

}
