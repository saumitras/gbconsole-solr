package models

import models.Messages._
import play.Logger

object SolrCollections {

  def performHealthCheck(solrCollections:scala.collection.immutable.HashMap[String, CollectionStat]) = {

    for((collection, stats) <- solrCollections) {
      val cores = stats.cores

      for((coreName, coreData) <- cores) {
        val baseUrl = coreData(0)
        val state = coreData(1)

        if(state == "down") {
          Logger.debug(s"NodeDown: Collection=$collection, CoreName=$coreName, BaseUrl=$baseUrl State=$state")
        }
      }
    }
  }

  def getNodeStats() = {
    val solrCollections = ZkWatcher.getSolrCollections

    for(collection <- solrCollections) {

    }

  }

}
