package projectriskcbr.test.jcolibriext.method.retrieve.NNretrieval.similarity.global;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.Connector;
import jcolibri.connector.DataBaseConnector;
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

public class AdvancedAverageTest {

	CBRQuery averageQuery;
	Collection<CBRCase> cases;
	
	@Before
	public void setUp() throws Exception {
		jcolibri.test.test4.TravelDescription averageQueryDesc = new jcolibri.test.test4.TravelDescription();
		averageQueryDesc.setHolidayType("Bathing");
		Region rA = new Region();
		rA.setCity("Munich");
		rA.setCurrency("Euro");
		averageQueryDesc.setRegion(rA);
		averageQuery = new CBRQuery();
		averageQuery.setDescription(averageQueryDesc);

		jcolibri.test.database.HSQLDBserver.init();
		Connector connector = new DataBaseConnector();

		URL configfile = jcolibri.util.FileIO.findFile("jcolibri/test/test4/databaseconfig.xml");     	
		connector.initFromXMLfile(configfile);
		CBRCaseBase	caseBase = new LinealCaseBase();
		caseBase.init(connector);				

		cases = caseBase.getCases();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testComputeSimilarity() {
		NNConfig regionAverage = new NNConfig();
		NNConfig regionWeightedAverage = new NNConfig();
		
		Attribute holidayType =		new Attribute("HolidayType", TravelDescription.class);
		Attribute travelRegion =	new Attribute("Region", TravelDescription.class);
		Attribute currency =		new Attribute("currency", Region.class);
		Attribute nearestCity =		new Attribute("city", Region.class);
		
		regionAverage.setDescriptionSimFunction(new Average());
		regionAverage.addMapping(holidayType, new Equal());
		regionAverage.setWeight(holidayType, 0.3);
		regionAverage.addMapping(travelRegion, new Average());
		regionAverage.setWeight(travelRegion, 0.8);
		regionAverage.addMapping(currency, new Equal());
		regionAverage.setWeight(currency, 1.0);
		regionAverage.addMapping(nearestCity, new Equal());
		regionAverage.setWeight(nearestCity, 0.5);
		
		regionWeightedAverage.setDescriptionSimFunction(new Average());
		regionWeightedAverage.addMapping(holidayType, new Equal());
		regionWeightedAverage.setWeight(holidayType, 0.3);
		AdvancedAverage advancedAverage = new AdvancedAverage();
		regionWeightedAverage.addMapping(travelRegion, advancedAverage);
		regionWeightedAverage.setWeight(travelRegion, 0.8);
		regionWeightedAverage.addMapping(currency, new Equal());
		advancedAverage.localSimConfig.setWeight(currency, 1.0);
		regionWeightedAverage.addMapping(nearestCity, new Equal());
		advancedAverage.localSimConfig.setWeight(nearestCity, 0.5);
		
		Assert.assertTrue("Loaded cases must be more than 0", cases.size() > 0);
		
		Collection<RetrievalResult> averageResult = NNScoringMethod.evaluateSimilarity(cases, averageQuery, regionAverage);
		Collection<RetrievalResult> averageWeightResult = NNScoringMethod.evaluateSimilarity(cases, averageQuery, regionWeightedAverage);
		
		Iterator<RetrievalResult> averageResultIterator = averageResult.iterator();
		Iterator<RetrievalResult> averageWeightResultIterator = averageWeightResult.iterator();
		while(averageResultIterator.hasNext() && averageWeightResultIterator.hasNext()) {
			RetrievalResult averageRR = averageResultIterator.next();
			RetrievalResult weightedRR = averageWeightResultIterator.next();
			Assert.assertEquals("results should be equal between case " + averageRR.get_case() + " and " + weightedRR.get_case(), 
								averageRR.getEval(), weightedRR.getEval());
		}
	}

}
