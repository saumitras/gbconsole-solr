package models

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.CloudSolrServer
import play.api.{Play, Logger}

import scala.collection.JavaConverters._


object SolrStats {


  val EPOCH_SEPERATOR = "___"
  val ZKHOSTS = Play.current.configuration.getString("zk.hosts") match {case Some(x) => x case None => ""}


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

  def getAliases() = {
    val client:CloudSolrServer = new CloudSolrServer(ZKHOSTS)
    client.connect()

    val aliases = client.getZkStateReader.getAliases.getCollectionAliasMap

    /*aliases match {
      case Some(x) => x.asScala.toMap
      case None => Map[String, String]()
    }
*/
    if(aliases != null) aliases.asScala.toMap else Map[String,String]()

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


  def getCollectionDetails(collection:String) = {
    //name, mps, state, docCount, indexSize, epochStart, epochEnd, replicasList

    val regex = s"$EPOCH_SEPERATOR.*"
    val mps = collection.replaceAll(regex,"")

    val state = "active"

    val client:CloudSolrServer = new CloudSolrServer(ZKHOSTS)
    client.setDefaultCollection(collection)
    client.connect()
    val query:SolrQuery = new SolrQuery()
    query.setParam("q","*:*")
    query.setParam("rows","0")

    val response = client.query(query)
    val docCount = response.getResults.getNumFound.toString

    val cores = getCollectionsByName(collection)(collection)


    for((core, stats) <- cores) {
      val baseUrl = stats("baseUrl")
      val client = new DefaultHttpClient()
      val request:HttpGet = new HttpGet(s"$baseUrl/$core/replication?action=details")
      val response:HttpResponse = client.execute(request)


    }


    client.shutdown()

    Map (
      "name" -> collection,
      "mps" -> mps,
      "docCount" -> docCount,
      "cores" -> cores.toString()
    )

  }



}
