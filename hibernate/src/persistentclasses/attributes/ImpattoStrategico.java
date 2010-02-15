/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;

/**
 *  La classe descrive gli attributi inerenti l'impatto strategico di un progetto
 */
public class ImpattoStrategico {
	public static final Integer RANGE = 5;
	
    private int value;

    //costruttori
    public ImpattoStrategico(){
        //di default il valore e' -1 (campo non valido)
        value = -1;
    }

    public ImpattoStrategico(int x) {
        value = x;
    }


    //setters e getters
    public void setValue(int x){
        value = x;
    }

    public int getValue(){
        return value;
    }

    //conversione a stringa
    @Override
    public String toString(){
        String s = new String(""+value);
        return s;
    }
}
