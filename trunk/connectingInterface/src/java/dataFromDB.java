import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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

            /*XXX RIGHE DI DEBUG PER TESTING DAVID*/
            session.setAttribute("Progetto", Progetto.getById(Progetto.class, "P2"));
            session.setAttribute("RisksAddedToProject", extractRisksFromRequestDummy(request));
            session.setAttribute("ActionsAddedToProject", extractActionsFromRequestDummy(request));
            /*END*/
            List lista;
            Iterator it;
            int index=0;
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
                {
                    //giving to user the list of suggested risks for each configured group
                    /*XXX da completare suggerimento rischi*/

                    //checking if project already defined
                    Progetto p = (Progetto) session.getAttribute("Progetto");
                    if(p==null){
                        //terminating: if a suggestion has to be made, a project has to be previously created
                        out.println("<error>Erorr: must create a project before asking suggested risks</error>");
                        break;
                    }

                    int groupnumber = getNumberOfGroupsDummy();//XXX sostituire non Dummy
                    index = 0;//index for the xml object to be created from the interface
                    for(int i=0; i<groupnumber; i++){
                        out.println("<gruppo idName=\""+i+"\">\n\t<nomeGruppo>Gruppo "+i+"</nomeGruppo>");
                        lista = risksGroupDummy(groupnumber);//XXX sostituire con la funzione vera
                        it = lista.iterator();
                        while(it.hasNext()){
                            Rischio r = (Rischio) it.next();
                            //filling not suggestable fields
                            fillRiskNotSuggestableFields(r, session);
                            //printing risk
                            printRisk(r,out,index,false);
                            index++;
                        }
                        out.println("</gruppo>");
                    }
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
                        //filling not suggestable fields
                        fillRiskNotSuggestableFields(r, session);
                        //printing risk
                        printRisk(r, out, index,false);
                        index++;
                    }
                    out.println("</root>");
                    break;
                //take_risksbycategory
                case 5:
                case 10: //take_allchkrisks
                    //giving to user all the risks in DB, also if not suggested in any other case
                    out.println("<root label=\"Tutte le Categorie\" type=\"fuori\">\n"+
                                    "\t<node label=\"Tutti i rischi\" type=\"categoria\" >");
                    index=0;//xml identifier
                    lista = CkRischi.executeQuery("from CkRischi");//notSuggestedCkRisks();/*XXX pensare: tutti o solo quelli non selezionati prima?*/
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
                                        "\t<idRischio>"+r.getCodice()+"</idRischio>");
                        it = lista.iterator();
                        int identifier = 1;
                        while(it.hasNext()){
                            Azioni a = (Azioni) it.next();
                            //retrieving description
                            Descrizione dobj;
                            if(a.getPrimaryKey().getTipo() ==  'M') 
                                dobj = (Descrizione) CkMitigazione.getById(CkMitigazione.class, a.getPrimaryKey().getIdAzione());
                            else if(a.getPrimaryKey().getTipo() ==  'R')
                                dobj = (Descrizione) CkRecovery.getById(CkRecovery.class, a.getPrimaryKey().getIdAzione());
                            else
                                break;
                            //setting description
                            a.setDescrizione(dobj.getDescrizione());

                            //printing action
                            out.println("\t<azione idName=\""+(index++)+"\">\n"+
                                        "\t\t<identifier>"+(identifier++)+"<identifier>"+
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
                //give_confirm
                case 105:
                {
                    //if data=="true" writes the new project and close session
                    //if data=="false" exits and closes session
                    //server will answer "ok" or "error"
                    /*XXX formato della risposta da definire E testare da interfaccia con David*/

                    Boolean confirm = Boolean.parseBoolean((String) session.getAttribute("data"));
                    if(!confirm){
                        //closing session
                        session.invalidate();
                        break;
                    }
                    //if confirmed, build the project from session data
                    //!!!using the next "case" statement
                    index=1;//using index as a flag

                }
                //take_newidrischio
                case 14:
                {
                    //generating a new id for risk
                    String riskid = generateRiskId(session);
                    if(riskid==null){
                        out.println("<error>Erorr: must create a project before asking risk ids</error>");
                        break;
                    }
                    out.println(riskid);
                }
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
                    if(index == 0) //it means: we are executing "take_digest
                        printDigestDummy(p,riskList,actionList,out);


                    else if(index == 1){//it means: we are executing give_confirm
                        //building project and storing into DB
                        Progetto complete = buildProject(p, riskList, actionList);
                        if(complete == null)
                            out.println("<error>ERROR</error>");
                        else{
                            complete.salvaProgetto();//XXX controllare dove e quando viene generata la chiave del progetto
                            out.println("OK");
                        }
                    }
                }
                    break;
                //take_openedprojects
                case 8:
                {
                    //giving the list of all open projects (that can be modified)

                    //retrieving open projects from DB
                    lista = Progetto.getOpenProjects();

                    //writing a summary to user, to let him choose between them
                    it=lista.iterator();
                    index = 1;
                    while(it.hasNext()){
                        Progetto p = (Progetto) it.next();
                        out.println("<progetto idName=\""+(index++)+"\">"+
                                        "\t<idProgramma>"+p.getCodice()+"</idProgramma>"+
                                        "\t<oggettoFornitura>"+p.getOggettoFornitura()+"</oggettoFornitura>"+
                                        "\t<cliente>"+p.getNomeCliente()+"</cliente>"+
                                    "</progetto>");
                    }
                }
                    break;
                //give_whatopenedproject
                case 106:
                {
                    //user gives me the identifier of the selected project
                    String idProgramma = request.getParameter("data");
                    session.setAttribute("idProgramma", idProgramma);
                }
                    break;
                //take_whatopenedproject
                case 15:
                {
                    //giving project to user
                    String idProgramma = (String) session.getAttribute("idProgramma");
                    if(idProgramma == null || idProgramma.isEmpty()){
                        out.println("<error>No project requested</error>"+idProgramma);
                        break;
                    }

                    //retrieving project from DB
                    Progetto p = (Progetto) Progetto.getById(Progetto.class, idProgramma);
                    p.caricaRischi();

                    //printing project
                    printProject(p, out);
                }
                    break;
                //take_actionsbyutilization
                case 9:
                {
                    //taking ALL actions from DB, selecting between:
                    //-actions already used for a specified risk
                    //-all others
                    String riskChecklistCode = (String) request.getParameter("data");
                    String actiontype = (String) request.getParameter("actiontype");
                    String table;
                    if(actiontype.trim().compareTo("R")==0) table = "Recovery";
                    else if(actiontype.trim().compareTo("M")==0) table = "Mitigazione";
                    else break;
                    out.println("<root label=\"Tutte le Categorie\" type=\"fuori\">\n"+
                                    "\t<node label=\"Azioni "+table+"\" type=\"categoria\" >");
                    
                    /*preferred actions for this risk are the actions that were
                     previously added to a correspondent risk into a previous project
                     now, building a query to find that actions*/
                    String preferredQuery = "from Ck"+table+" where "+
                                            "codChecklist in (" +
                                            "select primaryKey.idAzione from Azioni where " +
                                            "primaryKey.idRischio in (" +
                                            "select codice from Rischio as R where " +
                                            "R.codiceChecklist = "+riskChecklistCode+") " +
                                            "and primaryKey.tipo = '"+actiontype.trim()+"')";

                    List preferredActions = persistentBase.executeQuery(preferredQuery);

                    /*taking now all actions not previously taken*/
                    String otherQuery = "from Ck"+table+" where "+
                                        "codChecklist not in (" +
                                        "select primaryKey.idAzione from Azioni where " +
                                        "primaryKey.idRischio in (" +
                                        "select codice from Rischio as R where " +
                                        "R.codiceChecklist = "+riskChecklistCode+") " +
                                        "and primaryKey.tipo = '"+actiontype.trim()+"')";

                    lista = persistentBase.executeQuery(otherQuery);

                    //printing preferred actions
                    it = preferredActions.iterator();
                    out.println("\t\t<node label=\"Azioni comuni per questo rischio\" type=\"comuni\" >");
                    while(it.hasNext()){
                        Descrizione d = (Descrizione) it.next();
                        out.println("\t\t\t<node codiceChecklist=\""+d.getCodChecklist()+
                                    "\" label=\""+escapeChars(d.getDescrizione())+"\" type=\"azione\" />");
                    }
                    out.println("\t\t</node>");

                    //printing other actions
                    out.println("\t\t<node label=\"Altre azioni\" type=\"altre\" >");
                    it = lista.iterator();
                    while(it.hasNext()){
                        Descrizione d = (Descrizione) it.next();
                        out.println("\t\t\t<node codiceChecklist=\""+d.getCodChecklist()+
                                    "\" label=\""+escapeChars(d.getDescrizione())+"\" type=\"rischio\" />");
                    }
                    out.println("\t\t</node>\n\t</node>\n</root>");
                }
                    break;
                //give_mx
                case 107:
                    //user gives me all the project with modifications,i will read it and write on DB
                    /*XXX definire formato di scambio
                     considerare di usare formati già usati per azioni e rischi prima*/
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
                    //user gives codchecklist and new description for the risk. I will update that description
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
                    if(tipo.compareTo("R")==0){
                        codChecklist= (Integer) CkRecovery.generateAutoKey();
                        CkRecovery rec = new CkRecovery();
                        rec.setFields(codChecklist, descrizione);
                        rec.write();
                    }else if(tipo.compareTo("M")==0){
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
                    if(tipo.compareTo("R")==0){
                        CkRecovery rec = (CkRecovery) CkRecovery.getById(CkRecovery.class, codChecklist);
                        rec.setDescrizione(escapeChars(descrizione));
                        rec.update();
                    }else if(tipo.compareTo("M")==0){
                        CkMitigazione mit = (CkMitigazione) CkMitigazione.getById(CkMitigazione.class, codChecklist);
                        mit.setDescrizione(escapeChars(descrizione));
                        mit.update();
                    }

                }
                    break;
                //closesession
                case 12:
                    session.invalidate();
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

    //prints a project in xml format into stream 'out'
    private void printProject(Progetto p, PrintWriter out){
        out.println("<progetto idName=\""+0+"\">\n"+
                        "\t<codice>"+p.getCodice()+"</codice>\n"+
                        "\t<reparto>"+p.getReparto()+"</reparto>\n"+
                        "\t<classeDiRischio>"+p.getClasseRischio()+"</classeDiRischio>\n"+
                        "\t<valoreEconomico>"+p.getValoreEconomico()+"</valoreEconomico>\n"+
                        "\t<durataContratto>"+p.getDurataContratto()+"</durataContratto>\n"+
                        "\t<oggettoFornitura>"+p.getOggettoFornitura()+"</oggettoFornitura>\n"+
                        "\t<nomeCliente>"+p.getNomeCliente()+"</nomeCliente>\n"+
                        "\t<!-- LIVELLO DI RISCHIO -->\n"+
                        "\t<rp1>"+p.getPaese().getR1()+"</rp1>" +
                        " <rp2>"+p.getPaese().getR2()+"</rp2>" +
                        " <rp3>"+p.getPaese().getR3()+"</rp3>\n"+
                        "\t<rmc1>"+p.getMercatoCliente().getR1()+"</rmc1>" +
                        " <rmc2>"+p.getMercatoCliente().getR2()+"</rmc2>" +
                        " <rmc3>"+p.getMercatoCliente().getR3()+"</rmc3>\n"+
                        "\t<rc1>"+p.getContratto().getR1()+"</rc1>" +
                        " <rc2>"+p.getContratto().getR2()+"</rc2>" +
                        " <rc3>"+p.getContratto().getR3()+"</rc3>\n"+
                        "\t<rcp1>"+p.getComposizionePartnership().getR1()+"</rcp1>" +
                        " <rcp2>"+p.getComposizionePartnership().getR2()+"</rcp2>" +
                        " <rcp3>"+p.getComposizionePartnership().getR3()+"</rcp3>\n"+
                        "\t<ri1>"+p.getIngegneria().getR1()+"</ri1>" +
                        " <ri2>"+p.getIngegneria().getR2()+"</ri2>" +
                        " <ri3>"+p.getIngegneria().getR3()+"</ri3>\n"+
                        "\t<ra1>"+p.getApprovvigionamento().getR1()+"</ra1>" +
                        " <ra2>"+p.getApprovvigionamento().getR2()+"</ra2>" +
                        " <ra3>"+p.getApprovvigionamento().getR3()+"</ra3>\n"+
                        "\t<rf1>"+p.getFabbricazione().getR1()+"</rf1>" +
                        " <rf2>"+p.getFabbricazione().getR2()+"</rf2>" +
                        " <rf3>"+p.getFabbricazione().getR3()+"</rf3>\n"+
                        "\t<rm1>"+p.getMontaggio().getR1()+"</rm1>" +
                        " <rm2>"+p.getMontaggio().getR2()+"</rm2>" +
                        " <rm3>"+p.getMontaggio().getR3()+"</rm3>\n"+
                        "\t<rav1>"+p.getAvviamento().getR1()+"</rav1>" +
                        " <rav2>"+p.getAvviamento().getR2()+"</rav2>" +
                        " <rav3>"+p.getAvviamento().getR3()+"</rav3>\n"+
                        "\t<!-- IMPATTO STRATEGICO -->\n"+
                        "\t<IM>"+p.getIm()+"</IM>\n"+
                        "\t<IC>"+p.getIc()+"</IC>\n"+
                        "\t<IP>"+p.getIp()+"</IP>\n"+
                        "\t<IA>"+p.getIa()+"</IA>\n"+
                        "\t<IPP>"+p.getIpp()+"</IPP>\n"+
                        "\t<!-- ABILITAZIONE DEI CAMPI -->\n"+
                        "\t<ckrp1>"+(p.getPaese().getR1() != null)+"</ckrp1> <ckrp2>"+(p.getPaese().getR2() != null)+"</ckrp2> <ckrp3>"+(p.getPaese().getR3() != null)+"</ckrp3>\n"+
                        "\t<ckrmc1>"+(p.getMercatoCliente().getR1() != null)+"</ckrmc1> <ckrmc2>"+(p.getMercatoCliente().getR2() != null)+"</ckrmc2> <ckrmc3>"+(p.getMercatoCliente().getR3() != null)+"</ckrmc3>\n"+
                        "\t<ckrc1>"+(p.getContratto().getR1() != null)+"</ckrc1> <ckrc2>"+(p.getContratto().getR2() != null)+"</ckrc2> <ckrc3>"+(p.getContratto().getR3() != null)+"</ckrc3>\n"+
                        "\t<ckrcp1>"+(p.getComposizionePartnership().getR1() != null)+"</ckrcp1> <ckrcp2>"+(p.getComposizionePartnership().getR2() != null)+"</ckrcp2> <ckrcp3>"+(p.getComposizionePartnership().getR3() != null)+"</ckrcp3>\n"+
                        "\t<ckri1>"+(p.getIngegneria().getR1() != null)+"</ckri1> <ckri2>"+(p.getIngegneria().getR2() != null)+"</ckri2> <ckri3>"+(p.getIngegneria().getR3() != null)+"</ckri3>\n"+
                        "\t<ckra1>"+(p.getApprovvigionamento().getR1() != null)+"</ckra1> <ckra2>"+(p.getApprovvigionamento().getR2() != null)+"</ckra2> <ckra3>"+(p.getApprovvigionamento().getR3() != null)+"</ckra3>\n"+
                        "\t<ckrf1>"+(p.getFabbricazione().getR1() != null)+"</ckrf1> <ckrf2>"+(p.getFabbricazione().getR2() != null)+"</ckrf2> <ckrf3>"+(p.getFabbricazione().getR3() != null)+"</ckrf3>\n"+
                        "\t<ckrm1>"+(p.getMontaggio().getR1() != null)+"</ckrm1> <ckrm2>"+(p.getMontaggio().getR2() != null)+"</ckrm2> <ckrm3>"+(p.getMontaggio().getR3() != null)+"</ckrm3>\n"+
                        "\t<ckrav1>"+(p.getAvviamento().getR1() != null)+"</ckrav1> <ckrav2>"+(p.getAvviamento().getR2() != null)+"</ckrav2> <ckrav3>"+(p.getAvviamento().getR3() != null)+"</ckrav3>\n"+
                        "\t<ckIM>"+(p.getIm().getValue() != -1)+"</ckIM>\n"+
                        "\t<ckIC>"+(p.getIc().getValue() != -1)+"</ckIC>\n"+
                        "\t<ckIP>"+(p.getIp().getValue() != -1)+"</ckIP>\n"+
                        "\t<ckIA>"+(p.getIa().getValue() != -1)+"</ckIA>\n"+
                        "\t<ckIPP>"+(p.getIpp().getValue() != -1)+"</ckIPP>\n");
        //writing risks
        if(p.getRischi() != null){
            List rischi = p.getRischi();
            Iterator it = rischi.iterator();
            int index = 1;
            while(it.hasNext()){
                Rischio r = (Rischio) it.next();
                printRisk(r, out, index++, true);
            }
        }
        out.println("</progetto>");
        return;
    }
    //prints a risk in xml format into the stream 'out'. If printActions == true prints also all actions
    private void printRisk(Rischio r, PrintWriter out, int index, boolean printActions){
        out.println("\t<rischio idName=\""+index+"\">\n"+
                        "\t\t<idRischio>"+r.getCodice()+"</idRischio>\n"+
                        "\t\t<codiceChecklist>"+r.getCodiceChecklist()+"</codiceChecklist>\n"+
                        "\t\t<stato>"+r.getStato().getStato()+"</stato>\n"+
                        "\t\t<categoria>"+r.getCategoria()+"</categoria>\n"+
                        "\t\t<rVer>"+r.getVerificato()+"</rVer>\n"+
                        "\t\t<descrizione>"+escapeChars(r.getDescrizione())+"</descrizione>\n"+
                        "\t\t<contingency>"+r.getContingency()+"</contingency>\n"+
                        "\t\t<causa>"+escapeChars(r.getCausa())+"</causa>\n"+
                        "\t\t<effetto>"+escapeChars(r.getEffetto())+"</effetto>\n"+
                        "\t\t<probIniziale>"+r.getProbabilitaIniziale()+"</probIniziale>\n"+
                        "\t\t<impattoIniziale>"+r.getImpattoIniziale()+"</impattoIniziale>\n"+
                        "\t\t<costoPotenzialeImpatto>"+r.getCostoPotenzialeImpatto()+"</costoPotenzialeImpatto>\n");
        //printing actions
        if(printActions && r.getAzioni() != null){
            List actions = r.getAzioni();
            Iterator it = actions.iterator();
            int actIndex = 1;
            while(it.hasNext()){
                Azioni a = (Azioni) it.next();
                printAction(a,out,actIndex++);
            }
        }
        out.println("\t</rischio>");
        return;
    }
    //prints an action in xml format into the stream 'out'
    private void printAction(Azioni a, PrintWriter out, int index){
        out.println("\t<azione idName=\""+index+"\">\n"+
			"\t\t<idAzione>"+a.getPrimaryKey().getIdAzione()+"</idAzione>\n" +
                        "\t\t<identifier>"+a.getPrimaryKey().getIdentifier()+"</identifier>\n"+
			"\t\t<tipo>"+a.getPrimaryKey().getTipo()+"</tipo>\n"+
			"\t\t<stato>"+a.getStato()+"</stato>\n"+
			"\t\t<descrizione>"+escapeChars(a.getDescrizione())+"</descrizione>\n"+
			"\t\t<revisione>"+a.getRevisione()+"</revisione>\n"+
			"\t\t<intensita>"+a.getIntensita()+"</intensita>\n"+
                    "\t</azione>");
        return;
    }
    //function that escapes characters to print into xml
    private String escapeChars(String s){
        s=s.replace('"', '\'');
        s=s.replaceAll("\\ufffd", "a\'");
        /*XXXString[] splitted = s.split("Monitoring costante delle attivit");
        if(!(splitted.length == 1 && splitted[0].trim().compareTo("")!=0)){
            StringBuilder sb = new StringBuilder();

            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%h",""+splitted[1].charAt(0)+"");
            return sb.toString();
        }*/
        return s;
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
    //function to build a project from alla datas passed as argument
    private Progetto buildProject(Progetto p, List  riskList, List actionList){
        try{
            //checking if project has a valid identifier
            if(!Progetto.checkAvailable(p.getCodice()))
                return null;
            //adding all risks to the project
            Iterator it = riskList.iterator();
            while(it.hasNext()){
                Rischio r = (Rischio) it.next();
                //adding actions to risk
                Iterator ait = actionList.iterator();
                int idM = 1;//id for mitigation actions
                int idR = 1;//id for recovery actions
                while(it.hasNext()){
                    Azioni a = (Azioni) ait.next();
                    //action for the current risk
                    if(a.getPrimaryKey().getIdRischio().compareTo(r.getCodice()) == 0){
                        //setting idAzioni
                        if(a.getPrimaryKey().getTipo() == 'R')
                            a.getPrimaryKey().setIdAzione(idR++);
                        else if(a.getPrimaryKey().getTipo() == 'M')
                            a.getPrimaryKey().setIdAzione(idM++);
                        else
                            return null;
                        r.aggiungiAzione(0, a);
                        actionList.remove(a);
                    }
                }
                //added actions to risk
                /*XXX SUPPONGO OGNI RISCHIO ABBIA GIA' LA GIUSTA CHIAVE!!*/
                p.aggiungiRischio(r);
            }
        } catch (Exception e){
            return null;
        }
        return p;
    }
    //generates risk ids
    private String generateRiskId(HttpSession session) throws Exception {
        //checking if project already defined
        Progetto p = (Progetto) session.getAttribute("Progetto");
        if(p==null){
            //terminating: if a suggestion has to be made, a project has to be previously created
            return null;
        }
        //project defined,charging the list of already given risk ids from session
        LinkedList<String> givenRiskIds = (LinkedList<String>) session.getAttribute("givenRiskIds");
        if(givenRiskIds== null)
            givenRiskIds = new LinkedList<String>();

        String riskid = new String();
        //generating id
        for(int i=1;;i++){
            riskid = p.getCodice()+"R"+i;
            if(Rischio.checkAvailable(riskid) &&
                    !givenRiskIds.contains(riskid)){
                givenRiskIds.add(riskid);
                session.setAttribute("givenRiskIds", givenRiskIds);
                return riskid;
            }
        }
    }
    //fills risk fields that are not suggestable 
    private void fillRiskNotSuggestableFields(Rischio r,HttpSession session) throws Exception{
        //inserting risk identifier into risk
        r.setCodice(generateRiskId(session));
        //inserting default description into risk
        CkRischi ckr = (CkRischi) CkRischi.getById(CkRischi.class, r.getCodiceChecklist());
        r.setDescrizione(ckr.getDescrizione());
    }
    //writes digest in xml
    private void printDigest(Progetto p, List riskList, List actionList, PrintWriter out){
        return;
    }
    /**    
     * @return List of Risks that were not suggested in any group or in the NoGroup list
     */
    /*XXXprivate List notSuggestedCkRisks(){
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
*/

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
            return Rischio.executeQuery("from Rischio where idProgramma = 'P2'");
        } catch (Exception e){}
        
        return new LinkedList();
    }
    private Progetto extractProjectFromRequestDummy(HttpServletRequest request){
        try{
            Progetto p = (Progetto) Progetto.getById(Progetto.class, "P2");
            p.setCodice(Progetto.generateAutoKey());
            return p;
        } catch (Exception e) {}

        return new Progetto();
    }
    private List suggestActionsDummy(Progetto p, Rischio r){
        try{
            return Azioni.executeQuery("from Azioni where primaryKey.idRischio = 'P2R1'");
        } catch (Exception e){}

        return new LinkedList();
    }
    private List extractActionsFromRequestDummy(HttpServletRequest request){
        try{
            return Azioni.executeQuery("from Azioni where primaryKey.idRischio = 'P2R1' or primaryKey.idRischio = 'P2R2'");
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
    /*XXXprivate List<Integer> alreadySuggestedCkRisks(){
        try{
            return (List<Integer>) Rischio.executeQuery("select codChecklist from CkRischi where codChecklist <= 10");
        } catch (Exception e) {}

        return new LinkedList();
    }*/

}
