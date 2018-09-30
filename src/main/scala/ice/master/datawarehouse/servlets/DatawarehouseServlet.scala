package ice.master.datawarehouse.servlets

import ice.master.datawarehouse.Database
import org.scalatra._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.mongodb.scala.Document
import ice.master.datawarehouse.CsvAdapter
import ice.master.datawarehouse.MongoHelpers._

class DatawarehouseServlet extends ScalatraServlet with CorsSupport {

    /**
     * Adds CORS header to each response in order to prevent cross-origin
     * domain issues.
     */
    after("/*") {
        response.setHeader("Access-Control-Allow-Origin", "*")
    }

    get("/init") {
        val database: Database = new Database()
        
        CsvAdapter.fill(database)
    }

}
