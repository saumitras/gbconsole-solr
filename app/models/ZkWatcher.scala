package models

import models.Messages._
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.framework.recipes.cache._
import org.apache.curator.retry.ExponentialBackoffRetry
import org.json4s._
import org.json4s.jackson.JsonMethods.{parse => jparse}
import play.Logger
import play.api.Play

object ZkWatcher {

  import models.Messages.SolrCollectionState._

  //implicit val formats = Serialization.formats(NoTypeHints)
  implicit val formats = DefaultFormats

  private val ZKHOSTS = Play.current.configuration.getString("zk.hosts") match {case Some(x) => x case None => ""}
  private val ZK_CLUSTERSTATE_FILE = "/collections"
  private val retryPolicy = new ExponentialBackoffRetry(1000, 3)

  private val curatorZkClient = CuratorFrameworkFactory.newClient(ZKHOSTS, retryPolicy)
  private var solrCollections = scala.collection.immutable.HashMap[String, CollectionStat]()

  def registerZkClusterStateWatcher() = {
    Logger.debug(s"Registering zookeeper watcher. ZKHOST = $ZKHOSTS ")
    curatorZkClient.start
    curatorZkClient.getZookeeperClient.blockUntilConnectedOrTimedOut

    val clusterStateData = curatorZkClient.getData.forPath(ZK_CLUSTERSTATE_FILE)
    if (clusterStateData != null) {
      val data = new String(clusterStateData)
      if (data.length > 2) processZkClusterState(data) else processZkClusterState( """{}""")
    }


    val clusterStateCache:TreeCache = new TreeCache(curatorZkClient,ZK_CLUSTERSTATE_FILE)
    clusterStateCache.getListenable.addListener(new TreeCacheListener {
      override def childEvent(client: CuratorFramework, event: TreeCacheEvent): Unit = {

        try {
          if(event.getData != null) {
            val path = event.getData.getPath
            if (path.matches(".*state.json")) {
              val data = new String(event.getData.getData)
              processZkClusterState(data)
            }
          }

        } catch {
          case ex: Exception =>
            ex.printStackTrace()
            Logger.debug(s"[SolrLog] Exception while watching change for node $ZK_CLUSTERSTATE_FILE. Reason: " + ex.getLocalizedMessage)
        }
      }
    })
    clusterStateCache.start()

  }

  private def processZkClusterState(clusterStateJson: String):Unit = {
    //println("[processZkClusterState] \n" + clusterStateJson)
    Logger.debug(s"[SolrLog] Processing clusterstate.json")

    //var newState = scala.collection.immutable.HashMap[String, CollectionStat]()

    try {
      val cs = jparse(clusterStateJson)

      val collections = cs.extract[Map[String, JObject]].keys.toList

      for (collection <- collections) {
        //if(solrCollections.contains(collection)) {
        //val numShards = solrCollections(collection).numShards
        //val replicationFactor = solrCollections(collection).replicationFactor
        val shardsJson = (cs \\ collection \\ "shards")
        val shards = (shardsJson.extract[Map[String, JObject]]).keys.toList
        var cores = scala.collection.immutable.HashMap[String, List[String]]()
        for (shard <- shards) {
          val replicas = ((shardsJson \\ shard \\ "replicas").extract[Map[String, JObject]]).keys.toList
          for (replica: String <- replicas) {
            val coreNode = (shardsJson \\ shard \\ "replicas" \\ replica).extract[Map[String, String]]
            val coreName = coreNode("core")
            val baseUrl = coreNode("base_url")
            val state = coreNode("state")
            cores += (coreName -> List(baseUrl, state))
          }
        }

        if (cores.size > 0) {
          val stats = CollectionStat(1, 1, LOADED, cores)
          solrCollections += (collection -> stats)
        }

      }

      //solrCollections = newState
    } catch {
      case ex: com.fasterxml.jackson.core.JsonParseException =>
        Logger.debug("[SolrLog] Error in parsing " + ZK_CLUSTERSTATE_FILE)
    }

    Logger.debug("SolrCollection = " + solrCollections)

    SolrCollections.performHealthCheck(solrCollections)

  }

  def getSolrCollections = solrCollections

}
