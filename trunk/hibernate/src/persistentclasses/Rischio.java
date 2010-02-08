/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses;
import persistentclasses.attributes.AzioniPrimaryKey;
import persistentclasses.attributes.CategoriaRischio;
import persistentclasses.attributes.RevisionePrimaryKey;
import persistentclasses.attributes.StatoRischio;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author narduz
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

    //in tabella a parte
    private String              causa;
    private String              effetto;

    //campi aggiunti
    private double   costoPotenzialeImpatto;//da specifica

    //lista di azioni per il rischio.
    private List azioni;

    //storico per questo determinato rischio. Se null, il rischio non ha subito modifiche
    private List storico;

    public Rischio()
    {
        azioni = new LinkedList();
        storico = new LinkedList();
    }

    //usato per testing
    public Rischio( String a,
                    String b,
                    int c,
                    String d,
                    String e,
                    int f,
                    int g,
                    String h,
                    double i,
                    int l,
                    int m,
                    String n,
                    String o,
                    double p)
    {
    idProgramma = new String(a);
    codice = b;
    codiceChecklist = c;
    stato = new StatoRischio().setStato(d);
    categoria = new CategoriaRischio().setCategoria(e);
    verificato = f;
    numeroRevisione = g;
    descrizione = h;
    contingency = i;
    probabilitaIniziale = l;
    impattoIniziale = m;
    causa = n;
    effetto = o;
    costoPotenzialeImpatto = p;
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
    public Rischio setCategoria(CategoriaRischio x){
        categoria = x;
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


    public static List getAllPrimaryKeys() throws Exception
    {
         String queryString = new String("select codice from Rischio");
         return executeQuery(queryString);
    }
    /**
     * Controlla se la chiave immessa come argomento è disponibile o meno
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
     * "azioni"
     */
    public Rischio caricaAzioni() throws Exception
    {
        azioni = executeQuery("from Azioni where primaryKey.idRischio = '" + codice + "'");
        return this;
    }

    /**
     *
     * Aggiunge l'azione 'a' al rischio, col numero di revisione rev
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
        //XXX se non trovato, mando eccezione
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
     */
    public Rischio rimuoviAzione(String idR, int idA, char t) throws Exception
    {
        rimuoviAzione(new AzioniPrimaryKey(idA, idR, t));
        return this;
    }
    
    /**
     * Salva le azioni nel DB
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
     */
    public Rischio caricaStorico() throws Exception
    {
        storico = executeQuery("from Revisione where key.idRischio = '" + codice + "'");
        return this;
    }

    /**
     * aggiunge una revisione allo storico
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
     */
    public Rischio aggiungiRevisione(String idRischio, int numeroRevisione, int probabilita, int indiceImpatto) throws Exception
    {
        Revisione rv = new Revisione();
        rv.setIdRischio(idRischio);
        rv.setNumeroRevisione(numeroRevisione);
        rv.setIndiceImpatto(indiceImpatto);
        rv.setProbabilita(probabilita);
        aggiungiRevisione(rv);
        return this;
    }

    /**
     * Rimuove la revisione identificata dalla chiave key dal DB
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
        //XXX se non trovato, mando eccezione
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
     */
    public Rischio rimuoviRevisione(String idR, int numRev) throws Exception
    {
        rimuoviRevisione(new RevisionePrimaryKey(idR, numRev));
        return this;
    }
    
   /**
    * Salva lo sotrico nel DB
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
     * @function public int getIr()
     * calcola indice del rischio come
     * INDICE probabilità * INDICE impatto
     *
     * calcola l'IR per l'ultima revisione
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
     *
     */
    public double getImpattoAtteso(){
        return costoPotenzialeImpatto*probabilitaIniziale;
    }

    //tutte le azioni della revisione rev
    /*
    public List getAzioni(int numRev){
        //XXX serve??
        return null;
    }*/
    
    /**
     * ritorna tutte le azioni chiuse. Può ritornare lista vuota
     */
    public List getAzioniChiuse(){
        return azioniStato(true);
    }

    /**
     * ritorna tutte le azioni aperte. Può ritornare lista vuota
     */
    public List getAzioniAperte(){
        return azioniStato(false);
    }

    /**
     * Funzione di utilità per getAzioniAperte() e getAzioniChiuse()
     * b = true -> preleva le azioni chiuse
     * b = false -> preleva le azioni aperte
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