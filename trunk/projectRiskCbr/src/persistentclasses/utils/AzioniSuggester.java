package persistentclasses.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jcolibri.method.retrieve.RetrievalResult;
import jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage;

import persistentclasses.Azioni;
import persistentclasses.Progetto;
import persistentclasses.Rischio;
import persistentclasses.attributes.AzioniPrimaryKey;

/**
 * AzioniSuggester allows to suggest a list of Azioni for a Rischio trough its RischioSuggester.
 * @author Alessio Rocchi
 */
public class AzioniSuggester implements Comparable<AzioniSuggester> {
	Integer azioniType;
	String  azioniDescription;
	
	char	actionType;
	String	actionStatus;
	
	Boolean perfectMatch;
	
	Map<RetrievalResult, List<Azioni>> sortInfo;
	
	public AzioniSuggester(Integer azioniType) {
		this.azioniType = azioniType;
		sortInfo = new HashMap<RetrievalResult, List<Azioni>>();
		this.perfectMatch = false;
	}
	
	private AzioniSuggester addRR(RetrievalResult rr, Azioni a) {
		List<Azioni> occurrences = null;
		if((occurrences = sortInfo.get(rr)) == null) {
			occurrences = new LinkedList<Azioni>();
			sortInfo.put(rr, occurrences);

			this.actionType = a.getPrimaryKey().getTipo();
			this.actionStatus = a.getPrimaryKey().getDefaultStato();
		}
		occurrences.add(a);
		return this;
	}
	
	
	/**
	 * eval has two different meanings; it can be a measure of occurrences
	 * of the specified action, weighted with the rank of the project from which
	 * the action comes from;
	 * but in case of a perfect matcher, eval takes into account occurrence
	 * and project rank, but also impact.
	 * That is, if we take action A from a highly ranked project, 
	 * @return
	 */
	public Double getTotalEval() {
		Double eval = 0.0;
		if(!this.isPerfectMatcher() || this.actionType == 'R') {
			for(Map.Entry<RetrievalResult, List<Azioni>> entry : sortInfo.entrySet()) {
				eval += entry.getKey().getEval() * entry.getValue().size();
			}
		} else {
			for(Map.Entry<RetrievalResult, List<Azioni>> entry : sortInfo.entrySet()) {
				for(Azioni azione : entry.getValue()) {
					if(	azione.getIntensita() <= 10 &&
						azione.getIntensita() >= -10)
					eval += entry.getKey().getEval() * azione.getIntensita();
				}
			}			
		}
		
		return eval;
	}
	
	public List<Azioni> getAzioni() {
		List<Azioni> azioni = new LinkedList<Azioni>();
		for(List<Azioni> lr : sortInfo.values()) {
			azioni.addAll(lr);
		}
		return azioni;
	}
	
	public int getAzioniCount() {
		int rischiCount = 0;
		for(Map.Entry<RetrievalResult, List<Azioni>> entry : sortInfo.entrySet()) {
			rischiCount += entry.getValue().size();
		}
		return rischiCount;
	}
	
	private Boolean isPerfectMatcher() {
		return this.perfectMatch;
	}
	
	private AzioniSuggester setPerfectMatch(Boolean perfectMatch) {
		this.perfectMatch = perfectMatch;
		return this;
	}
	
	/**
	 * compares two AzioniSuggester.
	 * AzioniSuggesters that contain matching actions have a greater ranking than those
	 * who do not contain matching actions.
	 * Occurrences are taken into account; and impact is be considered for actions
	 * in matching AzioniSuggesters, as getTotalEval returns a weighted sum of all impacts.
	 * 
	 * One a is less than b if a contains perfect matches (closed mitigation actions with positive trend),
	 * or if it contains imperfect matches (open mitigation actions) but with greater occurrences count
	 */
	@Override
	public int compareTo(AzioniSuggester compared) {

		if(	(this.isPerfectMatcher() && !compared.isPerfectMatcher()) ||
				((!this.isPerfectMatcher() && !compared.isPerfectMatcher()) &&
				this.getTotalEval() > compared.getTotalEval()))
			return -1;
		if(	(!this.isPerfectMatcher() && compared.isPerfectMatcher()) ||
				((!this.isPerfectMatcher() && !compared.isPerfectMatcher()) &&
				this.getTotalEval() < compared.getTotalEval()))
			return 1;
		
		return 0;
	}
	
	/**
	 * Suggests azioni from a collection of rischi.
	 * First we select all actions from a certain risk across all the projects the risk appears in: if they are mitigation actions 
	 * they will get filtered according to their impatto.
	 * Then we get all open mitigation actions, and suggest them if they are found across projects multiple times.
	 * @param rischio the RischioSuggester that specifies the risk type for which we wanto to get a list of suggested Azioni
	 * @param rischi a Collection<RischioSuggester> that tells us occurrences of Azioni across numerours rischi 
	 * @return a List<AzioniSuggester>. Each RischioSuggester represent an Azioni type.
	 */	
	public static List<AzioniSuggester> getSuggesters(RischioSuggester rischio, Collection<RischioSuggester> rischi) {
		return getTopKSuggesters(rischio, rischi, null);
	}
	
	/**
	 * Suggests risks from a RetrievalResult collection of cases
	 * @param cases a Collection<RetrievalResult> where RetrievalResult description is of type Progetto 
	 * @param k the number of suggested risks we want to get. Can be null
	 * @return a List<RischioSuggester>. Each RischioSuggester represent a risk type.
	 */
	public static List<AzioniSuggester> getTopKSuggesters(RischioSuggester rischio, Collection<RischioSuggester> rischi, Integer k) {
		Map<Integer, AzioniSuggester> azioniByType = new HashMap<Integer, AzioniSuggester>();
		
		// get actions from selected risk. Matching actions are labeled
		for(Map.Entry<RetrievalResult, List<Azioni>> entry : rischio.getAzioni().entrySet()) {
			RetrievalResult rr = entry.getKey();
			for(Azioni azione: entry.getValue()) {
				Integer azioneType = azione.getPrimaryKey().getIdAzione();
				AzioniSuggester azioniSuggester = null;
				// label matching actions
				if(	azione.getPrimaryKey().getTipo() == 'M' ||
					(azione.getPrimaryKey().getTipo() == 'R' &&
						(azione.getStato().equals("Closed")))) {
					if((azioniSuggester = azioniByType.get(azioneType)) == null) {
						azioniSuggester = new AzioniSuggester(azioneType);
						azioniByType.put(azioneType, azioniSuggester);
					}
					azioniSuggester.addRR(rr, azione);
					
					// label matching actions
					if(	azione.getPrimaryKey().getTipo() == 'M' ||
						(azione.getPrimaryKey().getTipo() == 'R' &&
							(azione.getStato().equals("Closed") &&
							azione.getIntensita() > 0)))
						azioniSuggester.setPerfectMatch(true);
				}
			}
		}
		
		// removing all azioniSuggesters with closed actions that had a negative impact
		List<Integer> toRemove = new LinkedList<Integer>();
		for(Map.Entry<Integer, AzioniSuggester> entry : azioniByType.entrySet()) {
			if(!entry.getValue().isPerfectMatcher())
				toRemove.add(entry.getKey());
		}
		for(Integer key : toRemove)
			azioniByType.remove(key);
		
		// get all actions from all other risks
		for(RischioSuggester rischioSuggester : rischi) {
			if(rischioSuggester != rischio) {
				for(Map.Entry<RetrievalResult, List<Azioni>> entry : rischioSuggester.getAzioni().entrySet()) {
					RetrievalResult rr = entry.getKey();
					for(Azioni azione: entry.getValue()) {
						Integer azioneType = azione.getPrimaryKey().getIdAzione();
						AzioniSuggester azioniSuggester = null;
						if((azioniSuggester = azioniByType.get(azioneType)) == null) {
							azioniSuggester = new AzioniSuggester(azioneType);
							azioniByType.put(azioneType, azioniSuggester);
						}
						azioniSuggester.addRR(rr, azione);
					}
				}
			}
		}
		
		
		Collection<AzioniSuggester> azioniSortInfo = azioniByType.values();
		List<AzioniSuggester> azioniSortable = new LinkedList<AzioniSuggester>();
		azioniSortable.addAll(azioniSortInfo);
		Collections.sort(azioniSortable);
		
		if(k == null)
			k = azioniSortable.size();
		else
			k = Math.min(k, azioniSortable.size());
		
		List<AzioniSuggester> topKAzioniSorted = azioniSortable.subList(0, k);
		
		return topKAzioniSorted;
	}
	
	/**
	 * @return a suggestion for selected risk
	 */
	public Azioni getSuggestion() {
		Azioni suggestedAction = new Azioni();
		
		//TODO la descrizione andrebbe presa dalla checklist
		// suggestedAction.setDescrizione(this.azioneDescription);
		
		suggestedAction.setPrimaryKey(new AzioniPrimaryKey());
		suggestedAction.getPrimaryKey().setTipo(this.actionType);
		suggestedAction.setStato(this.actionStatus);
		
		//TODO i valori di intensità dovrebbero essere calcolati dal rischio
		this.adapt(suggestedAction);
		
		return suggestedAction;		
	}
	
	/**
	 * Adapts an Azioni instance by calculating weighted sum of Intensita
	 * @param azione the Azioni instance we want to adapt
	 * @return this
	 */
	private AzioniSuggester adapt(Azioni azione) {		
		AdvancedAverage iAverage = new AdvancedAverage();
		
		int iComputedAverage;
		int size = this.getAzioniCount();
		
		double[] iArray = new double[size];
		double[] weightsArray = new double[size];
		
		int index = 0;
		for(Map.Entry<RetrievalResult, List<Azioni>> entry : sortInfo.entrySet()) {
			for(Azioni a: entry.getValue()) {
				int intensita = a.getIntensita();
				if(intensita >= -10 && intensita <= 10) {
					iArray[index] = intensita;
					weightsArray[index++] = entry.getKey().getEval();
				}
			}
		}
		size = index;
		
		
		iComputedAverage = new Long(Math.round(iAverage.computeSimilarity(iArray, weightsArray, size))).intValue();
		azione.setIntensita(iComputedAverage);

		return this;
	}
}
