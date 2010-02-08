/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;

/**
 * XXX considerare di poter salvare le categorie in DB per poterne aggiungere altre in futuro da interfaccia
 * @author narduz
 */
public class CategoriaRischio {
    String categoria;

    public CategoriaRischio(){}

    public CategoriaRischio(String s)
    {
        //XXX riscrivere per effettuare controlli
        categoria = s;
    }

    public String getCategoria(){
        return categoria;
    }

    public CategoriaRischio setCategoria(String s){
        //controllo appartenenza a categorie note
        /*
        if(!s.contentEquals("Design & Development") &&
           !s.contentEquals("Industrial Engineering")&&
           !s.contentEquals("Supply Chain")&&
           !s.contentEquals("Manufacturing")&&
           !s.contentEquals("Program Management")&&
           !s.contentEquals("External Constraints")&&
           !s.contentEquals("Supply Chain")&&
           !s.contentEquals("Delivery"))
            throw new Exception("Tipo di stato del rischio errato");
         */
        categoria = new String(s);
        return this;
    }

    public String toString(){
        return categoria;
    }
}
