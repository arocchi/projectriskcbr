import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import persistentclasses.*;

/**
 *
 * @author User
 */
public class dataFromDB {
    dataFromDB (int type, PrintWriter out) {

        //parte provvisoria, va sostituita col meccanismo delle sessioni
        try{
            
            SessionObject.getStarted();
            SessionObject.newTransaction();           

            List lista;
            Iterator it;
            int index;
            switch(type) {

                // Getting the checklist of CLIENTE
                case 0:
                    lista = Progetto.executeQuery("select distinct nomeCliente from Progetto");
                    it = lista.iterator();
                    index = 0;
                    while(it.hasNext()) {
                        String cl = (String) it.next();
                        out.println("<nome idName=\""+index+"\">"+cl+"</nome>");
                        index++;
                    }
                    /* EXAMPLE OUTPUT
                     * OUTPUT (for each record):
                     * out.println("<nome idName="idCliente">nomeCliente</nome>");
                     * out.println("<nome idName="1">nomeCliente1</nome>");
                     */
                    break;

                // Getting the checklist of OGGETTOFORNITURA
                case 1:
                    // The same of above
                    lista = Progetto.executeQuery("select distinct oggettoFornitura from Progetto");
                    it = lista.iterator();
                    index = 0;
                    while(it.hasNext()) {
                        String of = (String) it.next();
                        out.println("<oggettoFornitura idName=\""+index+"\">"+of+"</oggettoFornitura>");
                        index++;
                    }
                    //out.println("<oggettoFornitura idName="1">nomeCliente1</oggettoFornitura>");
                    break;

                // Getting the checklist of REPARTO
                case 2:
                    // The same of above
                    lista = Progetto.executeQuery("select distinct reparto from Progetto");
                    it = lista.iterator();
                    index = 0;
                    while(it.hasNext()) {
                        int rep = (Integer) it.next();
                        out.println("<reparto idName=\""+index+"\">"+rep+"</reparto>");
                        index++;
                    }
                    //out.println("<reparto idName="1">nomeCliente1</reparto>");
                    break;
                //getting the selected risks by group
                case 3:
                    int groupnumber = getNumberOfGroups();//XXX andrà letto da file di configurazione
                    index = 0;//index for the xml object to be created from the interface
                    for(int i=0; i<groupnumber; i++){
                        out.println("<gruppo idName=\""+i+">\n\t<nomeGruppo>Gruppo "+i+"</nomeGruppo>");
                        lista = risksGroup(groupnumber);
                        it = lista.iterator();
                        while(it.hasNext()){
                            Rischio r = (Rischio) it.next();
                            printRisk(r,out,index);
                            index++;
                        }
                        out.println("</gruppo>");
                    }
                    break;
                //getting the common risks, that not appear in any group
                case 4:
                    out.println("<root idName=\"NoGroup\">");
                    index=0;//xml identifier
                    lista = risknoGroup();
                    it = lista.iterator();
                    while(it.hasNext()){
                        Rischio r = (Rischio) it.next();
                        printRisk(r, out, index);
                        index++;
                    }
                    out.println("</root>");
                    break;
                //getting all the risks in DB, also if not suggested in any other case
                case 5:
                    out.println("<root label=\"Tutte le Categorie\" type=\"fuori\">\n"+
                                    "\t<node label=\"Rischi non suggeriti\" type=\"categoria\" >");
                    index=0;//xml identifier
                    lista = notSuggestedCkRisks();
                    it = lista.iterator();
                    while(it.hasNext()){
                        CkRischi r = (CkRischi) it.next();
                        out.println("\t\t<node codiceChecklist=\""+r.getCodChecklist()+
                                    "\" label=\""+r.getDescrizione()+"\" type=\"rischio\" />");
                    }
                    out.println("\t</node>\n</root>");
                    break;
            }
            SessionObject.endTransaction();
        } catch (Exception e){out.println(e);}

        //out.println("END");
        //out.println("Hai richiesto: " + type + "\n");
        
        /*out.println("<name>Franci</name>");
        out.println("<name>Luigi</name>");
        out.println("<name>Maria</name>");
        out.println("<name>Ettore</name>");*/
        
    }

    //utility functions
    private void printRisk(Rischio r, PrintWriter out, int index){
        out.println("\t<rischio idName=\""+index+"\">"+
                        "\t\t<codiceChecklist>"+r.getCodiceChecklist()+"</codiceChecklist>"+
                        "\t\t<stato>"+r.getStato().getStato()+"</stato>"+
                        "\t\t<categoria>"+r.getCategoria()+"</categoria>"+
                        "\t\t<rVer>"+r.getVerificato()+"</rVer>"+
                        "\t\t<descrizione>"+r.getDescrizione()+"</descrizione>"+
                        "\t\t<contingency>"+r.getContingency()+"</contingency>"+
                        "\t\t<causa>"+r.getCausa()+"</causa>"+
                        "\t\t<effetto>"+r.getEffetto()+"</effetto>"+
                        "\t\t<probIniziale>"+r.getProbabilitaIniziale()+"</probIniziale>"+
                        "\t\t<impattoIniziale>"+r.getImpattoIniziale()+"</impattoIniziale>"+
                    "\t</rischio>");
        return;
    }
    /**    
     * @return List of Risks that were not suggested in any group or in the NoGroup list
     */
    private List notSuggestedCkRisks(){
        String query = "from CkRischi ";
        List<Integer> suggested = alreadySuggestedCkRisks();
        Iterator it = suggested.iterator();
        if(it.hasNext())
            query = query + "where ";//XXX REMOVE THE FIRST AND
        while(it.hasNext()){
            Integer current = (Integer) it.next();
            query = query + " and codChecklist != "+current;
        }
        try{
            return Rischio.executeQuery(query);
        } catch (Exception e){}

        return new LinkedList();
    }

    //dummy functions used as stubs
    private int getNumberOfGroups(){
        return 3;
    }
    private List risksGroup(int groupnumber){
        try{
            return Rischio.executeQuery("from Rischio where idProgramma = 'P"+groupnumber+"'");
        } catch (Exception e){}

        return new LinkedList();
    }
    private List risknoGroup(){
        try{
            return Rischio.executeQuery("from Rischio where idProgramma = 'P7'");
        } catch (Exception e){}

        return new LinkedList();
    }
    //returns a list of checklists codes of all previously suggested risks. Dummy function
    private List<Integer> alreadySuggestedCkRisks(){
        try{
            return (List<Integer>) Rischio.executeQuery("select codChecklist from CkRischi where codChecklist <= 10");
        } catch (Exception e) {}

        return new LinkedList();
    }

}
