package test.projectsriskcbr.config;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import projectriskcbr.config.ConfigurationAttribute;
import projectriskcbr.config.ConfigurationGroup;

public class ConfigurationGroupTest {
	ConfigurationGroup testGroup;

	@Before
	public void setUp() throws Exception {
		testGroup = new ConfigurationGroup("test group");
		String[] attributes = {"a", "b", "c", "c.a", "d.a"};
		for(String attributeName : attributes) {
			ConfigurationAttribute attribute = new ConfigurationAttribute(attributeName);
			attribute.setWeight(0.3);
			testGroup.attributes.add(attribute);
		}
	}

	@After
	public void tearDown() throws Exception {
		testGroup = null;
	}

	@Test
	public void testHasAttribute() {
		Assert.assertEquals("testGroup does have attribute a", testGroup.hasAttribute("a"), true);
		Assert.assertEquals("testGroup does not have attribute z", testGroup.hasAttribute("z"), false);
	}

	@Test
	public void testGetSimpleAttributes() {
		List<ConfigurationAttribute> attributes = testGroup.getSimpleAttributes(); 
		Assert.assertEquals("testGroup has three simple attributes", attributes.size(), 3);
	}

	@Test
	public void testGetNestedAttributes() {
		Map<String, ConfigurationGroup> groups = testGroup.getNestedAttributes(); 
		Assert.assertEquals("testGroup has two nested attributes", groups.size(), 2);
		Assert.assertEquals("testGroup c attribute has one child", groups.get("c").attributes.size(), 1);
		Assert.assertEquals("testGroup c attribute has one child: a", groups.get("c").attributes.get(0).getName(), "a");
		Assert.assertEquals("testGroup c attribute has one child: a, with weight 0.3", groups.get("c").attributes.get(0).getWeight(), 0.3, 0.0);
		Assert.assertEquals("testGroup d attribute has one child", groups.get("d").attributes.size(), 1);
		Assert.assertEquals("testGroup d attribute has one child: a", groups.get("d").attributes.get(0).getName(), "a");
		Assert.assertEquals("testGroup d attribute has one child: a, with weight 0.3", groups.get("d").attributes.get(0).getWeight(), 0.3, 0.0);
	}

}
