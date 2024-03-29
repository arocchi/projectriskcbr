package persistentclasses;

import persistentclasses.attributes.AzioniPrimaryKey;
import java.util.Iterator;
import java.util.List;

/**
 * Classe che rappresenta la singola azione sul DB.
 */
public class Azioni extends persistentBase{
	public static String CLOSED = "Closed";
	public static String PLANNED = "Planned";
	public static String BACKUP = "Back-up";
	
    private AzioniPrimaryKey    primaryKey;//idRischio + idAzione + tipo

    private String      stato;//Planned, Closed, Back-up, In progress
    private String      descrizione;
    private int         revisione;//revisione a cui si riferisce l'azione
    private int         intensita;//da -10  10, effetto dell'azione (solo per azioni chiuse, quindi presumibile default 0)

    //costruttore di default
    public Azioni()
    {
        //non esistono valori di default per la chiave primaria.
        //vanno settati necessariamente
        primaryKey = new AzioniPrimaryKey();
        stato = "Not specified";
        descrizione = "not specified";
        revisione = 0;
        intensita = -50; //valore fuori range, perchè questo campo è valido solo per azioni chiuse
    }

    //setters e getters
    public Azioni setPrimaryKey(AzioniPrimaryKey x)
    {
        primaryKey = x;
        return this;
    }

    public Azioni setStato(String x){
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
            intensita=x;
        return this;
    }
     
    public Azioni unsetIntensita() {
    	intensita = -50;
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
     
     public boolean hasIntensita() {
    	 return (intensita >= -10 && intensita <= 10);
     }
     
     public boolean isClosed() {
    	 if(this.stato.equals(CLOSED)) return true;
    	 return false;
     }
     
     public boolean isPlanned() {
    	 if(this.stato.equals(PLANNED)) return true;
    	 return false;
     }
     
     public boolean isBackUp() {
    	 if(this.stato.equals(BACKUP)) return true;
    	 return false;
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
      * Funzione che accede al DataBase, e legge tutte le chiavi primarie
      * delle azioni presenti.
      * Serve come utilita' per altre funzioni.
      * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
      *
      * @return Lista di tutte le chiavi primarie di Azioni, contenute ne DB
      * @throws Exception
      */
     public static List getAllPrimaryKeys() throws Exception
     {
         String queryString = new String("select primaryKey from Azioni");
         return persistentBase.executeQuery(queryString);
     }

    /**
     * Controlla che la chiave imessa sia disponibile.
     * Utile nel caso in cui alcuni parametri siano decisi dall'utente
     * anziche' generati automaticamente.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param key   chiave di un oggetto Azioni
     * @return      false: chiave gia' in uso per un'altra azione. true: chiave disponibile
     * @throws Exception
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
    public Azioni generaClone(){
        Azioni a = this;
        Azioni add = new Azioni();
        AzioniPrimaryKey pk = new AzioniPrimaryKey(a.getPrimaryKey().getIdentifier(),
                                                a.getPrimaryKey().getIdAzione(),
                                                a.getPrimaryKey().getIdRischio(),
                                                a.getPrimaryKey().getTipo());
        add.setPrimaryKey(pk);
        add.setDescrizione(a.getDescrizione());
        add.setIntensita(a.getIntensita());
        add.setRevisione(a.getRevisione());
        add.setStato(a.getStato());
        return add;
    }

    /**
     * Interfaccia per la classe precedente.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * Anziche' immettere un oggetto AzioniPrimaryKey,
     * se ne specificano i singoli campi:
     *
     * @param idAzione  campo della chiave
     * @param idRischio campo della chiave
     * @param tipo      campo della chiave
     * @return          false: chiave occupata; true: chiave libera
     * @throws Exception
     */
    public static boolean checkAvailable(int identifier, int idAzione, String idRischio, char tipo) throws Exception
    {
        AzioniPrimaryKey pk = new AzioniPrimaryKey();
        pk.setIdentifier(identifier);
        pk.setIdAzione(idAzione);
        pk.setIdRischio(idRischio);
        pk.setTipo(tipo);
        return checkAvailable(pk);
    }
}
