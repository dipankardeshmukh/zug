package DatabaseLayer;

// Generated Nov 2, 2009 5:59:33 PM by Hibernate Tools 3.2.4.GA

/**
 * Topologyattributes generated by hbm2java
 */
@SuppressWarnings("serial")
public class Topologyattributes implements java.io.Serializable {

	int topologyAttributesId;
	Topology topology;
	String attributeName;
	String attributeValue;

	public Topologyattributes() {
	}

	public Topologyattributes(int topologyAttributesId, Topology topology,
			String attributeName, String attributeValue) {
		this.topologyAttributesId = topologyAttributesId;
		this.topology = topology;
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	public int getTopologyAttributesId() {
		return this.topologyAttributesId;
	}

	public void setTopologyAttributesId(int topologyAttributesId) {
		this.topologyAttributesId = topologyAttributesId;
	}

	public Topology getTopology() {
		return this.topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return this.attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

}
