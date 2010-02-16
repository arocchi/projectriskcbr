package persistentclasses;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jcolibri.cbrcore.Attribute;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage;
import jcolibriext.method.retrieve.NNretrieval.similarity.local.IntervalImpattoStrategico;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import persistentclasses.attributes.ImpattoStrategico;
import persistentclasses.attributes.LivelloDiRischio;

/**
 * Classe che rappresenta un singolo progetto nel database.
 */
public class Progetto 
			extends persistentBase 
			implements jcolibri.cbrcore.CaseComponent, projectriskcbr.config.SelfNNConfigurator, projectriskcbr.config.SelfNNTotalSimilarityConfigurator {
    private boolean isCase; //indica se il progetto va usato come caso per successivi retrieve
    private boolean isOpen; //indica se il progetto e' ancora aperto o è gia' stato chiuso
    
    private String 	codice; //id programma
    private int		reparto;
    private int		classeRischio;
    private Double  valoreEconomico;
    private int 	durataContratto; //anni durata progetto
    private String  oggettoFornitura; //oggetto nel DB
    private String  nomeCliente; //cliente nel DB

    private LivelloDiRischio    paese;
    private LivelloDiRischio    mercatoCliente;
    private LivelloDiRischio    contratto;
    private LivelloDiRischio    composizionePartnership;
    private LivelloDiRischio    ingegneria;
    private LivelloDiRischio    approvvigionamento;
    private LivelloDiRischio    fabbricazione;
    private LivelloDiRischio    montaggio;
    private LivelloDiRischio    avviamento;
    
    private ImpattoStrategico   im;
    private ImpattoStrategico   ic;
    private ImpattoStrategico   ip;
    private ImpattoStrategico   ia;
    private ImpattoStrategico   ipp;

    //dati estrapolati dalla tabella dei rischi
    private List rischi;//lista rischi associati al progetto

    //XXX considerare di rimuovere
    //dati inutilizzati
    private Calendar dataInizio; //unused
    private Calendar dataFine; //unused

    //costruttore di default, necessario per hibernate
    public Progetto()
    {
        rischi = new LinkedList();
        isCase = false; //di default un progetto non e' inserito nella base dei casi
        isOpen = true; //alla creazione si considera che il progetto sia aperto
        //codice va specificato obbligatoriamente
        reparto = -1; //se non specificato il campo e' non valido
        classeRischio = -1; //se non specificato il campo e' non valido
        valoreEconomico = -1.0; //se non specificato il campo e' non valido
        durataContratto = -1; //se non specificato il campo e' non valido
        oggettoFornitura = new String();
        nomeCliente = new String();

        //i valori di default per i restanti campi sono assegnati dai relativi costruttori
        paese = new LivelloDiRischio();
        mercatoCliente = new LivelloDiRischio();
        contratto = new LivelloDiRischio();
        composizionePartnership = new LivelloDiRischio();
        ingegneria = new LivelloDiRischio();
        approvvigionamento = new LivelloDiRischio();
        fabbricazione = new LivelloDiRischio();
        montaggio = new LivelloDiRischio();
        avviamento = new LivelloDiRischio();
        im = new ImpattoStrategico();
        ic = new ImpattoStrategico();
        ip = new ImpattoStrategico();
        ia = new ImpattoStrategico();
        ipp = new ImpattoStrategico();
    }

    //setters e getters
    public Progetto setIsCase(boolean x){
        isCase = x;
        return this;
    }
    public Progetto setIsOpen(boolean x)
    {
        isOpen = x;
        return this;
    }
    public boolean getIsCase(){
        return isCase;
    }
    public boolean getIsOpen(){
        return isOpen;
    }
    public Progetto setCodice(String s){
        codice = s;
        return this;
    }
    public String getCodice(){
        return codice;
    }

    public Progetto setReparto(int x){
        reparto = x;
        return this;
    }
    public int getReparto(){
        return reparto;
    }

    public Progetto setClasseRischio(int x){
        classeRischio = x;
        return this;
    }
    public int getClasseRischio(){
        return classeRischio;
    }

    public Progetto setValoreEconomico(Double x){
        valoreEconomico = x;
        return this;
    }
    public Double getValoreEconomico(){
        return valoreEconomico;
    }

    public Progetto setDurataContratto(int x){
        durataContratto= x;
        return this;
    }
    public int getDurataContratto(){
        return durataContratto;
    }

    public Progetto setOggettoFornitura(String x){
        oggettoFornitura = x;
        return this;
    }
    public String getOggettoFornitura(){
        return oggettoFornitura;
    }

    public Progetto setNomeCliente(String x){
        nomeCliente = x;
        return this;
    }
    public String getNomeCliente(){
        return nomeCliente;
    }

    public Progetto setPaese(LivelloDiRischio a){
        paese = a;
        return this;
    }
    public LivelloDiRischio getPaese(){
        return paese;
    }

    public Progetto setMercatoCliente(LivelloDiRischio a){
        mercatoCliente = a;
        return this;
    }
    public LivelloDiRischio getMercatoCliente(){
        return mercatoCliente;
    }
    
    public Progetto setContratto(LivelloDiRischio a){
        contratto = a;
        return this;
    }
    public LivelloDiRischio getContratto(){
        return contratto;
    }

    public Progetto setComposizionePartnership(LivelloDiRischio a){
        composizionePartnership = a;
        return this;
    }
    public LivelloDiRischio getComposizionePartnership(){
        return composizionePartnership;
    }

    public Progetto setIngegneria(LivelloDiRischio a){
        ingegneria = a;
        return this;
    }
    public LivelloDiRischio getIngegneria(){
        return ingegneria;
    }

    public Progetto setApprovvigionamento(LivelloDiRischio a){
        approvvigionamento = a;
        return this;
    }
    public LivelloDiRischio getApprovvigionamento(){
        return approvvigionamento;
    }

    public Progetto setFabbricazione(LivelloDiRischio a){
        fabbricazione = a;
        return this;
    }
    public LivelloDiRischio getFabbricazione(){
        return fabbricazione;
    }

    public Progetto setMontaggio(LivelloDiRischio a){
        montaggio = a;
        return this;
    }
    public LivelloDiRischio getMontaggio(){
        return montaggio;
    }

    public Progetto setAvviamento(LivelloDiRischio a){
        avviamento = a;
        return this;
    }
    public LivelloDiRischio getAvviamento(){
        return avviamento;
    }

    public Progetto setIm(ImpattoStrategico x){
        im = x;
        return this;
    }
    public ImpattoStrategico getIm(){
        return im;
    }

    public Progetto setIc(ImpattoStrategico x){
        ic = x;
        return this;
    }
    public ImpattoStrategico getIc(){
        return ic;
    }

    public Progetto setIa(ImpattoStrategico x){
        ia = x;
        return this;
    }
    public ImpattoStrategico getIa(){
        return ia;
    }

    public Progetto setIpp(ImpattoStrategico x){
        ipp = x;
        return this;
    }
    public ImpattoStrategico getIpp(){
        return ipp;
    }

    public Progetto setIp(ImpattoStrategico x){
        ip = x;
        return this;
    }
    public ImpattoStrategico getIp(){
        return ip;
    }

    public Progetto setDataInizio(Calendar d){
        dataInizio = d;
        return this;
    }
    public Calendar getDataInizio(){
        return dataInizio;
    }

    public Progetto setDataFine(Calendar d){
        dataFine = d;
        return this;
    }
    public Calendar getDataFine(){
        return dataFine;
    }

    /**
     * Preleva dal DB le chiavi primarie di tutti i progetti.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return  Lista di chiavi
     * @throws Exception
     */
    public static List getAllPrimaryKeys() throws Exception
     {
         String queryString = new String("select codice from Progetto");
         return persistentBase.executeQuery(queryString);
     }

    /**
     * Controlla se la chiave immessa come argomento è disponibile o meno
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param key   chiave da testare
     * @return      true: chiave disponibile. false altrimenti
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
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return  chiave generata
     * @throws Exception
     */
    public static String generateAutoKey() throws Exception
    {
        String key = "P1";
        int i = 1;
        while(!checkAvailable(key))
        {
            i++;
            key = "P" + i;
        }
        return "P"+i;
    }

    /**
     * Carica tutti i rischi del progetto dal DB.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return  puntatore al progetto (come un setter method)
     * @throws Exception
     */
    public Progetto caricaRischi() throws Exception
    {
        rischi = Rischio.executeQuery("from Rischi where idProgramma = '" + codice + "'");
        //carico tutte le azioni e le revisioni per ogni rischio
        Iterator it = rischi.iterator();
        while(it.hasNext())
        {
            Rischio r = (Rischio) it.next();
            r.caricaAzioni();
            r.caricaStorico();
        }
        return this;
    }
    /**
     * Aggiunge il rischio al progetto.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return puntatore al progetto (come un setter method
     * @throws Exception
     */
    public Progetto aggiungiRischio(Rischio r) throws Exception
    {
        //controllo che l'id programma del programma sia lo stesso
        //del rischio. Se non lo è, lo modifico opportunamente
        if(r.getIdProgramma().compareTo(codice) != 0)
            r.setIdProgramma(codice);

        //controllo che non esista un rischio con stessa chiave, già presente nel DB
        if(!Rischio.checkAvailable(r.getCodice()))
            //se c'è già la stessa chiave, lancio una eccezione
            throw new Exception("in programma " + codice +
                                ", idRischio "+r.getCodice()+" gia' presente");

        //se tutto va bene aggiungo l'azione
        rischi.add(r);
        return this;
    }

    /**
     * Rimuove il rischio con codice=id dal progetto e dal DB.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return puntatore al progetto (come un setter method
     * @throws Exception
     */
    public Progetto rimuoviRischio(String id) throws Exception
    {
         Iterator it = rischi.iterator();
        Rischio r = null;
        boolean found = false;
        while(it.hasNext() && !found)
        {
            r = (Rischio) it.next();
            if(r.getCodice().compareTo(id) == 0)
                found = true;
        }
        //XXX se non trovato, mando eccezione
        if(!found)
        {
            throw new Exception("Rischio "+ id  +" inesistente");
        }
        r.delete();
        rischi.remove(r);
        return this;
    }

    /**
     * Salva i rischi del progetto
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return puntatore al progetto (come un setter method
     * @throws Exception
     */
    public Progetto salvaRischi() throws Exception
    {
        //controllo quali campi sono stati modificati o aggiunti
        Iterator it = rischi.iterator();
        //confronto db attuale
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Query q = (Query) session.createQuery("from Rischio where idProgramma = '" +codice+"'");
        q.setReadOnly(true);
        List l = q.list();
        session.flush();
        session.close();

        while(it.hasNext())
        {
            Rischio r = (Rischio) it.next();
            Iterator i = l.iterator();
            boolean found = false;
            Rischio ck = null;
            while(i.hasNext() && !found)
            {
                ck = (Rischio) i.next();
                if(r.getCodice().compareTo(ck.getCodice())==0)
                {
                    //oggetto già esistente in DB
                    found = true;
                }
            }
            if(found)
            {
                if(ck.getCategoria() != r.getCategoria() ||
                   ck.getIdProgramma().compareTo(r.getIdProgramma()) != 0 ||
                   ck.getCodiceChecklist() != r.getCodiceChecklist() ||
                   ck.getStato() != r.getStato() ||
                   ck.getVerificato() != r.getVerificato() ||
                   ck.getNumeroRevisione() != r.getNumeroRevisione() ||
                   ck.getDescrizione().compareTo(r.getDescrizione()) != 0 ||
                   ck.getContingency() != r.getContingency() ||
                   ck.getProbabilitaIniziale() != r.getProbabilitaIniziale() ||
                   ck.getImpattoIniziale() != r.getImpattoIniziale() ||
                   ck.getCausa().compareTo(r.getCausa())!=0 ||
                   ck.getEffetto().compareTo(r.getEffetto()) != 0 ||
                   ck.getCostoPotenzialeImpatto() != r.getCostoPotenzialeImpatto())
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
            r.salvaAzioni();
            r.salvaStorico();
        }
        return this;
    }

    /**
     * Ritorna una lista con tutti i progetti nel DB
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return  Lista di oggetti Progetto
     * @throws Exception
     */
    public static List<?> getAllProjects() throws Exception
    {
        return executeQuery("from Progetto");
    }

    /**
     * ritorna una lista con tutti i progetti aperti
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return  Lista di progetti aperti (ancora modificabili)
     * @throws Exception
     */
    public static List<?> getOpenProjects() throws Exception
    {
        return executeQuery("from Progetto where isOpen = true");
    }

    /**
     * Ritorna la lista di progetti che forma la base di casi
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return Lista di progetti costituenti la base di casi
     * @throws Exception
     */
    public static List<?> getCases() throws Exception
    {
        return executeQuery("from Progetto where isCase = true");
    }

    /**
     * A differenza della funzione write() ereditata dalla classe base
     * questa funzione salva sia i campi del progetto, sia tutti i rischi e le azioni
     * associate al progetto stesso.
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return puntatore al progetto salvato (come un setter method)
     * @throws Exception
     */
    public Progetto salvaProgetto() throws Exception
    {
        write();
        salvaRischi();
        return this;
    }
    
    public jcolibri.cbrcore.Attribute getIdAttribute() {
    	return new Attribute("codice", this.getClass());
    }

	public Object getSimilarityFunction(String arg0) {
		Class<?> internalFieldClass = null;
		try {
			Field internalField = this.getClass().getDeclaredField(arg0);
			internalFieldClass = internalField.getType();
		} catch(NoSuchFieldException e) {
			System.err.println("Error getting similarity function for Progetto." + arg0 );
		}
		
		if(internalFieldClass == null)
			return null;
		else if(internalFieldClass.equals(ImpattoStrategico.class))
			return new IntervalImpattoStrategico(ImpattoStrategico.RANGE);
		else if(internalFieldClass.equals(LivelloDiRischio.class))
			return new AdvancedAverage();
		else {
			System.err.println("Error getting unknown field Progetto." + arg0
								+ " : returning Equal() similarity function");
			
			return new Equal();
		}
	}

	@Override
	public NNConfig getTotalSimilarityConfig(NNConfig simConfig) {
		if(simConfig == null)
			simConfig = new NNConfig();
		
		simConfig.setDescriptionSimFunction(new AdvancedAverage());
		
		Field[] allFields = this.getClass().getDeclaredFields();
		for(Field field : allFields) {
			Class<?> fieldClass = field.getClass();
			String fieldName = field.getName();
			if(	fieldClass.equals(LivelloDiRischio.class) ||
				fieldClass.equals(ImpattoStrategico.class)) {
				Object similarityFunction = this.getSimilarityFunction(fieldName);
				if(similarityFunction instanceof LocalSimilarityFunction)
					simConfig.addMapping(new Attribute(fieldName, fieldClass), (LocalSimilarityFunction)this.getSimilarityFunction(fieldName));
				else
					simConfig.addMapping(new Attribute(fieldName, fieldClass), (GlobalSimilarityFunction)this.getSimilarityFunction(fieldName));
			}				
		}
		
		LivelloDiRischio dummy = new LivelloDiRischio();
		dummy.getTotalSimilarityConfig(simConfig);
		
		return simConfig;
	}
}
