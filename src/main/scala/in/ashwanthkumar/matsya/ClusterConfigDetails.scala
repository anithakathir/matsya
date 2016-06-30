package in.ashwanthkumar.matsya

object ConfigEnum extends Enumeration {
	val Fleet, ASG = Value
}

trait ClusterDetails {
	def `type`: ConfigEnum.Value
	def getMachineType: String
}

