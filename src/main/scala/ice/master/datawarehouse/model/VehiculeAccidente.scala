package ice.master.datawarehouse.model

import org.bson.types.ObjectId

case class VehiculeAccidente(_id: String, vehiculeId: String, accidentId: String, var obstaclesFixesId: List[Int], var obstaclesMobilesId: List[Int], var manoeuvresId: List[Int])