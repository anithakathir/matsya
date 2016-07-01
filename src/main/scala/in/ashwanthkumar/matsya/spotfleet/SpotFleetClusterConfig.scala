package in.ashwanthkumar.matsya.spotfleet

import com.amazonaws.services.ec2.model.{SpotFleetRequestConfigData, SpotFleetLaunchSpecification}
import com.typesafe.config.Config
import scala.collection.JavaConversions._
import scala.collection.mutable

case class SpotFleetClusterConfig(config: SpotFleetRequestConfigData) {
	def getMachineType: mutable.Buffer[String] = {
		config.getLaunchSpecifications.map {
			spec => spec.getInstanceType
		}
	}
}

object SpotFleetClusterConfig {
	def from(config: Config): SpotFleetClusterConfig = {
		val clientToken = config.getString("ClientToken")
		val globalSpotPrice = config.getString("SpotPrice")
		val targetCapacity = config.getInt("TargetCapacity")
		val iamFleetRole = config.getString("IamFleetRole")
		val allocationStrategy = if(config.hasPath("AllocationStrategy")) config.getString("AllocationStrategy") else "lowestPrice"
		val launchSpecificationsConfig = config.getConfigList("LaunchSpecifications")
		val launchSpecifications = launchSpecificationsConfig.map {
		l =>
			val imageId = l.getString("ImageId")
			val instanceType = l.getString("InstanceType")
			val subnetId = l.getString("SubnetId")
			val weightedCapacity = l.getDouble("WeightedCapacity")
			val instanceSpotPrice =  l.getString("SpotPrice")
				val launchSpec = new SpotFleetLaunchSpecification()
					launchSpec.setImageId(imageId)
					launchSpec.setInstanceType(instanceType)
					launchSpec.setSubnetId(subnetId)
					launchSpec.setWeightedCapacity(weightedCapacity)
					launchSpec.setSpotPrice(instanceSpotPrice)
				launchSpec
		}
  val spotFleetRequestConfigData: SpotFleetRequestConfigData = new SpotFleetRequestConfigData()
		spotFleetRequestConfigData.setSpotPrice(globalSpotPrice)
		spotFleetRequestConfigData.setTargetCapacity(targetCapacity)
		spotFleetRequestConfigData.setIamFleetRole(iamFleetRole)
		spotFleetRequestConfigData.setAllocationStrategy(allocationStrategy)
		spotFleetRequestConfigData.setLaunchSpecifications(launchSpecifications)
		spotFleetRequestConfigData.setClientToken(clientToken)
		SpotFleetClusterConfig(spotFleetRequestConfigData)
	}
}
