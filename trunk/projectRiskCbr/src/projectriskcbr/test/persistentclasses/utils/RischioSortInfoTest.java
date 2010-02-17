package projectriskcbr.test.persistentclasses.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jcolibri.cbrcore.CBRCase;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.selection.SelectCases;

import org.junit.Before;
import org.junit.Test;

import persistentclasses.Progetto;
import persistentclasses.Rischio;
import persistentclasses.utils.RischioSortInfo;

public class RischioSortInfoTest {
	Collection<RetrievalResult> cases;
	
	@Before
	public void setUp() {
		cases = new ArrayList<RetrievalResult>();
		CBRCase		cbrCase;
		RetrievalResult cbrCaseRR;
		Progetto	progetto;
		Rischio		rischio;
		Integer		rischioTypeA = 1;
		Integer		rischioTypeB = 2;
		
		/* 
		 * adding first case with two risk A and one risk B to cases collection
		 * with an eval of 0.7
		 */
		
		cbrCase 	= new CBRCase();
		progetto 	= new Progetto();
		
		try {
			rischio 	= new Rischio();
			rischio.setCodiceChecklist(rischioTypeA);
			progetto.aggiungiRischio(rischio);
			
			rischio 	= new Rischio();
			rischio.setCodiceChecklist(rischioTypeA);
			progetto.aggiungiRischio(rischio);
			
			rischio		= new Rischio();
			rischio.setCodiceChecklist(rischioTypeB);
			progetto.aggiungiRischio(rischio);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		cbrCase.setDescription(progetto);
		cbrCaseRR = new RetrievalResult(cbrCase, 0.7);
		cases.add(cbrCaseRR);
		
		
		/* 
		 * adding second case with one risk A and two risk B to cases collection
		 * with an eval of 0.7
		 */
		
		cbrCase 	= new CBRCase();
		progetto 	= new Progetto();
		
		try {
			rischio 	= new Rischio();
			rischio.setCodiceChecklist(rischioTypeA);
			progetto.aggiungiRischio(rischio);
			
			rischio 	= new Rischio();
			rischio.setCodiceChecklist(rischioTypeB);
			progetto.aggiungiRischio(rischio);
			
			rischio		= new Rischio();
			rischio.setCodiceChecklist(rischioTypeB);
			progetto.aggiungiRischio(rischio);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		cbrCase.setDescription(progetto);
		cbrCaseRR = new RetrievalResult(cbrCase, 0.7);
		cases.add(cbrCaseRR);		
	}
	
	@Test
	public void testGetTotalEval() {

	}
	
	@Test
	public void testGetTopKRischio() {
		
	}

}
