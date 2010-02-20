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
import persistentclasses.attributes.AzioniPrimaryKey;

/**
 * AzioniSuggester allows to suggest a list of Azioni for a Rischio trough its RischioSuggester.
 * @author Alessio Rocchi
 */
public class AzioniSuggester implements Comparable<AzioniSuggester> {
	Integer actionId;
	String  actionDescription;
	
	char	actionType;

	String	actionStatus;
	
	Boolean perfectMatch;
	
	Map<RetrievalResult, List<Azioni>> sortInfo;
	
	public AzioniSuggester(Integer actionId) {
		this.actionId = actionId;
		sortInfo = new HashMap<RetrievalResult, List<Azioni>>();
		this.perfectMatch = false;
	}
	
	public Integer getActionId() {
		return actionId;
	}
	
	private AzioniSuggester addRR(RetrievalResult rr, Azioni a) {
		List<Azioni> occurrences = null;
		if((occurrences = sortInfo.get(rr)) == null) {
			occurrences = new LinkedList<Azioni>();
			sortInfo.put(rr, occurrences);

			this.actionType = a.getPrimaryKey().getTipo();
			this.actionStatus = a.getPrimaryKey().getDefaultStato();
			// TODO dove sono le informazioni della checklist?
			this.actionDescription = a.getDescrizione();
		}
		occurrences.add(a);
		return this;
	}
	
	
	/**
	 * eval has two different meanings; it can be a measure of occurrences
	 * of the specified action, weighted with the rank of the project from which
	 * the action comes from;
	 * but in case of a perfect matcher, eval takes into account occurrences
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
					if(	azione.hasIntensita())
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
	
	public Boolean isPerfectMatcher() {
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
	 * Between perfect matchers, actions of type mitigation always score higher than those of type recovery. 
	 * 
	 * One a is less than b if a contains perfect matches (closed mitigation actions with positive trend),
	 * or if it contains imperfect matches (open mitigation actions) but with greater occurrences count
	 */
	@Override
	public int compareTo(AzioniSuggester compared) {
		if(this.isPerfectMatcher() && compared.isPerfectMatcher() &&
			(this.actionType != compared.actionType)) {
			if(this.actionType == 'M')
				return -1;
			else
				return 1;
		}
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
		
		/*
		 *  adding all actions from selected risk which were either
		 *  Mitigations actions with at least one closed action instance, or
		 *  Recovery actions. 
		 */
		for(Map.Entry<RetrievalResult, List<Azioni>> entry : rischio.getAzioni().entrySet()) {
			RetrievalResult rr = entry.getKey();
			for(Azioni azione: entry.getValue()) {
				Integer azioneType = azione.getPrimaryKey().getIdAzione();
				AzioniSuggester azioniSuggester = null;
				// label matching actions
				if(	azione.getPrimaryKey().getTipo() == 'R' ||
					(azione.getPrimaryKey().getTipo() == 'M' &&
						(azione.getStato().equals("Closed")))) {
					if((azioniSuggester = azioniByType.get(azioneType)) == null) {
						azioniSuggester = new AzioniSuggester(azioneType);
						azioniByType.put(azioneType, azioniSuggester);
					}
					azioniSuggester.addRR(rr, azione);
				}
			}
		}
		
		// adding all actions from selected risk which were never Closed
		Map<Integer, AzioniSuggester> otherAzioniByType = new HashMap<Integer, AzioniSuggester>();
		for(Map.Entry<RetrievalResult, List<Azioni>> entry : rischio.getAzioni().entrySet()) {
			RetrievalResult rr = entry.getKey();
			for(Azioni azione: entry.getValue()) {
				Integer azioneType = azione.getPrimaryKey().getIdAzione();
				AzioniSuggester azioniSuggester = null;
				/*
				 * adding all non-closed actions which were not added before.
				 * This means that we already considered mitigation actions 
				 * for which we had both closed and planned instances 
				 */
				if(	azione.getPrimaryKey().getTipo() == 'M' &&
					(!azione.getStato().equals("Closed"))) {
					/* if azioniByType contains this azione, it means that
					 * the selected risk contains at least two mitigation actions of the same type,
					 * one of which is Closed, while the other is Planned.
					 * In this case, we discard the planned one as we have a better estimate
					 * of the action trough the Intesita value that we got from its application results
					 */
					if((azioniSuggester = azioniByType.get(azioneType)) == null) {
						if((azioniSuggester = otherAzioniByType.get(azioneType)) == null) {
							azioniSuggester = new AzioniSuggester(azioneType);
							otherAzioniByType.put(azioneType, azioniSuggester);
						}
					}
					azioniSuggester.addRR(rr, azione);
				}
			}
		}
		azioniByType.putAll(otherAzioniByType);
		
		// removing all azioniSuggesters with closed actions that had an overall negative impact
		List<Integer> toRemove = new LinkedList<Integer>();
		for(Map.Entry<Integer, AzioniSuggester> entry : azioniByType.entrySet()) {
			int expectedIntesity = entry.getValue().getSuggestion().getIntensita();
			// the overall impact of the mitigation actions was negative
			if(	entry.getValue().actionType == 'M' &&
				(expectedIntesity < 0 && expectedIntesity >= -10))
				toRemove.add(entry.getKey());
			/*
			 *  none of the closed mitigation actions did not have an intensity value set,
			 *  or this mitigation actions did not have closed instances
			 */
			else if(entry.getValue().actionType == 'M' &&
					expectedIntesity < -10 || expectedIntesity > 10)
				;	
			/*
			 * label both Recovery actions coming from selected risk 
			 * and Mitigation actions which had an overall positive impact on risk
			 * as perfect matchers.
			 */
			else if(entry.getValue().actionType == 'R' ||
					(entry.getValue().actionType == 'M' &&
					expectedIntesity >= 0 && expectedIntesity <= 10))
				entry.getValue().setPerfectMatch(true);
 
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
		return getSuggestion(true);
	}
	

	/**
	 * @param adaptImpact adapt Intensita for suggested risk
	 * @return an action suggestion for selected risk
	 */
	public Azioni getSuggestion(boolean adaptImpact) {
		Azioni suggestedAction = new Azioni();
		
		suggestedAction.setDescrizione(this.actionDescription);
		
		suggestedAction.setPrimaryKey(new AzioniPrimaryKey());
		suggestedAction.getPrimaryKey().setTipo(this.actionType);
		suggestedAction.setStato(this.actionStatus);
		
		if(adaptImpact == true) {
			//TODO i valori di intensità dovrebbero essere calcolati dal rischio
			this.adapt(suggestedAction);
		}
		
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
				if(a.hasIntensita()) {
				int intensita = a.getIntensita();
					iArray[index] = intensita;
					weightsArray[index++] = entry.getKey().getEval();
				}
			}
		}
		size = index;

		// there are no stored impact informations for this suggester actions
		if(size != 0) {
			iComputedAverage = new Long(Math.round(iAverage.computeSimilarity(iArray, weightsArray, size))).intValue();
			azione.setIntensita(iComputedAverage);
		}

		return this;
	}
}
