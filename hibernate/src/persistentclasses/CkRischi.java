package persistentclasses;

/* Checklist dei rischi
 * Classe derivata da Descrizione.
 * Questa struttura consente di mappare su tabelle diverse, classi
 * con gli stessi campi senza riscriverle completamente
 */
public class CkRischi extends Descrizione{

    /**
     * Controlla se la chiave immessa come argomento è disponibile o meno
     */
    public static boolean checkAvailable(int key) throws Exception
    {
        return !executeQuery("select codChecklist from CkRischi").contains(key);
    }

    /**
     * Genera automaticamente una nuova chiave
     */
    @SuppressWarnings("empty-statement")
    public static int generateAutoKey() throws Exception
    {
        int i;
        for(i =1; !checkAvailable(i); i++ );
        return i;
    }

}
