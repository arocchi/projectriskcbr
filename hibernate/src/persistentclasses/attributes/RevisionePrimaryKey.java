/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;

import java.io.Serializable;

/**
 *
 * Chiave primaria per la tabella dello storico
 */
public class RevisionePrimaryKey implements Serializable {
    private String      idRischio;
    private int         numeroRevisione;

    public RevisionePrimaryKey(){}

    public RevisionePrimaryKey(String idR, int numRev)
    {
        idRischio = idR;
        numeroRevisione = numRev;
    }

    //setters e getters

    public RevisionePrimaryKey setIdRischio(String x)
    {
        idRischio = x;
        return this;
    }

    public RevisionePrimaryKey setNumeroRevisione(int x)
    {
        numeroRevisione = x;
        return this;
    }

    public String getIdRischio()
    {
        return idRischio;
    }

    public int getNumeroRevisione()
    {
        return numeroRevisione;
    }
    public boolean equals(RevisionePrimaryKey x)
    {
            if(x instanceof RevisionePrimaryKey &&
               x.idRischio.compareTo(idRischio) == 0 &&
               x.numeroRevisione == numeroRevisione)
                return true;
            return false;
    }

    public String toString()
    {
        return "idRischio = " +idRischio +"\nnumeroRevisione = " +numeroRevisione;
    }

}
