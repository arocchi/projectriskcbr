package persistentclasses;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *  Classe da cui deriveranno tutti gli oggenti da rendere persistenti su database.
 *  Tutti avranno dunque a comune la sessionFactory Hibernate
 *  (configurazione per il salvataggio dati, vedere documentazione Hibernate).
 *  Inizialmente sarà necessario chiamarela getStarted() per settaggi iniziali.
 *  Quando si vuole modificare il database, si apre una nuova transazione,
 *  si leggono e scrivono dati nel database, poi si chiude la transazione:
 *
 *  SessionObject.getStarted(); //solo la prima volta
 *
 *  SessionObject.startTransaction();
 *  //letture e scritture di oggetti di oggetti che estendono la classe SessionObject
 *  SessionObject.endTransaction();
 *
 *  Alla chiusura della transazione, viene fatto commit di tutte le modifiche apportate al DB
 *  da quando la transazione era stata aperta.
 *  Tentativi di modifica del DB senza previa apertura di una transazione producono
 *  eccezioni.
 *
 *  Dato che operazioni di scrittura e modifica del database sono effettuate solo alla chiamata della
 *  endTransaction, se l'oggetto subisce delle modifiche DOPO aver fatto write() e PRIMA dell' endTransaction()
 *  allora tali modifiche sono riportate nel database.
 */
public class SessionObject {
    
    public static Session session;

    public static SessionFactory sessionFactory;

    //questa funzione va chiamata una volta sola all'inizio del programma
    public static SessionFactory getStarted(){
         SessionObject.sessionFactory = new Configuration().configure().buildSessionFactory();
         SessionObject.session = null;
         return sessionFactory;
    }
    //interfaccia differente, per il salvataggio di sessionfactories tra servlet
    public static SessionFactory getStarted(SessionFactory factory){
        sessionFactory = factory;
        session = null;
        return sessionFactory;
    }

    //inizia una nuova transazione
    public static void newTransaction() throws Exception{

        if(SessionObject.session != null)
            throw new Exception("Wrong newTransaction() operation\n");

        Session s = SessionObject.sessionFactory.openSession();
        SessionObject.session = s;
    }

    //termina una transazione
    public static void endTransaction() throws Exception{
        if(SessionObject.session == null)
            throw new Exception("Wrong endTransaction() operation\n");

        SessionObject.session.flush();
        SessionObject.session.close();
        SessionObject.session = null;
    }    
}
