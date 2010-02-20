package test.projectsriskcbr.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.test.test4.Region;
import jcolibri.test.test4.TravelDescription;
import jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import projectriskcbr.config.Configuration;
import projectriskcbr.config.ConfigurationAttribute;
import projectriskcbr.config.ConfigurationGroup;
import projectriskcbr.config.NNConfigurator;

public class NNConfiguratorTest {
	Collection<CBRCase> cases;
	CBRQuery query;
	Configuration storedSimConfig;
	NNConfig manualSimConfig;
	
	@Before
	public void setUp() throws Exception {
		cases = new ArrayList<CBRCase>();
		CBRCase cbrCase;
		
		// populating casebase
		cbrCase = new CBRCase();
		TravelDescription td1 = new TravelDescription();
		Region r1 = new Region();
		r1.setRegion("Miami");
		r1.setCurrency("Euro");
		td1.setHolidayType("Bathing");
		td1.setNumberOfPersons(2);
		td1.setRegion(r1);
		cbrCase.setDescription(td1);
		cases.add(cbrCase);
		
		cbrCase = new CBRCase();
		TravelDescription td2 = new TravelDescription();
		Region r2 = new Region();
		r2.setRegion("Bornholm");
		r2.setCurrency("Euro");
		td2.setHolidayType("Walking");
		td2.setNumberOfPersons(2);
		td2.setRegion(r2);
		cbrCase.setDescription(td2);
		cases.add(cbrCase);
		
		cbrCase = new CBRCase();
		TravelDescription td3 = new TravelDescription();
		Region r3 = new Region();
		r3.setRegion("Bornholm");
		r3.setCurrency("Euro");
		td3.setHolidayType("Bathing");
		td3.setNumberOfPersons(4);
		td3.setRegion(r3);
		cbrCase.setDescription(td3);
		cases.add(cbrCase);
		
		cbrCase = new CBRCase();
		TravelDescription td4 = new TravelDescription();
		Region r4 = new Region();
		r4.setRegion("Bornholm");
		r4.setCurrency("Dollar");
		td4.setHolidayType("Bathing");
		td4.setNumberOfPersons(2);
		td4.setRegion(r4);
		cbrCase.setDescription(td4);
		cases.add(cbrCase);

		
		// setting query
		TravelDescription queryDesc = new TravelDescription();		
		Region r = new Region();
		r.setRegion("Bornholm");
		r.setCurrency("Euro");
		queryDesc.setHolidayType("Bathing");
		queryDesc.setNumberOfPersons(2);
		queryDesc.setRegion(r);

		query = new CBRQuery();
		query.setDescription(queryDesc);
		
		// manual similarity configuration
		manualSimConfig = new NNConfig();
		manualSimConfig.setDescriptionSimFunction(new Average());

		Attribute at1 = new Attribute("HolidayType", TravelDescription.class);
		manualSimConfig.addMapping(at1, new Equal());
		manualSimConfig.setWeight(at1, 0.6);
		
		Attribute at3 = new Attribute("NumberOfPersons", TravelDescription.class);
		manualSimConfig.addMapping(at3, new Equal());
		manualSimConfig.setWeight(at3, 0.3);
		
		AdvancedAverage regionAdvancedAverage = new AdvancedAverage();
		Attribute at9 = new Attribute("Region", TravelDescription.class); 
		manualSimConfig.addMapping(at9, regionAdvancedAverage);
		manualSimConfig.setWeight(at9, 0.8);

		Attribute at10 = new Attribute("region", Region.class);
		manualSimConfig.addMapping(at10, new Equal());
		regionAdvancedAverage.localSimConfig.setWeight(at10, 0.7);
		
		Attribute at11 = new Attribute("currency", Region.class);
		manualSimConfig.addMapping(at11, new Equal());
		regionAdvancedAverage.localSimConfig.setWeight(at11, 0.3);
		
		
		// configuration values for automatic similarity configuration
		ConfigurationAttribute confAtt1 = new ConfigurationAttribute("HolidayType");
		confAtt1.setWeight(0.6);
		
		ConfigurationAttribute confAtt2 = new ConfigurationAttribute("NumberOfPersons");
		confAtt2.setWeight(0.3);
		
		ConfigurationAttribute confAtt3 = new ConfigurationAttribute("Region");
		confAtt3.setWeight(0.8);
		
		ConfigurationAttribute confAtt4 = new ConfigurationAttribute("Region.region");
		confAtt4.setWeight(0.7);
		
		ConfigurationAttribute confAtt5 = new ConfigurationAttribute("Region.currency");
		confAtt5.setWeight(0.3);
		

		ConfigurationGroup confGroup = new ConfigurationGroup("group A");
		confGroup.attributes.add(confAtt1);
		confGroup.attributes.add(confAtt2);
		confGroup.attributes.add(confAtt3);
		confGroup.attributes.add(confAtt4);
		confGroup.attributes.add(confAtt5);

		storedSimConfig = new Configuration();
		storedSimConfig.groups.add(confGroup);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConfigureSimilarity() {
		NNConfig autoSimConfig = new NNConfig();
		NNConfigurator.configureSimilarity(autoSimConfig, storedSimConfig.groups.get(0), TravelDescription.class, new TravelDescriptionExternalConfigurator());
		
		Collection<RetrievalResult> manualResults = NNScoringMethod.evaluateSimilarity(cases, query, manualSimConfig);
		Collection<RetrievalResult> autoResults = NNScoringMethod.evaluateSimilarity(cases, query, autoSimConfig);
		
		Iterator<RetrievalResult> manualResultsIterator = manualResults.iterator();
		Iterator<RetrievalResult> autoResultsIterator = autoResults.iterator();
		while(manualResultsIterator.hasNext() && autoResultsIterator.hasNext()) {
			RetrievalResult manualRR = manualResultsIterator.next();
			RetrievalResult autoRR = autoResultsIterator.next();
			Assert.assertEquals("results should be equal between case " + manualRR.get_case() + " and " + autoRR.get_case(), 
								manualRR.getEval(), autoRR.getEval());
		}
	}

}
