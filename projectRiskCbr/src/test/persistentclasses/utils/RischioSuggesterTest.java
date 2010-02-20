package test.persistentclasses.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jcolibri.cbrcore.CBRCase;
import jcolibri.method.retrieve.RetrievalResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import persistentclasses.Progetto;
import persistentclasses.Rischio;
import persistentclasses.utils.RischioSuggester;

public class RischioSuggesterTest {
	Collection<RetrievalResult> cases;
	CBRCase query;
	
	Collection<RischioSuggester> suggestersNoQuery;
	Collection<RischioSuggester> suggestersQuery;
	
	@Before
	public void setUp() {
		cases = 		new ArrayList<RetrievalResult>();
		
		CBRCase			cbrCase;
		RetrievalResult cbrCaseRR;
		Progetto		progetto;
		Rischio			rischio;
		Integer			rischioTypeA = 1;
		Integer			rischioTypeB = 2;
		
		/* 
		 * adding first case with two risk A and one risk B to cases collection
		 * with an eval of 0.8
		 */
		
		cbrCase 	= new CBRCase();
		progetto 	= new Progetto();
		
		try {
			progetto.setCodice("progetto a");
			progetto.setValoreEconomico(1000.0);
			
			rischio 	= new Rischio();
			rischio.setCodice("a");
			rischio.setCodiceChecklist(rischioTypeA);
			rischio.setProbabilitaIniziale(30);
			rischio.setImpattoIniziale(3);
			rischio.setCostoPotenzialeImpatto(100);
			rischio.setContingency(50);
			progetto.getRischi().add(rischio);
			
			rischio 	= new Rischio();
			rischio.setCodice("b");
			rischio.setCodiceChecklist(rischioTypeA);
			rischio.setProbabilitaIniziale(70);
			rischio.setImpattoIniziale(1);
			rischio.setCostoPotenzialeImpatto(50);
			rischio.setContingency(100);
			progetto.getRischi().add(rischio);
			
			rischio		= new Rischio();
			rischio.setCodice("c");
			rischio.setCodiceChecklist(rischioTypeB);
			rischio.setProbabilitaIniziale(21);
			rischio.setImpattoIniziale(3);
			rischio.setCostoPotenzialeImpatto(76);
			rischio.setContingency(4300);
			progetto.getRischi().add(rischio);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		cbrCase.setDescription(progetto);
		cbrCaseRR = new RetrievalResult(cbrCase, 0.8);
		cases.add(cbrCaseRR);
		
		
		/* 
		 * adding second case with one risk A and two risk B to cases collection
		 * with an eval of 0.4
		 */
		
		cbrCase 	= new CBRCase();
		progetto 	= new Progetto();
		
		try {
			progetto.setCodice("progetto b");
			progetto.setValoreEconomico(100.0);
			
			rischio 	= new Rischio();
			rischio.setCodice("d");
			rischio.setCodiceChecklist(rischioTypeA);
			rischio.setProbabilitaIniziale(33);
			rischio.setImpattoIniziale(5);
			rischio.setCostoPotenzialeImpatto(6);
			rischio.setContingency(11);
			progetto.getRischi().add(rischio);
			
			rischio 	= new Rischio();
			rischio.setCodice("e");
			rischio.setCodiceChecklist(rischioTypeB);
			rischio.setProbabilitaIniziale(70);
			rischio.setImpattoIniziale(5);
			rischio.setCostoPotenzialeImpatto(15);
			rischio.setContingency(500);
			progetto.getRischi().add(rischio);
			
			rischio		= new Rischio();
			rischio.setCodice("f");
			rischio.setCodiceChecklist(rischioTypeB);
			rischio.setProbabilitaIniziale(90);
			rischio.setImpattoIniziale(3);
			rischio.setCostoPotenzialeImpatto(25);
			rischio.setContingency(700);
			progetto.getRischi().add(rischio);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		cbrCase.setDescription(progetto);
		cbrCaseRR = new RetrievalResult(cbrCase, 0.4);
		cases.add(cbrCaseRR);
		
		cbrCase 	= new CBRCase();
		progetto 	= new Progetto();
		progetto.setCodice("query");
		progetto.setValoreEconomico(10.0);
		cbrCase.setDescription(progetto);
		query = cbrCase;
		
		suggestersNoQuery 	= RischioSuggester.getSuggesters(cases);
		suggestersQuery		= RischioSuggester.getSuggesters(query, cases);
	}
	
	@Test
	public void testGetSuggesters() {
		Assert.assertEquals("we should have 2 suggestersNoQuery, 1 for rischioTypeA and 1 for rischioTypeB", suggestersNoQuery.size(), 2);
	}
	
	@Test
	public void testGetTotalEval() {
		Iterator<RischioSuggester> iterator = suggestersNoQuery.iterator();
		Assert.assertEquals("rischioTypeA eval = 2*0.8 + 0.4", 2.0, iterator.next().getTotalEval(), 0.0);
		Assert.assertEquals("rischioTypeB eval = 0.8 + 2*0.4", 1.6, iterator.next().getTotalEval(), 0.0);
	}
	
	@Test
	public void testGetRischiCount() {
		Iterator<RischioSuggester> iterator = suggestersNoQuery.iterator();
		Assert.assertEquals("rischioTypeA has 3 instances, 2 in project a, 1 in project b", 3, iterator.next().getRischiCount());
		Assert.assertEquals("rischioTypeB has 3 instances, 1 in project a, 2 in project b", 3, iterator.next().getRischiCount());
	}
	
	@Test
	public void testSuggestionNoQuery() {
		Iterator<RischioSuggester> iterator = suggestersNoQuery.iterator();
		Rischio rischio;
		
		rischio = iterator.next().getSuggestion();
		Assert.assertEquals("rischioTypeA suggested probabilita' iniziale",		Math.round((1.6 * 50+ 0.4 * 33)/2.0),	rischio.getProbabilitaIniziale());
		Assert.assertEquals("rischioTypeA suggested impatto iniziale",			Math.round((1.6 * 2 + 0.4 * 5)/2.0),	rischio.getImpattoIniziale(), 0.0);
		Assert.assertEquals("rischioTypeA suggested costo potenziale impatto",	(1.6 * 75 			+ 0.4 * 6)/2.0,				rischio.getCostoPotenzialeImpatto(), 0.0);
		Assert.assertEquals("rischioTypeA suggested contingency", 				(1.6 * 75 			+ 0.4 * 11)/2.0,				rischio.getContingency(), 0.0);
		
		rischio = iterator.next().getSuggestion();
		Assert.assertEquals("rischioTypeB suggested probabilita' iniziale",		Math.round((0.8 * 21+ 0.8 * 80)/1.6),	rischio.getProbabilitaIniziale(), 0.0);
		Assert.assertEquals("rischioTypeB suggested impatto iniziale", 			Math.round((0.8 * 3 + 0.8 * 4)/1.6),	rischio.getImpattoIniziale(), 0.0);
		Assert.assertEquals("rischioTypeB suggested costo potenziale impatto", 	(0.8 * 76 			+ 0.8 * 20)/1.6,	rischio.getCostoPotenzialeImpatto(), 0.0);
		Assert.assertEquals("rischioTypeB suggested contingency", 				(0.8 * 4300 		+ 0.8 * 600)/1.6,	rischio.getContingency(), 0.0);
	}
	
	@Test
	public void testSuggestionQuery() {
		Iterator<RischioSuggester> iterator = suggestersQuery.iterator();
		Rischio rischio;
		
		rischio = iterator.next().getSuggestion();
		Assert.assertEquals("rischioTypeA suggested probabilita' iniziale",		Math.round((1.6 * 50+ 0.4 * 33)/2.0),		rischio.getProbabilitaIniziale(),	0.0);
		Assert.assertEquals("rischioTypeA suggested impatto iniziale",			Math.round((1.6 * 2 + 0.4 * 5)/2.0),		rischio.getImpattoIniziale(), 		0.0);
		Assert.assertEquals("rischioTypeA suggested costo potenziale impatto",	(0.01 * 1.6 * 75 	+ 0.1 * 0.4 * 6)/2.0,	rischio.getCostoPotenzialeImpatto(),0.0001);
		Assert.assertEquals("rischioTypeA suggested contingency", 				(0.01 * 1.6 * 75 	+ 0.1 * 0.4 * 11)/2.0,	rischio.getContingency(), 			0.0001);
		
		rischio = iterator.next().getSuggestion();
		Assert.assertEquals("rischioTypeB suggested probabilita' iniziale",		Math.round((0.8 * 21+ 0.8 * 80)/1.6),		rischio.getProbabilitaIniziale(), 	0.0);
		Assert.assertEquals("rischioTypeB suggested impatto iniziale", 			Math.round((0.8 * 3 + 0.8 * 4)/1.6),		rischio.getImpattoIniziale(), 		0.0);
		Assert.assertEquals("rischioTypeB suggested costo potenziale impatto", 	(0.01 * 0.8 * 76 	+ 0.1 * 0.8 * 20)/1.6,	rischio.getCostoPotenzialeImpatto(),0.0001);
		Assert.assertEquals("rischioTypeB suggested contingency", 				(0.01 * 0.8 * 4300 	+ 0.1 * 0.8 * 600)/1.6,	rischio.getContingency(), 			0.0001);		
	}
}
