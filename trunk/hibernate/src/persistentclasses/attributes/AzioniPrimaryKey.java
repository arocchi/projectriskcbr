package persistentclasses.attributes;

import java.io.Serializable;

/**
 * Chiave primaria delle azioni
 */
public class AzioniPrimaryKey implements Serializable{
	public static char MITIGATION = 'M';
	public static char RECOVERY = 'R';
	
    private int         idAzione;
    private String      idRischio;//univoco in tutto il db
    private char        tipo;//M o R per mitigazione o recovery


    public AzioniPrimaryKey(){}

    public AzioniPrimaryKey(int a, String r, char t)
    {
        idAzione = a;
        idRischio = r;
        tipo = t;
    }
    
    public AzioniPrimaryKey setIdRischio(String x){
        idRischio = x;
        return this;
    }

    public AzioniPrimaryKey setIdAzione(int x){
        idAzione=x;
        return this;
    }

    public AzioniPrimaryKey setTipo(char x)
    {
        tipo = x;
        return this;
    }

    public String getIdRischio(){
         return idRischio;
     }

     public int getIdAzione(){
         return idAzione;
     }

     public char getTipo()
     {
         return tipo;
     }
     
     public boolean isRecovery() {
    	 return (tipo == RECOVERY);
     }
     
     public boolean isMitigation() {
    	 return (tipo == MITIGATION);
     }

     //serve per i confronti fatti da hibernate
     public boolean equals(AzioniPrimaryKey x)
     {
            if(x instanceof AzioniPrimaryKey &&
               x.idRischio.compareTo(idRischio) == 0 &&
               x.idAzione == idAzione &&
               x.tipo == tipo)
                return true;
            return false;
     }

     //funzione usata in debug per leggere il contenuto delle azioni
    @Override
     public String toString()
     {
         return new String("idAzione = " + idAzione + "\nidRischio = " + idRischio +"\n" +"tipo = "+tipo+"\n");
     }
    
    public String getDefaultStato() {
		if(this.getTipo() == 'M')
			return "Planned";
		else if(this.getTipo() == 'M')
			return "Back-up";
		return "Error";
    }
}
