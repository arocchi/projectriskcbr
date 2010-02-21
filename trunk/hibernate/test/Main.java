import persistentclasses.*;
import persistentclasses.attributes.*;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author narduz
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            switch(18)
            {
                //con mie funzioni
                case 1:
                {
                    //System.out.println("1");
                    //TEST BASATI SULLA VARIABILE PROVA
                    /*
                    SessionObject.getStarted();
                    System.out.println(SessionObject.prova);
                    Progetto p = new Progetto();
                    System.out.println(SessionObject.prova);
                    SessionObject.newTransaction();
                    System.out.println(SessionObject.prova);
                    p.getById(9);
                    System.out.println(SessionObject.prova);
                    System.out.println(p);
                    p.setCodice("scritta1");
                    System.out.println(SessionObject.prova);
                    //p.update();
                    SessionObject.session.update(p);
                    System.out.println(SessionObject.prova);
                    SessionObject.endTransaction();
                    System.out.println(SessionObject.prova);
                     */
                    break;
                }


                //testing sessioni, scrittura su Db e get progetto
                case 2:
                {
                    //System.out.println("2");
                    Progetto p = new Progetto();
                    //SessionObject.sessionFactory = new Configuration().configure().buildSessionFactory();
                    //SessionObject.session = SessionObject.sessionFactory.openSession();
                    SessionObject.getStarted();
                    SessionObject.newTransaction();

                    //p = (Progetto) SessionObject.session.get(Progetto.class, 7);
                    //p= (Progetto) p.getById(3);
                    System.out.println(p);

                    //p.setStringa("stammerda");
                    p.delete();
                    //SessionObject.session.delete(p);
                    //p.update();
                    //SessionObject.session.merge(p);
                    //SessionObject.session.update(p);
                    //SessionObject.session.flush();
                    //SessionObject.session.close();
                    SessionObject.endTransaction();
                    break;
                }


                case 3:
                {
                   break;
                }
                //testing progetto, rischio e azioni 25-1-10
                case 4:
                {
                    GregorianCalendar g1 = new GregorianCalendar(2010,01,01);
                    GregorianCalendar g2 = new GregorianCalendar(2010,01,25);

                    //creo progetto con tutti i suoi campi
                    Progetto p = new Progetto();
                    //p.setId(10);
                    p.setIsCase(true);
                    p.setApprovvigionamento(new LivelloDiRischio(1,2,3));
                    p.setAvviamento(new LivelloDiRischio(1,2,3));
                    p.setClasseRischio(2);
                    p.setCodice("codice");
                    p.setComposizionePartnership(new LivelloDiRischio(2,1,3));
                    p.setContratto(new LivelloDiRischio(3,3,3));
                    p.setDataFine(g2);
                    p.setDataInizio(g1);
                    p.setDurataContratto(10);
                    p.setFabbricazione(new LivelloDiRischio(1,1,1));
                    p.setIa(new ImpattoStrategico(2));
                    p.setIc(new ImpattoStrategico(3));
                    p.setIp(new ImpattoStrategico(3));
                    p.setIm(new ImpattoStrategico(1));
                    p.setIngegneria(new LivelloDiRischio(1, 2, 2));
                    p.setIpp(new ImpattoStrategico(0));
                    p.setMercatoCliente(new LivelloDiRischio(3, 3, 2));
                    p.setMontaggio(new LivelloDiRischio(1, 1, 1));
                    p.setNomeCliente("Ilmioclientepreferito");
                    p.setOggettoFornitura("Lamiafornitura");
                    p.setPaese(new LivelloDiRischio(1, 0, 0));
                    p.setReparto(12);
                    p.setValoreEconomico(103.1);

                    //creo rischio con tutti i suoi campi
                    Rischio r = new Rischio();
                    r.setCategoria(new CategoriaRischio("Design & Development"));
                    r.setCausa("Troppa puzza");
                    r.setCodice("0102");
                    r.setCodiceChecklist(1012);
                    r.setContingency(1000);
                    r.setCostoPotenzialeImpatto(120.1);
                    r.setDescrizione("Rischioderivante da troppa roba");
                    r.setEffetto("distruzione dell'universo");
                    r.setImpattoIniziale(2);
                    r.setNumeroRevisione(01);
                    r.setProbabilitaIniziale(3);
                    r.setStato(new StatoRischio("mitigazione"));
                    r.setVerificato(0);


                    //creo Azione associata
                    Azioni a = new Azioni();
                    a.setDescrizione("mitigazione esplosione");
                    //a.setIdAzione(01);
                    //a.setIdRischio("0102");
                    a.setIntensita(1);
                    a.setRevisione(1);
                    a.setStato("Planned");
                    //a.setTipo('m');


                    //salvataggio di tut                   ti gli elementi nel DB
                    SessionObject.getStarted();
                    SessionObject.newTransaction();

                    p.write();
                    r.setIdProgramma("P12");
                    r.write();
                    a.write();
                    SessionObject.endTransaction();

                    //finita scrittura su DB
                    break;
                }
                case 5:
                {
                    //apertura di progetti, rischi, azioni
                    Progetto p = new Progetto();
                    Rischio r = new Rischio();

                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    //p= (Progetto) p.getById(1);

                    System.out.print(p.getCodice() + " " + p.getNomeCliente() + " " + p.getDataFine() + " " + p.getIm() + "\n");
                    p.setNomeCliente("Pincopallo");
                    System.out.print(p.getCodice() + " " + p.getNomeCliente() + " " + p.getDataFine() + " " + p.getIm() + "\n");

                    //r = (Rischio) r.getById(2);
                    System.out.print(r.getCodice() + " " + r.getCategoria() + "\n");
                    r.setCategoria(new CategoriaRischio("Manifacturing"));
                    System.out.print(r.getCodice() + " " + r.getCategoria() + "\n");

                    //updating
                    SessionObject.endTransaction();
                    break;
                }
                case 6: //assegnamento manuale della chiave
                {
                    Azioni a = new Azioni();
                    a.setDescrizione("ciccoa");
                    //a.setIdAzione(220);
                    //a.setIdRischio("220risk");
                    //ap.setTipo('M');
                    a.setIntensita(1);
                    a.setRevisione(1);
                    a.setStato("mitigazione");
                    
                    //a.setId(6);

                    SessionObject.getStarted();
                    SessionObject.newTransaction();

                    a.write();

                    SessionObject.endTransaction();
                    break;
                }
                case 7://retrieving degli id per la classe Azioni
                {
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    List l = Azioni.getAllPrimaryKeys();
                    System.out.print(l);
                    System.out.print("\n");
                    List l2 = Rischio.getAllPrimaryKeys();
                    System.out.print(l2);
                    System.out.print("\n");
                    List l3 = Progetto.getAllPrimaryKeys();
                    System.out.print(l3);
                    System.out.print("\n");

                    SessionObject.endTransaction();
                    break;
                }
                case 8://creazione azioni con doppia primary key
                {
                    Azioni a = new Azioni();
                    a.setDescrizione("ciccoa");
                    AzioniPrimaryKey ap = new AzioniPrimaryKey();
                    ap.setIdAzione(250);
                    ap.setIdRischio("250risk");
                    ap.setTipo('M');
                    a.setPrimaryKey(ap);
                    a.setIntensita(1);
                    a.setRevisione(1);
                    a.setStato("Planned");
                    
                    SessionObject.getStarted();
                    SessionObject.newTransaction();

                    a.write();

                    SessionObject.endTransaction();
                    break;
                }
                case 9://esecuzione di una query con ricerca di doppia primary key
                {
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    List l = Azioni.getAllPrimaryKeys();
                    //stampa a video del contenuto della lista
                    Iterator it = l.iterator();
                    while(it.hasNext())
                    {
                        AzioniPrimaryKey a = (AzioniPrimaryKey) it.next();
                        System.out.print("AZIONE: "+a.getIdAzione()+"\nRISCHIO: "+a.getIdRischio()+"\n\n");
                    }
                    //System.out.print(l);
                    System.out.print("\n");
                    SessionObject.endTransaction();
                    break;
                }
                case 10://testing di assigned primary keys pr progetto, rischio e azioni
                {
                    GregorianCalendar g1 = new GregorianCalendar(2010,01,01);
                    GregorianCalendar g2 = new GregorianCalendar(2010,01,25);

                    //creo progetto con tutti i suoi campi
                    Progetto p = new Progetto();
                    //p.setId(10);
                    p.setIsCase(true);
                    p.setApprovvigionamento(new LivelloDiRischio(1,2,3));
                    p.setAvviamento(new LivelloDiRischio(1,2,3));
                    p.setClasseRischio(2);
                    p.setCodice("P4");
                    p.setComposizionePartnership(new LivelloDiRischio(2,1,3));
                    p.setContratto(new LivelloDiRischio(3,3,3));
                    p.setDataFine(g2);
                    p.setDataInizio(g1);
                    p.setDurataContratto(10);
                    p.setFabbricazione(new LivelloDiRischio(1,1,1));
                    p.setIa(new ImpattoStrategico(2));
                    p.setIc(new ImpattoStrategico(3));
                    p.setIp(new ImpattoStrategico(3));
                    p.setIm(new ImpattoStrategico(1));
                    p.setIngegneria(new LivelloDiRischio(1, 2, 2));
                    p.setIpp(new ImpattoStrategico(0));
                    p.setMercatoCliente(new LivelloDiRischio(3, 3, 2));
                    p.setMontaggio(new LivelloDiRischio(1, 1, 1));
                    p.setNomeCliente("Ilmioclientepreferito");
                    p.setOggettoFornitura("Lamiafornitura");
                    p.setPaese(new LivelloDiRischio(1, 0, 0));
                    p.setReparto(12);
                    p.setValoreEconomico(103.1);

                    //creo rischio con tutti i suoi campi
                    Rischio r = new Rischio();
                    r.setCategoria(new CategoriaRischio("Design & Development"));
                    r.setCausa("Troppa puzza");
                    r.setCodice("P1R1");
                    r.setIdProgramma("P1");
                    r.setCodiceChecklist(1012);
                    r.setContingency(1000);
                    r.setCostoPotenzialeImpatto(120.1);
                    r.setDescrizione("Rischio derivante da troppa roba");
                    r.setEffetto("distruzione dell'universo");
                    r.setImpattoIniziale(2);
                    r.setNumeroRevisione(01);
                    r.setProbabilitaIniziale(3);
                    r.setStato(new StatoRischio("mitigazione"));
                    r.setVerificato(0);


                    //creo Azione associata
                    Azioni a = new Azioni();
                    a.setDescrizione("mitigazione esplosione");
                    AzioniPrimaryKey ap = new AzioniPrimaryKey();
                    ap.setIdAzione(250);
                    ap.setIdRischio("250risk");
                    ap.setTipo('m');
                    a.setPrimaryKey(ap);
                    a.setIntensita(1);
                    a.setRevisione(1);
                    a.setStato("Planned");


                    //salvataggio di tutti gli elementi nel DB
                    SessionObject.getStarted();
                    SessionObject.newTransaction();

                    p.write();
                    r.write();
                    a.write();
                    SessionObject.endTransaction();
                    break;
                }
                case 11: //test su generazione chiavi
                {
                    String key;
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    boolean b = Azioni.checkAvailable(1,250,"250risk",'m');
                    SessionObject.endTransaction();

                    System.out.print(b + "\n");
                    break;

                }
                case 12: //test su salvataggio revisione
                {
                    Revisione r1 = new Revisione();
                    Revisione r2 = new Revisione();
                    Revisione r3 = new Revisione();
                    Revisione r4 = new Revisione();
                    r1.setIdRischio("R1");
                    r1.setIndiceImpatto(1);
                    r1.setNumeroRevisione(1);
                    r1.setProbabilita(10);
                    r2.setIdRischio("R2");
                    r2.setIndiceImpatto(1);
                    r2.setNumeroRevisione(1);
                    r2.setProbabilita(10);
                    r3.setIdRischio("R1");
                    r3.setIndiceImpatto(1);
                    r3.setNumeroRevisione(2);
                    r3.setProbabilita(500);
                    r4.setIdRischio("R1");
                    r4.setIndiceImpatto(1);
                    r4.setNumeroRevisione(3);
                    r4.setProbabilita(1);
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    r1.write();
                    r2.write();
                    r3.write();
                    r4.write();
                    SessionObject.endTransaction();
                    break;
                }
                case 13: //test retrieve storico per un dato rischio
                {
                    Rischio r = new Rischio();
                    r.setCodice("R1");
                    List rl;
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    r.caricaStorico();
                    Revisione rev = (Revisione) r.getStorico().iterator().next();
                    rev.setIndiceImpatto(1);
                    //r.aggiungiRevisione("R1",4, 11, 2);
                    r.salvaStorico();
                    System.out.println(r.getStorico());
                    SessionObject.endTransaction();
                    break;
                }
                case 14: //aggiunta e scrittura azioni
                {
                    Rischio r = new Rischio();
                    r.setCodice("R3");
                    Azioni action = new Azioni();
                    action.setDescrizione("descrizione azione");
                    action.setIntensita(9);
                    action.setRevisione(0);
                    action.setStato("Planned");
                    action.setPrimaryKey(new AzioniPrimaryKey(1,1,"R3", 'M'));
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    r.caricaAzioni();
                    r.aggiungiAzione(7, action);
                    r.salvaAzioni();
                    System.out.println(r.getAzioni());
                    SessionObject.endTransaction();
                    break;
                }
                case 15: //cancellazione azioni
                {
                    Rischio r = new Rischio();
                    r.setCodice("R7");
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    r.caricaAzioni();
                    //System.out.println(r.getAzioni());
                    r.rimuoviAzione(new AzioniPrimaryKey(1,3,"R3", 'M'));
                    r.salvaAzioni();
                    System.out.println(r.getAzioni());
                    SessionObject.endTransaction();
                }
                case 16:
                {
                    CkMitigazione ck = (CkMitigazione) new CkMitigazione().setFields(1, "descrizione1");
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    ck = (CkMitigazione) CkMitigazione.getById(CkMitigazione.class, 1);
                    System.out.println(ck.getDescrizione());
                    SessionObject.endTransaction();
                    //System.out.println("|| -> \n" + p.getClass().getName()+"||");
                    break;
                }
                case 17:
                {
                    Rischio r = new Rischio();
                    r.setCodice("R3");
                    SessionObject.getStarted();
                    SessionObject.newTransaction();
                    //r.caricaAzioni();
                    //r.aggiungiAzione(7, action);
                    //r.salvaAzioni();
                    r.write();
                    //System.out.println(r.getAzioni());
                    SessionObject.endTransaction();
                    break;
                }
                case 18:
                {

                    break;
                }
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }
}
