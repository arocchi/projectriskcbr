/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;

/**
 *
 * @author narduz
 */
public class LivelloDiRischio {

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
        R1 = 0;
        R2 = 0;
        R3 = 0;
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
}
