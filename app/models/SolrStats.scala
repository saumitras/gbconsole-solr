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

  def getCoresHealth(filter:String) = {

    val solrCollections = models.ZkWatcher.getSolrCollections

    var activeCores = Map[String, Map[String, String]]()
    var nonActiveCores = Map[String, Map[String, String]]()

    for((collection, stats) <- solrCollections) {
      val cores = stats.cores

      for((coreName, coreData) <- cores) {
        val baseUrl = coreData(0)
        val state = coreData(1)
        //val coreStat = CoreStat(collection, baseUrl, state)
        val coreStat = Map("collection"->collection, "baseUrl" -> baseUrl, "state" -> state)

        if(state == "active") activeCores += (coreName -> coreStat)  else nonActiveCores += (coreName -> coreStat)

      }
    }

    if(filter == "active") Map("active" -> activeCores)
    else if(filter == "inactive")  Map("inactive" -> nonActiveCores)
    else Map("active" -> activeCores, "inactive" -> nonActiveCores)

  }

  def getCoresHealthByMps(mps:String) = {
    val allCores = getCoresHealth("all")
    val regex = s"${mps}.*".r

    def filterCores(cores:Map[String, Map[String, String]]) = {
      cores.filterKeys { regex.pattern.matcher(_).matches }
    }

    Map("active" -> filterCores(allCores("active")), "inactive" -> filterCores(allCores("inactive")))

  }


}
