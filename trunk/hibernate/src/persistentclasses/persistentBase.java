package persistentclasses;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Query;

/**
 * Classe base da cui tutti gli oggetti persistenti dovranno derivare
 * Implementa metodi per salvare l'oggetto sul database *
 */
public abstract class persistentBase {

    //scrive un nuovo oggetto sul database
    public void write() throws Exception{
        if(SessionObject.session == null)// || tx == null)
            throw new Exception("write() operation needs a previous newTransaction() operation\n");

        SessionObject.session.save(this);
    }

    //aggiorna le modifiche di un oggetto su database
    public void update() throws Exception{
         if(SessionObject.session == null)// || tx == null)
            throw new Exception("update() operation needs a previous newTransaction() operation\n");

        //non funziona lo sostituisco con una delete + save
        //SessionObject.session.merge(this);
        SessionObject.session.delete(this);
        SessionObject.session.save(this);
    }

    /**
     * Preleva dal DB un oggetto, mediante il suo id.
     * Sintassi di chiamata della funzione:
     * p= (Progetto) persistentBase.getById(classe, identificatore);
     * dove p è l'oggetto a cui assegnare il progetto
     * @param c classe di appartenenza dell'oggetto da trovare
     * @param x identificatore dell'oggetto da trovare
     * @return oggetto ritrovato (he deriva da persistentBase)
     * @throws Exception
     */
    public static persistentBase getById(Class c, Object x) throws Exception{ //da implementare per ogni classe derivata
         if(SessionObject.session == null)// || tx == null)
            throw new Exception("getById() operation needs a previous newTransaction() operation\n");

        //XXX SessionObject.prova = "getById";
        return (persistentBase) SessionObject.session.get(c,(Serializable) x);
        
    }

    //cancella un oggetto da DB
    public void delete() throws Exception{
        if(SessionObject.session == null)// || tx == null)
            throw new Exception("delete() operation needs a previous newTransaction() operation\n");

        SessionObject.session.delete(this);
    }

    /**
     * Esegue la query passata come agomento, ritornandone i risultati in una lista.
     * @paran queryString   stringa rappresentante la query da eseguire
     * @return lista di oggetti prodotti dalla query
     * @throws Exception
     */
    protected static List<?> executeQuery(String queryString) throws Exception
    {
        if(SessionObject.session == null)// || tx == null)
            throw new Exception("executeQuery() operation needs a previous newTransaction() operation\n");

         Query q = SessionObject.session.createQuery(queryString);
         q.setReadOnly(true);
         List<?> l = q.list();
         return l;
    }
}
