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

object CsvCaracteristiquesAdapter {
    def main(args: Array[String]): Unit = {
        System.setProperty("javax.net.ssl.trustStore", f"C:/Programmation/Java/bin/jre1.8.0_181/lib/security/cacerts")
            
       var lieux = Seq[Lieu]()
       var accidents = Seq[Accident]()
       
       var firstLine = true
       
       for (fields <- CSVReader.open(new File("src/main/resources/caracteristiques_2016.csv")).all()) {
           if (firstLine) {
               firstLine = false
           }
           else {
        	   lieux = lieux :+ Lieu(fields(15), fields(11), nomCommune(fields(15), fields(11)))
        	   accidents = accidents :+ Accident(new ObjectId(), fields(0))
           }
       }
       
        println(lieux)
       
       val database = new Database()
        
       database.persistAccidents(accidents)
       database.persistLieux(lieux)
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
                (jobject \ "name") match {
                    case JString(name) => name
                    case _ => ""
                }
                ""
                
            } catch {
                case e: FileNotFoundException => {
//                    e.printStackTrace()
                    ""
                }
            }
        }
            
    }
    
}