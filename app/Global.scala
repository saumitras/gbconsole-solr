import play.api._
import models._
object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("GBSolrAdmin has started")
    ZkWatcher.registerZkClusterStateWatcher()
  }

}
