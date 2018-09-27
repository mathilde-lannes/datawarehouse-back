package ice.master.datawarehouse.model

import org.bson.types.ObjectId

final case class CategorieDeManoeuvre(_id: Int, label: String)
final case class Manoeuvre(_id: Int, details: String, categorie: Int)
