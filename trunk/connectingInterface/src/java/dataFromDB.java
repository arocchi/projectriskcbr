import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Formatter;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.hibernate.SessionFactory;
import persistentclasses.*;

//similarity imports
import projectriskcbr.config.*;
import jcolibri.casebase.*;
import jcolibri.cbrcore.*;
import jcolibri.connector.*;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.test.*;
import jcolibri.test.test4.*;
import jcolibri.method.retrieve.*;
import jcolibri.method.retrieve.NNretrieval.*;
import jcolibri.method.retrieve.NNretrieval.similarity.global.*;
import jcolibri.method.retrieve.NNretrieval.similarity.local.*;
import jcolibri.method.retrieve.selection.SelectCases;
import persistentclasses.attributes.*;
import persistentclasses.utils.AzioniSuggester;
import persistentclasses.utils.RischioSuggester;

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
                    break;
                //give_newproject
                case 101:
                {
                    //reading the data of the new created project to insert them into a session variable

                    //reading project from request
                    Progetto p = extractProjectFromRequest(request, out);

                    p.setIsCase(false);

                    p.setIsOpen(true);

                    //setting auto code for project
                    p.setCodice(Progetto.generateAutoKey());

                    //saving it into session varaiable
                    session.setAttribute("Progetto", p);
                    //making suggestions
                    suggestions(p, session, out);
                    out.println("ok");
                    
                }
                    break;
                //take_risksbygroup
                case 3:
                {
                    //giving to user the list of suggested risks for each configured group

                    //checking if project already defined
                    Progetto p = (Progetto) session.getAttribute("Progetto");
                    if(p==null){
                        //terminating: if a suggestion has to be made, a project has to be previously created
                        out.println("<error>Erorr: must create a project before asking suggested risks</error>");
                        break;
                    }

                    int groupnumber = getNumberOfGroups(session);

                    index = 0;//index for the xml object to be created from the interface
                    for(int i=0; i<groupnumber; i++){
                        out.println("<gruppo idName=\""+i+"\">\n\t<nomeGruppo>Gruppo "+i+"</nomeGruppo>");
                        lista = risksGroup(i,session);
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
                //take_risksnogroup
                case 4:
                    //getting the common risks, that not appear in any group
                    out.println("<root idName=\"NoGroup\">");
                    index=0;//xml identifier
                    lista = risknoGroup(session);
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
                    lista = CkRischi.executeQuery("from CkRischi");
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
                                        "\t\t<rVer>0</rVer>"+
                                        "\t\t<descrizione>"+escapeChars(r.getDescrizione())+"</descrizione>"+
                                        "\t\t<contingency>-1</contingency>"+
                                        "\t\t<causa></causa>"+
                                        "\t\t<effetto></effetto>"+
                                        "\t\t<probIniziale>-1</probIniziale>"+
                                        "\t\t<impattoIniziale>-1</impattoIniziale>"+
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
                    lista = extractRisksFromRequest(request,true);
                    //compare given risks to decide if store the project as a case
                    LinkedList<Rischio>[] gruppi = (LinkedList<Rischio>[]) session.getAttribute("gruppi");
                    if(compareModificationsRisks(gruppi, lista)){
                        Progetto p = (Progetto) session.getAttribute("Progetto");
                        p.setIsCase(true);
                        session.setAttribute("Progetto", p);
                    };
                    //saving these datas into the current session
                    session.setAttribute("RisksAddedToProject",lista);
                    out.println("ok");

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
                        lista = suggestActions(session,r,out);
                        out.println("<rischio idName=\""+(riskIndex++)+"\">\n"+
                                        "\t<idRischio>"+r.getCodice()+"</idRischio>");
                        it = lista.iterator();

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
                            if(dobj!=null){
                                a.setDescrizione(dobj.getDescrizione());
                            }else a.setDescrizione("Problem retrieving description for "+a.getPrimaryKey().getIdAzione());

                            //printing action
                            out.println("\t<azione idName=\""+(index++)+"\">\n"+
                                        "\t\t<identifier>"+a.getPrimaryKey().getIdentifier()+"</identifier>"+
                                        "\t\t<idAzione>"+a.getPrimaryKey().getIdAzione()+"</idAzione>"+
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
                {
                    //user gives me all actions added to the current project

                    //reading actions list from request
                    lista = extractActionsFromRequest(request,true,out);
                    //generating correct identifiers for all actions
                    Iterator actIter = lista.iterator();
                    while(actIter.hasNext()){
                        Azioni a = (Azioni) actIter.next();
                        int identifier = generateIdentifier(session, a.getPrimaryKey(),out);
                        a.getPrimaryKey().setIdentifier(identifier);
                    }

                    //checking if any action was modified
                    LinkedList<Azioni> prev = (LinkedList<Azioni>) session.getAttribute("azioni");
                    if(compareModificationsActions(prev, lista, out)){
                        Progetto p = (Progetto) session.getAttribute("Progetto");
                        p.setIsCase(true);
                        session.setAttribute("Progetto", p);
                    }

                    //saving actions into session
                    session.setAttribute("ActionsAddedToProject",lista);
                    out.println("ok");
                    break;
                }
                //give_confirm
                case 105:
                {
                    //if data=="true" writes the new project and close session
                    //if data=="false" exits and closes session
                    //server will answer "ok" or "error"

                    String confirmStr = (String) request.getParameter("data");
                    if(confirmStr == null || !confirmStr.equals("true")){
                        //closing session
                        session.invalidate();
                        out.println("Project not added");
                        break;
                    }
                    //if confirmed, build the project from session data
                    //!!!using the next "case" statement
                    //building project and storing into DB
                    Progetto complete = (Progetto) session.getAttribute("BuildProject");
                    if(complete == null)
                        out.println("<error>ERROR</error>");
                    else{
                        complete.salvaProgetto();
                        out.println("OK");
                    }
                }
                    break;
                //take_digest
                case 7:
                {
                    //giving user the entire project, to check it before writing it into DB
                    
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
                    Progetto c = buildProject(p, riskList, actionList, out, session);
                    session.setAttribute("BuildProject", c);
                    printProject(c, out);
                }
                    break;
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

                    //saving to compare modifications
                    session.setAttribute("Progetto", p);

                    //printing project
                    printProject(p, out);
                }
                    break;
                //give_actionsbyutilization
                case 112:
                {
                    String riskChecklistCode = (String) request.getParameter("data");
                    String actiontype = (String) request.getParameter("actiontype");

                    session.setAttribute("actiontype", actiontype);
                    session.setAttribute("data", riskChecklistCode);
                }
                    break;
                //take_actionsbyutilization
                case 9:
                {
                    //taking ALL actions from DB, selecting between:
                    //-actions already used for a specified risk
                    //-all others

                    String riskChecklistCode = (String) session.getAttribute("data");
                    String actiontype = (String) session.getAttribute("actiontype");
                    String table;
                    if(actiontype.trim().compareTo("R")==0) table = "Recovery";
                    else if(actiontype.trim().compareTo("M")==0) table = "Mitigazione";
                    else break;

                    out.println("<root label=\"Azioni "+table+"\" type=\"fuori\">");
                    
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
                    out.println("\t\t<node label=\"Azioni comuni per questo rischio\" type=\"categoria\" >");
                    while(it.hasNext()){
                        Descrizione d = (Descrizione) it.next();
                        out.println("\t\t\t<node codiceChecklist=\""+d.getCodChecklist()+
                                    "\" label=\""+escapeChars(d.getDescrizione())+"\" type=\"rischio\" />");
                    }
                    out.println("\t\t</node>");

                    //printing other actions
                    out.println("\t\t<node label=\"Altre azioni\" type=\"categoria\" >");
                    it = lista.iterator();
                    while(it.hasNext()){
                        Descrizione d = (Descrizione) it.next();
                        out.println("\t\t\t<node codiceChecklist=\""+d.getCodChecklist()+
                                    "\" label=\""+escapeChars(d.getDescrizione())+"\" type=\"rischio\" />");
                    }
                    out.println("\t\t</node>\n</root>");
                }
                    break;
                //give_mx
                case 107:
                {
                    //user gives me all the project with modifications,i will read it and write on DB
                    //reading project from request

                    //checking if the previous project exists
                    Progetto p = (Progetto) session.getAttribute("Progetto");
                    if(p==null){
                        out.println("<error>Error, no previously opened project to modify</error>");
                        break;
                    }
                    
                    Progetto newProject = extractProjectFromRequest(request, out);
                    List oldRisks = extractRisksFromRequest(request,true);
                    List newRisks = extractRisksFromRequest(request,false);
                    List oldActions = extractActionsFromRequest(request,true,out);
                    List newActions = extractActionsFromRequest(request,false,out);

                    //parse newActions and newRisks to generate identifiers and update storico and THEN build the new project
                    parseNewItems(oldRisks, newRisks, oldActions, newActions, session, out);

                    //merging old risks into newRisks list
                    it = oldRisks.iterator();
                    while(it.hasNext()){
                        Rischio r = (Rischio) it.next();
                        newRisks.add(r);
                    }
                    //merging old actions into newActions list
                    it=oldActions.iterator();
                    while(it.hasNext()){
                        Azioni a = (Azioni) it.next();
                        newActions.add(a);
                    }

                    //old project
                    //Progetto oldProject = (Progetto) session.getAttribute("Progetto");
                    Progetto oldProject = (Progetto) Progetto.getById(Progetto.class, p.getCodice());
                    oldProject.caricaRischi();
                    deleteProject(oldProject, out);

                    //building project with all actions and risks, correctly parsed and updated
                    newProject = buildProject(newProject, newRisks, newActions, out, session);
                    
                    //saving new project
                    newProject.salvaProgetto();

                    //giving response
                    out.println("OK");
                    break;
                }
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
                //closeproject
                case 999:
                {
                    String idP = request.getParameter("data");
                    Progetto pro = (Progetto) Progetto.getById(Progetto.class, idP);
                    pro.setIsOpen(false);
                    pro.update();
                }
                //test
                case 666:
                {
                    /*TEST ROWS FOR RENEWING PROJECTS*/
                    String nome = (String) session.getAttribute("nome");
                    if(nome == null || nome.isEmpty())
                        session.setAttribute("nome", "MIOPRO");

                    String pname = (String) session.getAttribute("nome");
                    session.setAttribute("nome", pname+"0");
                    Progetto newP = generateRandomProject(nome+"0");
                    Progetto oldP = (Progetto) Progetto.getById(Progetto.class, pname);//(Progetto) session.getAttribute("MIOPRO");
                    out.println("here I am");
                    if(oldP == null){
                        newP.salvaProgetto();
                        out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --- Creato progetto nuovo in sessione, scritto su DB");
                        printProject(newP, out);
                    }else{
                        oldP.caricaRischi();
                        //Progetto retrieved = (Progetto) Progetto.getById(Progetto.class, "MIOPRO");
                        //retrieved.caricaRischi();
                        //out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx - RETRIEVED PROJECT");
                        //printProject(retrieved, out);
                        out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -- END");
                        //renewProjects(oldP, newP, out);
                        out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx - WRITTEN PROJECT");
                        printProject(newP, out);
                        out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -- END");
                    }                    
                }
                break;
            }
            SessionObject.endTransaction();
        } catch (Exception e){out.println(e);}        
    }

    /*UTILITY FUNCTIONS*/

    //prints a project in xml format into stream 'out'
    private void printProject(Progetto p, PrintWriter out){
        out.println("<progetto idName=\""+0+"\">");
                        out.println("\t<isCase>"+p.getIsCase()+"</isCase>");
                        out.println("\t<isOpen>"+p.getIsOpen()+"</isOpen>");
                        out.println("\t<codice>"+p.getCodice()+"</codice>");
                        out.println("\t<reparto>"+p.getReparto()+"</reparto>");
                        out.println("\t<classeDiRischio>"+p.getClasseRischio()+"</classeDiRischio>");
                        out.println("\t<valoreEconomico>"+p.getValoreEconomico()+"</valoreEconomico>");
                        out.println("\t<durataContratto>"+p.getDurataContratto()+"</durataContratto>");
                        out.println("\t<oggettoFornitura>"+p.getOggettoFornitura()+"</oggettoFornitura>");
                        out.println("\t<nomeCliente>"+p.getNomeCliente()+"</nomeCliente>");

                        out.println("\t<!-- LIVELLO DI RISCHIO -->");
                        if( p.getPaese() != null){
                            out.println("\t<rp1>"+p.getPaese().getR1()+"</rp1>");
                            out.println(" <rp2>"+p.getPaese().getR2()+"</rp2>");
                            out.println(" <rp3>"+p.getPaese().getR3()+"</rp3>");
                            out.println("\t<ckrp1>"+(p.getPaese().getR1() != null)+"</ckrp1> <ckrp2>"+(p.getPaese().getR2() != null)+"</ckrp2> <ckrp3>"+(p.getPaese().getR3() != null)+"</ckrp3>");
                        } else { out.println("<rp1></rp1><rp2></rp2><rp3></rp3>" +
                                             "<ckrp1>false</ckrp1><ckrp2>false</ckrp2><ckrp3>false</ckrp3>");
                        }

                        if( p.getMercatoCliente() != null){
                            out.println("\t<rmc1>"+p.getMercatoCliente().getR1()+"</rmc1>");
                            out.println(" <rmc2>"+p.getMercatoCliente().getR2()+"</rmc2>");
                            out.println(" <rmc3>"+p.getMercatoCliente().getR3()+"</rmc3>");
                            out.println("\t<ckrmc1>"+(p.getMercatoCliente().getR1() != null)+"</ckrmc1> <ckrmc2>"+(p.getMercatoCliente().getR2() != null)+"</ckrmc2> <ckrmc3>"+(p.getMercatoCliente().getR3() != null)+"</ckrmc3>");
                        } else { out.println("<rmc1></rmc1><rmc2></rmc2><rmc3></rmc3>" +
                                             "<ckrmc1>false</ckrmc1><ckrmc2>false</ckrmc2><ckrmc3>false</ckrmc3>");
                        }

                        if( p.getContratto() != null){
                            out.println("\t<rc1>"+p.getContratto().getR1()+"</rc1>");
                            out.println(" <rc2>"+p.getContratto().getR2()+"</rc2>");
                            out.println(" <rc3>"+p.getContratto().getR3()+"</rc3>");
                            out.println("\t<ckrc1>"+(p.getContratto().getR1() != null)+"</ckrc1> <ckrc2>"+(p.getContratto().getR2() != null)+"</ckrc2> <ckrc3>"+(p.getContratto().getR3() != null)+"</ckrc3>");
                        } else { out.println("<rc1></rc1><rc2></rc2><rc3></rc3>" +
                                             "<ckrc1>false</ckrc1><ckrc2>false</ckrc2><ckrc3>false</ckrc3>");
                        }

                        if( p.getComposizionePartnership() != null){
                            out.println("\t<rcp1>"+p.getComposizionePartnership().getR1()+"</rcp1>");
                            out.println(" <rcp2>"+p.getComposizionePartnership().getR2()+"</rcp2>");
                            out.println(" <rcp3>"+p.getComposizionePartnership().getR3()+"</rcp3>");
                            out.println("\t<ckrcp1>"+(p.getComposizionePartnership().getR1() != null)+"</ckrcp1> <ckrcp2>"+(p.getComposizionePartnership().getR2() != null)+"</ckrcp2> <ckrcp3>"+(p.getComposizionePartnership().getR3() != null)+"</ckrcp3>");

                        } else { out.println("<rcp1></rcp1><rcp2></rcp2><rcp3></rcp3>" +
                                             "<ckrcp1>false</ckrcp1><ckrcp2>false</ckrcp2><ckrcp3>false</ckrcp3>");
                        }

                        if( p.getIngegneria() != null){
                            out.println("\t<ri1>"+p.getIngegneria().getR1()+"</ri1>");
                            out.println(" <ri2>"+p.getIngegneria().getR2()+"</ri2>");
                            out.println(" <ri3>"+p.getIngegneria().getR3()+"</ri3>");
                            out.println("\t<ckri1>"+(p.getIngegneria().getR1() != null)+"</ckri1> <ckri2>"+(p.getIngegneria().getR2() != null)+"</ckri2> <ckri3>"+(p.getIngegneria().getR3() != null)+"</ckri3>");
                        } else { out.println("<ri1></ri1><ri2></ri2><ri3></ri3>" +
                                             "<ckri1>false</ckri1><ckri2>false</ckri2><ckri3>false</ckri3>");
                        }

                        if( p.getApprovvigionamento() != null){
                            out.println("\t<ra1>"+p.getApprovvigionamento().getR1()+"</ra1>");
                            out.println(" <ra2>"+p.getApprovvigionamento().getR2()+"</ra2>");
                            out.println(" <ra3>"+p.getApprovvigionamento().getR3()+"</ra3>");
                            out.println("\t<ckra1>"+(p.getApprovvigionamento().getR1() != null)+"</ckra1> <ckra2>"+(p.getApprovvigionamento().getR2() != null)+"</ckra2> <ckra3>"+(p.getApprovvigionamento().getR3() != null)+"</ckra3>");
                        } else { out.println("<ra1></ra1><ra2></ra2><ra3></ra3>" +
                                             "<ckra1>false</ckra1><ckra2>false</ckra2><ckra3>false</ckra3>");
                        }

                        if( p.getFabbricazione() != null){
                            out.println("\t<rf1>"+p.getFabbricazione().getR1()+"</rf1>");
                            out.println(" <rf2>"+p.getFabbricazione().getR2()+"</rf2>");
                            out.println(" <rf3>"+p.getFabbricazione().getR3()+"</rf3>");
                            out.println("\t<ckrf1>"+(p.getFabbricazione().getR1() != null)+"</ckrf1> <ckrf2>"+(p.getFabbricazione().getR2() != null)+"</ckrf2> <ckrf3>"+(p.getFabbricazione().getR3() != null)+"</ckrf3>");
                        } else { out.println("<rf1></rf1><rf2></rf2><rf3></rf3>" +
                                             "<ckrf1>false</ckrf1><ckrf2>false</ckrf2><ckrf3>false</ckrf3>");
                        }

                        if( p.getMontaggio() != null){
                            out.println("\t<rm1>"+p.getMontaggio().getR1()+"</rm1>");
                            out.println(" <rm2>"+p.getMontaggio().getR2()+"</rm2>");
                            out.println(" <rm3>"+p.getMontaggio().getR3()+"</rm3>");
                            out.println("\t<ckrm1>"+(p.getMontaggio().getR1() != null)+"</ckrm1> <ckrm2>"+(p.getMontaggio().getR2() != null)+"</ckrm2> <ckrm3>"+(p.getMontaggio().getR3() != null)+"</ckrm3>");
                        } else { out.println("<rm1></rm1><rm2></rm2><rm3></rm3>" +
                                             "<ckrm1>false</ckrm1><ckrm2>false</ckrm2><ckrm3>false</ckrm3>");
                        }

                        if( p.getAvviamento() != null){
                            out.println("\t<rav1>"+p.getAvviamento().getR1()+"</rav1>");
                            out.println(" <rav2>"+p.getAvviamento().getR2()+"</rav2>");
                            out.println(" <rav3>"+p.getAvviamento().getR3()+"</rav3>");
                            out.println("\t<ckrav1>"+(p.getAvviamento().getR1() != null)+"</ckrav1> <ckrav2>"+(p.getAvviamento().getR2() != null)+"</ckrav2> <ckrav3>"+(p.getAvviamento().getR3() != null)+"</ckrav3>");
                        } else { out.println("<rav1></rav1><rav2></rav2><rav3></rav3>" +
                                             "<ckrav1>false</ckrav1><ckrav2>false</ckrav2><ckrav3>false</ckrav3>");
                        }


                        out.println("\t<!-- IMPATTO STRATEGICO -->");
                        out.println("\t<IM>"+p.getIm()+"</IM>");
                        out.println("\t<IC>"+p.getIc()+"</IC>");
                        out.println("\t<IP>"+p.getIp()+"</IP>");
                        out.println("\t<IA>"+p.getIa()+"</IA>");
                        out.println("\t<IPP>"+p.getIpp()+"</IPP>");
                        out.println("\t<!-- ABILITAZIONE DEI CAMPI -->");
                        
                        out.println("\t<ckIM>"+(p.getIm() != null && p.getIm().getValue() != -1)+"</ckIM>");
                        out.println("\t<ckIC>"+(p.getIc() != null && p.getIc().getValue() != -1)+"</ckIC>");
                        out.println("\t<ckIP>"+(p.getIp() != null && p.getIp().getValue() != -1)+"</ckIP>");
                        out.println("\t<ckIA>"+(p.getIa() != null && p.getIa().getValue() != -1)+"</ckIA>");
                        out.println("\t<ckIPP>"+(p.getIpp() != null && p.getIpp().getValue() != -1)+"</ckIPP>\n");
                        
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
        out.println("\t<rischio idName=\""+index+"\">");
                    out.println("\t\t<idRischio>"+r.getCodice()+"</idRischio>");
                    out.println("\t\t<codiceChecklist>"+r.getCodiceChecklist()+"</codiceChecklist>");
                    out.println("\t\t<stato>"+r.getStato().getStato()+"</stato>");
                    out.println("\t\t<categoria>"+r.getCategoria()+"</categoria>");
                    out.println("\t\t<rVer>"+r.getVerificato()+"</rVer>");
                    out.println("\t\t<descrizione>"+escapeChars(r.getDescrizione())+"</descrizione>");
                    out.println("\t\t<contingency>"+r.getContingency()+"</contingency>\n");
                    out.println("\t\t<causa>"+escapeChars(r.getCausa())+"</causa>\n");
                    out.println("\t\t<effetto>"+escapeChars(r.getEffetto())+"</effetto>\n");
                    out.println("\t\t<probIniziale>"+r.getProbabilitaAttuale()+"</probIniziale>\n");//XXX fatta modifica, verificare tutto funzioni correttamente
                    out.println("\t\t<impattoIniziale>"+r.getImpattoAttuale()+"</impattoIniziale>\n");//XXX
                    out.println("\t\t<costoPotenzialeImpatto>"+r.getCostoPotenzialeImpatto()+"</costoPotenzialeImpatto>\n");
                    out.println("\t\t<revisione>"+r.getNumeroRevisione()+"</revisione>");
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
        out.println("\t<azione idName=\""+index+"\">");
                out.println("\t\t<idAzione>"+a.getPrimaryKey().getIdAzione()+"</idAzione>");
                out.println("\t\t<identifier>"+a.getPrimaryKey().getIdentifier()+"</identifier>");
                out.println("\t\t<idRischio>"+a.getPrimaryKey().getIdRischio()+"</idRischio>");
                out.println("\t\t<tipo>"+a.getPrimaryKey().getTipo()+"</tipo>");
                out.println("\t\t<stato>"+a.getStato()+"</stato>");
                out.println("\t\t<descrizione>"+escapeChars(a.getDescrizione())+"</descrizione>");
                out.println("\t\t<revisione>"+a.getRevisione()+"</revisione>");
                out.println("\t\t<intensita>"+a.getIntensita()+"</intensita>");
                out.println("\t\t<ckintensita>"+(a.getIntensita()==-50)+"</ckintensita>");
        out.println("\t</azione>");
        return;
    }
    //function that escapes characters to print into xml
    private String escapeChars(String s){
        if(s==null || s.isEmpty())
            return "";
        
        s=s.replace('"', '\'');
        s=s.replaceAll("\\ufffd", "a\'");
        return s;
    }
    //function that returns the number of groups configured by the user
    private int getNumberOfGroups(HttpSession session){
        int groupNumber = (Integer) session.getAttribute("groupnumber");
        return groupNumber;
    }
    //function that return the suggested risks for the group 'groupnumber' into a List
    private List risksGroup(int groupnumber, HttpSession session){
        LinkedList<Rischio>[] gruppi = (LinkedList<Rischio>[]) session.getAttribute("gruppi");
        return gruppi[groupnumber];
    }
    //suggests common risks
    private List risknoGroup(HttpSession session) throws Exception{

        List<Long> listint = (List<Long>) Rischio.executeQuery("select count(codiceChecklist) from Rischio group by codiceChecklist");
        Double averageNum;
        Double tot = 0.0;
        Double num = 0.0;
        Iterator it = listint.iterator();
        while(it.hasNext()){
            Long i = (Long) it.next();
            tot+=i;
            num++;
        }
        averageNum = tot/num;
        Integer averageInt = averageNum.intValue();
        List<Integer> usualRisks = (List<Integer>) Rischio.executeQuery("select codiceChecklist from " +
                            "Rischio group by codiceChecklist having count(codiceChecklist) > " +averageInt);

        it = usualRisks.iterator();
        LinkedList<Rischio> resultList = new LinkedList<Rischio>();
        while(it.hasNext()){
            Integer id = (Integer) it.next();
            CkRischi chk = (CkRischi)CkRischi.getById(CkRischi.class, id);
            Rischio r = new Rischio();
            r.setCodice(generateRiskId(session));
            r.setDescrizione(escapeChars(chk.getDescrizione()));
            r.setCodiceChecklist(chk.getCodChecklist());
            suggestCategory(r);
            resultList.add(r);
        }
        return resultList;
    }
    /**
     * function to create a list of risks from the current request.
     * as for actions, this function returns the list of old actions if enable is true,
     * and the list of new actions added to the project if the enable is false
     */
    private List extractRisksFromRequest(HttpServletRequest request, boolean enable){

        LinkedList<Rischio> list = new LinkedList<Rischio>();
        String what = "";
        if(enable)
            what = "risk_";
        else
            what = "newrisk_";

        //reading the number of risks to load
        Integer cnt = Integer.parseInt(request.getParameter(what+"cnt"));
        if(cnt == null)
            return list;

        //reading fields and building objects
        for(int i=0; i<cnt; i++){
            Rischio r = new Rischio();
            //not null parameter
            r.setCodice((String) request.getParameter(what+"idRischio_"+i));
            //can be empty
            String descrizione = (String) request.getParameter(what+"descrizione_"+i);
            if(descrizione == null || descrizione.isEmpty())
                descrizione="";
            r.setDescrizione(descrizione);
            //can be empty
            String causa = (String) request.getParameter(what+"causa_"+i);
            if(causa == null || causa.isEmpty())
                causa="";
            r.setCausa(causa);
            //can be null
            String effetto = (String) request.getParameter(what+"effetto_"+i);
            if(effetto==null || effetto.isEmpty())
                effetto="";
            r.setEffetto(effetto);
            //not null parameter
            r.setCodiceChecklist(Integer.parseInt(request.getParameter(what+"codiceChecklist_"+i)));
            //not null parameter
            r.setStato((String) request.getParameter(what+"stato_"+i));
            //not null parameter
            r.setVerificato(Integer.parseInt(request.getParameter(what+"rVer_"+i)));
            //may be null
            String contingency = request.getParameter(what+"contingency_"+i);
            if(contingency == null || contingency.isEmpty())
                contingency="0.0";
            r.setContingency(Double.parseDouble(contingency));
            //not null parameter
            r.setProbabilitaIniziale(Integer.parseInt(request.getParameter(what+"probIniziale_"+i)));
            //not null parameter
            r.setImpattoIniziale(Integer.parseInt(request.getParameter(what+"impattoIniziale_"+i)));
            //can be empty
            String categoria = request.getParameter(what+"categoria_"+i);
            if(categoria==null || categoria.isEmpty())
                categoria="";
            r.setCategoria(categoria);
            //not null parameter
            r.setCostoPotenzialeImpatto(Double.parseDouble(request.getParameter(what+"costoPotenzialeImpatto_"+i)));
            String numRev = request.getParameter(what+"revisione_"+i);
            if(numRev == null || numRev.isEmpty())
                numRev = "0";
            r.setNumeroRevisione(Integer.parseInt(numRev));

            //filled fields
            list.add(r);
        }

        return list;
    }
    //function to extract a project from the current request
    private Progetto extractProjectFromRequest(HttpServletRequest request, PrintWriter out){
        Progetto p = new Progetto();


        try{
            p.setIsCase(Boolean.parseBoolean(request.getParameter("isCase")));
            p.setIsOpen(Boolean.parseBoolean(request.getParameter("isOpen")));
        }catch (Exception e){}

        
        p.setCodice(request.getParameter("codice"));
        p.setReparto(Integer.parseInt(request.getParameter("reparto")));
        p.setClasseRischio(Integer.parseInt(request.getParameter("classeDiRischio")));
        p.setValoreEconomico(Double.parseDouble(request.getParameter("valoreEconomico")));
        p.setDurataContratto(Integer.parseInt(request.getParameter("durataContratto")));
        p.setOggettoFornitura(request.getParameter("oggettoFornitura"));
        p.setNomeCliente(request.getParameter("nomeCliente"));


        LivelloDiRischio l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckrp1"))?Integer.parseInt(request.getParameter("rp1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckrp2"))?Integer.parseInt(request.getParameter("rp2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckrp3"))?Integer.parseInt(request.getParameter("rp3")):null);
        p.setPaese(l);

        l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckrmc1"))?Integer.parseInt(request.getParameter("rmc1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckrmc2"))?Integer.parseInt(request.getParameter("rmc2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckrmc3"))?Integer.parseInt(request.getParameter("rmc3")):null);
        p.setMercatoCliente(l);

         l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckrc1"))?Integer.parseInt(request.getParameter("rc1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckrc2"))?Integer.parseInt(request.getParameter("rc2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckrc3"))?Integer.parseInt(request.getParameter("rc3")):null);
        p.setContratto(l);

         l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckrcp1"))?Integer.parseInt(request.getParameter("rcp1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckrcp2"))?Integer.parseInt(request.getParameter("rcp2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckrcp3"))?Integer.parseInt(request.getParameter("rcp3")):null);
        p.setComposizionePartnership(l);

         l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckri1"))?Integer.parseInt(request.getParameter("ri1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckri2"))?Integer.parseInt(request.getParameter("ri2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckri3"))?Integer.parseInt(request.getParameter("ri3")):null);
        p.setIngegneria(l);

         l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckra1"))?Integer.parseInt(request.getParameter("ra1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckra2"))?Integer.parseInt(request.getParameter("ra2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckra3"))?Integer.parseInt(request.getParameter("ra3")):null);
        p.setApprovvigionamento(l);

         l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckrf1"))?Integer.parseInt(request.getParameter("rf1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckrf2"))?Integer.parseInt(request.getParameter("rf2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckrf3"))?Integer.parseInt(request.getParameter("rf3")):null);
        p.setFabbricazione(l);

         l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckrm1"))?Integer.parseInt(request.getParameter("rm1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckrm2"))?Integer.parseInt(request.getParameter("rm2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckrm3"))?Integer.parseInt(request.getParameter("rm3")):null);
        p.setMontaggio(l);

         l = new LivelloDiRischio();
        l.setR1(Boolean.parseBoolean(request.getParameter("ckrav1"))?Integer.parseInt(request.getParameter("rav1")):null);
        l.setR2(Boolean.parseBoolean(request.getParameter("ckrav2"))?Integer.parseInt(request.getParameter("rav2")):null);
        l.setR3(Boolean.parseBoolean(request.getParameter("ckrav3"))?Integer.parseInt(request.getParameter("rav3")):null);
        p.setAvviamento(l);

        String ckIm = request.getParameter("ckIM");
        String ckIc = request.getParameter("ckIC");
        String ckIp = request.getParameter("ckIP");
        String ckIa = request.getParameter("ckIA");
        String ckIpp = request.getParameter("ckIPP");

        String Im = request.getParameter("IM");
        String Ic = request.getParameter("IC");
        String Ip = request.getParameter("IP");
        String Ia = request.getParameter("IA");
        String Ipp = request.getParameter("IPP");

        if(ckIm == null || Im==null) p.setIm(new ImpattoStrategico(-1));
        else p.setIm(new ImpattoStrategico(Integer.parseInt(Im)));

         if(ckIc == null || Im==null) p.setIc(new ImpattoStrategico(-1));
        else p.setIc(new ImpattoStrategico(Integer.parseInt(Ic)));

         if(ckIp == null || Im==null) p.setIp(new ImpattoStrategico(-1));
        else p.setIp(new ImpattoStrategico(Integer.parseInt(Ip)));

         if(ckIa == null || Im==null) p.setIa(new ImpattoStrategico(-1));
        else p.setIa(new ImpattoStrategico(Integer.parseInt(Ia)));

         if(ckIpp == null || Im==null) p.setIpp(new ImpattoStrategico(-1));
        else p.setIpp(new ImpattoStrategico(Integer.parseInt(Ipp)));
        return p;
    }
    //function to suggest actions for the 'p' project and the 'r' risk
    private List suggestActions(HttpSession session, Rischio r, PrintWriter out){
        LinkedList<Azioni> azioni = (LinkedList<Azioni>) session.getAttribute("azioni");
        LinkedList<Azioni> azioniDelRischio = new LinkedList<Azioni>();

        Iterator it = azioni.iterator();
        while(it.hasNext()){
            Azioni a = (Azioni) it.next();
            if(a.getPrimaryKey().getIdRischio().compareTo(r.getCodice()) == 0){
                azioniDelRischio.add(a);
                //printAction(a, out, 0);
            }
        }
        return azioniDelRischio;
    }
    /**
     * function to extract actions from the current request
     * If "enable" is set to false, the function reads from http request, all fields starting with "action_"
     * Else, when "enable" is set to "false", the function reads from request, all fields sarting with "newaction_"
     *
     * In other words, with enable set to true, reads all the old actions from request.
     * When using extractActionsFromRequest() with a false enable, the function will return all
     * the actions added to the project in the current revision
     */
    private List extractActionsFromRequest(HttpServletRequest request, boolean enable, PrintWriter out){
        
        LinkedList<Azioni> list = new LinkedList<Azioni>();
        String what = "";

        if(enable)
            what = "action_";
        else
            what = "newaction_";

        //reading the number of actions to load
        Integer cnt = Integer.parseInt(request.getParameter(what+"cnt"));
        if(cnt == null)
            return list;
        //temporary identifier
        int identif = 1;

        //building actions
        for(int i=0; i<cnt; i++){
            Azioni a = new Azioni();

            //not null field
            a.getPrimaryKey().setIdAzione(Integer.parseInt(request.getParameter(what+"idAzione_"+i)));
            //this field is a false (non valid) id if the action is a new one added to a new risk. It wil be fixed in parseNewItems function
            a.getPrimaryKey().setIdRischio(request.getParameter(what+"idRischio_"+i));
            //for the new actions, this field is set to 0, update it later (parseNewItems function)
            a.getPrimaryKey().setIdentifier(Integer.parseInt(request.getParameter(what+"identifier_"+i)));
            //not null field
            a.getPrimaryKey().setTipo(request.getParameter(what+"tipo_"+i).charAt(0));
            //this field can be null
            String descrizione = request.getParameter(what+"descrizione_"+i);
            if(descrizione == null || descrizione.isEmpty())
                descrizione = "";
            a.setDescrizione(descrizione);
            //this field is 0 for the new actions. Update it later
            a.setRevisione(Integer.parseInt(request.getParameter(what+"revisione_"+i)));
            //not null field
            a.setStato(request.getParameter(what+"stato_"+i));
            //not null field
            if(!Boolean.parseBoolean(request.getParameter(what+"ckintensita_"+i)))
                a.setIntensita(-50);
            else
                a.setIntensita(Integer.parseInt(request.getParameter(what+"intensita_"+i)));
            list.add(a);
        }
        return list;
    }

    //function to build a project from alla datas passed as argument
    private Progetto buildProject(Progetto p, List  riskList, List actionList, PrintWriter out, HttpSession session){
        try{           
            //adding all risks to the project
            Iterator it = riskList.iterator();
            while(it.hasNext()){
                Rischio r = (Rischio) it.next();
                //adding actions to risk
                Iterator ait = actionList.iterator();
                while(ait.hasNext()){
                    Azioni a = (Azioni) ait.next();
                    //action for the current risk
                    if(a.getPrimaryKey().getIdRischio().compareTo(r.getCodice()) == 0){
                        //setting identifier
                        //int ident = generateIdentifier(session, a.getPrimaryKey());
                        //a.getPrimaryKey().setIdentifier(ident);
                        //out.println("generato identifier "+ident);
                        r.aggiungiAzione(0, a);
                        //ait.remove();
                    }
                }
                //added actions to risk
                p.aggiungiRischio(r);
            }
        } catch (Exception e){
            out.println(e);
            return null;
        }
        return p;
    }
    /*
     * Replace the temporary ids given to new actions and risks, with new ones to be used into the DB
     */
    private void parseNewItems(List<Rischio> oldRisks, List<Rischio> newRisks, List<Azioni> oldActions, List<Azioni> newActions, HttpSession session, PrintWriter out) throws Exception{
        //for each new Risk, building a new identifier and replacing the old one into the newAcions list
        Iterator it = newRisks.iterator();
        while(it.hasNext()){
            //take risk
            Rischio risk = (Rischio) it.next();
            //taking id
            String riskId = risk.getCodice();
            String effectiveId = generateRiskId(session);
            //replacing this id for each new action
            Iterator actIt = newActions.iterator();
            while(actIt.hasNext()){
                Azioni action  = (Azioni) actIt.next();
                if(action.getPrimaryKey().getIdRischio().equals(riskId)){
                    action.getPrimaryKey().setIdRischio(effectiveId);
                    break;
                }
            }
            //replacing the risk id
            risk.setCodice(effectiveId);
        }
        //now, all risk codes are correct.
        //now fixing "revisione" and "identifier" fields for all new actions

        //fixing "revisione" for new actions
        Progetto p = (Progetto) session.getAttribute("Progetto");
        int maxRev = p.getMaxRevisione();
        it = newActions.iterator();
        while(it.hasNext()){
            Azioni action = (Azioni) it.next();
            action.setRevisione(maxRev+1);
        }
        //fixing revisione for actions closed in this revision:
        //if the action was closed in the current revision, the "revisione" field
        //has to be updated
        it = oldActions.iterator();
        while(it.hasNext()){
            Azioni action = (Azioni) it.next();
            Azioni oldAction = (Azioni) Azioni.getById(Azioni.class, action.getPrimaryKey());
            if(action.getStato().equals("Closed") && !oldAction.getStato().equals("Closed"))
                action.setRevisione(maxRev+1);
        }
        
        //fixing the "identifier" field into new actions, 
        //to allow multiple actions with the same checklist code for the same risk
        it = newActions.iterator();
        while(it.hasNext()){
            Azioni action = (Azioni) it.next();
            int newIdentifier = generateIdentifier(session, action.getPrimaryKey(), out);
            action.getPrimaryKey().setIdentifier(newIdentifier);
        }

        //setting the correct "storico" field for all old risks (the new ones will have it empty)
        it = oldRisks.iterator();
        while(it.hasNext()){
            Rischio current = (Rischio) it.next();
            Rischio dbRisk = (Rischio) Rischio.getById(Rischio.class, current.getCodice());
            dbRisk.caricaStorico();
            current.caricaStorico();
            if(dbRisk.getProbabilitaAttuale() != current.getProbabilitaIniziale() ||
                    dbRisk.getImpattoAttuale() != current.getImpattoIniziale()){
                //if are different, the "storico" field will be updated and the correct value must be written
                //into probabilitaIniziale and impattoIniziale
                Revisione rev = new Revisione();
                rev.setIdRischio(current.getCodice());
                rev.setNumeroRevisione(maxRev+1);
                rev.setIndiceImpatto(current.getImpattoIniziale());
                rev.setProbabilita(current.getProbabilitaIniziale());
                current.aggiungiRevisione(rev);
                current.setImpattoIniziale(dbRisk.getImpattoIniziale());
                current.setProbabilitaIniziale(dbRisk.getProbabilitaIniziale());
            }
        }
        return;
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
        //XXX consider to insert default description into risk
        //does nothing
    }
    //writes digest in xml
    private void printDigest(Progetto p, List riskList, List actionList, PrintWriter out){
        return;
    }
    /*
     * This function will compare if there are modifications between the risks into "lista" and
     * the risks with the same id into "gruppi". If at least one risk was modified, it returns true
     */
    private boolean compareModificationsRisks(LinkedList<Rischio>[] gruppi, List lista){
        
        /*DUMMY*/
        /*the real function will compare if ther are modifications between suggested fields
         and given ones, to decide if the project will be a case in the case base or not*/
        return true;
    }
    //same as above, but for actions
    private boolean compareModificationsActions(List<Azioni> prev, List<Azioni> current, PrintWriter out){
        /*dummy*/
        /*same as above function*/
        return true;
    }
    //generates valid actionId
    private int generateIdentifier(HttpSession session, AzioniPrimaryKey pk, PrintWriter out) throws Exception{
        LinkedList<AzioniPrimaryKey> listaPkAzioni = (LinkedList<AzioniPrimaryKey>) session.getAttribute("listaPkAzioni");
        if(listaPkAzioni == null){
            listaPkAzioni = new LinkedList<AzioniPrimaryKey>();
        }

        AzioniPrimaryKey t = new AzioniPrimaryKey(pk.getIdentifier(), pk.getIdAzione(), pk.getIdRischio(), pk.getTipo());

        while(!Azioni.checkAvailable(t)){
            while(!Azioni.checkAvailable(t)){
                int old = t.getIdentifier();
                t.setIdentifier(old+1);
            }
            boolean found = true;
            while(found && !listaPkAzioni.isEmpty()){
                //checking already set id
                Iterator k = listaPkAzioni.iterator();
                found=false;
                while(k.hasNext() && !found){
                    AzioniPrimaryKey x = (AzioniPrimaryKey) k.next();
                    if(x.equals(t))
                        found=true;
                }
                //generating one only if the current already exists
                if(found){
                    int old = t.getIdentifier();
                    t.setIdentifier(old+1);
                }
            }
        }
        
        listaPkAzioni.add(t);
        session.setAttribute("listaPkAzioni", listaPkAzioni);

        return t.getIdentifier();
    }

    private void deleteProject(Progetto previous,PrintWriter out) throws Exception{

        //creating list of idS of the risks to delete
        Iterator vecchiRischi = previous.getRischi().iterator();
        LinkedList<String> idVecchi = new LinkedList<String>();
        while(vecchiRischi.hasNext()){
            Rischio r = (Rischio) vecchiRischi.next();
            idVecchi.add(r.getCodice());
        }

        //deleting old project
        vecchiRischi = idVecchi.iterator();
        while(vecchiRischi.hasNext()){
            previous.rimuoviRischio((String) vecchiRischi.next());
        }

        previous.delete();
    }


    /*SIMILARITY UTILITIES*/
    private void suggestions(Progetto queryDesc, HttpSession session,PrintWriter out) throws Exception {
        
        String file = Config.configFilePath;
        FileInputStream configFile = new FileInputStream(file);
        // LOAD CONFIGURATION
        Configuration configuration = Configuration.load(configFile);
        configFile.close();

        //LOAD CASES
        Collection<CBRCase> cases = new LinkedList<CBRCase>();
        // Progetto extends CBRCase, since a Progetto can be a case
        List<Progetto> progetti = (List<Progetto>)Progetto.getCases();
        for(Progetto progetto : progetti) {
                CBRCase cbrCase = new CBRCase();
                cbrCase.setDescription(progetto);
                cases.add(cbrCase);
        }

        //CONFIGURE SIMILARITY
        // the query must be a CBRCase, the description of whom is the Progetto just defined
        CBRCase query = new CBRCase();
        query.setDescription(queryDesc);

        /* NNConfig is a configuration structure for the similarity algorithm
         * We put NNConfigs in a map so that we can have at any moment about the ConfigurationGroup that generated them
         */
        Map<NNConfig, ConfigurationGroup> simConfigs = new HashMap<NNConfig, ConfigurationGroup>();
        /*
         * globalSimConfig is a similarity configuration that gives a global ranking on a project,
         * that is it's a ranking that is more "general" to the ranking given in each group.
         * It will be used later with more explanations.
         */
        NNConfig globalSimConfig = queryDesc.getTotalSimilarityConfig(null);


        /*
         * NNConfigurator extracts information from a ConfigurationGroup and stores them
         * inside a simConfig structure.
         * Note you must pass the Progetto class to the configureSimilarity function.
         */
        for(ConfigurationGroup groupConfig : configuration.groups) {
                NNConfig simConfig = new NNConfig();
                NNConfigurator.configureSimilarity(simConfig, groupConfig, Progetto.class);
                simConfigs.put(simConfig, groupConfig);
        }

        /* ------------------------- */
	/* CASES SELECTION IN GROUPS */
	/* ------------------------- */

	/*
	 * We build a list groupsResults, where each Collection of CBRCases (groupResult) corresponds to the top k results
	 * of the similarity evaluation according to settings of each group.
	 * Those k best results (according to the group similarity settings) are then ranked again inside each group
	 * with the globalSimilarity similarity configuration (global ranking)
	 *
	 * RetrievalResult is a structure that contains a numerical evalution of the similarity between the query and a case
	 * trough the getEval method, and the case for which the similarity towards the query has been calculated,
	 * available trough the get_case method.
	 * You can access the corrisponding Progetto instance trough the getDescription method of that case
	 */
	 Map<ConfigurationGroup, Collection<RetrievalResult>> groupsResults = new HashMap<ConfigurationGroup, Collection<RetrievalResult>>();

         /* We calculate similarity according to the similarity criterias specified for each group.
	 * Then, we get the top k projects for the group.
	 */
	for(Map.Entry<NNConfig, ConfigurationGroup> entry : simConfigs.entrySet()) {
		NNConfig simConfig = entry.getKey();
		// we calculate similarity according to the similarity configuratino for this group
		Collection<RetrievalResult> simEval = NNScoringMethod.evaluateSimilarity(cases, query, simConfig);
		Collection<CBRCase> bestEval = SelectCases.selectTopK(simEval, configuration.kProgetto);

		// as last, we give a global ranking to the best kProgetto entries of this group
		Collection<RetrievalResult> globallyEvaluatedResul = NNScoringMethod.evaluateSimilarity(bestEval, query, globalSimConfig);

                groupsResults.put(entry.getValue(), globallyEvaluatedResul);
	}

        /* ----------------- */
	/* RISKS EXTRACTION  */
	/* ----------------- */
	Map<ConfigurationGroup, Collection<RischioSuggester>>  rischioSuggestersByGroup = new HashMap<ConfigurationGroup, Collection<RischioSuggester>>();

	// for each group, we will now extract all risks, and get the most relevant ones
	for(Map.Entry<ConfigurationGroup, Collection<RetrievalResult>> entry : groupsResults.entrySet()) {
		Collection<RischioSuggester> rischioSuggesters = RischioSuggester.getTopKSuggesters(query, entry.getValue(), configuration.kRischio);
		rischioSuggestersByGroup.put(entry.getKey(), rischioSuggesters);
	}

        /* ------------------- */
	/* ACTIONS EXTRACTION  */
	/* ------------------- */

	// a rather complex data structure to save all actionsSuggesters in all risks by all groups
	Map<Map.Entry<ConfigurationGroup,RischioSuggester>, Collection<AzioniSuggester>>  azioniSuggestersByGroup = new HashMap<Map.Entry<ConfigurationGroup,RischioSuggester>, Collection<AzioniSuggester>>();


        int groupNumber = configuration.groups.size();
        int groupIndex = 0;
        LinkedList<Rischio>[] gruppi;
        gruppi = new LinkedList[groupNumber];

        LinkedList<Azioni> azioni = new LinkedList<Azioni>();

	// for each group, we will now extract all actions for each risk, and get the most relevant ones
	for(Map.Entry<ConfigurationGroup, Collection<RischioSuggester>> entry : rischioSuggestersByGroup.entrySet()) {

            gruppi[groupIndex] = new LinkedList<Rischio>();

            for(RischioSuggester rischioSuggester : entry.getValue()) {
			Collection<AzioniSuggester> azioniSuggesters = AzioniSuggester.getTopKSuggesters(rischioSuggester, entry.getValue(), configuration.kAzioni);

                        Rischio suggestedRischio = rischioSuggester.getSuggestion();
                        String codice = generateRiskId(session);
			suggestedRischio.setCodice(codice);
                        suggestCategory(suggestedRischio);
			gruppi[groupIndex].add(suggestedRischio);

			for(AzioniSuggester suggester: azioniSuggesters) {
				Azioni azione = suggester.getSuggestion(configuration.adaptIntensita);
                                azione.getPrimaryKey().setIdRischio(codice);
                                azione.getPrimaryKey().setIdentifier(0);
                                azioni.add(azione);
			}
            }
            groupIndex++;
        }

        //storing data into session
        session.setAttribute("gruppi", gruppi);
        session.setAttribute("azioni",azioni);
        session.setAttribute("groupnumber", groupNumber);
        return;
    }
    //retrieves from DB the category for the selected risk
    private void suggestCategory(Rischio r) throws Exception {

        List categorie = Rischio.executeQuery("select categoria.categoria from Rischio where codiceChecklist = "+r.getCodiceChecklist());
        Iterator it = categorie.iterator();
        if(!it.hasNext())
            return;
        String cat = (String) it.next();
        r.setCategoria(cat);
    }


    /*FUNCTIONS USED TO DO SOME TESTS*/
    private Progetto generateRandomProject(String projectId) throws Exception{
        Progetto generate = new Progetto();

        Random rn = new Random();

        //setting casual fields
        generate.setIsCase(true);
        generate.setIsOpen(true);

        generate.setCodice(projectId);
        generate.setReparto(rn.nextInt());
        generate.setClasseRischio(rn.nextInt(2)-1);
        generate.setValoreEconomico((rn.nextDouble())*(10000));
        generate.setDurataContratto(rn.nextInt(15));
        generate.setOggettoFornitura("OBJ");
        generate.setNomeCliente("CLIO");

        generate.setPaese(generateRandomLR());
        generate.setMercatoCliente(generateRandomLR());
        generate.setContratto(generateRandomLR());
        generate.setComposizionePartnership(generateRandomLR());
        generate.setIngegneria(generateRandomLR());
        generate.setApprovvigionamento(generateRandomLR());
        generate.setFabbricazione(generateRandomLR());
        generate.setMontaggio(generateRandomLR());
        generate.setAvviamento(generateRandomLR());

        generate.setIm(generateRandomIs());
        generate.setIc(generateRandomIs());
        generate.setIp(generateRandomIs());
        generate.setIa(generateRandomIs());
        generate.setIpp(generateRandomIs());


        int iterat = rn.nextInt(10);
        for(int i=0; i<iterat; i++){
            generate.aggiungiRischio(generateRandomRischio(projectId+"R"+i));
        }
        return generate;
    }

    /*TESTING FUNCTIONS TO GENERATE RANDOM PROJECTS*/
    private LivelloDiRischio generateRandomLR(){
        Random rn = new Random();
        if(rn.nextBoolean()){
            return null;
        }

        LivelloDiRischio lr = new LivelloDiRischio();
        if(rn.nextBoolean()){
            lr.setR1(rn.nextInt(3));
        }
        if(rn.nextBoolean()){
            lr.setR2(rn.nextInt(3));
        }
        if(rn.nextBoolean()){
            lr.setR3(rn.nextInt(3));
        }
        return lr;
    }

    private ImpattoStrategico generateRandomIs(){
        Random rn = new Random();
        if(rn.nextBoolean()){
            return new ImpattoStrategico();
        }
        return new ImpattoStrategico(rn.nextInt(3));
    }

    private Rischio generateRandomRischio(String riskId) throws Exception{
        Rischio r = new Rischio();
        Random rn = new Random();

        r.setCodice(riskId);
        r.setCodiceChecklist(rn.nextInt());
        r.setStato((rn.nextBoolean())?"Mitigation":(rn.nextBoolean())?"Monitoring":"Closed");
        r.setCategoria("CATE");
        r.setVerificato(rn.nextInt(2));
        r.setNumeroRevisione(0);
        r.setDescrizione("DESC");
        r.setContingency(rn.nextDouble()*10000);
        r.setProbabilitaIniziale(rn.nextInt(4)+1);
        r.setImpattoIniziale(rn.nextInt(4)+1);
        r.setCausa("CAusa");
        r.setEffetto("SMER");
        r.setCostoPotenzialeImpatto(rn.nextDouble()*10000);

        int n = rn.nextInt(6);
        for(int i=0; i<n; i++){
            r.aggiungiAzione(0, generateRandomAzioni(i));
        }
        return r;
    }

    private Azioni generateRandomAzioni(int k){
        Azioni act = new Azioni();
        Random rn = new Random();

        AzioniPrimaryKey azpk = new AzioniPrimaryKey(k, rn.nextInt(100) , "", rn.nextBoolean()?'M':'R');
        act.setPrimaryKey(azpk);
        act.setIntensita(rn.nextInt(4)+1);
        act.setRevisione(0);
        act.setStato("STATO");
        act.setDescrizione("AZIONEDESC");
        return act;
    }



    /*DUMMY FUNCTIONS USED AS STUBS FOR TESTING*/
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
         Progetto p = new Progetto();
                    //p.setId(10);
                    p.setIsCase(true);
                    p.setApprovvigionamento(new LivelloDiRischio(1,2,3));
                    p.setAvviamento(new LivelloDiRischio(1,2,3));
                    p.setClasseRischio(2);
                    p.setCodice("codice");
                    p.setComposizionePartnership(new LivelloDiRischio(2,1,3));
                    p.setContratto(new LivelloDiRischio(3,3,3));

                    p.setDurataContratto(10);
                    p.setFabbricazione(new LivelloDiRischio(1,1,1));
                    p.setIa(new ImpattoStrategico(2));
                    p.setIc(new ImpattoStrategico(3));
                    p.setIp(new ImpattoStrategico(3));
                    p.setIm(new ImpattoStrategico(1));
                    p.setIngegneria(new LivelloDiRischio(1, 2, 2));
                    p.setIpp(new ImpattoStrategico(0));
                    p.setMercatoCliente(new LivelloDiRischio(3, 3, 2));
                    p.setMontaggio(new LivelloDiRischio(1, 1, 1));
                    p.setNomeCliente("Ilmioclientepreferito");
                    p.setOggettoFornitura("Lamiafornitura");
                    p.setPaese(new LivelloDiRischio(1, 0, 0));
                    p.setReparto(12);
                    p.setValoreEconomico(103.1);

        return p;
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
    private List risknoGroupDummy(){
        try{
            return Rischio.executeQuery("from Rischio where idProgramma = 'P7'");
        } catch (Exception e){}

        return new LinkedList();
    }
}
