package persistentclasses;

import java.util.List;

/**
 * Classe base, usata per salvare le descrizioni delle checklist associate ad
 * azioni di mitigazione, monitoraggio e rischi
 */
public class Descrizione extends persistentBase{
    private int         codChecklist;
    private String      descrizione;

    public Descrizione(){}

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

    public Descrizione setFields(int x, String y)
    {
        codChecklist = x;
        descrizione = y;
        return this;
    }
}
