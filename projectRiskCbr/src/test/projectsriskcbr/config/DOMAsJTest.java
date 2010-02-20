package test.projectsriskcbr.config;

import org.junit.Assert;
import org.junit.Test;

import projectriskcbr.config.DOMAsJ;

public class DOMAsJTest {

	@Test
	public void testIsNested() {
		String simpleAttribute = "myAttribute";
		Assert.assertEquals("Expecting attribute named " + simpleAttribute + " to be a simple attribute",
							DOMAsJ.isNested(simpleAttribute), false);
		
		String nestedAttribute = "myCompositeAttribute.nestedAttribute";
		Assert.assertEquals("Expecting attribute named " + nestedAttribute + " not to be a simple attribute",
							DOMAsJ.isNested(nestedAttribute), true);
	}

	@Test
	public void testStripParent() {
		String nestedAttribute = "myCompositeAttribute.nestedAttribute";
		String expectedStrippedAttribute = "nestedAttribute";
		String strippedAttribute = DOMAsJ.stripParent(nestedAttribute);
		Assert.assertEquals("Expecting attribute named " + nestedAttribute + " to be stripped in " + expectedStrippedAttribute,
							strippedAttribute, expectedStrippedAttribute);
	}
	
	@Test
	public void testStripLeaf() {
		String nestedAttribute = "myCompositeAttribute.nestedAttribute";
		String expectedStrippedAttribute = "myCompositeAttribute";
		String strippedAttribute = DOMAsJ.stripLeaf(nestedAttribute);
		Assert.assertEquals("Expecting attribute named " + nestedAttribute + " to be stripped in " + expectedStrippedAttribute,
							strippedAttribute, expectedStrippedAttribute);
	}
	
	@Test
	public void testGetParent() {
		String nestedAttribute = "myCompositeAttribute.nestedAttribute";
		String expectedParentAttribute = "myCompositeAttribute";
		String parentAttribute = DOMAsJ.getParent(nestedAttribute);
		Assert.assertEquals("Expecting attribute named " + nestedAttribute + " to have parent " + expectedParentAttribute,
							parentAttribute, expectedParentAttribute);
		
		String simpleAttribute = "mySimpleAttribute";
		String simpleParentAttribute = DOMAsJ.getParent(simpleAttribute);
		Assert.assertEquals("Expecting attribute named " + simpleAttribute + " to have parent " + simpleAttribute,
							simpleAttribute, simpleParentAttribute);
	}

	@Test
	public void testGetLeaf() {
		String nestedAttribute = "myCompositeAttribute.nestedAttribute";
		String expectedParentAttribute = "nestedAttribute";
		String parentAttribute = DOMAsJ.getLeaf(nestedAttribute);
		Assert.assertEquals("Expecting attribute named " + nestedAttribute + " to have parent " + expectedParentAttribute,
							parentAttribute, expectedParentAttribute);
		
		String simpleAttribute = "mySimpleAttribute";
		String simpleLeafAttribute = DOMAsJ.getLeaf(simpleAttribute);
		Assert.assertEquals("Expecting attribute named " + simpleAttribute + " to have parent " + simpleAttribute,
							simpleAttribute, simpleLeafAttribute);
	}
}
