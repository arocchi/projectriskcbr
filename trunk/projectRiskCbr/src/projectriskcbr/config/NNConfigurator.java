package projectriskcbr.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import jcolibri.cbrcore.Attribute;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage;

public class NNConfigurator {
	public static void configureSimilarity(	NNConfig simConfig, 
											ConfigurationGroup storedSimConfig,
											Class<?> baseClass) {
		configureSimilarity(simConfig, storedSimConfig, baseClass, null);
	}
	
	public static void configureSimilarity(	NNConfig simConfig, 
			ConfigurationGroup storedSimConfig,
			Class<?> baseClass,
			SelfNNConfigurator externalConfigurator) {
		configureSimilarity(simConfig, storedSimConfig, baseClass, null, externalConfigurator);
	}
	
	
	
	private static void configureSimilarity(NNConfig simConfig, 
											ConfigurationGroup storedSimConfig,
											Class<?> baseClass, Attribute baseAttribute,
											SelfNNConfigurator externalConfigurator) {
		Boolean firstExecution = (baseAttribute == null);
		
		if(firstExecution)
			simConfig.setDescriptionSimFunction(new AdvancedAverage());
		
		Object similarityFunction;
		NNConfig weightsSimConfig;
		
		if(firstExecution)
			weightsSimConfig = simConfig;
		else {
			AdvancedAverage average = (AdvancedAverage)simConfig.getGlobalSimilFunction(baseAttribute);
			weightsSimConfig = average.localSimConfig;
		}
		
		List<ConfigurationAttribute> simpleAttributes = storedSimConfig.getSimpleAttributes();
		for(ConfigurationAttribute attr : simpleAttributes) {
			Attribute jcolibriAttribute = new Attribute(attr.getName(), baseClass);	
			
			LocalSimilarityFunction localSimilarity = simConfig.getLocalSimilFunction(jcolibriAttribute);
			GlobalSimilarityFunction globalSimilarity = simConfig.getGlobalSimilFunction(jcolibriAttribute);
			
			if(externalConfigurator != null)
				similarityFunction = externalConfigurator.getSimilarityFunction(attr.getName());
			else {
				try {
					Method getSimilarity = baseClass.getMethod("getSimilarityFunction", String.class);
					Object baseClassInstance = baseClass.newInstance();
					similarityFunction = getSimilarity.invoke(baseClassInstance, attr.getName());
				} catch(Exception e) {
					similarityFunction = null;
					System.err.println("Error trying to execute getSimilarity on class " + baseClass.getName());
				}
			}
			
			if(			localSimilarity == null && 
						similarityFunction instanceof LocalSimilarityFunction)
				simConfig.addMapping(	jcolibriAttribute, 
										(LocalSimilarityFunction)similarityFunction);
			else if(	globalSimilarity == null && 
						similarityFunction instanceof GlobalSimilarityFunction)
				simConfig.addMapping(	jcolibriAttribute, 
										(GlobalSimilarityFunction)similarityFunction);
			else if(localSimilarity != null || globalSimilarity != null){
				; // similarity function for specified attribute has already been added to simConfig mapping
			} else {
				System.err.println( "NNConfigurator: " + baseClass.getName() + ".getSimilarityFunction(" + attr.getName() + ") " +
									"returned a null similarity function; attribute " + attr.getName()
									+ ((baseAttribute != null) ? (" is located in " + baseAttribute.getName()) : "")
									+ " in configuration group " + storedSimConfig.getName());
			}

			weightsSimConfig.setWeight(jcolibriAttribute, attr.getWeight());
		}
		
		Map<String, ConfigurationGroup> nestedAttributesGroups = storedSimConfig.getNestedAttributes();
		for(String parentAttribute : nestedAttributesGroups.keySet()) {
			try {
				Field parentAttributeField = baseClass.getDeclaredField(parentAttribute);
				Class<?> parentAttributeClass = parentAttributeField.getType();
				
				Attribute jcolibriParentAttribute = new Attribute(parentAttribute, baseClass);
				NNConfigurator.configureSimilarity(	simConfig, 
													nestedAttributesGroups.get(parentAttribute), 
													parentAttributeClass, jcolibriParentAttribute,
													externalConfigurator);
				
			} catch(NoSuchFieldException e) {
				System.err.println(	"Error while accessing attribute " + parentAttribute + " of " + baseClass + 
									": " + e.getStackTrace());
			};
		}
	}
}
