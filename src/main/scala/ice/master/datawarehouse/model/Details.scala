package ice.master.datawarehouse.model

import org.bson.types.ObjectId

/**
 * The details of an Accident.
 */
case class Details(_id: ObjectId, graviteId: Int, trajetId: Int)

object Details {
    def apply(graviteId: Int, trajetId: Int): Details =
        Details(new ObjectId(), graviteId, trajetId)
}