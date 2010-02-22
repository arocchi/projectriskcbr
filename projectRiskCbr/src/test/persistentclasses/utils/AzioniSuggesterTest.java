package test.persistentclasses.utils;


import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.method.retrieve.RetrievalResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import persistentclasses.Azioni;
import persistentclasses.Progetto;
import persistentclasses.Rischio;
import persistentclasses.attributes.AzioniPrimaryKey;
import persistentclasses.utils.AzioniSuggester;
import persistentclasses.utils.RischioSuggester;

public class AzioniSuggesterTest {
	Collection<RetrievalResult> cases;
	
	Collection<RischioSuggester> rischioSuggesters;
	RischioSuggester rischioASuggester;
	
	Collection<AzioniSuggester> azioniSuggesters;
	
	Integer				rischioTypeA = 1;
	Integer				rischioTypeB = 2;
	Integer				rischioTypeC = 3;
	Integer				rischioTypeD = 4;
	
	Integer				azioneIdA	= 1;
	Integer				azioneIdB	= 2;
	Integer				azioneIdC	= 3;
	Integer				azioneIdD	= 4;
	Integer				azioneIdE	= 5;
	Integer				azioneIdF	= 6;
	Integer				azioneIdG	= 7;
	Integer				azioneIdH	= 8;
	Integer				azioneIdL	= 9;
	Integer				azioneIdM	= 10;
	Integer				azioneIdN	= 11;
	
	@Before
	public void setUp() {
		cases = 		new ArrayList<RetrievalResult>();
		
		CBRCase				cbrCase;
		RetrievalResult 	cbrCaseRR;
		Progetto			progetto;
		Rischio				rischio;
		Azioni				azione;
		AzioniPrimaryKey	azionePK;
		
		/* 
		 * adding first case with two risk A and one risk B to cases collection
		 * with an eval of 0.8
		 */
		
		cbrCase 	= new CBRCase();
		progetto 	= new Progetto();
		
		try {
			progetto.setCodice("a");
			
			rischio 	= new Rischio();
			rischio.setCodice("a");
			rischio.setCodiceChecklist(rischioTypeA);
			rischio.setProbabilitaIniziale(30);
			rischio.setImpattoIniziale(3);
			rischio.setCostoPotenzialeImpatto(100);
			rischio.setContingency(50);
			progetto.getRischi().add(rischio);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("a");
			azione.setIntensita(10);
			azionePK 	= new AzioniPrimaryKey(1, azioneIdA, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("b");
			azione.setIntensita(1);
			azionePK 	= new AzioniPrimaryKey(2, azioneIdB, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Back-up");
			azione.setDescrizione("e");
			azionePK 	= new AzioniPrimaryKey(3, azioneIdE, null, 'R');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("g");
			azione.setIntensita(3);
			azionePK 	= new AzioniPrimaryKey(4, azioneIdG, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("h");
			azione.setIntensita(-1);
			azionePK 	= new AzioniPrimaryKey(5, azioneIdH, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("l");
			azione.setIntensita(-1);
			azionePK 	= new AzioniPrimaryKey(6, azioneIdL, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Planned");
			azione.setDescrizione("m");
			azionePK 	= new AzioniPrimaryKey(7, azioneIdM, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("n");
			azione.setIntensita(-3);
			azionePK 	= new AzioniPrimaryKey(8, azioneIdN, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			
			rischio		= new Rischio();
			rischio.setCodice("b");
			rischio.setCodiceChecklist(rischioTypeB);
			rischio.setProbabilitaIniziale(21);
			rischio.setImpattoIniziale(3);
			rischio.setCostoPotenzialeImpatto(76);
			rischio.setContingency(43);
			progetto.getRischi().add(rischio);
			
			azione 		= new Azioni();
			azione.setStato("Planned");
			azione.setDescrizione("c");
			azionePK 	= new AzioniPrimaryKey(9, azioneIdC, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
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
			progetto.setCodice("b");
			
			rischio 	= new Rischio();
			rischio.setCodice("a");
			rischio.setCodiceChecklist(rischioTypeA);
			rischio.setProbabilitaIniziale(33);
			rischio.setImpattoIniziale(5);
			rischio.setCostoPotenzialeImpatto(66);
			rischio.setContingency(11);
			progetto.getRischi().add(rischio);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("a");
			azione.setIntensita(5);
			azionePK 	= new AzioniPrimaryKey(10, azioneIdA, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("b");
			azione.setIntensita(3);
			azionePK 	= new AzioniPrimaryKey(11, azioneIdB, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("h");
			azione.setIntensita(3);
			azionePK 	= new AzioniPrimaryKey(12, azioneIdH, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("l");
			azione.setIntensita(-1);
			azionePK 	= new AzioniPrimaryKey(13, azioneIdL, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("g");
			azione.setIntensita(-50);
			azionePK 	= new AzioniPrimaryKey(14, azioneIdG, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Planned");
			azione.setDescrizione("g");
			azionePK 	= new AzioniPrimaryKey(15, azioneIdG, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Closed");
			azione.setDescrizione("n");
			azione.setIntensita(1);
			azionePK 	= new AzioniPrimaryKey(16, azioneIdN, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			
			
			rischio 	= new Rischio();
			rischio.setCodice("c");
			rischio.setCodiceChecklist(rischioTypeC);
			rischio.setProbabilitaIniziale(70);
			rischio.setImpattoIniziale(5);
			rischio.setCostoPotenzialeImpatto(15);
			rischio.setContingency(500);
			progetto.getRischi().add(rischio);
			
			azione 		= new Azioni();
			azione.setStato("Planned");
			azione.setDescrizione("c");
			azionePK 	= new AzioniPrimaryKey(17, azioneIdC, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			azione 		= new Azioni();
			azione.setStato("Back-up");
			azione.setDescrizione("f");
			azionePK 	= new AzioniPrimaryKey(18, azioneIdF, null, 'R');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
			
			rischio 	= new Rischio();
			rischio.setCodice("d");
			rischio.setCodiceChecklist(rischioTypeD);
			rischio.setProbabilitaIniziale(70);
			rischio.setImpattoIniziale(5);
			rischio.setCostoPotenzialeImpatto(15);
			rischio.setContingency(500);
			progetto.getRischi().add(rischio);
			
			azione 		= new Azioni();
			azione.setStato("Planned");
			azione.setDescrizione("d");
			azionePK 	= new AzioniPrimaryKey(19, azioneIdD, null, 'M');
			azione.setPrimaryKey(azionePK);
			rischio.getAzioni().add(azione);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		cbrCase.setDescription(progetto);
		cbrCaseRR = new RetrievalResult(cbrCase, 0.4);
		cases.add(cbrCaseRR);
		
		rischioSuggesters = RischioSuggester.getSuggesters(cases);
		rischioASuggester = rischioSuggesters.iterator().next();
		
		azioniSuggesters = AzioniSuggester.getSuggesters(rischioASuggester, rischioSuggesters);
	}
	
	/**
	 * action A, B, G, E, H should be perfect matchers.
	 * action M should be suggested, but not be a perfect matcher
	 */
	@Test
	public void testPerfectMatchers() {
		AzioniSuggester suggesterA = null;
		AzioniSuggester suggesterB = null;
		AzioniSuggester suggesterG = null;
		AzioniSuggester suggesterE = null;
		AzioniSuggester suggesterH = null;
		AzioniSuggester suggesterM = null;
		
		for(AzioniSuggester azioniSuggester : azioniSuggesters) {
			if(azioniSuggester.getActionId().equals(azioneIdA)) {
				suggesterA = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdB)) {
				suggesterB = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdG)) {
				suggesterG = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdE)) {
				suggesterE = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdH)) {
				suggesterH = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdM)) {
				suggesterM = azioniSuggester;
			}
		}
		
		Assert.assertNotNull("action A should get suggested", suggesterA);
		Assert.assertNotNull("action B should get suggested", suggesterB);
		Assert.assertNotNull("action G should get suggested", suggesterG);
		Assert.assertNotNull("action E should get suggested", suggesterE);
		Assert.assertNotNull("action H should get suggested", suggesterH);
		Assert.assertNotNull("action M should get suggested", suggesterM);
		
		Assert.assertTrue("action A should be a perfect matcher", suggesterA.isPerfectMatcher());
		Assert.assertTrue("action B should be a perfect matcher", suggesterB.isPerfectMatcher());
		Assert.assertTrue("action G should be a perfect matcher", suggesterG.isPerfectMatcher());
		Assert.assertTrue("action E should be a perfect matcher", suggesterE.isPerfectMatcher());
		Assert.assertTrue("action H should be a perfect matcher", suggesterH.isPerfectMatcher());
		Assert.assertFalse("action M should not be a perfect matcher", suggesterM.isPerfectMatcher());
	}
	
	/**
	 * perfectMatchers should be ranked according to impact and occurrences. 
	 * 
	 * action A should have greater ranking than action B and action G
	 * action B should have greater ranking than action G and H
	 * there should be no suggestions for action N or action L
	 */
	@Test
	public void testMitigationMatchersSort() {
		Integer indexA = null;
		AzioniSuggester suggesterA = null;
		Integer indexB = null;
		AzioniSuggester suggesterB = null;
		Integer indexG = null;
		AzioniSuggester suggesterG = null;
		Integer indexH = null;
		AzioniSuggester suggesterH = null;
		Integer indexL = null;
		AzioniSuggester suggesterL = null;
		Integer indexN = null;
		AzioniSuggester suggesterN = null;
		
		int index = 0;
		for(AzioniSuggester azioniSuggester : azioniSuggesters) {
			if(azioniSuggester.getActionId().equals(azioneIdA)) {
				indexA = new Integer(index);
				suggesterA = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdB)) {
				indexB = new Integer(index);
				suggesterB = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdG)) {
				indexG = new Integer(index);
				suggesterG = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdH)) {
				indexH = new Integer(index);
				suggesterH = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdL)) {
				indexL = new Integer(index);
				suggesterL = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdN)) {
				indexN = new Integer(index);
				suggesterN = azioniSuggester;
			}
			
			index++;
		}
		
		Assert.assertNotNull("action A should get suggested", indexA);
		Assert.assertNotNull("action B should get suggested", indexB);
		Assert.assertNotNull("action G should get suggested", indexG);
		Assert.assertNotNull("action H should get suggested", indexH);
		
		Assert.assertTrue("action A should have greater ranking than action B", indexA.compareTo(indexB) < 0);
		Assert.assertTrue("action A should have greater ranking than action G", indexA.compareTo(indexG) < 0);
		Assert.assertTrue("action A should have greater eval than action G", suggesterA.getTotalEval().compareTo(suggesterG.getTotalEval()) > 0);

		
		Assert.assertTrue("action B should have greater ranking than action G", indexB.compareTo(indexG) < 0);
		
		Assert.assertTrue("action B should have greater ranking than action H", indexB.compareTo(indexH) < 0);
		
		Assert.assertNull("action L should not get suggested", indexL);
		Assert.assertNull("action N should not get suggested", indexN);		
	}

	/**
	 * Non perfectMatchers should be ranked according to occurrences and project ranking
	 * 
	 * action C should have greater ranking than action D
	 * action C should have greater ranking than action M
	 */
	@Test
	public void testNotMatchersSort() {
		Integer indexC = null;
		AzioniSuggester suggesterC = null;
		Integer indexD = null;
		AzioniSuggester suggesterD = null;
		Integer indexM = null;
		AzioniSuggester suggesterM = null;
		
		int index = 0;
		for(AzioniSuggester azioniSuggester : azioniSuggesters) {
			if(azioniSuggester.getActionId().equals(azioneIdC)) {
				indexC = new Integer(index);
				suggesterC = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdD)) {
				indexD = new Integer(index);
				suggesterD = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdM)) {
				indexM = new Integer(index);
				suggesterM = azioniSuggester;
			}
			index++;
		}
		
		
		Assert.assertNotNull("action C should get suggested", suggesterC);
		Assert.assertNotNull("action D should get suggested", suggesterD);
		Assert.assertNotNull("action M should get suggested", suggesterM);
		
		Assert.assertFalse("action C should be a perfect matcher", suggesterC.isPerfectMatcher());
		Assert.assertFalse("action D should be a perfect matcher", suggesterD.isPerfectMatcher());
		Assert.assertFalse("action M should not be a perfect matcher", suggesterM.isPerfectMatcher());
		
		Assert.assertTrue("action C should have greater ranking than action D", indexC.compareTo(indexD) < 0);
		Assert.assertTrue("action C should have greater ranking than action M", indexC.compareTo(indexM) < 0);
	}
	
	/**
	 * Mitigations come before Recoveries if they are both perfectMatchers
	 * 
	 * action A should have greater ranking than action E
	 * action B should have greater ranking than action E
	 */
	@Test
	public void testMitigationVsRecoveryMatchersSort() {
		Integer	indexA = null;
		Integer	indexB = null;
		Integer	indexE = null;

		
		int index = 0;
		for(AzioniSuggester azioniSuggester : azioniSuggesters) {
			if(azioniSuggester.getActionId().equals(azioneIdA)) {
				indexA = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdB)) {
				indexB = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdE)) {
				indexE = new Integer(index);
			}
			index++;
		}
		
		Assert.assertTrue("action A should have greater ranking than action E", indexA.compareTo(indexE) < 0);
		Assert.assertTrue("action B should have greater ranking than action E", indexB.compareTo(indexE) < 0);		

	}
	
	/**
	 * perfectMatchers should always rank higher than non perfectMatchers
	 * A,B,E,G rank higher than C,D,F,M 
	 */
	@Test
	public void testMatchersVsNotMatchersSort() {
		Integer indexA = null;
		Integer indexB = null;
		Integer indexC = null;
		Integer indexD = null;
		Integer indexE = null;
		Integer indexF = null;
		Integer indexG = null;
		Integer indexM = null;
		
		int index = 0;
		for(AzioniSuggester azioniSuggester : azioniSuggesters) {
			if(azioniSuggester.getActionId().equals(azioneIdA)) {
				indexA = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdB)) {
				indexB = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdC)) {
				indexC = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdD)) {
				indexD = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdE)) {
				indexE = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdF)) {
				indexF = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdG)) {
				indexG = new Integer(index);
			} else if(azioniSuggester.getActionId().equals(azioneIdM)) {
				indexM = new Integer(index);
			}
			
			index++;
		}
				
		Assert.assertTrue("action A should have greater ranking than action C", indexA.compareTo(indexC) < 0);
		Assert.assertTrue("action A should have greater ranking than action D", indexA.compareTo(indexD) < 0);
		Assert.assertTrue("action A should have greater ranking than action F", indexA.compareTo(indexF) < 0);
		Assert.assertTrue("action A should have greater ranking than action M", indexA.compareTo(indexM) < 0);

		Assert.assertTrue("action B should have greater ranking than action C", indexB.compareTo(indexC) < 0);
		Assert.assertTrue("action B should have greater ranking than action D", indexB.compareTo(indexD) < 0);
		Assert.assertTrue("action B should have greater ranking than action F", indexB.compareTo(indexF) < 0);
		Assert.assertTrue("action B should have greater ranking than action M", indexB.compareTo(indexM) < 0);
		
		Assert.assertTrue("action E should have greater ranking than action C", indexE.compareTo(indexC) < 0);
		Assert.assertTrue("action E should have greater ranking than action D", indexE.compareTo(indexD) < 0);
		Assert.assertTrue("action E should have greater ranking than action F", indexE.compareTo(indexF) < 0);
		Assert.assertTrue("action E should have greater ranking than action M", indexE.compareTo(indexM) < 0);
		
		Assert.assertTrue("action G should have greater ranking than action C", indexG.compareTo(indexC) < 0);
		Assert.assertTrue("action G should have greater ranking than action D", indexG.compareTo(indexD) < 0);
		Assert.assertTrue("action G should have greater ranking than action F", indexG.compareTo(indexF) < 0);
		Assert.assertTrue("action G should have greater ranking than action M", indexG.compareTo(indexM) < 0);
	}
	
	@Test
	public void testSuggestersSuggestion() {
		Integer indexA = null;
		AzioniSuggester suggesterA = null;
		Azioni suggestedA = null;
		Integer indexB = null;
		AzioniSuggester suggesterB = null;
		Azioni suggestedB = null;
		Integer indexE = null;
		AzioniSuggester suggesterE = null;
		Azioni suggestedE = null;
		Integer indexG = null;
		AzioniSuggester suggesterG = null;
		Azioni suggestedG = null;
		
		int index = 0;
		for(AzioniSuggester azioniSuggester : azioniSuggesters) {
			if(azioniSuggester.getActionId().equals(azioneIdA)) {
				indexA = new Integer(index);
				suggesterA = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdB)) {
				indexB = new Integer(index);
				suggesterB = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdE)) {
				indexE = new Integer(index);
				suggesterE = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdG)) {
				indexG = new Integer(index);
				suggesterG = azioniSuggester;
			}
			index++;
		}
				
		suggestedA = suggesterA.getSuggestion();
		suggestedB = suggesterB.getSuggestion();
		suggestedE = suggesterE.getSuggestion();
		suggestedG = suggesterG.getSuggestion();
		
		Assert.assertEquals("action A has same id as suggesterA", suggesterA.getActionId().intValue(), suggestedA.getPrimaryKey().getIdAzione());
		Assert.assertEquals("action B has same id as suggesterB", suggesterB.getActionId().intValue(), suggestedB.getPrimaryKey().getIdAzione());
		Assert.assertEquals("action E has same id as suggesterE", suggesterE.getActionId().intValue(), suggestedE.getPrimaryKey().getIdAzione());
		Assert.assertEquals("action G has same id as suggesterG", suggesterG.getActionId().intValue(), suggestedG.getPrimaryKey().getIdAzione());
		
		
		Assert.assertTrue("action A suggestion verification", suggestedA.isPlanned());
		Assert.assertTrue("action A suggestion verification", suggestedA.getPrimaryKey().isMitigation());
		Assert.assertTrue("action A suggestion verification", suggestedA.getDescrizione().equals("a"));
		Assert.assertTrue("action B suggestion verification", suggestedB.isPlanned());
		Assert.assertTrue("action B suggestion verification", suggestedB.getPrimaryKey().isMitigation());
		Assert.assertTrue("action B suggestion verification", suggestedB.getDescrizione().equals("b"));
		Assert.assertTrue("action E suggestion verification; isBackup", 		suggestedE.isBackUp());
		Assert.assertTrue("action E suggestion verification: isRecovery", 		suggestedE.getPrimaryKey().isRecovery());
		Assert.assertTrue("action E suggestion verification: getDescrizione", 	suggestedE.getDescrizione().equals("e"));
		Assert.assertTrue("action G suggestion verification", suggestedG.isPlanned());
		Assert.assertTrue("action G suggestion verification", suggestedG.getPrimaryKey().isMitigation());
		Assert.assertTrue("action G suggestion verification", suggestedG.getDescrizione().equals("g"));
		
	}
	
	/**
	 * verify impact adaptation
	 */
	@Test
	public void testSuggestersImpactAdaptation() {
		AzioniSuggester suggesterA = null;
		AzioniSuggester suggesterB = null;
		AzioniSuggester suggesterG = null;
		
		int index = 0;
		for(AzioniSuggester azioniSuggester : azioniSuggesters) {
			if(azioniSuggester.getActionId().equals(azioneIdA)) {
				suggesterA = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdB)) {
				suggesterB = azioniSuggester;
			} else if(azioniSuggester.getActionId().equals(azioneIdG)) {
				suggesterG = azioniSuggester;
			}
			index++;
		}
				
		Assert.assertEquals("action A impact adaptation verification", Math.round((0.8 * 10 + 0.4 * 5) / 1.2), 	suggesterA.getSuggestion().getIntensita());
		Assert.assertEquals("action B impact adaptation verification", Math.round((0.8 + 3*0.4)/1.2),			suggesterB.getSuggestion().getIntensita());
		Assert.assertEquals("action G impact adaptation verification", 3, 										suggesterG.getSuggestion().getIntensita());
	}
}
