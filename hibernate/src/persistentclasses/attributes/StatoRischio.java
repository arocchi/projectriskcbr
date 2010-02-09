/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;

import java.util.List;

/**
 *
 * @author narduz
 */
public class StatoRischio {
    /**
     * Valori concessi:
     * -mitigazione
     * -monitoraggio
     * -chiuso
     */
    private String stato;

    public StatoRischio(){}

    public StatoRischio(String x){
        stato = x;
    }
    public String getStato() {
        return stato;
    }
    public StatoRischio setStato(String s){
        //controllo dei tipi consentiti
        if(!s.contentEquals("Mitigation") &&
           !s.contentEquals("Monitoring")&&
           !s.contentEquals("Closed"))
            return this;
            
        stato = s;
        return this;
    }

    @Override
    public String toString(){
        return stato;
    }
}
