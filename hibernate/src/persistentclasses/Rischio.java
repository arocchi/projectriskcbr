package persistentclasses;

import persistentclasses.attributes.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Classe che rappresenta i rischi nel database.
 */
public class Rischio extends persistentBase{

    //campi forniti
    private String              idProgramma;
    private String              codice;//id Rischio nel DB
    private int                 codiceChecklist;
    private StatoRischio        stato;
    private CategoriaRischio    categoria; //tabella di categorie
    private int                 verificato;//r_ver in DB, vale 0,1,2
    private int                 numeroRevisione;//revisione attuale del singolo rischio
    private String              descrizione;
    private double              contingency;
    private int                 probabilitaIniziale;
    private int                 impattoIniziale;//impatto

    private String              causa;
    private String              effetto;

    //campi aggiunti
    private double   costoPotenzialeImpatto;//da specifica

    //lista di azioni per il rischio.
    private List azioni;

    //storico per questo determinato rischio. Se null, il rischio non ha subito modifiche
    private List storico;

    //costruttore di default
    public Rischio()
    {
        //id programma va specificato alla creazione del  rischio
        idProgramma = null;
        //codice viene deciso dall'utente e' presente una funzione
        //generateAutoKey che consente di generarlo automaticamente
        codice = null;
        //codiceChecklist va associato al rischio nel momento in cui e' creato

        stato = new StatoRischio("Mitigation");//a causa di alcuni campi vuoti nel DB formito, diamo un valore di default
        categoria = new CategoriaRischio();
        verificato = 0; //default, un rischio appena creato si intende aperto
        numeroRevisione = 0; //rischio creato di default nella rev 0
        descrizione = new String();
        contingency = -1.0; //se non specificata manualmente, campo non valido
        probabilitaIniziale = -1; //se non specificato manualmente, campo non valido
        impattoIniziale = -1; //se non specificato manualmente, campo non valido

        causa = new String();
        effetto = new String();

        costoPotenzialeImpatto = 100;//il campo non e' fornito da database. Assumiamo un valoredi default
        azioni = new LinkedList();
        storico = new LinkedList();
    }

    //setters e getters
    public String getIdProgramma(){
        return idProgramma;
    }
    public String getCodice(){
        return codice;
    }
    public int getCodiceChecklist(){
        return codiceChecklist;
    }
    public StatoRischio getStato(){
        return stato;
    }
    public CategoriaRischio getCategoria(){
        return categoria;
    }
    public int getVerificato(){
        return verificato;
    }
    public int getNumeroRevisione(){
        return numeroRevisione;
    }
    public String getDescrizione(){
        return descrizione;
    }
    public double getContingency(){
        return contingency;
    }
    public int getProbabilitaIniziale(){
        return probabilitaIniziale;
    }
    public int getImpattoIniziale(){
        return impattoIniziale;
    }
    public String getCausa(){
        return causa;
    }
    public String getEffetto(){
        return effetto;
    }
    public List getStorico()
    {
        return storico;
    }
    public List getAzioni(){
        return azioni;
    }
    
    public Rischio setIdProgramma(String x){
        idProgramma = new String(x);
        return this;
    }
    public Rischio setCodice(String x){
        codice = x;
        return this;
    }
    public Rischio setCodiceChecklist(int x){
        codiceChecklist = x;
        return this;
    }
    public Rischio setStato(StatoRischio x){
        stato = x;
        return this;
    }
    public Rischio setStato(String x){
        stato = new StatoRischio(x);
        return this;
    }
    public Rischio setCategoria(CategoriaRischio x){
        categoria = x;
        return this;
    }
    public Rischio setCategoria(String x){
        categoria.setCategoria(x);
        return this;
    }
    public Rischio setVerificato(int x){
        verificato = x;
        return this;
    }
    public Rischio setNumeroRevisione(int x){
        numeroRevisione = x;
        return this;
    }
    public Rischio setDescrizione(String x){
        descrizione = x;
        return this;
    }
    public Rischio setContingency(double x){
        contingency = x;
        return this;
    }
    public Rischio setProbabilitaIniziale(int x){
        probabilitaIniziale = x;
        return this;
    }
    public Rischio setImpattoIniziale(int x){
        impattoIniziale = x;
        return this;
    }
    public Rischio setCausa(String x){
        causa = x;
        return this;
    }
    public Rischio setEffetto(String x){
        effetto = x;
        return this;
    }
    public Rischio setStorico(List x)
    {
        storico = x;
        return this;
    }
    public Rischio setAzioni(List x)
    {
        azioni = x;
        return this;
    }

    public double getCostoPotenzialeImpatto(){
        return costoPotenzialeImpatto;
    }
    public Rischio setCostoPotenzialeImpatto(double x){
        costoPotenzialeImpatto = x;
        return this;
    }


    /**
     * Ritorna una lista con le chiavi primarie
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return List di chiavi primarie
     * @throws Exception
     */
    public static List getAllPrimaryKeys() throws Exception
    {
         String queryString = new String("select codice from Rischio");
         return executeQuery(queryString);
    }

    /**
     * Controlla se la chiave immessa come argomento è disponibile o meno.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return true se la chiave e' disponibile. false altrimenti
     * @throws Exception
     */
    public static boolean checkAvailable(String key) throws Exception
    {
        List allKeys = getAllPrimaryKeys();
        boolean found;
        found = allKeys.contains(key);
        return !found;
    }

    /**
     * Genera automaticamente una nuova chiave
     * La stringa progetto passata come argomento è
     * l'id del progetto su cui il rischio andrà istanziato
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param progetto  Stringa identificativa del progetto (usata per generare chiavi automatiche per i rischi
     * @return          stringa (chiave generata)
     * @throws Exception
     */
    public static String generateAutoKey(String progetto) throws Exception
    {
        String key = progetto+"R1";
        int i = 1;
        while(!checkAvailable(key))
        {
            i++;
            key = progetto+"R" + i;
        }
        return progetto+"R"+i;
    }
    
    /**
     * Legge dal database la lista delle azioni associate al rischio e aggiorna il campo
     * "azioni".
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return riferimento al rischio (come un setter)
     * @throws Exception
     */
    public Rischio caricaAzioni() throws Exception
    {
        azioni = executeQuery("from Azioni where primaryKey.idRischio = '" + codice + "'");
        return this;
    }

    /**
     * Aggiunge l'azione 'a' al rischio, col numero di revisione rev
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param rev   revisione del rischio alla quale si sta aggiungendo l'azione
     * @param a     azione da aggiungere al rischio
     * @return      puntatore al rischio
     * @throws Exception
     */
    public Rischio aggiungiAzione(int rev, Azioni a) throws Exception
    {
        //controllo che l'id rischio dell'azione sia lo stesso
        //del rischio. Se non lo è, lo modifico opportunamente
        if(a.getPrimaryKey().getIdRischio().compareTo(codice) != 0)
            a.getPrimaryKey().setIdRischio(codice);

        //controllo che non esista un'azione con stessa chiave, già presente nel DB
        if(!Azioni.checkAvailable(a.getPrimaryKey()))
            //se c'è già la stessa chiave, lancio una eccezione
            throw new Exception("rischio " + codice +
                                ", idAzione "+a.getPrimaryKey().getIdAzione()+", tipo " +
                                a.getPrimaryKey().getTipo()+" gia' presente in DB");

        //se tutto va bene aggiungo l'azione
        azioni.add(a);
        return this;
    }

    /**
     * Rimuove dalla lista azioni l'azione con la chiave inserita come argomento
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param key   Chiave primaria dell'azione da rimuovere
     * @return puntatore al rischio
     * @throws Exception
     */
    public Rischio rimuoviAzione(AzioniPrimaryKey key) throws Exception
    {
        Iterator it = azioni.iterator();
        Azioni a = null;
        boolean found = false;
        while(it.hasNext() && !found)
        {
            a = (Azioni) it.next();
            if(a.getPrimaryKey().equals(key))
                found = true;
        }
        //se non trovato, mando eccezione
        if(!found)
        {
            throw new Exception("Azione "+ key  +" inesistente");
        }
        a.delete();
        azioni.remove(a);
        return this;
    }

    /**
     * interfaccia per la classe precedente
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param idR   id rischio
     * @param idA   id azione
     * @param t     tipo azione
     * @return      puntatore al rischio
     * @throws Exception
     */
    public Rischio rimuoviAzione(int id, String idR, int idA, char t) throws Exception
    {
        rimuoviAzione(new AzioniPrimaryKey(id, idA, idR, t));
        return this;
    }
    
    /**
     * Salva le azioni nel DB
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return puntatore al rischio
     * @throws Exception
     */
    public Rischio salvaAzioni() throws Exception
    {
        //controllo quali campi sono stati modificati o aggiunti
        Iterator it = azioni.iterator();
        //confronto db attuale
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Query q = (Query) session.createQuery("from Azioni where primaryKey.idRischio = '" +codice+"'");
        q.setReadOnly(true);
        List l = q.list();
        session.flush();
        session.close();

        while(it.hasNext())
        {
            Azioni a = (Azioni) it.next();
            Iterator i = l.iterator();
            boolean found = false;
            Azioni ck = null;
            while(i.hasNext() && !found)
            {
                ck = (Azioni) i.next();
                if(a.getPrimaryKey().equals(ck.getPrimaryKey()))
                {
                    //oggetto già esistente in DB
                    found = true;
                }
            }
            if(found)
            {
                if(ck.getStato().compareTo(a.getStato()) != 0 ||
                   ck.getDescrizione().compareTo(a.getDescrizione()) != 0 ||
                   ck.getRevisione() != a.getRevisione() ||
                   ck.getIntensita() != a.getIntensita())
                {
                    //devo aggiornare
                    a.update();
                }
                l.remove(ck);
            }
            else
            {
                //devo creare la nuova entry
                a.write();
            }
        }
        return this;
    }

    /**
     * Legge dal database lo storico di questo determinato rischio, aggiornando il campo
     * "storico"
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return puntatore al rischio
     * @throws Exception
     */
    public Rischio caricaStorico() throws Exception
    {
        storico = executeQuery("from Revisione where key.idRischio = '" + codice + "'");
        return this;
    }

    /**
     * aggiunge una revisione allo storico
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param x oggetto revisione da aggiungere
     * @return puntatore al rischio
     * @throws Exception
     */
    public Rischio aggiungiRevisione(Revisione x) throws Exception
    {
        //controllo se è corretto l'id rel rischio
        if(!x.getIdRischio().equals(codice))
            //se diverso (o null) lo sovrascrivo con quello corretto
            x.setIdRischio(codice);
        
        //controllo se esiste già una revisione con lo stesso indice
        Iterator it = storico.iterator();
        boolean found = false;
        while(it.hasNext() && found == false)
        {
            Revisione r = (Revisione) it.next();
            if(r.getNumeroRevisione() == x.getNumeroRevisione())
                found = true;
        }
        if(found == true)
            throw new Exception("Revisione "+x.getNumeroRevisione()+
                                " gia' presente nel rischio "+x.getIdRischio());
        storico.add(x);
        return this;
    }

    /**
     * Interfaccia per la classe precedente
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param numeroRevisione   numero della revosione da aggiungere
     * @param probabilita       probabilita' nella nuov revisione
     * @param indiceImpatto     indiceImpatto nella nuova revisione
     * @return                  puntatore al rischio
     * @throws Exception
     */
    public Rischio aggiungiRevisione(int numeroRevisione, int probabilita, int indiceImpatto) throws Exception
    {
        Revisione rv = new Revisione();
        rv.setIdRischio(codice);
        rv.setNumeroRevisione(numeroRevisione);
        rv.setIndiceImpatto(indiceImpatto);
        rv.setProbabilita(probabilita);
        aggiungiRevisione(rv);
        return this;
    }

    /**
     * Rimuove la revisione identificata dalla chiave key dal DB
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param key       chiave primaria della revisione da rimuovere
     * @return          puntatre al rischio
     * @throws Exception
     */
    public Rischio rimuoviRevisione(RevisionePrimaryKey key) throws Exception
    {
        Iterator it = storico.iterator();
        Revisione r = null;
        boolean found = false;
        while(it.hasNext() && !found)
        {
            r = (Revisione) it.next();
            if(r.getKey().equals(key))
                found = true;
        }
        //se non trovato, mando eccezione
        if(!found)
        {
            throw new Exception("Revisione "+ key  +" inesistente");
        }
        r.delete();
        azioni.remove(r);
        return this;
    }

    /**
     * Interfaccia per la classe precedente
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param numRev    numero revisione da eliminare dal rischio
     * @return
     * @throws Exception
     */
    public Rischio rimuoviRevisione(int numRev) throws Exception
    {
        rimuoviRevisione(new RevisionePrimaryKey(codice, numRev));
        return this;
    }
    
   /**
    * Salva lo sotrico nel DB
    * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
    * @return puntatore al rischio
    * @throws Exception
    */
    public Rischio salvaStorico() throws Exception
    {
        //controllo quali campi sono stati modificati o aggiunti
        Iterator it = storico.iterator();
        //confronto db attuale
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Query q = (Query) session.createQuery("from Revisione where key.idRischio = '" +codice+"'");
        q.setReadOnly(true);
        List l = q.list();
        session.flush();
        session.close();

        while(it.hasNext())
        {
            Revisione r = (Revisione) it.next();
            Iterator i = l.iterator();
            boolean found = false;
            Revisione ck = null;
            while(i.hasNext() && !found)
            {
                ck = (Revisione) i.next();
                if(r.getKey().equals(ck.getKey()))
                {
                    //oggetto già esistente in DB
                    found = true;
                }
            }
            if(found)
            {
                if(ck.getProbabilita() != r.getProbabilita() ||
                     ck.getIndiceImpatto() != r.getIndiceImpatto())
                {
                    //devo aggiornare
                    r.update();
                }
                l.remove(ck);
            }
            else
            {
                //devo creare la nuova entry
                r.write();
            }
        }
        return this;
    }

    /**
     * Calcola l'IR per la revisione passata come argomento
     * Ritorna -1 se la revisione non esiste
     * @param numvrev   numero della revisione di cui prendere Ir
     * @return          intero rappresentante l'Ir
     */
    public int getIr(int numrev)
    {
        if(numrev == 0)
            return probabilitaIniziale * impattoIniziale;

        Iterator it = storico.iterator();
        Revisione r = null;
        boolean found = false;
        while(it.hasNext() && !found)
        {
            r = (Revisione) it.next();
            if(r.getNumeroRevisione() == numrev)
                found = true;
        }
        if(!found)
            return -1;
        
        //se trovato ritorno il valore corretto
        return r.getIr();
    }
    /**
     * calcola indice del rischio come
     * INDICE probabilità * INDICE impatto
     *
     * calcola l'IR per l'ultima revisione
     * @return  IR per l'ultima revisione
     */
    public int getIr(){
        Iterator it = storico.iterator();
        int max = 0;
        while(it.hasNext())
        {
            Revisione r = (Revisione) it.next();
            if(r.getNumeroRevisione() > max)
                max = r.getNumeroRevisione();
        }
        getIr(max);
        return 0;
    }

    /**
     * Da specifica, l'impatto atteso e' costo potenziale * probabilita' iniziale
     * @return  impatto atteso
     */
    public double getImpattoAtteso(){
        return costoPotenzialeImpatto*probabilitaIniziale;
    }

    /**
     * ritorna tutte le azioni chiuse. Può ritornare lista vuota
     * Fa uso della funzione utilita' azioniStato
     * @return Lista di azioni chiuse
     */
    public List getAzioniChiuse(){
        return azioniStato(true);
    }

    /**
     * ritorna tutte le azioni aperte. Può ritornare lista vuota
     * Fa uso della funzione utilita' azioniStato
     * @return Lista di azioni aperte
     */
    public List getAzioniAperte(){
        return azioniStato(false);
    }

    /**
     * Funzione di utilità per getAzioniAperte() e getAzioniChiuse()
     * @param b true -> preleva le azioni chiuse;
     *          false -> preleva le azioni aperte
     * @return  Lista di azioni
     */
    private List azioniStato(boolean b){
        LinkedList l = new LinkedList();
        Iterator it = azioni.iterator();
        while(it.hasNext())
        {
            Azioni a = (Azioni) it.next();
            if(b && a.getStato().compareTo("Closed") == 0 ||
               !b && a.getStato().compareTo("Closed") != 0)
                l.add(a);

        }
        return l;
    }
}