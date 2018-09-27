package ice.master.datawarehouse

import java.io.FileNotFoundException

import scala.io.Source
import com.github.tototoshi.csv._

import org.bson.types.ObjectId
import org.json4s._
import org.json4s.jackson.JsonMethods._

import com.fasterxml.jackson.databind.ObjectMapper
import ice.master.datawarehouse.model.Accident
import ice.master.datawarehouse.model.Lieu
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import ice.master.datawarehouse.model.Details
import ice.master.datawarehouse.model.VehiculeAccidente

object CsvAdapter {
    
    def main(args: Array[String]): Unit = {
       // CARACTERISTIQUES
        
       var lieux = Set[Lieu]()
       var accidents = Set[Accident]()
       
       var isHeadersLine = true
       
       for (caracteristiques <- CSVReader.open(new File("src/main/resources/caracteristiques_2016.csv")).allWithHeaders()) {
           if (isHeadersLine) {
               isHeadersLine = false
           }
           else {
               val accidentId = caracteristiques("Num_Acc")
               val commune = caracteristiques("com")
               val departement = caracteristiques("dep")
               
        	   lieux += Lieu(departement, commune, nomCommune(departement, commune))
        	   accidents += Accident(new ObjectId(), accidentId)
           }
       }
        
        // USAGERS
        
        isHeadersLine = true
        
        var details = Set[Details]()
       
       for (usager<- CSVReader.open(new File("src/main/resources/usagers_2016.csv")).allWithHeaders()) {
           if (isHeadersLine) {
               isHeadersLine = false // skip headers
           }
           else {
               if (usager("grav") != "" && usager("trajet") != "")
                   details += Details(usager("grav").toInt, usager("trajet").toInt, usager("Num_Acc"))
           }
       }
        
       // VEHICULES
        
        isHeadersLine = true
        
        var vehiculesAccidentes = Map[String, VehiculeAccidente]()
       
       for (fields <- CSVReader.open(new File("src/main/resources/vehicules_2016.csv")).all()) {
           if (isHeadersLine) {
               isHeadersLine = false // skip headers
           }
           else {
               val List(accidentId, senc, catv, occutc, obs, obsm, choc, manv, id) = fields
               
               if (vehiculesAccidentes contains id) {
                   vehiculesAccidentes(id).manoeuvresId = (manv.toInt) :: vehiculesAccidentes(id).manoeuvresId
        		   vehiculesAccidentes(id).obstaclesFixesId = (obs.toInt) :: vehiculesAccidentes(id).obstaclesFixesId
        		   vehiculesAccidentes(id).obstaclesMobilesId = (obsm.toInt) :: vehiculesAccidentes(id).obstaclesMobilesId
               }
               else {
                   vehiculesAccidentes = vehiculesAccidentes + (id -> VehiculeAccidente(id, accidentId, List(obs.toInt), List(obsm.toInt), List(manv.toInt)))
               }
           }
       }
       
       val database = new Database()
       database.recreate()
       
       // Wait for the database to re-create itself --> better allow to subscribe somehow...
       Thread.sleep(5000)
        
       database.persistAccidents(accidents)
       database.persistLieux(lieux)
       database.persistDetails(details)
       database.persistVehiculesAccidentes(vehiculesAccidentes.values.toSet)
    }
    
    def nomCommune(departement: String, commune: String): String = {
        if (departement == null)
            ""
        else if (departement.last != '0') {
            "hors métropole"
        }
        else {
            val insee = departement.substring(0, 2) + commune
            try {
                val json = Source.fromURL(s"https://geo.api.gouv.fr/communes/$insee?fields=nom&format=json&geometry=centre")
                val jobject = parse(json.mkString)
                
                (jobject \ "nom") match {
                    case JString(name) => name
                    case _ => "" // should never happen
                }
                
            } catch {
                case e: FileNotFoundException => {
                    // the web service is not up-to-date and some INSEE code are not known
                    ""
                }
            }
        }
            
    }
    
}