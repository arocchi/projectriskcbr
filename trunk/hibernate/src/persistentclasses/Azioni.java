package persistentclasses;

import persistentclasses.attributes.AzioniPrimaryKey;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class Azioni extends persistentBase{
    private AzioniPrimaryKey    primaryKey;//idRischio + idAzione + tipo

    private String      stato;//Planned, Closed, Back-up, In progress
    private String      descrizione;
    private int         revisione;//revisione a cui si riferisce l'azione
    private int         intensita;//da -10  10, effetto dell'azione (solo per azioni chiuse, quindi presumibile default 0)

    //setters e getters
    public Azioni setPrimaryKey(AzioniPrimaryKey x)
    {
        primaryKey = x;
        return this;
    }

    public Azioni setStato(String x){
        //XXX considerare di rimuovere il controllo
        //se non è uno stato consentito non faccio nulla
        /*if(x.equals("Planned") ||
           x.equals("Closed") ||
           x.equals("Back-up") ||
           x.equals("In progress"))
            stato=x;*/
        stato = x;
        return this;
    }

    public Azioni setDescrizione(String x){
        descrizione=x;
        return this;
    }

    public Azioni setRevisione(int x){
        revisione=x;
        return this;
    }

     public Azioni setIntensita(int x){
        //se non è un valore consentito non faccio nulla
        if(x>=-10 && x<=10)
            revisione=x;
        return this;
    }

    public AzioniPrimaryKey getPrimaryKey()
    {
        return primaryKey;
    }
    
     public String getStato(){
         return stato;
     }

     public String getDescrizione(){
         return descrizione;
     }

     public int getRevisione(){
         return revisione;
     }

     public int getIntensita(){
         return intensita;
     }

     /**
      * Per poter avere chiavi primarie composte da piu' attributi
      * la classe deve implementare il metodo .equals() riscritto
      * affinche' due oggetti siano uguali quando hanno uguale
      * chiave primaria
      */
     public boolean equals(Azioni x)
     {
            if(x instanceof Azioni &&
               x.primaryKey.equals(primaryKey))
                return true;
            return false;
     }

     /**
      *
      * XXX Funzione per debug, mostra la chiave dell'azione
      */
     public String toString()
     {
         return primaryKey.toString();
     }

     public static List getAllPrimaryKeys() throws Exception
     {
         String queryString = new String("select primaryKey from Azioni");
         return persistentBase.executeQuery(queryString);
     }

     /**
     * Controlla se la chiave immessa come argomento è disponibile o meno
     */
    public static boolean checkAvailable(AzioniPrimaryKey key) throws Exception
    {
        List allKeys = getAllPrimaryKeys();
        AzioniPrimaryKey azpk = new AzioniPrimaryKey();
        boolean found = false;
        Iterator it = allKeys.iterator();
        while(it.hasNext() && found == false)
        {
            azpk = (AzioniPrimaryKey) it.next();
            if(azpk.equals(key))
                found = true;
        }
        return !found;
    }

    /**
     * interfaccia per la classe precedente
     */
    public static boolean checkAvailable(int idAzione, String idRischio, char tipo) throws Exception
    {
        AzioniPrimaryKey pk = new AzioniPrimaryKey();
        pk.setIdAzione(idAzione);
        pk.setIdRischio(idRischio);
        pk.setTipo(tipo);
        return checkAvailable(pk);
    }
}
