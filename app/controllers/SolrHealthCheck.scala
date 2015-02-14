package controllers

import models.SolrStats
import play.api.libs.json.Json
import play.api.mvc._

object SolrHealthCheck extends Controller {

  def getClusterHealth(filter:String) = Action {
    val stats = SolrStats.getCoresHealth(filter)
    Ok(Json.toJson(stats))
  }

  def getClusterHealthByMps(mps:String) = Action {
    val stats = SolrStats.getCoresHealthByMps(mps)
    Ok(Json.toJson(stats))
  }

}
