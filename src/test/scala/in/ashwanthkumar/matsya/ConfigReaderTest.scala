package in.ashwanthkumar.matsya

import org.scalatest.FlatSpec
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper, have}

class ConfigReaderTest extends FlatSpec {
  "ConfigReader" should "read the asg test configuration" in {
    val config = ConfigReader.load("test-clusters")

    config.slackWebHook should be(Some("https://hooks.slack.com/services/foo/bar/baz"))
    config.workingDir should be("working-dir")
    config.stateDir should be("working-dir/state")
    config.historyDir should be("working-dir/history")

    config.machineTypes should be(Set("c3.2xlarge"))
    config.asgClusters should have size 1
    val cluster = config.asgClusters.head
    cluster.name should be("Test ASG Cluster")
    cluster.spotASG should be("test-asg-spot")
    cluster.odASG should be("test-asg-od")
    cluster.maxBidPrice should be(0.420)
    cluster.odPrice should be(0.420)
    cluster.machineType should be("c3.2xlarge")
    cluster.maxThreshold should be(0.8)
    cluster.maxNrOfTimes should be(5)
    cluster.subnets should be(Map(
      "us-east-1a" -> "subnet-1",
      "us-east-1b" -> "subnet-2",
      "us-east-1c" -> "subnet-3",
      "us-east-1d" -> "subnet-4",
      "us-east-1e" -> "subnet-5"
    ))
    cluster.fallBackToOnDemand should be(true)
    cluster.odCoolOffPeriodInMillis should be(45 * 60 * 1000) // in milliseconds
  }

  "ConfigReader" should "read the spot fleet test configuration" in {
    val config = ConfigReader.load("test-clusters")

    config.slackWebHook should be(Some("https://hooks.slack.com/services/foo/bar/baz"))
    config.workingDir should be("working-dir")
    config.stateDir should be("working-dir/state")
    config.historyDir should be("working-dir/history")

    config.spotFleetClusters should have size 1
    val cluster = config.spotFleetClusters.head
    cluster.config.getSpotPrice should be("0.85")
    cluster.config.getTargetCapacity should be(10)
    cluster.config.getIamFleetRole should be("arn:aws:iam::123456789012:role/my-spot-fleet-role")
    cluster.config.getAllocationStrategy should be("diversified")

    cluster.config.getLaunchSpecifications should have size 5
    cluster.config.getLaunchSpecifications.get(0).getInstanceType should be("m3.xlarge")
    cluster.config.getLaunchSpecifications.get(1).getWeightedCapacity should be(2)
  }

}
