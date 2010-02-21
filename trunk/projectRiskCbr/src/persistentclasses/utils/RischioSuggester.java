package persistentclasses.utils;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jcolibri.cbrcore.CBRCase;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage;
import persistentclasses.Azioni;
import persistentclasses.Progetto;
import persistentclasses.Rischio;

/**
 * RischioSuggester allows to quickly sort Rischio instances according to
 * the RetrievalResults evals of Progetto instances on which the Rischio is present.
 * Each Rischio is considered according to its type (codiceCheckList), and not its instance.
 * Two Rischio with same type are considered equal
 * @author Alessio Rocchi
 */
public class RischioSuggester implements Comparable<RischioSuggester> {
	Integer riskType;
	CBRCase query;
	
	String	riskDescription;
	Map<RetrievalResult, List<Rischio>> sortInfo;
	
	Rischio revisedSuggestion;
	
	public RischioSuggester(Integer riskType) {
		this.riskType = riskType;
		sortInfo = new HashMap<RetrievalResult, List<Rischio>>();
		query = null;
		revisedSuggestion = null;
	}
	
	public RischioSuggester(Integer riskType, CBRCase query) {
		this.riskType = riskType;
		sortInfo = new HashMap<RetrievalResult, List<Rischio>>();
		this.query = query;
		revisedSuggestion = null;
	}
	
	/**
	 * @return the id of the risk from the checklist
	 */
	public Integer getRiskId() {
		return this.riskType;
	}
	
	private RischioSuggester addRR(RetrievalResult rr, Rischio r) {
		List<Rischio> occurrences = null;
		if((occurrences = sortInfo.get(rr)) == null) {
			//
			this.riskDescription = r.getDescrizione();
			
			occurrences = new LinkedList<Rischio>();
			sortInfo.put(rr, occurrences);
		}
		
		occurrences.add(r);
		
		return this;
	}
	
	public Double getTotalEval() {
		Double eval = 0.0;
		for(Map.Entry<RetrievalResult, List<Rischio>> entry : sortInfo.entrySet()) {
			eval += entry.getKey().getEval() * entry.getValue().size();
		}
		
		return eval;
	}
	
	public Map<RetrievalResult, List<Azioni>> getAzioni() {
		Map<RetrievalResult, List<Azioni>> azioniMap = new HashMap<RetrievalResult, List<Azioni>>();
		List<Azioni> azioniList;
		
		for(Map.Entry<RetrievalResult, List<Rischio>> entry : sortInfo.entrySet()) {
			azioniList = new LinkedList<Azioni>();
			for(Rischio rischio : entry.getValue()) {
				azioniList.addAll(rischio.getAzioni());
			}
			azioniMap.put(entry.getKey(), azioniList);
		}
		
		return azioniMap;
	}	
	
	
	public List<Rischio> getRischi() {
		List<Rischio> rischi = new LinkedList<Rischio>();
		for(List<Rischio> lr : sortInfo.values()) {
			rischi.addAll(lr);
		}
		return rischi;
	}
	
	public int getRischiCount() {
		int rischiCount = 0;
		for(Map.Entry<RetrievalResult, List<Rischio>> entry : sortInfo.entrySet()) {
			rischiCount += entry.getValue().size();
		}
		return rischiCount;
	}
	
	/**
	 * compares two RischioSuggester.
	 * One a is less than b if a has a greater TotalEval
	 */
	@Override
	public int compareTo(RischioSuggester compared) {
		Double thisTotalEval = this.getTotalEval();
		Double comparedTotalEval = compared.getTotalEval();
		if(thisTotalEval > comparedTotalEval)
			return -1;
		else if(thisTotalEval < comparedTotalEval)
			return 1;
		else return 0;
	}

	/**
	 * Suggests risks from a RetrievalResult collection of cases
	 * @param cases a Collection<RetrievalResult> where RetrievalResult description is of type Progetto 
	 * @return a List<RischioSuggester>. Each RischioSuggester represent a risk type.
	 */	
	public static List<RischioSuggester> getSuggesters(Collection<RetrievalResult> cases) {
		return getTopKSuggesters(null, cases, null);
	}
	
	
	/**
	 * Suggests risks from a RetrievalResult collection of cases
	 * @param cases a Collection<RetrievalResult> where RetrievalResult description is of type Progetto 
	 * @return a List<RischioSuggester>. Each RischioSuggester represent a risk type.
	 */	
	public static List<RischioSuggester> getSuggesters(CBRCase query, Collection<RetrievalResult> cases) {
		return getTopKSuggesters(query, cases, null);
	}
	
	/**
	 * Suggests risks from a RetrievalResult collection of cases
	 * @param cases a Collection<RetrievalResult> where RetrievalResult description is of type Progetto 
	 * @param k the number of suggested risks we want to get. Can be null
	 * @return a List<RischioSuggester>. Each RischioSuggester represent a risk type.
	 */
	public static List<RischioSuggester> getTopKSuggesters(CBRCase query, Collection<RetrievalResult> cases, Integer k) {
		Map<Integer, RischioSuggester> rischiByType = new HashMap<Integer, RischioSuggester>();
		List<Rischio> rischi = new LinkedList<Rischio>();
		
		// get all risks from all projects
		for(RetrievalResult rr : cases) {
			List<Rischio> rrRischi = ((Progetto)rr.get_case().getDescription()).getRischi();
			rischi.addAll(rrRischi);
		}
		
		// sorts risks by type
		for(Rischio rischio : rischi) {
			Integer rischioType = rischio.getCodiceChecklist();
			if(!rischiByType.containsKey(rischioType)) {
				if(query == null)
					rischiByType.put(rischioType, new RischioSuggester(rischioType));
				else
					rischiByType.put(rischioType, new RischioSuggester(rischioType, query));
			}
		}
		
		// adds information to each risk type according to projects retrieval results
		for(RetrievalResult rr : cases) {
			List<Rischio> rrRischi = ((Progetto)rr.get_case().getDescription()).getRischi();
			for(Rischio rischio : rrRischi) {
				RischioSuggester rischioSuggester = rischiByType.get(rischio.getCodiceChecklist()); 
				rischioSuggester.addRR(rr, rischio);
			}
		}
		
		Collection<RischioSuggester> rischiSortInfo = rischiByType.values();
		List<RischioSuggester> rischiSortable = new LinkedList<RischioSuggester>();
		rischiSortable.addAll(rischiSortInfo);
		Collections.sort(rischiSortable);
		
		if(k == null)
			k = rischiSortable.size();
		else
			k = Math.min(k, rischiSortable.size());
		
		List<RischioSuggester> topKRischiSorted = rischiSortable.subList(0, k);
		
		return topKRischiSorted;
	}
	
	public Rischio getSuggestion() {
		Rischio suggestion = new Rischio();
		suggestion.setCodiceChecklist(this.riskType);
		suggestion.setDescrizione(this.riskDescription);
		
		this.adapt(suggestion);
		return suggestion;
	}
	
	public boolean hasRevisedSuggestion() {
		return (revisedSuggestion != null);
	}
	
	public Rischio getRevisedSuggestion() {
		return revisedSuggestion;
	}

	public void setRevisedSuggestion(Rischio revisedSuggestion) {
		this.revisedSuggestion = revisedSuggestion;
	}

	/**
	 * Adapts impattoIniziale, probabilitaIniziale and contingency
	 * for the specified risk according to info in this suggester 
	 * @param rischio
	 * @return
	 */
	private RischioSuggester adapt(Rischio rischio) {
		// impatto iniziale
		AdvancedAverage iiAverage = new AdvancedAverage();
		
		// probabilita' iniziale
		AdvancedAverage piAverage = new AdvancedAverage();
		
		// costo potenziale impatto
		AdvancedAverage cpiAverage = new AdvancedAverage();
		
		// contingency
		AdvancedAverage coAverage = new AdvancedAverage();
		
		int iiComputedAverage;
		int piComputedAverage;
		double cpiComputedAverage;
		double coComputedAverage;
		
		int size = this.getRischiCount();
		
		double[] iiArray = new double[size];
		double[] piArray = new double[size];
		double[] coArray = new double[size];
		double[] cpiArray = new double[size];
		double[] weightsArray = new double[size];
		
		int index = 0;
		for(Map.Entry<RetrievalResult, List<Rischio>> entry : sortInfo.entrySet()) {
			Progetto rrProj = (Progetto)entry.getKey().get_case().getDescription();
			Progetto qProj 	= (query == null? null : (Progetto)query.getDescription());
			
			for(Rischio r: entry.getValue()) {
				iiArray[index] = r.getImpattoIniziale();
				piArray[index] = r.getProbabilitaIniziale();
				
				if(query == null) {
					cpiArray[index] = r.getCostoPotenzialeImpatto();
					coArray[index] 	= r.getContingency();
				} else if(qProj.getValoreEconomico() > 0) {
					Double directProportion = qProj.getValoreEconomico() / rrProj.getValoreEconomico();
					cpiArray[index] = r.getCostoPotenzialeImpatto() * directProportion;
					coArray[index] 	= r.getContingency() 			* directProportion;
				}
				
				weightsArray[index++] = entry.getKey().getEval();
			}
		}
		
		
		iiComputedAverage = new Long(Math.round(iiAverage.computeSimilarity(iiArray, weightsArray, size))).intValue();
		piComputedAverage = new Long(Math.round(piAverage.computeSimilarity(piArray, weightsArray, size))).intValue();
		cpiComputedAverage = cpiAverage.computeSimilarity(cpiArray, weightsArray, size);
		coComputedAverage = coAverage.computeSimilarity(coArray, weightsArray, size);
		rischio.setImpattoIniziale(iiComputedAverage);
		rischio.setProbabilitaIniziale(piComputedAverage);
		rischio.setContingency(coComputedAverage);
		rischio.setCostoPotenzialeImpatto(cpiComputedAverage);
		return this;
	}

}
