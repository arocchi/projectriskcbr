package persistentclasses.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jcolibri.method.retrieve.RetrievalResult;

import persistentclasses.Progetto;
import persistentclasses.Rischio;

/**
 * RischioSortInfo allows to quickly sort Rischio instances according to
 * the RetrievalResults evals of Progetto instances on which the Rischio is present.
 * @author Alessio Rocchi
 */
public class RischioSortInfo implements Comparable<RischioSortInfo> {
	Integer riskType;
	Map<RetrievalResult, Integer> sortInfo;
	
	public RischioSortInfo(Integer riskType) {
		this.riskType = riskType;
		sortInfo = new HashMap<RetrievalResult, Integer>();
	}
	
	public RischioSortInfo addRR(RetrievalResult rr) {
		Integer occurrences = null;
		if((occurrences = sortInfo.get(rr)) != null)
			sortInfo.put(rr, occurrences + 1);
		else
			sortInfo.put(rr, 1);
		
		return this;
	}
	
	public Double getTotalEval() {
		Double eval = 0.0;
		for(Map.Entry<RetrievalResult, Integer> entry : sortInfo.entrySet()) {
			eval += entry.getKey().getEval() * entry.getValue();
		}
		
		return eval;
	}
	
	public List<Rischio> getRischi() {
		List<Rischio> rischi = new LinkedList<Rischio>();
		for(RetrievalResult rr : sortInfo.keySet()) {
			Progetto progetto = (Progetto)rr.get_case().getDescription();
			List<Rischio> rischiProgetto = progetto.getRischi();
			for(Rischio rischio : rischiProgetto) {
				if(this.riskType.equals(rischio.getCodiceChecklist()))
					rischi.add(rischio);
			}
		}
		return rischi;
	}
	
	@Override
	public int compareTo(RischioSortInfo compared) {
		Double thisTotalEval = this.getTotalEval();
		Double comparedTotalEval = compared.getTotalEval();
		if(thisTotalEval < comparedTotalEval)
			return -1;
		else if(thisTotalEval > comparedTotalEval)
			return 1;
		else return 0;
	}
	
	public static List<RischioSortInfo> getTopKRischio(Collection<RetrievalResult> cases, Integer k) {
		Map<Integer, RischioSortInfo> rischiByType = new HashMap<Integer, RischioSortInfo>();
		List<Rischio> rischi = new LinkedList<Rischio>();
		for(RetrievalResult rr : cases) {
			List rrRischi = ((Progetto)rr.get_case().getDescription()).getRischi();
			rischi.addAll(rrRischi);
		}
		
		for(Rischio rischio : rischi) {
			RischioSortInfo rischioSortInfo;
			Integer rischioType = rischio.getCodiceChecklist();
			if((rischioSortInfo = rischiByType.get(rischioType)) == null) {
				rischiByType.put(rischioType, new RischioSortInfo(rischioType));
			}
		}
		
		for(RetrievalResult rr : cases) {
			List<Rischio> rrRischi = ((Progetto)rr.get_case().getDescription()).getRischi();
			for(Rischio rischio : rrRischi) {
				RischioSortInfo rischioSortInfo = rischiByType.get(rischio.getCodiceChecklist()); 
				rischioSortInfo.addRR(rr);
			}
		}
		
		Collection<RischioSortInfo> rischiSortInfo = rischiByType.values();
		List<RischioSortInfo> rischiSortable = new LinkedList<RischioSortInfo>();
		rischiSortable.addAll(rischiSortInfo);
		Collections.sort(rischiSortable);
		
		if(k == null)
			k = rischiSortable.size();
		else
			k = Math.min(k, rischiSortable.size());
		
		List<RischioSortInfo> topKRischiSorted = rischiSortable.subList(0, k);
		
		return topKRischiSorted;
	}

}
