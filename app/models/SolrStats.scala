package models

object SolrStats {

  def getAllSolrCollections = {

    val data = models.ZkWatcher.getSolrCollections

    var colMap = scala.collection.immutable.HashMap[String,scala.collection.immutable.HashMap[String, scala.collection.immutable.HashMap[String, String]]]()

    for((cname, stats) <- data) {
      var coreMap = scala.collection.immutable.HashMap[String,scala.collection.immutable.HashMap[String, String]]()

      val cores = stats.cores

      for((coreName, coreData) <- cores) {
        val baseUrl = coreData(0)
        val state = coreData(1)

        coreMap += (coreName -> scala.collection.immutable.HashMap(
          "state" -> state,
          "baseUrl" -> baseUrl
        ))
      }
      colMap += (cname -> coreMap)

    }
    colMap
  }


  def getCollectionsByCluster = {
    getAllSolrCollections
  }

  def getCollectionsByName(c:String) = {
    getAllSolrCollections filterKeys Set(c)
  }

  def getCollectionsByMps(mps:String) = {
    val collections = getAllSolrCollections
    val regex = s"${mps}.*".r
    collections.filterKeys { regex.pattern.matcher(_).matches }
  }

}
