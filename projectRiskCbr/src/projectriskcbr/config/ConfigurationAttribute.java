package projectriskcbr.config;

public class ConfigurationAttribute {
	String	name;
	Double	weight;
	
	public ConfigurationAttribute(String name) {
		this.name = new String(name);
		this.weight = new Double(1.0);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = new String(name);
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = new Double(weight);
	}
};
