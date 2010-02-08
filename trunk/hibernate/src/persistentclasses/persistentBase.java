/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Query;

/**
 *
 * @author narduz
 */
public abstract class persistentBase {
    /*XXX id, setters e getters non più necessari
    protected int id;

    public void setId(int x){
        id = x;

    }
    public int getId(){
        return id;
    }
     * */
    //operazioni sull'oggetto
    public void write() throws Exception{
        if(SessionObject.session == null)// || tx == null)
            throw new Exception("write() operation needs a previous newTransaction() operation\n");

        SessionObject.session.save(this);
    }
    public void update() throws Exception{
         if(SessionObject.session == null)// || tx == null)
            throw new Exception("update() operation needs a previous newTransaction() operation\n");

        //non funziona lo sostituisco con una delete + save
        //SessionObject.session.merge(this);
        SessionObject.session.delete(this);
        SessionObject.session.save(this);

        //XXX SessionObject.prova = "FATTA UPDATE";
    }

    /*
     * Sintassi di chiamata della funzione:
     * p= (Progetto) p.getById(12);
     * dove p è l'oggetto a cui assegnare il progetto
     */
    public static persistentBase getById(Class c, Object x) throws Exception{ //da implementare per ogni classe derivata
         if(SessionObject.session == null)// || tx == null)
            throw new Exception("getById() operation needs a previous newTransaction() operation\n");

        //XXX SessionObject.prova = "getById";
        return (persistentBase) SessionObject.session.get(c,(Serializable) x);
        
    }

    public void delete() throws Exception{
        if(SessionObject.session == null)// || tx == null)
            throw new Exception("delete() operation needs a previous newTransaction() operation\n");

        SessionObject.session.delete(this);
    }

    //funzioni che le classi derivate devono implementare
    /**
     * Esegue la query passata come agomento, ritornandone i risultati in una lista.
     */
    protected static List executeQuery(String queryString) throws Exception
    {
        if(SessionObject.session == null)// || tx == null)
            throw new Exception("executeQuery() operation needs a previous newTransaction() operation\n");

         Query q = SessionObject.session.createQuery(queryString);
         q.setReadOnly(true);
         List l = q.list();
         return l;
    }
}
