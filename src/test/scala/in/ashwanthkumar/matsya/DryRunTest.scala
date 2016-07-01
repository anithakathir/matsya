package in.ashwanthkumar.matsya

import com.amazonaws.services.ec2.{AmazonEC2Client, AbstractAmazonEC2}
import com.amazonaws.services.ec2.model.{CancelSpotFleetRequestsRequest, DescribeSpotFleetInstancesRequest, SpotFleetRequestConfigData, RequestSpotFleetRequest}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class DryRunTest extends FlatSpec {

	"RequestSpotFleetRequest" should "send a request with the given spotFleetConfigData" in {
		val config = ConfigReader.load("valid-spotfleet")
		val spotConfig = config.spotFleetClusters.head
		println(spotConfig.config.getIamFleetRole)
		val request: RequestSpotFleetRequest = new RequestSpotFleetRequest().withSpotFleetRequestConfig(spotConfig.config)

		val response = new AmazonEC2Client().requestSpotFleet(request)

		val id = response.getSpotFleetRequestId
		println("Id::" + response.getSpotFleetRequestId)

		println(new AmazonEC2Client().describeSpotFleetRequests())


	}


	"CancelSpotFleetRequest" should "cancel a request with the given spotFleetConfigData" in {
		val config = ConfigReader.load("valid-spotfleet")
		val spotConfig = config.spotFleetClusters.head
		println(spotConfig.config.getIamFleetRole)
		val request: RequestSpotFleetRequest = new RequestSpotFleetRequest().withSpotFleetRequestConfig(spotConfig.config)
		//
		//		val response = new AmazonEC2Client().requestSpotFleet(request)
		//		val id = response.getSpotFleetRequestId
		//		println("Id::" + response.getSpotFleetRequestId)
		val cancelRequest = new CancelSpotFleetRequestsRequest()
		cancelRequest.setSpotFleetRequestIds(List("sfr-6d147624-37d7-4eff-93e5-b39faa7532ed"))
		cancelRequest.setTerminateInstances(true)
		println(new AmazonEC2Client().cancelSpotFleetRequests(cancelRequest))


	}
}
