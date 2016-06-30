package in.ashwanthkumar.matsya

import java.io.File
import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}
import in.ashwanthkumar.matsya.spotfleet.SpotFleetClusterConfig

import scala.collection.mutable

case class ClusterConfig(name: String,
                         spotASG: String,
                         odASG: String,
                         machineType: String,

                         /**
                          * Key -> AZ
                          * Value -> Subnet
                          */
                         subnets: Map[String, String],
                         maxThreshold: Double,
                         maxNrOfTimes: Int, // TODO - Should we use duration insted of nr of times? 
                         maxBidPrice: Double,
                         odPrice: Double,
                         fallBackToOnDemand: Boolean,
                         odCoolOffPeriodInMillis: Long) {

  def allAZs = subnets.keySet
  def getMachineType = this.machineType
}

object ClusterConfig {
  def from(config: Config): ClusterConfig = {
    val subnetConfigs = config.getConfig("subnets")
    import scala.collection.JavaConversions._
    val subnets = subnetConfigs.entrySet.map(entry => (entry.getKey, entry.getValue.unwrapped().toString)).toMap
    val fallBackToOD = config.getBoolean("fallback-to-od")
    val coolOffDuration = if(fallBackToOD) config.getDuration("od-cool-off-period", TimeUnit.MILLISECONDS) else Long.MaxValue

    ClusterConfig(
      name = config.getString("name"),
      spotASG = config.getString("spot-asg"),
      odASG = config.getString("od-asg"),
      machineType = config.getString("machine-type"),
      subnets = subnets,
      maxThreshold = config.getDouble("max-threshold"),
      maxNrOfTimes = config.getInt("nr-of-times"),
      maxBidPrice = config.getDouble("bid-price"),
      odPrice = config.getDouble("od-price"),
      fallBackToOnDemand = fallBackToOD,
      odCoolOffPeriodInMillis = coolOffDuration
    )
  }
}

case class MatsyaConfig(asgClusters: List[ClusterConfig] = List(), spotFleetClusters: List[SpotFleetClusterConfig] = List(),
                        workingDir: String, slackWebHook: Option[String]) {
  def stateDir = workingDir + "/" + "state"
  def historyDir = workingDir + "/" + "history"
  def getRegion = "us-east-1"
  //FIXME what should the machine types be for spotFleet?
  def machineTypes = asgClusters.map(_.machineType).toSet //++ spotFleetClusters.map(_.getMachineType)
}

object MatsyaConfig {
  def from(config: Config) = {
    import scala.collection.JavaConverters._
    val asgClustersConfig = if(config.hasPath("asgClusters")) config.getConfigList("asgClusters").asScala.toList
    else List.empty[Config]
    val spotFleetClustersConfig = if(config.hasPath("spotFleetClusters")) config.getConfigList("spotFleetClusters").asScala.toList
    else List()
    val asgClusters = asgClustersConfig.map(ClusterConfig.from)
    val spotFleetClusters = spotFleetClustersConfig.map(SpotFleetClusterConfig.from)

    MatsyaConfig(
      asgClusters = asgClusters,
      spotFleetClusters = spotFleetClusters,
      workingDir = config.getString("working-dir"),
      slackWebHook = optionString(config, "slack-webhook")
    )
  }

  private def optionString(config: Config, key: String) = {
    if (config.hasPath(key)) Some(config.getString(key))
    else None
  }
}

object ConfigReader {
  val NAMESPACE = "matsya"

  def readFrom(file: String): MatsyaConfig = load(ConfigFactory.parseFile(new File(file)))

  def load(name: String): MatsyaConfig = load(ConfigFactory.load(name))

  def load(config: Config): MatsyaConfig = {
    val globalConfig: Config = config.getConfig(NAMESPACE)
    MatsyaConfig.from(globalConfig)
  }
}