package persistentclasses.attributes;

import java.util.List;

/**
 * 
 */
public class StatoRischio {

    private String stato;

    public StatoRischio(){}

    public StatoRischio(String x){
        stato = x;
    }
    public String getStato() {
        return stato;
    }
    public StatoRischio setStato(String s){            
        stato = s;
        return this;
    }

    @Override
    public String toString(){
        return stato;
    }
}
