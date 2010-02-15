package persistentclasses.attributes;

import jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;

/**
 * La classe descrive i livelli di rischio per un progetto
 */
public class LivelloDiRischio 
		implements projectriskcbr.config.SelfNNConfigurator {
	public static final Integer RANGE = 5;
	
    private int R1;
    private int R2;
    private int R3;

    public LivelloDiRischio(int a, int b ,int c){

        R1 = a;
        R2 = b;
        R3 = c;
    }

    //default constructor
    public LivelloDiRischio(){
        //di default sono impostati a -1 (campo non valido)
        R1 = -1;
        R2 = -1;
        R3 = -1;
    }

    public void setR1(int x){
        R1 = x;
    }
    public int getR1(){
        return R1;
    }

    public void setR2(int x){
        R2 = x;
    }
    public int getR2(){
        return R2;
    }

    public void setR3(int x){
        R3 = x;
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
}
