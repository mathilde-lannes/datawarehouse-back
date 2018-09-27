package ice.master.datawarehouse

import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.mongodb.scala.Completed
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros.createCodecProvider

import ice.master.datawarehouse.model.CategorieDeManoeuvre
import ice.master.datawarehouse.model.Gravite
import ice.master.datawarehouse.model.Lieu
import ice.master.datawarehouse.model.Manoeuvre
import ice.master.datawarehouse.model.ObstacleFixe
import ice.master.datawarehouse.model.ObstacleMobile
import ice.master.datawarehouse.model.Trajet
import ice.master.datawarehouse.model.Accident
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.InsertManyOptions


class Database {
    val mongo: MongoClient = MongoClient()
    
    val codecRegistry = fromRegistries(fromProviders(classOf[Accident], classOf[Gravite], classOf[ObstacleFixe], classOf[Trajet], classOf[Lieu], classOf[Manoeuvre], classOf[ObstacleFixe], classOf[ObstacleMobile], classOf[CategorieDeManoeuvre]), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = mongo.getDatabase("warehouse").withCodecRegistry(codecRegistry)

    def drop() = {
        database.drop()
    }

    def recreate() = {
        drop().subscribe((_: Completed) => {
            
            // CREATE TRAJETS
            
            val trajets: MongoCollection[Trajet] = database.getCollection("trajets")
            val newTrajets: Seq[Trajet] = Seq(
                Trajet(0, "Non-renseigné"),
                Trajet(1, "Domicile — travail"),
                Trajet(2, "Domicile — école"),
                Trajet(3, "Courses — achats"),
                Trajet(4, "Utilisation professionnelle"),
                Trajet(5, "Promenade — loisirs"),
                Trajet(9, "Autre")
            )
            trajets.insertMany(newTrajets).subscribe((e: Throwable) => e.printStackTrace(), () => Unit)

            val gravites: MongoCollection[Gravite] = database.getCollection("gravites")
            val newGravites: Seq[Gravite] = Seq(
                Gravite(0, "Non-renseigné"),
                Gravite(1, "Indemne"),
                Gravite(2, "Tué"),
                Gravite(3, "Blessé hospitalisé"),
                Gravite(4, "Blessé léger")
            )
            gravites.insertMany(newGravites).subscribe((e: Throwable) => e.printStackTrace(), () => Unit)
            
            // CREATE MANOEUVRES

            val categoriesDeManoeuvres: MongoCollection[CategorieDeManoeuvre] = database.getCollection("category_de_manoeuvre")
            val newCategoriesDeManoeuvre = Seq(
                CategorieDeManoeuvre(0, "Manoeuvre principale avant l'accident"),
                CategorieDeManoeuvre(1, "Changeant de file"),
                CategorieDeManoeuvre(2, "Déporté"),
                CategorieDeManoeuvre(3, "Tournant"),
                CategorieDeManoeuvre(4, "Dépassant"),
                CategorieDeManoeuvre(5, "Divers")
            )
            categoriesDeManoeuvres.insertMany(newCategoriesDeManoeuvre).subscribe((e: Throwable) => e.printStackTrace(), () => Unit)

            val manoeuvres: MongoCollection[Manoeuvre] = database.getCollection("manoeuvres")
            val newManoeuvres = Seq(
                Manoeuvre(0, "Non-renseigné", newCategoriesDeManoeuvre(5)._id),
                Manoeuvre(1, "Sans changement de direction", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(2, "Même sens, même file", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(3, "Entre 2 files", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(4, "En marche arrière", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(5, "À contresens", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(6, "En franchissant le terre-plein central", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(7, "Dans le couloir bus, dans le même sens", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(8, "Dans le couloir bus, dans le sens inverse", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(9, "En s'insérant", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(10, "En faisant demi-tour sur la chaussée", newCategoriesDeManoeuvre(0)._id),
                Manoeuvre(11, "À gauche", newCategoriesDeManoeuvre(1)._id),
                Manoeuvre(12, "À droite", newCategoriesDeManoeuvre(1)._id),
                Manoeuvre(13, "À gauche", newCategoriesDeManoeuvre(2)._id),
                Manoeuvre(14, "À droite", newCategoriesDeManoeuvre(2)._id),
                Manoeuvre(15, "À gauche", newCategoriesDeManoeuvre(3)._id),
                Manoeuvre(16, "À droite", newCategoriesDeManoeuvre(3)._id),
                Manoeuvre(17, "À gauche", newCategoriesDeManoeuvre(4)._id),
                Manoeuvre(18, "À droite", newCategoriesDeManoeuvre(4)._id),
                Manoeuvre(19, "Traversant la chaussée", newCategoriesDeManoeuvre(5)._id),
                Manoeuvre(20, "Manoeuvre de stationnement", newCategoriesDeManoeuvre(5)._id),
                Manoeuvre(21, "Manoeuvre d'évitement", newCategoriesDeManoeuvre(5)._id),
                Manoeuvre(22, "Ouverture de porte", newCategoriesDeManoeuvre(5)._id),
                Manoeuvre(23, "Arrêté (hors stationnement)", newCategoriesDeManoeuvre(5)._id),
                Manoeuvre(24, "En stationnement (avec occupants)", newCategoriesDeManoeuvre(5)._id)
            )
            manoeuvres.insertMany(newManoeuvres).subscribe((e: Throwable) => e.printStackTrace(), () => Unit)
            
            // CREATE OBSTACLES

            val obstaclesFixes: MongoCollection[ObstacleFixe] = database.getCollection("obstacles_fixes")
            val newObstaclesFixes = Seq(
                ObstacleFixe(0, "Non-renseigné"),
                ObstacleFixe(1, "Véhicule en stationnement"),
                ObstacleFixe(2, "Arbre"),
                ObstacleFixe(3, "Glissière métallique"),
                ObstacleFixe(4, "Glissière béton"),
                ObstacleFixe(5, "Autre glissière"),
                ObstacleFixe(6, "Bâtiment, mur, pile de pont"),
                ObstacleFixe(7, "Support de signalisation verticale ou poste d'appel d'urgence"),
                ObstacleFixe(8, "Poteau"),
                ObstacleFixe(9, "Mobilier urbain"),
                ObstacleFixe(10, "Parapet"),
                ObstacleFixe(11, "Ilot, refuge, borne haute"),
                ObstacleFixe(12, "Bordure de trottoir"),
                ObstacleFixe(13, "Fossé, talus, paroi rocheuse"),
                ObstacleFixe(14, "Autre obstacle fixe sur la chaussée"),
                ObstacleFixe(15, "Autre obstacle fixe sur trottoir ou accotement"),
                ObstacleFixe(16, "Sortie de chaussée sans obstacle")
            )
            obstaclesFixes.insertMany(newObstaclesFixes).subscribe((e: Throwable) => e.printStackTrace(), () => Unit)

            val obstaclesMobiles: MongoCollection[ObstacleMobile] = database.getCollection("obstacles_mobiles")
            val newObstaclesMobiles = Seq(
                ObstacleMobile(0, "Non-renseigné"),
                ObstacleMobile(2, "Véhicule"),
                ObstacleMobile(1, "Piéton"),
                ObstacleMobile(4, "Véhicule sur rail"),
                ObstacleMobile(5, "Animal domestique"),
                ObstacleMobile(6, "Animal sauvage"),
                ObstacleMobile(9, "Autre")
            )
            obstaclesMobiles.insertMany(newObstaclesMobiles).subscribe((e: Throwable) => e.printStackTrace(), () => Unit) 
        })
    }
    
    def persistAccidents(newAccidents: Seq[Accident]) {
        val accidents: MongoCollection[Accident] = database.getCollection("accidents")
        accidents.insertMany(newAccidents).subscribe((e: Throwable) => e.printStackTrace(), () => Unit)
    }
    
    def persistLieux(newLieux: Seq[Lieu]) {
        val lieux: MongoCollection[Lieu] = database.getCollection("lieux")
        lieux.insertMany(newLieux).subscribe((e: Throwable) => e.printStackTrace(), () => Unit)
    }

    def closeConnection(): Unit = {
        mongo.close()
    }
}
