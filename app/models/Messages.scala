package models

object Messages {

  object OperationStatus extends Enumeration {
    type OperationStatus = Value
    val FAILURE, SUCCESS, INPROGRESS = Value
  }

  object SolrCollectionState extends Enumeration {
    type SolrCollectionState = Value
    val LOADED, UNLOADED, DOESNTEXIST, CREATING, LOADING, UNLOADING = Value
  }

  import SolrCollectionState._

  sealed trait SolrMessages

  case class CollectionStat(numShards:Int, replicationFactor:Int, state:SolrCollectionState, cores:scala.collection.immutable.HashMap[String,List[String]]) extends SolrMessages

}