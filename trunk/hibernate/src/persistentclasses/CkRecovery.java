package persistentclasses;

/* Checklist delle azioni di recovery.
 * Classe derivata da Descrizione.
 * Questa struttura consente di mappare su tabelle diverse, classi
 * con gli stessi campi senza riscriverle completamente
 */
public class CkRecovery extends Descrizione{

    /**
     * Controlla se la chiave immessa come argomento è disponibile o meno
     */
    public static boolean checkAvailable(int key) throws Exception
    {
        return !executeQuery("select codChecklist from CkRecovery").contains(key);
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
