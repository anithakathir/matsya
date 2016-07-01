package in.ashwanthkumar.matsya.spotfleet

case class SpotFleetState(clientToken: String)


trait SpotFleetStateStore extends AutoCloseable {
	def exists(clusterName: String): Boolean
	def get(clusterName: String): SpotFleetState
	def save(clusterName: String, state: SpotFleetState): Unit
	def updateLastRun(identifier: String): Unit
	def lastRun(identifier: String): Long
}



