/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;

/**
 *  La classe descrive gli attributi inerenti l'impatto strategico di un progetto
 *  
 * @author narduz
 */
public class ImpattoStrategico {
    private int value;

    //costruttori
    public ImpattoStrategico(){
        value = 0;
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
    public String toString(){
        String s = new String(""+value);
        return s;
    }
}
