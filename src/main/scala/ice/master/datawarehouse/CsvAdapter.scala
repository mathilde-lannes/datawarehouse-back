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
import ice.master.datawarehouse.MongoHelpers._
import org.mongodb.scala.MongoCollection
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import org.mongodb.scala.Completed
import org.mongodb.scala.Observable

object CsvAdapter {
    
    def fill(database: Database) = {
        
        // RETRIEVE CARACTERISTIQUES

        val communesPerInsee: Map[String, String] = communesFrom(new File("src/main/resources/laposte_hexasmal.csv"))
        val Tuple2(lieux, accidents) = caracteristiquesFrom(new File("src/main/resources/caracteristiques_2016.csv"), communesPerInsee) 
        
        // RETRIEVE USAGERS
        
        val details = detailsFrom(new File("src/main/resources/usagers_2016.csv"))
        
        // RETRIEVE VEHICULES
        
        val vehiculesAccidentes = vehiculesFrom(new File("src/main/resources/vehicules_2016.csv"))

        // FILL DATABASE (requires caller to subscribe the returned Observable)

        database.recreate()
        
        database.accidents.insertMany(accidents.toList).join()
        database.lieux.insertMany(lieux.toList).join()
        database.details.insertMany(details.toList).join()
        database.vehicules.insertMany(vehiculesAccidentes.toList).join()
    }
    
    def communesFrom(communesCSV: File): Map[String, String] = {
        // Makes possible to parse the CSV using ';' as delimiter
        implicit object SemicolonSeparatorFormat extends DefaultCSVFormat {
            override val delimiter = ';'
        }
        
        var communesPerInsee = Map[String, String]()
        
        for (commune <- CSVReader.open(communesCSV).iteratorWithHeaders) {
            val insee = commune("Code_commune_INSEE")
            val name = commune("Nom_commune")
                
            def isValid(s: String) = s != null && s != "";
                
            // Incorrect values are ignored
            if (isValid(insee) && isValid(name)) {
                communesPerInsee += (insee -> name)
            }
        }
        
        communesPerInsee
    }
    
    def caracteristiquesFrom(caracteristiquesCSV: File, communesPerInsee: Map[String, String]): Tuple2[Set[Lieu], Set[Accident]] = {
        var lieux = Set[Lieu]()
        var accidents = Set[Accident]()
        
        var communePerInsee: Map[String, String] = Map()

        for (caracteristiques <- CSVReader.open(caracteristiquesCSV).iteratorWithHeaders) {
            val accidentId = caracteristiques("Num_Acc")
            val commune = caracteristiques("com")
            val departement = caracteristiques("dep")
            
            // Incorrect values are ignored
            if (commune.length == 3 && departement.length == 3)
                lieux += Lieu(departement, commune, nomCommune(communePerInsee, departement, commune))
            
            accidents += Accident(accidentId)
        }
        
        return (lieux, accidents)
    }

    def nomCommune(communes: Map[String, String], departement: String, commune: String): String = {
        if (departement.last != '0') {
            "hors métropole"
        } else {
            val insee = departement.substring(0, 2) + commune
            communes getOrElse (insee, "")
        }
    }
    
    def detailsFrom(detailsCSV: File): Set[Details] = {
        var details = Set[Details]()

        for (usager <- CSVReader.open(detailsCSV).iteratorWithHeaders) {
            if (usager("grav") != "" && usager("trajet") != "")
                details += Details(usager("grav").toInt, usager("trajet").toInt, usager("Num_Acc"))
        }
        
        details
    }
    
    def vehiculesFrom(vehiculesCSV: File): Set[VehiculeAccidente] = {
        var isHeadersLine = true

        var vehiculesAccidentes = Map[(String, String), VehiculeAccidente]()

        for (fields <- CSVReader.open(vehiculesCSV).iterator) {
            if (isHeadersLine) {
                isHeadersLine = false // skip headers
            } else {
                val List(accidentId, senc, catv, occutc, obs, obsm, choc, manv, vehiculeId) = fields
                
                val id = (accidentId, vehiculeId)

                if (vehiculesAccidentes contains (id)) {
                    if (manv != "")
                        vehiculesAccidentes(id).manoeuvresId = (manv.toInt) :: vehiculesAccidentes(id).manoeuvresId

                    if (obs != "")
                        vehiculesAccidentes(id).obstaclesFixesId = (obs.toInt) :: vehiculesAccidentes(id).obstaclesFixesId

                    if (obsm != "")
                        vehiculesAccidentes(id).obstaclesMobilesId = (obsm.toInt) :: vehiculesAccidentes(id).obstaclesMobilesId
                } else {
                    def parse(n: String) = Try(List(n.toInt)).getOrElse(List()) 
                    vehiculesAccidentes += (id -> VehiculeAccidente(id.toString, vehiculeId, accidentId, parse(obs), parse(obsm), parse(manv)))
                }
            }
        }
        vehiculesAccidentes.values.toSet
    }

}