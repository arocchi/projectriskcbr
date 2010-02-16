package projectriskcbr.config;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ConfigurationGroup {
	String name = new String("anonymous Group");
	public List<ConfigurationAttribute> attributes = new ArrayList<ConfigurationAttribute>();
	
	
	public ConfigurationGroup(String name) {
		this.name = new String(name);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = new String(name);
	}
	
	/**
	 * Checks if this similarity configuration has a configuration entry for specified attribute
	 * @param attribute the name of the attribute we want to check
	 * @return true if attribute is found, false otherwise
	 */
	public Boolean hasAttribute(String attribute) {
		for(ConfigurationAttribute desiredAttr : this.attributes) {
			if(desiredAttr.getName().equals(attribute)) {
				return true;
			}
		}
		return false;
	}
	
	public List<ConfigurationAttribute> getSimpleAttributes() {
		List<ConfigurationAttribute> returnList = new ArrayList<ConfigurationAttribute>();
		for(ConfigurationAttribute desiredAttr : this.attributes) {
			if(!DOMAsJ.isNested(desiredAttr.getName()))
				returnList.add(desiredAttr);
		}
		return returnList;
	}
	
	public Map<String, ConfigurationGroup> getNestedAttributes() {
		Map<String, ConfigurationGroup> returnDictionary = new Hashtable<String, ConfigurationGroup>();
		
		for(ConfigurationAttribute desiredAttr : this.attributes) {
			if(DOMAsJ.isNested(desiredAttr.getName())) {
				String parentAttribute = DOMAsJ.getParent(desiredAttr.getName());
				
				ConfigurationAttribute strippedAttribute = new ConfigurationAttribute(DOMAsJ.stripParent(desiredAttr.getName()));
				strippedAttribute.setWeight(desiredAttr.getWeight());
				
				ConfigurationGroup desiredGroup = returnDictionary.get(parentAttribute);
				if(desiredGroup == null) {
					desiredGroup = new ConfigurationGroup("Children of " + parentAttribute);
					returnDictionary.put(parentAttribute, desiredGroup);
				}
				
				desiredGroup.attributes.add(strippedAttribute);
			}
		}
		return returnDictionary;
	}
};
