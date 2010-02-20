package persistentclasses.attributes;

import persistentclasses.Progetto;
import jcolibri.cbrcore.Attribute;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;

/**
 * La classe descrive i livelli di rischio per un progetto
 */
public class LivelloDiRischio 
		implements	jcolibri.cbrcore.CaseComponent, 
					projectriskcbr.config.SelfNNConfigurator, 
					projectriskcbr.config.SelfNNTotalSimilarityConfigurator {
	public static final Integer RANGE = 3;
	
    private Integer R1;
    private Integer R2;
    private Integer R3;

    public LivelloDiRischio(int a, int b ,int c){

        R1 = new Integer(a);
        R2 = new Integer(b);
        R3 = new Integer(c);
    }

    //default constructor
    public LivelloDiRischio(){
    	/*
        //di default sono impostati a -1 (campo non valido)
        R1 = -1;
        R2 = -1;
        R3 = -1;
        */
    	
    	R1 = null;
        R2 = null;
        R3 = null;
    }

    public void setR1(int x){
        R1 = new Integer(x);
    }
    public int getR1(){
        return R1;
    }

    public void setR2(int x){
        R2 = new Integer(x);
    }
    public int getR2(){
        return R2;
    }

    public void setR3(int x){
        R3 = new Integer(x);
    }
    public int getR3(){
        return R3;
    }

    @Override
    public String toString(){
        String s = new String(R1 + " " + R2 +" "+ R3);
        return s;
    }

	public Object getSimilarityFunction(String arg0) {
		return new Interval(LivelloDiRischio.RANGE);
	}

	@Override
	public NNConfig getTotalSimilarityConfig(NNConfig simConfig) {
		if(simConfig == null)
			simConfig = new NNConfig();
		
		simConfig.addMapping(new Attribute("R1", this.getClass()), (LocalSimilarityFunction)this.getSimilarityFunction("R1"));
		simConfig.addMapping(new Attribute("R2", this.getClass()), (LocalSimilarityFunction)this.getSimilarityFunction("R2"));
		simConfig.addMapping(new Attribute("R3", this.getClass()), (LocalSimilarityFunction)this.getSimilarityFunction("R3"));

		return simConfig;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("codice", Progetto.class);
	}
}