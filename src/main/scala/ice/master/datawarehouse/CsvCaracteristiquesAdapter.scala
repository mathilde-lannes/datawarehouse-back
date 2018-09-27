package ice.master.datawarehouse

import java.io.File

import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import scalaj.http._
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.ObjectMapper
import ice.master.datawarehouse.model.Lieu
import scala.io.Source
import java.io.FileNotFoundException
import ice.master.datawarehouse.model.Accident
import org.bson.types.ObjectId

case class Caracteristique(Num_Acc:String,  dep: String, com: String)

object CsvCaracteristiquesAdapter {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
            .builder()
            .master("local[*]")
            .appName("M2 ICE — Datawarehouse")
            .getOrCreate()

        val customer = spark.read
            .option("header", true)
            .csv(new File("src/main/resources/caracteristiques_2016.csv").getAbsolutePath)
            
        import spark.implicits._
        
        val database = new Database()
        
        val lieux = customer
            .as[Caracteristique]
            .map { case Caracteristique(acc, dep, com) => (acc, Lieu(dep, com, nomCommune(dep, com))) }
            .collect()
            
       database.persistAccidents(lieux.map { case Tuple2(numAcc, _) => Accident(new ObjectId(), numAcc) })
       database.persistLieux(lieux.map(_._2))
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
                
                val mapper = new ObjectMapper() with ScalaObjectMapper
                mapper.registerModule(DefaultScalaModule)
                val parsedJson = mapper.readValue[Map[String, Object]](json.reader())
                
                parsedJson("nom").asInstanceOf[String]
                
            } catch {
                case e: FileNotFoundException => {
//                    e.printStackTrace()
                    ""
                }
            }
        }
            
    }
    
}