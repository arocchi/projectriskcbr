package persistentclasses;

import persistentclasses.attributes.RevisionePrimaryKey;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class Revisione extends persistentBase {
    private RevisionePrimaryKey key;
    private int                 probabilita;
    private int                 indiceImpatto;//indice impatto relativo alla revisione 'numeroRevisione'

    
    public Revisione()
    {
        key = new RevisionePrimaryKey();
    }
    //setters e getters
    public Revisione setKey(RevisionePrimaryKey x)
    {
        key = x;
        return this;
    }

    public Revisione setIdRischio(String x)
    {
        key.setIdRischio(x);
        return this;
    }

    public Revisione setNumeroRevisione(int x)
    {
        key.setNumeroRevisione(x);
        return this;
    }

    public String getIdRischio()
    {
        return key.getIdRischio();
    }

    public int getNumeroRevisione()
    {
        return key.getNumeroRevisione();
    }

    public Revisione setProbabilita(int x)
    {
        probabilita = x;
        return this;
    }

    public Revisione setIndiceImpatto(int x)
    {
        indiceImpatto = x;
        return this;
    }

    public RevisionePrimaryKey getKey()
    {
        return key;
    }
    public int getProbabilita()
    {
        return probabilita;
    }

    public int getIndiceImpatto()
    {
        return indiceImpatto;
    }

    public static List getAllPrimaryKeys() throws Exception
    {
        String queryString = new String("select key from Revisione");
        return persistentBase.executeQuery(queryString);
    }

    /**
     * Controlla se la chiave passata come argomento è disponibile o meno
     */
    public static boolean checkAvailable(RevisionePrimaryKey key) throws Exception
    {
        List allKeys = getAllPrimaryKeys();
        RevisionePrimaryKey rvpk = new RevisionePrimaryKey();
        boolean found = false;
        Iterator it = allKeys.iterator();
        while(it.hasNext() && found == false)
        {
            rvpk = (RevisionePrimaryKey) it.next();
            if(rvpk.equals(key))
                found = true;
        }
        return !found;
    }

    /**
     * interfaccia per la classe precedente
     */
    public static boolean checkAvailable(String idRischio, int numeroRevisione) throws Exception
    {
        RevisionePrimaryKey pk = new RevisionePrimaryKey();
        pk.setIdRischio(idRischio);
        pk.setNumeroRevisione(numeroRevisione);
        return checkAvailable(pk);
    }

    //XXX funzione usata per debug
    @Override
    public String toString()
    {
        return "idRischio = " + key.getIdRischio() + "\n" +
               "numeroRevisione = " + key.getNumeroRevisione() + "\n" +
               "probabilita = " + probabilita + "\n" +
               "indiceImpatto = " + indiceImpatto;
    }
    
    /**
     * calcola IR per la revisione in questione
     */
    public int getIr(){
        return probabilita*indiceImpatto;
    }

}

