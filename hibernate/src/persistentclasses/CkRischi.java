package persistentclasses;

/* Checklist dei rischi
 * Classe derivata da Descrizione.
 * Questa struttura consente di mappare su tabelle diverse, classi
 * con gli stessi campi senza riscriverle completamente
 */
public class CkRischi extends Descrizione{

    /**
     * Controlla se la chiave immessa come argomento è disponibile o meno
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @param key   chiave da testare
     * @return      true: chiave disponibile;false altrimenti
     * @throws Exception
     */
    public static boolean checkAvailable(int key) throws Exception
    {
        return !executeQuery("select codChecklist from CkRischi").contains(key);
    }

    /**
     * Genera automaticamente una nuova chiave per l'oggetto, leggendo dal DB
     * RICHIEDE CHE SIA APERTA UNA SESSIONE (vedi SessionObject)
     * @return      chiave generata
     * @throws Exception
     */
    @SuppressWarnings("empty-statement")
    public static int generateAutoKey() throws Exception
    {
        int i;
        for(i =1; !checkAvailable(i); i++ );
        return i;
    }

}
