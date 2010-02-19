import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.hibernate.SessionFactory;
import persistentclasses.*;

/**
 *
 * @author User
 */
public class dataFromDB {
    dataFromDB (int type, PrintWriter out, HttpSession session, HttpServletRequest request) {

        try{
            //retrieving sessionfactory from session
            SessionObject.getStarted((SessionFactory) session.getAttribute("sessionfactory"));
            SessionObject.newTransaction();           

            List lista;
            Iterator it;
            int index;
            switch(type) {

                //take_cliente
                case 0:// Getting the checklist of CLIENTE
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

                //take_oggettofornitura
                case 1: // Getting the checklist of OGGETTOFORNITURA
                    lista = Progetto.executeQuery("select distinct oggettoFornitura from Progetto");
                    it = lista.iterator();
                    index = 0;
                    while(it.hasNext()) {
                        String of = (String) it.next();
                        out.println("<oggettoFornitura idName=\""+index+"\">"+escapeChars(of)+"</oggettoFornitura>");
                        index++;
                    }
                    //out.println("<oggettoFornitura idName="1">nomeCliente1</oggettoFornitura>");
                    break;

                //take_reparto
                case 2:// Getting the checklist of REPARTO
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
                //give_newproject
                case 101:
                {
                    //reading the data of the new created project to insert them into a session variable

                    //reading project from request
                    Progetto p = extractProjectFromRequestDummy(request);/*XXX sostituire con funzione effettiva*/
                    //saving it into session variable
                    session.setAttribute("Progetto", p);
                }
                    break;
                //take_risksbygroup
                case 3:
                    //giving to user the list of suggested risks for each configured group
                    /*XXX da completare suggerimento rischi*/
                    int groupnumber = getNumberOfGroupsDummy();//XXX andrà letto da file di configurazione
                    index = 0;//index for the xml object to be created from the interface
                    for(int i=0; i<groupnumber; i++){
                        out.println("<gruppo idName=\""+i+"\">\n\t<nomeGruppo>Gruppo "+i+"</nomeGruppo>");
                        lista = risksGroupDummy(groupnumber);//XXX sostituire con la funzione vera
                        it = lista.iterator();
                        while(it.hasNext()){
                            Rischio r = (Rischio) it.next();
                            printRisk(r,out,index);
                            index++;
                        }
                        out.println("</gruppo>");
                    }
                    break;
                //take_risknogroup
                case 4:
                    //getting the common risks, that not appear in any group
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
                //take_risksbycategory
                case 5:
                case 10: //take_allchkrisks
                    //giving to user all the risks in DB, also if not suggested in any other case
                    out.println("<root label=\"Tutte le Categorie\" type=\"fuori\">\n"+
                                    "\t<node label=\"Rischi non suggeriti\" type=\"categoria\" >");
                    index=0;//xml identifier
                    lista = notSuggestedCkRisks();/*XXX pensare: tutti o solo quelli non selezionati prima?*/
                    it = lista.iterator();
                    while(it.hasNext()){
                        CkRischi r = (CkRischi) it.next();
                        out.println("\t\t<node codiceChecklist=\""+r.getCodChecklist()+
                                    "\" label=\""+escapeChars(r.getDescrizione())+"\" type=\"rischio\" />");
                    }
                    out.println("\t</node>\n</root>");
                    break;
                //give_risksbycategory
                case 102:
                {
                    //reading selected risks
                    String data = request.getParameter("data");
                    //data contains checklist risk codes separated by #
                    String[] ckIds = data.split(" ");
                    //storing ckIds into session
                    session.setAttribute("ckIds", ckIds);
                }
                    break;
                //take_selectedrisksbycategory
                case 13:
                {
                    //giving to user risks previously read
                    String[] ckIds = (String[]) session.getAttribute("ckIds");
                    if(ckIds == null){
                        out.println("<error>No risk selected from checklist</error>");
                        break;
                    }
                    index = 0;
                    //checking if array contains at least a number
                    if(ckIds.length==1 && ckIds[0].trim().compareTo("")==0)
                        break;

                    //if so, print
                    out.println("<root idName=\"Selected\">");
                    for(int i=0; i<ckIds.length; i++){
                        CkRischi r = (CkRischi) CkRischi.getById(CkRischi.class, Integer.parseInt(ckIds[i].trim()));
                        if(r!=null)
                            out.println("\t<rischio idName=\""+(index++)+"\">"+
                                        "\t\t<codiceChecklist>"+ckIds[i]+"</codiceChecklist>"+
                                        "\t\t<stato></stato>"+
                                        "\t\t<categoria></categoria>"+
                                        "\t\t<rVer></rVer>"+
                                        "\t\t<descrizione>"+escapeChars(r.getDescrizione())+"</descrizione>"+
                                        "\t\t<contingency></contingency>"+
                                        "\t\t<causa></causa>"+
                                        "\t\t<effetto></effetto>"+
                                        "\t\t<probIniziale></probIniziale>"+
                                        "\t\t<impattoIniziale></impattoIniziale>"+
                                    "\t</rischio>");
                    }
                    out.println("</root>");
                }
                    break;
                //give_allrisksforproject
                case 103:
                    //user give me data about all risks added to the current project
                    //save them into session

                    //reading risks from the current request
                    lista = extractRisksFromRequestDummy(request);/*XXX sostituire con vera funzione*/
                    //saving these datas into the current session
                    session.setAttribute("RisksAddedToProject",lista);
                    break;
                //take_actionsbyrisk
                case 6:
                {
                    //giving user all the suggested actions from previously selected risks
                    /*XXX*/

                    //reading project from session
                    Progetto p = (Progetto) session.getAttribute("Progetto");
                    if(p==null){
                        //terminating: if a suggestion has to be made, a project has to be previously created
                        out.println("<error>Erorr: must create a project before request suggestion</error>");
                        break;
                    }
                    //reading given risks from session
                    List riskList = (List<Rischio>) session.getAttribute("RisksAddedToProject");
                    if(riskList==null || riskList.isEmpty()){
                        //terminating: as previuos check
                        out.println("<error>Erorr: must choose risks before adding actions</error>");
                        break;
                    }

                    //if passed previous checks, we have project and risks. Give suggestions
                    index = 0; //action index
                    int riskIndex = 0; //risk index

                    //for each risk, giving correspondent suggestion
                    Iterator riskIt = riskList.iterator();
                    while(riskIt.hasNext()){
                        Rischio r = (Rischio) riskIt.next();
                        lista = suggestActionsDummy(p,r);//XXX replace with real function
                        out.println("<rischio idName=\""+(riskIndex++)+"\">\n"+
                                        "\t<codiceChecklist>"+r.getCodiceChecklist()+"</codiceChecklist>");
                        it = lista.iterator();
                        while(it.hasNext()){
                            Azioni a = (Azioni) it.next();
                            out.println("\t<azione idName=\""+(index++)+"\">\n"+
                                        "\t\t<tipo>"+a.getPrimaryKey().getTipo()+"</tipo>"+
                                        "\t\t<stato>"+a.getStato()+"</stato>"+
                                        "\t\t<descrizione>"+escapeChars(a.getDescrizione())+"</descrizione>"+
                                        "\t\t<revisione>"+a.getRevisione()+"</revisione>"+
                                        "\t\t<intensita>"+a.getIntensita()+"</intensita>"+
                                        "\t</azione>");
                        }
                        out.println("</rischio>");
                    }
                }
                    break;
                //give_allactionsforproject
                case 104:
                    //user gives me all actions added to the current project
                    /*XXX format to define*/
                    
                    //reading actions list from request
                    lista = extractActionsFromRequestDummy(request);
                    //saving actions into session
                    session.setAttribute("ActionsAddedToProject",lista);
                    break;
                //take_digest
                case 7:
                {
                    //giving user the entire project, to check it before writing it into DB
                    /*XXX RICORDARSI DI SETTARE LO STORICO IN QUALCHE MODO*/

                    //reading project from session
                    Progetto p = (Progetto) session.getAttribute("Progetto");
                    if(p==null){
                        //terminating: if a suggestion has to be made, a project has to be previously created
                        out.println("<error>Erorr: must create a project before confirm project creation</error>");
                        break;
                    }
                    //reading given risks from session
                    List riskList = (List<Rischio>) session.getAttribute("RisksAddedToProject");
                    if(riskList==null || riskList.isEmpty()){
                        //terminating: as previuos check
                        out.println("<error>Erorr: must choose risks before confirm project creation</error>");
                        break;
                    }
                    //reading given actions from session
                    List actionList = (List<Azioni>) session.getAttribute("ActionsAddedToProject");
                    if(actionList==null || actionList.isEmpty()){
                        //terminating: as previuos check
                        out.println("<error>Erorr: must choose actions before confirm project creation</error>");
                        break;
                    }

                    //if here, all checks are passed. Printing the digest
                    printDigestDummy(p,riskList,actionList,out);
                }
                    break;
                //give_confirm
                case 105:
                    //if data=="true" writes the new project and close session
                    //if data=="false" exits and closes session
                    //server will answer "ok" or "error"
                    /*XXX formato della risposta da definire*/
                    break;
                //take_openedprojects
                case 8:
                {
                    //giving the list of all open projects (that can be modified)

                    //retrieving open projects from DB
                    lista = Progetto.getOpenProjects();

                    //writing a summary to user, to let him choose between them
                    /*XXX definire formato*/
                    it=lista.iterator();
                    out.println("Progetti aperti: ");
                    while(it.hasNext()){
                        Progetto p = (Progetto) it.next();
                        out.println(p.getCodice());
                    }
                }
                    break;
                //give_whatopenedproject
                case 106:
                    //user gives me the identifier of the selected project
                    String idProgramma = (String) request.getAttribute("data");

                    //sending to user all fields of the selected project
                    /*XXX definire formato*/
                    break;
                //take_actionsbyutilization
                case 9:
                {
                    //taking ALL actions from DB, selecting between:
                    //-actions already used for a specified risk
                    //-all oters
                    String riskChecklistCode = (String) request.getParameter("data");
                    String actiontype = (String) request.getParameter("actiontype");
                    String table;
                    if(actiontype.trim().compareTo("r")==0) table = "Recovery";
                    else if(actiontype.trim().compareTo("m")==0) table = "Mitigazione";
                    else break;
                    out.println("<root label=\"Tutte le Categorie\" type=\"fuori\">\n"+
                                    "\t<node label=\"Azioni "+table+"\" type=\"categoria\" >");
                    /*XXX modificare per fornire separatamente quelle consigliate per il rischio*/
                    lista = persistentBase.executeQuery("from Ck"+table);
                    it = lista.iterator();
                    while(it.hasNext()){
                        Descrizione d = (Descrizione) it.next();
                        out.println("\t\t<node codiceChecklist=\""+d.getCodChecklist()+
                                    "\" label=\""+escapeChars(d.getDescrizione())+"\" type=\"rischio\" />");
                    }
                    out.println("\t</node>\n</root>");
                }
                    break;
                //give_mx
                case 107:
                    //user gives me all the project with modifications,i will read it and write on DB
                    /*XXX definire formato di scambio*/
                    break;
                //give_newchkrisk
                case 108:
                {
                    //user gives me the description of the new risk to add to the system
                    String descrizione = request.getParameter("data");
                    int newKey = CkRischi.generateAutoKey();
                    //inserting the new risk in database
                    CkRischi ckr = new CkRischi();
                    ckr.setFields(newKey, descrizione);
                    ckr.write();
                }
                    break;
                //give_updatechkrisk
                case 109:
                {
                    //user gives codchecklist and new description for the risk. I will update that decsription
                    String descrizione = request.getParameter("data");
                    Integer codChecklist = Integer.parseInt(request.getParameter("codicechecklist").trim());
                    CkRischi chk = (CkRischi) CkRischi.getById(CkRischi.class, codChecklist);
                    chk.setDescrizione(escapeChars(descrizione));
                    chk.update();
                }
                    break;
                //take_allchkactions
                case 11:
                {
                    //giving to user all the action in the checklist
                    index=0;//xml identifier
                    String table;
                    out.println("<root label=\"Tutte le Categorie\" type=\"fuori\">\n");
                    for(int i=0; i<2; i++){
                        if(i == 0) table="Mitigazione";
                        else table = "Recovery";

                        lista = CkMitigazione.executeQuery("from Ck"+table);
                        out.println("\t<node label=\""+table+"\" type=\"categoria\" >");
                        it = lista.iterator();
                        while(it.hasNext()){
                            Descrizione d = (Descrizione) it.next();
                            out.println("\t\t<node codiceChecklist=\""+d.getCodChecklist()+
                                        "\" label=\""+escapeChars(d.getDescrizione())+"\" type=\""+table+"\" />");
                        }
                        out.println("\t</node>\n");
                    }
                    out.println("</root>");
                }
                    break;
                //give_newchkaction
                case 110:
                {
                    //user gives me a new action in the checklist to create
                    String descrizione = request.getParameter("data");
                    String tipo = request.getParameter("tipo");//'r' or 'm'
                    Integer codChecklist;
                    if(tipo.compareTo("r")==0){
                        codChecklist= (Integer) CkRecovery.generateAutoKey();
                        CkRecovery rec = new CkRecovery();
                        rec.setFields(codChecklist, descrizione);
                        rec.write();
                    }else if(tipo.compareTo("m")==0){
                        codChecklist= (Integer) CkMitigazione.generateAutoKey();
                        CkMitigazione mit = new CkMitigazione();
                        mit.setFields(codChecklist,descrizione);
                        mit.write();
                    }
                }
                    break;
                //give_updatechkaction
                case 111:
                {
                    //user gives me checklist code and type of action to update. I will update the action description
                    String descrizione = request.getParameter("data");
                    String tipo = request.getParameter("tipo");//'r' or 'm'
                    Integer codChecklist = Integer.parseInt(request.getParameter("codicechecklist"));
                    if(tipo.compareTo("r")==0){
                        CkRecovery rec = (CkRecovery) CkRecovery.getById(CkRecovery.class, codChecklist);
                        rec.setDescrizione(escapeChars(descrizione));
                        rec.update();
                    }else if(tipo.compareTo("m")==0){
                        CkMitigazione mit = (CkMitigazione) CkMitigazione.getById(CkMitigazione.class, codChecklist);
                        mit.setDescrizione(escapeChars(descrizione));
                        mit.update();
                    }

                }
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

    /*UTILITY FUNCTIONS*/

    //prints a risk in xml format into the stream 'out'
    private void printRisk(Rischio r, PrintWriter out, int index){
        out.println("\t<rischio idName=\""+index+"\">"+
                        "\t\t<idRischio>"+r.getCodice()+"</idRischio>"+
                        "\t\t<codiceChecklist>"+r.getCodiceChecklist()+"</codiceChecklist>"+
                        "\t\t<stato>"+r.getStato().getStato()+"</stato>"+
                        "\t\t<categoria>"+r.getCategoria()+"</categoria>"+
                        "\t\t<rVer>"+r.getVerificato()+"</rVer>"+
                        "\t\t<descrizione>"+escapeChars(r.getDescrizione())+"</descrizione>"+
                        "\t\t<contingency>"+r.getContingency()+"</contingency>"+
                        "\t\t<causa>"+escapeChars(r.getCausa())+"</causa>"+
                        "\t\t<effetto>"+escapeChars(r.getEffetto())+"</effetto>"+
                        "\t\t<probIniziale>"+r.getProbabilitaIniziale()+"</probIniziale>"+
                        "\t\t<impattoIniziale>"+r.getImpattoIniziale()+"</impattoIniziale>"+
                    "\t</rischio>");
        return;
    }
    //function that escapes characters to print into xml
    private String escapeChars(String s){
        /*XXX finire escaping*/
        return s.replace('"', '\'');
    }
    //function that returns the number of groups configured by the user
    private int getNumberOfGroups(){
        /*XXX FILL*/
        return 0;
    }
    //function that return the suggested risks for the group 'groupnumber' into a List
    private List risksGroup(int groupnumber){
        return new LinkedList();
    }
    //function to create a list of risks from the current request
    private List extractRisksFromRequest(HttpServletRequest request){
        return new LinkedList();
    }
    //function to extract a project from the current request
    private Progetto extractProjectFromRequest(HttpServletRequest request){
        return new Progetto();
    }
    //function to suggest actions for the 'p' project and the 'r' risk
    private List suggestActions(Progetto p, Rischio r){
        return new LinkedList();
    }
    //function to extract actions from the current request
    private List extractActionsFromRequest(HttpServletRequest request){
        return new LinkedList();
    }
    //writes digest in xml
    private void printDigest(Progetto p, List riskList, List actionList, PrintWriter out){
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
            query = query + "where codChecklist != "+(Integer) it.next();
        while(it.hasNext()){
            Integer current = (Integer) it.next();
            query = query + " and codChecklist != "+current;
        }
        try{
            return Rischio.executeQuery(query);
        } catch (Exception e){}

        return new LinkedList();
    }


    /*DUMMY FUNCTIONS USED AS STUBS*/
    private int getNumberOfGroupsDummy(){
        return 3;
    }
    private List risksGroupDummy(int groupnumber){
        try{
            return Rischio.executeQuery("from Rischio where idProgramma = 'P"+groupnumber+"'");
        } catch (Exception e){}

        return new LinkedList();
    }
    private List extractRisksFromRequestDummy(HttpServletRequest request){
        try{
            return Rischio.executeQuery("from Rischio where codice = 'P1R1'");
        } catch (Exception e){}
        
        return new LinkedList();
    }
    private Progetto extractProjectFromRequestDummy(HttpServletRequest request){
        try{
            Progetto p = (Progetto) Progetto.getById(Progetto.class, "P1");
            p.setCodice(Progetto.generateAutoKey());
            return p;
        } catch (Exception e) {}

        return new Progetto();
    }
    private List suggestActionsDummy(Progetto p, Rischio r){
        try{
            return Azioni.executeQuery("from Azioni where primaryKey.idRischio = 'P1R1'");
        } catch (Exception e){}

        return new LinkedList();
    }
    private List extractActionsFromRequestDummy(HttpServletRequest request){
        try{
            return Azioni.executeQuery("from Azioni where primaryKey.idRischio = 'P1R1'");
        }catch(Exception e){}

        return new LinkedList();
    }
    private void printDigestDummy(Progetto p, List riskList, List actionList, PrintWriter out){
        out.println("<description>Descrizione del progetto con tutti i suoi campi</description>");
        return;
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
