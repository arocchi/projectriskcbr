package test.persistentclasses;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage;
import jcolibriext.method.retrieve.NNretrieval.similarity.local.IntervalImpattoStrategico;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import persistentclasses.Progetto;
import persistentclasses.attributes.ImpattoStrategico;
import persistentclasses.attributes.LivelloDiRischio;
import projectriskcbr.config.Configuration;
import projectriskcbr.config.ConfigurationAttribute;
import projectriskcbr.config.ConfigurationGroup;
import projectriskcbr.config.NNConfigurator;

public class ProgettoTest {
	Collection<CBRCase> cases;
	CBRQuery query;
	Configuration manualConfiguration;
	
	CBRCase cbrCaseA;
	CBRCase cbrCaseB;
	CBRCase cbrCaseO;
	
	@Before
	public void setUp() throws Exception {
		cases = new ArrayList<CBRCase>();
		
		Progetto progettoCase;
		ImpattoStrategico caseIm;
		ImpattoStrategico caseIp;
		
		LivelloDiRischio caseMercato;
		LivelloDiRischio casePaese;
		
		// populating casebase
		cbrCaseO = new CBRCase();
		progettoCase = new Progetto();
		progettoCase.setCodice("O");
		caseIm = new ImpattoStrategico(3);
		caseIp = new ImpattoStrategico(3);
		
		caseMercato = new LivelloDiRischio(3, 3, 3);
		casePaese = new LivelloDiRischio(3, 3, 3);
		
		progettoCase.setIm(caseIm);
		progettoCase.setIp(caseIp);
		progettoCase.setMercatoCliente(caseMercato);
		progettoCase.setPaese(casePaese);
		cbrCaseO.setDescription(progettoCase);
		cases.add(cbrCaseO);
		
		cbrCaseA = new CBRCase();
		progettoCase = new Progetto();
		progettoCase.setCodice("A");
		caseIm = new ImpattoStrategico(0);
		caseIp = new ImpattoStrategico(3);
		
		caseMercato = new LivelloDiRischio(0, 0, 0);
		casePaese = new LivelloDiRischio(3, 3, 3);
		
		progettoCase.setIm(caseIm);
		progettoCase.setIp(caseIp);
		progettoCase.setMercatoCliente(caseMercato);
		progettoCase.setPaese(casePaese);
		cbrCaseA.setDescription(progettoCase);
		cases.add(cbrCaseA);
		
		cbrCaseB = new CBRCase();
		progettoCase = new Progetto();
		progettoCase.setCodice("B");
		caseIm = new ImpattoStrategico(3);
		caseIp = new ImpattoStrategico(0);
		
		caseMercato = new LivelloDiRischio(3, 3, 3);
		casePaese = new LivelloDiRischio(0, 0, 0);
		
		progettoCase.setIm(caseIm);
		progettoCase.setIp(caseIp);
		progettoCase.setMercatoCliente(caseMercato);
		progettoCase.setPaese(casePaese);
		cbrCaseB.setDescription(progettoCase);
		cases.add(cbrCaseB);

		
		// setting query
		Progetto queryDesc = new Progetto();
		ImpattoStrategico queryIm = new ImpattoStrategico(3);
		ImpattoStrategico queryIp = new ImpattoStrategico(3);
		
		LivelloDiRischio queryMercato = new LivelloDiRischio(3, 3, 3);
		LivelloDiRischio queryPaese = new LivelloDiRischio(3, 3, 3);
		
		queryDesc.setIm(queryIm);
		queryDesc.setIp(queryIp);
		queryDesc.setPaese(queryPaese);
		queryDesc.setMercatoCliente(queryMercato);

		query = new CBRQuery();
		query.setDescription(queryDesc);
		
		// setting manual simConfig
		manualConfiguration = new Configuration();
		ConfigurationGroup groupA = new ConfigurationGroup("group A");
		ConfigurationGroup groupB = new ConfigurationGroup("group B");
		ConfigurationGroup groupTotal = new ConfigurationGroup("total Config");
		manualConfiguration.groups.add(groupA);
		manualConfiguration.groups.add(groupB);
		manualConfiguration.groups.add(groupTotal);
		
		ConfigurationAttribute attributePaese = new ConfigurationAttribute("paese");
		ConfigurationAttribute attributePaeseR1 = new ConfigurationAttribute("paese.R1");
		ConfigurationAttribute attributePaeseR2 = new ConfigurationAttribute("paese.R2");
		ConfigurationAttribute attributePaeseR3 = new ConfigurationAttribute("paese.R3");
		ConfigurationAttribute attributeIp = new ConfigurationAttribute("ip");
		groupA.attributes.add(attributePaese);
		groupA.attributes.add(attributePaeseR1);
		groupA.attributes.add(attributePaeseR2);
		groupA.attributes.add(attributePaeseR3);
		groupA.attributes.add(attributeIp);
		
		ConfigurationAttribute attributeMercato = new ConfigurationAttribute("mercatoCliente");
		ConfigurationAttribute attributeMercatoR1 = new ConfigurationAttribute("mercatoCliente.R1");
		ConfigurationAttribute attributeMercatoR2 = new ConfigurationAttribute("mercatoCliente.R2");
		ConfigurationAttribute attributeMercatoR3 = new ConfigurationAttribute("mercatoCliente.R3");
		ConfigurationAttribute attributeIm = new ConfigurationAttribute("im");
		groupB.attributes.add(attributeMercato);
		groupB.attributes.add(attributeMercatoR1);
		groupB.attributes.add(attributeMercatoR2);
		groupB.attributes.add(attributeMercatoR3);
		groupB.attributes.add(attributeIm);
		
		groupTotal.attributes.add(attributePaese);
		groupTotal.attributes.add(attributePaeseR1);
		groupTotal.attributes.add(attributePaeseR2);
		groupTotal.attributes.add(attributePaeseR3);
		groupTotal.attributes.add(attributeIp);
		groupTotal.attributes.add(attributeMercato);
		groupTotal.attributes.add(attributeMercatoR1);
		groupTotal.attributes.add(attributeMercatoR2);
		groupTotal.attributes.add(attributeMercatoR3);
		groupTotal.attributes.add(attributeIm);	
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test 
	public void testGetSimilarityFunction() throws Exception {
		Progetto progetto = new Progetto();
		Assert.assertTrue(progetto.getSimilarityFunction("paese") instanceof AdvancedAverage);
		Assert.assertTrue(progetto.getSimilarityFunction("mercatoCliente") instanceof AdvancedAverage);
		Assert.assertTrue(progetto.getSimilarityFunction("ip") instanceof IntervalImpattoStrategico);
		Assert.assertTrue(progetto.getSimilarityFunction("im") instanceof IntervalImpattoStrategico);
	}
	
	@Test
	public void testGetTotalSimilarityConfig() throws Exception {
		Progetto progetto = new Progetto();
		
		// testing group A for similarity results
		NNConfig groupASimConfig = new NNConfig();
		NNConfigurator.configureSimilarity(groupASimConfig, manualConfiguration.groups.get(0) , Progetto.class);
		Collection<RetrievalResult> groupAresults = NNScoringMethod.evaluateSimilarity(cases, query, groupASimConfig);
		Iterator<RetrievalResult> groupAresultsIterator = groupAresults.iterator();
		Assert.assertTrue(	"groupA should contain two cases with similarity 1.0, and the third should have similarity 0.0", 
							groupAresultsIterator.next().getEval() == 1.0 &&
							groupAresultsIterator.next().getEval() == 1.0 &&
							groupAresultsIterator.next().getEval() == 0.0);
		Collection<CBRCase> groupATop2 = SelectCases.selectTopK(groupAresults, 2);
		Assert.assertTrue(	"groupA should contain case O", groupATop2.contains(cbrCaseO));
		Assert.assertTrue(	"groupA should containt case A", groupATop2.contains(cbrCaseA));

		// testing group B for similarity results
		NNConfig groupBSimConfig = new NNConfig();
		NNConfigurator.configureSimilarity(groupBSimConfig, manualConfiguration.groups.get(1) , Progetto.class);
		Collection<RetrievalResult> groupBresults = NNScoringMethod.evaluateSimilarity(cases, query, groupBSimConfig);
		Iterator<RetrievalResult> groupBresultsIterator = groupBresults.iterator();
		Assert.assertTrue(	"groupB should containt two cases with similarity 1.0, and the third should have similarity 0.0", 
							groupBresultsIterator.next().getEval() == 1.0 &&
							groupBresultsIterator.next().getEval() == 1.0 &&
							groupBresultsIterator.next().getEval() == 0.0);
		Collection<CBRCase> groupBTop2 = SelectCases.selectTopK(groupBresults, 2);
		Assert.assertTrue("groupB should contain case O", groupBTop2.contains(cbrCaseO));
		Assert.assertTrue("groupB should containt case B", groupBTop2.contains(cbrCaseB));
		
		// testing group Total for similarity results on all cases
		NNConfig groupTotalSimConfig = new NNConfig();
		NNConfigurator.configureSimilarity(groupTotalSimConfig, manualConfiguration.groups.get(2) , Progetto.class);
		Collection<RetrievalResult> groupTotalresults = NNScoringMethod.evaluateSimilarity(cases, query, groupTotalSimConfig);
		Iterator<RetrievalResult> groupTotalresultsIterator = groupTotalresults.iterator();
		Assert.assertTrue(	"groupTotal should containt one case with similarity 1.0 " +
							"and two cases with similarity 0.5", 
							groupTotalresultsIterator.next().getEval() == 1.0 &&
							groupTotalresultsIterator.next().getEval() == 0.5 &&
							groupTotalresultsIterator.next().getEval() == 0.5);
		
		// testing totalSimilaritySimConfig for similarity results on all cases
		NNConfig totalSimilaritySimConfig;
		totalSimilaritySimConfig = progetto.getTotalSimilarityConfig(null);
		Assert.assertNotNull("getSimilarityConfig should not return a null simConfig", totalSimilaritySimConfig);
		Collection<RetrievalResult> globallySortedresults = NNScoringMethod.evaluateSimilarity(cases, query, totalSimilaritySimConfig);
		Iterator<RetrievalResult> globallySortedresultsIterator = globallySortedresults.iterator();
		Assert.assertEquals(	"globallySortedResults first RR should have similarity 1.0",
								1.0, globallySortedresultsIterator.next().getEval(), 0.0);
		Assert.assertEquals(	"globallySortedResults first RR should have similarity 0.5", 
								0.5, globallySortedresultsIterator.next().getEval(), 0.0);
		Assert.assertEquals(	"globallySortedResults first RR should have similarity 0.5",
								0.5, globallySortedresultsIterator.next().getEval(), 0.0);
		
		// testing totalSimilaritySimConfig for similarity results on group A results and group B results
		Collection<RetrievalResult> globallySortedGroupA = NNScoringMethod.evaluateSimilarity(groupATop2, query, totalSimilaritySimConfig);
		RetrievalResult[] globallySortedGroupAAsArray = new RetrievalResult[3]; 
		globallySortedGroupAAsArray = globallySortedGroupA.toArray(globallySortedGroupAAsArray);
		Assert.assertEquals("cbrCaseO should be first ranked for globally sorted groupA", 
							globallySortedGroupAAsArray[0].get_case(), cbrCaseO);
		Assert.assertEquals("cbrCaseO should be first ranked for globally sorted groupA with similarity 1.0", 
							1.0, globallySortedGroupAAsArray[0].getEval(),0.0);
		
		Assert.assertEquals("cbrCaseA should be second ranked for globally sorted groupA", 
							globallySortedGroupAAsArray[1].get_case(), cbrCaseA);
		Assert.assertEquals("cbrCaseA should be second ranked for globally sorted groupA with similarity 0.5", 
							0.5, globallySortedGroupAAsArray[1].getEval(), 0.0);
		
		Collection<RetrievalResult> globallySortedGroupB = NNScoringMethod.evaluateSimilarity(groupBTop2, query, totalSimilaritySimConfig);
		RetrievalResult[] globallySortedGroupBAsArray = new RetrievalResult[3]; 
		globallySortedGroupBAsArray = globallySortedGroupB.toArray(globallySortedGroupBAsArray);
		Assert.assertEquals("cbrCaseO should be first ranked for globally sorted groupB", 
							globallySortedGroupBAsArray[0].get_case(), cbrCaseO);
		Assert.assertEquals("cbrCaseO should be first ranked for globally sorted groupB with similarity 1.0", 
							1.0, globallySortedGroupBAsArray[0].getEval(), 0.0);
		
		Assert.assertEquals("cbrCaseB should be second ranked for globally sorted groupB", 
							globallySortedGroupBAsArray[1].get_case(), cbrCaseB);
		Assert.assertEquals("cbrCaseB should be second ranked for globally sorted groupB with similarity 0.5", 
							0.5, globallySortedGroupBAsArray[1].getEval(), 0.0);
	}

}
