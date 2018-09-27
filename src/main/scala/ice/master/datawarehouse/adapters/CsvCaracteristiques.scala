//package ice.master.datawarehouse.adapters
//
//import ice.master.datawarehouse.model.Lieu
//
//class CsvCaracteristiques(val path: String) {
//
//    def adapt(): Set[Lieu] = {
//        val bufferedSource = io.Source.fromFile(path)
//        
//        for (line <- bufferedSource.getLines) {
//            val Array(accidentId, a, m, j, h, lum, agg, int, atm, col, com, adr, gps, lat, long, dep) = line.split(",").map(_.trim)
//            
//        }
//        
//        null
//    }
//    
//    def adapt(accidentId, 
//
//}