package controllers

import play.api.libs.json.Json
import play.api.mvc._

object SolrCollectionInfo extends Controller {

  def getCollectionsByCluster = Action {
    Ok(Json.toJson(models.SolrStats.getAllSolrCollections))
  }

  def getCollectionsByName(c: String) = Action {
    Ok(Json.toJson(models.SolrStats.getCollectionsByName(c)))
  }

  def getCollectionsByMps(mps: String) = Action {
    Ok(Json.toJson(models.SolrStats.getCollectionsByMps(mps)))
  }

  def getCollectionDetails(collectionName: String) = Action {
    Ok(Json.toJson(models.SolrStats.getCollectionDetails(collectionName)))
  }

  def getAliases() = Action {
    Ok(Json.toJson(models.SolrStats.getAliases()))
  }
}
