package matsya.config;

import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

import java.util.Map;

public class ClusterConfig {
    private String spotASG;
    private String odASG;
    private String machineType;
    // FIXME - Add support for non-VPC based setup as well
    /**
     * Key -> AZ
     * Value -> Subnet
     */
    private Map<String, String> subnets;
    private double threshold;
    private int nrOfTimes;
    private double bidPrice;
    private boolean fallbackToOnDemand;

    /* default */ static ClusterConfig from(Config config) {
        Config subnetConfigs = config.getConfig("subnets");
        Map<String, String> subnets = Maps.newHashMap();
        for (Map.Entry<String, ConfigValue> azAndSubnet : subnetConfigs.entrySet()) {
            subnets.put(azAndSubnet.getKey(), azAndSubnet.getValue().unwrapped().toString());
        }

        return new ClusterConfig()
                .setSpotASG(config.getString("spot-asg"))
                .setOnDemandASG(config.getString("od-asg"))
                .setMachineType(config.getString("machine-type"))
                .setBidPrice(config.getDouble("bid-price"))
                .setSubnets(subnets)
                .setThreshold(config.getDouble("threshold"))
                .setFallbackToOnDemand(config.getBoolean("fallback-to-od"))
                .setNrOfTimes(config.getInt("nr-of-times"));
    }

    public String getSpotASG() {
        return spotASG;
    }

    public ClusterConfig setSpotASG(String spotASG) {
        this.spotASG = spotASG;
        return this;
    }

    public String getMachineType() {
        return machineType;
    }

    public ClusterConfig setMachineType(String machineType) {
        this.machineType = machineType;
        return this;
    }

    public Map<String, String> getSubnets() {
        return subnets;
    }

    public ClusterConfig setSubnets(Map<String, String> subnets) {
        this.subnets = subnets;
        return this;
    }

    public double getThreshold() {
        return threshold;
    }

    public ClusterConfig setThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }

    public int getNrOfTimes() {
        return nrOfTimes;
    }

    public ClusterConfig setNrOfTimes(int nrOfTimes) {
        this.nrOfTimes = nrOfTimes;
        return this;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public ClusterConfig setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
        return this;
    }

    public boolean isFallbackToOnDemand() {
        return fallbackToOnDemand;
    }

    public ClusterConfig setFallbackToOnDemand(boolean fallbackToOnDemand) {
        this.fallbackToOnDemand = fallbackToOnDemand;
        return this;
    }

    public String getOdASG() {
        return odASG;
    }

    public ClusterConfig setOnDemandASG(String odASG) {
        this.odASG = odASG;
        return this;
    }
}