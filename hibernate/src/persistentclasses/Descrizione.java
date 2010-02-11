package persistentclasses;

/**
 * Classe base, usata per salvare le descrizioni delle checklist associate ad
 * azioni di mitigazione, monitoraggio e rischi.
 * Da questa classe derivano le classi:
 * -CkMitigazione
 * -CkRecovery
 * -CkRischi
 *
 * Questo perche' la struttura delle tre classi e' identica, e si puo' dunque
 * definire una volta sola. Tuttavia Hibernate richiede che esistano classi distinte
 * per poterle salvare su tabelle distinte, per cui per distinguere tra
 * i vari tipi di checklist è necessario derivare le tre classi sopra esposte.
 */
public class Descrizione extends persistentBase{
    private int         codChecklist;
    private String      descrizione;

    public Descrizione()
    {
        //il codChecklist va immesso obbligatoriamente(e' chiave)
        descrizione = new String();
    }

    //setters e getters
    public Descrizione setCodChecklist(int x)
    {
        codChecklist = x;
        return this;
    }
    public Descrizione setDescrizione(String s)
    {
        descrizione = s;
        return this;
    }

    public int getCodChecklist()
    {
        return codChecklist;
    }
    public String getDescrizione()
    {
        return descrizione;
    }

    /**
     * Funzione di utilita' per settare tutti i parametri della classe inun'unica chiamata
     * @param x codice checklist
     * @param y descrizione
     * @return  ritorna un puntatore alla classe, come tutti i setters
     */
    public Descrizione setFields(int x, String y)
    {
        codChecklist = x;
        descrizione = y;
        return this;
    }
}
