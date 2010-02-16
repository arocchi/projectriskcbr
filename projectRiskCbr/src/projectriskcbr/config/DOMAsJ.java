package projectriskcbr.config;

import org.apache.commons.lang.StringUtils;

public class DOMAsJ {
	public static boolean isNested(String attributeName) {
		return (StringUtils.split(attributeName,'.').length > 1);
	}
	
	public static String stripParent(String attributeName) {
		String[] domArray = StringUtils.split(attributeName,'.');
		String strippedAttributeName;
		
		if(domArray.length > 1)
			strippedAttributeName = StringUtils.join(domArray, '.', 1, domArray.length);
		else
			strippedAttributeName = new String(attributeName);
		
		return strippedAttributeName;
	}
	
	public static String stripLeaf(String attributeName) {
		String[] domArray = StringUtils.split(attributeName,'.');
		String strippedAttributeName;
		
		if(domArray.length > 1)
			strippedAttributeName = StringUtils.join(domArray, '.', 0, domArray.length - 1);
		else
			strippedAttributeName = new String(attributeName);
		
		return strippedAttributeName;
	}
	
	public static String getParent(String attributeName) {
		String[] domArray = StringUtils.split(attributeName,'.');
		String parentAttributeName;
		
		if(domArray.length > 1)
			parentAttributeName = domArray[0];
		else
			parentAttributeName = new String(attributeName);
		
		return parentAttributeName;
	}
	
	public static String getLeaf(String attributeName) {
		String[] domArray = StringUtils.split(attributeName,'.');
		String parentAttributeName;
		
		if(domArray.length > 1)
			parentAttributeName = domArray[domArray.length - 1];
		else
			parentAttributeName = new String(attributeName);
		
		return parentAttributeName;
	}
}
