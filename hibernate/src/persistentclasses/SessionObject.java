/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *  Classe da cui deriveranno tutti gli oggenti da rendere persistenti su database.
 *  Tutti avranno dunque a comune la sessionFactory (configurazione per il salvataggio dati).
 *  Quando si vuole modificare il database, si apre una nuova transazione,
 *  si leggono e scrivono dati nel database, poi si chiude la transazione:
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
 *
 * @author narduz
 */
public class SessionObject {
    
    public static Session session;
    //public static String prova;//XXX
    //protected static org.hibernate.Transaction tx = null;

    public static SessionFactory sessionFactory;

    public static void getStarted(){
         SessionObject.sessionFactory = new Configuration().configure().buildSessionFactory();
         SessionObject.session = null;
         //SessionObject.prova = "getstarted";//XXX
    }
    //inizio e fine transazione
    public static void newTransaction() throws Exception{

        if(SessionObject.session != null)// || tx != null)
            throw new Exception("Wrong newTransaction() operation\n");

        Session s = SessionObject.sessionFactory.openSession();
        SessionObject.session = s;
        //prova = "newTransaction";//XXX
        //tx = session.beginTransaction();
    }

    public static void endTransaction() throws Exception{
        if(SessionObject.session == null)// || tx == null)
            throw new Exception("Wrong endTransaction() operation\n");

        //tx.commit();
        SessionObject.session.flush();
        SessionObject.session.close();
        //SessionObject.prova = "endTransaction"; XXX
        //per controllo sulla correttezza delle operazioni di write
        //tx = null;
        SessionObject.session = null;
    }

    
}
