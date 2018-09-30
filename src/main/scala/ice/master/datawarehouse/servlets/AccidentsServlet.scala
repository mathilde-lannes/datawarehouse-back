package ice.master.datawarehouse.servlets

import org.mongodb.scala.model.Filters._

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.scalatra._

import ice.master.datawarehouse.Database
import ice.master.datawarehouse.MongoHelpers._
import ice.master.datawarehouse.model.Details

class AccidentsServlet extends ScalatraServlet with CorsSupport {

    /**
     * Adds CORS header to each response in order to prevent cross-origin
     * domain issues.
     */
    after("/*") {
        response.setHeader("Access-Control-Allow-Origin", "*")
    }

    get("/mortal") {
    	val database = new Database()
		val totalAccidents = database.details.countDocuments.results()(0)
    			
        // I cannot make it work
        val mortalAccidents = database.details.distinct[Details]("accidentId", equal("graviteId", 2)).results()
        
        pretty(render(
            ("mortal_accidents" -> 0) ~ ("total_accidents" -> totalAccidents)))
    }

    get("/wounded") {
    	val database = new Database()
		val totalAccidents = database.details.countDocuments.results()(0)
		
        pretty(render(
            ("wounded" -> 84) ~ ("total_accidents" -> totalAccidents)))
    }
}
