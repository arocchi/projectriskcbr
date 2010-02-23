import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import persistentclasses.attributes.AzioniPrimaryKey;
import persistentclasses.attributes.ImpattoStrategico;
import persistentclasses.attributes.LivelloDiRischio;
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

            /*XXX RIGHE DI DEBUG PER TESTING DAVID*/
            /*session.setAttribute("Progetto", Progetto.getById(Progetto.class, "P2"));
            session.setAttribute("RisksAddedToProject", extractRisksFromRequestDummy(request));
            session.setAttribute("ActionsAddedToProject", extractActionsFromRequestDummy(request));*/
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
                    //out.println(request.toString());
                    Progetto p = extractProjectFromRequest(request, out);/*XXX sostituire con funzione effettiva*/

                    p.setIsCase(false);

                    p.setIsOpen(true);

                    p.setCodice(Progetto.generateAutoKey());

                    //saving it into session varaiable
                    session.setAttribute("Progetto", p);
                    //making suggestions
                    //XXXout.println(request.toString());
                    //XXXprintProject(p, out);
                    suggestions(p, session, out);
                    out.println("ok");
                    
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

                    int groupnumber = getNumberOfGroups(session);//XXX controllo correttezza

                    index = 0;//index for the xml object to be created from the interface
                    for(int i=0; i<groupnumber; i++){
                        out.println("<gruppo idName=\""+i+"\">\n\t<nomeGruppo>Gruppo "+i+"</nomeGruppo>");
                        lista = risksGroup(i,session);//XXX controllo correttezza
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
                    lista = risknoGroup(session);//XXX controllare se va bene
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
                    //XXXout.println(request.toString());
                    //reading risks from the current request
                    lista = extractRisksFromRequest(request,false);/*XXX testare*/
                    //XXX compare given risks to decide if store the project as a case
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
                        lista = suggestActions(session,r,out);//XXX testare
                        out.println("<rischio idName=\""+(riskIndex++)+"\">\n"+
                                        "\t<idRischio>"+r.getCodice()+"</idRischio>");
                        it = lista.iterator();
                        //XXXX int identifier = 1;
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
                            // XXX XXX
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
                    /*XXX format to define*/


                    //reading actions list from request
                    lista = extractActionsFromRequest(request,false,out);
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
                        //XXX out.println("IS CASE");
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
                    /*XXX formato della risposta da definire E testare da interfaccia con David*/

                    //Boolean confirm = Boolean.parseBoolean((String) session.getAttribute("data"));
                    String confirmStr = (String) request.getParameter("data");
                    /*out.println("DATO: "+confirmStr);
                    out.println(request.toString());*/
                    if(/*!confirm*/confirmStr == null || !confirmStr.equals("true")){
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
                        complete.salvaProgetto();//XXX controllare dove e quando viene generata la chiave del progetto
                        out.println("OK");
                    }
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

                    /*PRINTING LISTS*/
                    /*out.println("BEGIN");
                    Iterator m1 = riskList.iterator();
                    while(m1.hasNext()){
                        Rischio r = (Rischio) m1.next();
                        printRisk(r, out, 0, false);
                    }
                    m1 = actionList.iterator();
                    while(m1.hasNext()){
                        Azioni a = (Azioni) m1.next();
                        printAction(a, out, 0);
                    }*/
                    //out.println("END");
                    /****************/
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

                    //saving
                    session.setAttribute("Progetto_ch", p);

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

                    //reading project from request
                    Progetto pg = extractProjectFromRequest(request, out);
                    List rg = extractRisksFromRequest(request,true);
                    List ag = extractActionsFromRequest(request,true,out);
                    pg = buildProject(pg, rg, ag, out, session);

                    //old project
                    Progetto p = (Progetto) session.getAttribute("Progetto_ch");
                    //getting all actions
                    LinkedList<Azioni> al = new LinkedList<Azioni>();
                    it = p.getRischi().iterator();
                    while(it.hasNext()){
                        Rischio r = (Rischio) it.next();
                        Iterator j = r.getAzioni().iterator();
                        while(j.hasNext()){
                            al.add((Azioni) j.next());
                        }
                    }
                    //project read. Finding new, deleted and updated fields
                    it = ag.iterator();
                    while(it.hasNext()){
                        Azioni az = (Azioni) it.next();
                        if(!Azioni.checkAvailable(az.getPrimaryKey())){
                            //old action,unckecking from list
                            Iterator j = al.iterator();
                            while(j.hasNext()){
                                Azioni oldaz = (Azioni) j.next();
                                if(oldaz.getPrimaryKey().equals(az.getPrimaryKey())){
                                    al.remove(oldaz);
                                    break;
                                }
                            }
                        }
                        else //new action
                        {
                            //giving identifier
                            int id = generateIdentifier(session, az.getPrimaryKey(),out);
                            az.getPrimaryKey().setIdentifier(id);

                        }

                    }
                    //removing from DB all removed actions
                    it = al.iterator();
                    while(it.hasNext()){
                        Azioni toremId = (Azioni) it.next();
                        Azioni torem = (Azioni) Azioni.getById(Azioni.class, toremId.getPrimaryKey());
                        torem.delete();
                    }

                    //finding removed risks
                    it = rg.iterator();
                    while(it.hasNext()){
                        Rischio nuovorischio = (Rischio) it.next();
                        if(!Rischio.checkAvailable(nuovorischio.getCodice())){
                            //rischio vecchio
                            Iterator intit = p.getRischi().iterator();
                            while(intit.hasNext()){
                                Rischio oldr = (Rischio) intit.next();
                                if(oldr.getCodice().compareTo(nuovorischio.getCodice())==0){
                                    //controllo se devo aggiornare storico
                                    if(nuovorischio.getProbabilitaAttuale() != oldr.getProbabilitaAttuale()){
                                        nuovorischio.aggiungiRevisione(nuovorischio.getMaxRevisione()+1,nuovorischio.getProbabilitaIniziale(),nuovorischio.getImpattoIniziale());
                                        nuovorischio.setProbabilitaIniziale(oldr.getProbabilitaIniziale());
                                        nuovorischio.setImpattoIniziale(oldr.getImpattoIniziale());
                                        nuovorischio.setNumeroRevisione(nuovorischio.getMaxRevisione());
                                    }
                                    //rimuovo
                                    p.getRischi().remove(oldr);
                                    break;
                                }
                            }

                        }else{//rischio nuovo, faccio nulla
                        }

                    }
                    //rimangono in p.getRischi() queli cancellati
                    it = p.getRischi().iterator();
                    while(it.hasNext()){
                        Rischio r = (Rischio) it.next();
                        Rischio torem = (Rischio) Rischio.getById(Rischio.class, r.getCodice());
                        torem.delete();//cancello
                    }
                    //cancellati da DB campi cancellati dall'utente
                    //salvo progetto
                    pg.update();
                    pg.salvaRischi();
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
                //closeproject
                case 999:
                {
                    String idP = request.getParameter("data");
                    Progetto pro = (Progetto) Progetto.getById(Progetto.class, idP);
                    pro.setIsOpen(false);
                    pro.update();
                }
                case 666:
                {
                    out.println("test started");
                    AzioniPrimaryKey azpk1 = new AzioniPrimaryKey();
                    azpk1.setIdAzione(1);
                    azpk1.setIdRischio("P7R1");
                    azpk1.setTipo('M');
                    azpk1.setIdentifier(1);
                    for(int i=0; i<10; i++){
                        int ident = generateIdentifier(session, azpk1,out);
                        out.println(i+"\tGENERATO IDENT:"+ident);
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

    //prints a project in xml format into stream 'out'
    private void printProject(Progetto p, PrintWriter out){
        out.println("<progetto idName=\""+0+"\">\n" +
                        "\t<isCase>"+p.getIsCase()+"</isCase>\n" +
                        "\t<isOpen>"+p.getIsOpen()+"</isOpen>\n"+
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
                    out.println("\t\t<probIniziale>"+r.getProbabilitaIniziale()+"</probIniziale>\n");
                    out.println("\t\t<impattoIniziale>"+r.getImpattoIniziale()+"</impattoIniziale>\n");
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
    //function to create a list of risks from the current request
    private List extractRisksFromRequest(HttpServletRequest request, boolean enable){

        LinkedList<Rischio> list = new LinkedList<Rischio>();
        String what = "";
        for(int j=0; j<2; j++){
            if(enable){
                if(j==0) what = "risk_";
                else what = "newrisk_";
            }

            //reading the number of risks to load
            Integer cnt = Integer.parseInt(request.getParameter(what+"cnt"));
            if(cnt == null)
                return list;

            //reading fields and building objects
            for(int i=0; i<cnt; i++){
                Rischio r = new Rischio();
                r.setCodice((String) request.getParameter(what+"idRischio_"+i));
                r.setDescrizione((String) request.getParameter(what+"descrizione_"+i));
                r.setCausa((String) request.getParameter(what+"causa_"+i));
                r.setEffetto((String) request.getParameter(what+"effetto_"+i));
                r.setCodiceChecklist(Integer.parseInt(request.getParameter(what+"codiceChecklist_"+i)));
                r.setStato((String) request.getParameter(what+"stato_"+i));
                r.setVerificato(Integer.parseInt(request.getParameter(what+"rVer_"+i)));
                r.setContingency(Double.parseDouble(request.getParameter(what+"contingency_"+i)));
                r.setProbabilitaIniziale(Integer.parseInt(request.getParameter(what+"probIniziale_"+i)));
                r.setImpattoIniziale(Integer.parseInt(request.getParameter(what+"impattoIniziale_"+i)));
                r.setCategoria(request.getParameter(what+"categoria_"+i));
                r.setCostoPotenzialeImpatto(Integer.parseInt(request.getParameter(what+"costoPotenzialeImpatto_"+i)));
                r.setNumeroRevisione(Integer.parseInt(request.getParameter(what+"revisione_"+i)));
                


                //filled fields
                list.add(r);
            }
            if(!enable)
                break;
        }

        return list;
    }
    //function to extract a project from the current request
    private Progetto extractProjectFromRequest(HttpServletRequest request, PrintWriter out){
        Progetto p = new Progetto();


        try{
            p.setIsCase(Boolean.parseBoolean(request.getParameter("iscase")));
            p.setIsOpen(Boolean.parseBoolean(request.getParameter("isopen")));
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
        /*
        p.setIm(new ImpattoStrategico(Boolean.parseBoolean(request.getParameter("ckIM"))?Integer.parseInt(request.getParameter("IM")):null));
        p.setIc(new ImpattoStrategico(Boolean.parseBoolean(request.getParameter("ckIC"))?Integer.parseInt(request.getParameter("IC")):null));
        p.setIp(new ImpattoStrategico(Boolean.parseBoolean(request.getParameter("ckIP"))?Integer.parseInt(request.getParameter("IP")):null));
        p.setIa(new ImpattoStrategico(Boolean.parseBoolean(request.getParameter("ckIA"))?Integer.parseInt(request.getParameter("IA")):null));
        p.setIpp(new ImpattoStrategico(Boolean.parseBoolean(request.getParameter("ckIPP"))?Integer.parseInt(request.getParameter("IPP")):null));
        */
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
    //function to extract actions from the current request
    private List extractActionsFromRequest(HttpServletRequest request, boolean enable, PrintWriter out){
        
        LinkedList<Azioni> list = new LinkedList<Azioni>();
        String what = "";
        for(int j=0; j<2; j++){
            if(enable){
                if(j==0) what = "action_";
                else what = "newaction_";
            }

            //reading the number of actions to load
            Integer cnt = Integer.parseInt(request.getParameter(what+"cnt"));
            if(cnt == null)
                return list;
            //temporary identifier
            int identif = 1;

            //building actions
            for(int i=0; i<cnt; i++){
                Azioni a = new Azioni();

                a.getPrimaryKey().setIdAzione(Integer.parseInt(request.getParameter(what+"idAzione_"+i)));
                a.getPrimaryKey().setIdRischio(request.getParameter(what+"idRischio_"+i));
                a.getPrimaryKey().setIdentifier(Integer.parseInt(request.getParameter(what+"identifier_"+i)));//Integer.parseInt(request.getParameter("identifier_"+i)));
                a.getPrimaryKey().setTipo(request.getParameter(what+"tipo_"+i).charAt(0));
                a.setDescrizione(request.getParameter(what+"descrizione_"+i));
                a.setRevisione(Integer.parseInt(request.getParameter(what+"revisione_"+i)));
                a.setStato(request.getParameter(what+"stato_"+i));
                if(!Boolean.parseBoolean(request.getParameter(what+"ckintensita_"+i)))
                    a.setIntensita(-50);
                else
                    a.setIntensita(Integer.parseInt(request.getParameter(what+"intensita_"+i)));
                list.add(a);
            }
            if(!enable)
                break;
        }
        return list;
    }
    //function to build a project from alla datas passed as argument
    private Progetto buildProject(Progetto p, List  riskList, List actionList, PrintWriter out, HttpSession session){
        try{
            //checking if project has a valid identifier
            if(!Progetto.checkAvailable(p.getCodice()))
                return null;

            /*out.println("BEGIN");
            printProject(p, out);
            Iterator m1 = riskList.iterator();
            while(m1.hasNext()){
                Rischio r = (Rischio) m1.next();
                printRisk(r, out, 0, false);
            }
            m1 = actionList.iterator();
            while(m1.hasNext()){
                Azioni a = (Azioni) m1.next();
                printAction(a, out, 0);
            }*/
           
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
                /*XXX SUPPONGO OGNI RISCHIO ABBIA GIA' LA GIUSTA CHIAVE!!*/
                p.aggiungiRischio(r);
            }
        } catch (Exception e){
            out.println(e);
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
        //inserting default description into risk
        //XXX remove?
        /*CkRischi ckr = (CkRischi) CkRischi.getById(CkRischi.class, r.getCodiceChecklist());
        r.setDescrizione(ckr.getDescrizione());*/
    }
    //writes digest in xml
    private void printDigest(Progetto p, List riskList, List actionList, PrintWriter out){
        return;
    }
    //decides if the project is a case to store
    private boolean compareModificationsRisks(LinkedList<Rischio>[] gruppi, List lista){
        //XXX test
        Iterator it = lista.iterator();
        while(it.hasNext()){
            Rischio r = (Rischio) it.next();
            //finding risk
            boolean found = false;
            for(int i=0; i<gruppi.length && !found;i++){
                Iterator t = gruppi[i].iterator();
                while(t.hasNext() && !found){
                    Rischio c = (Rischio) t.next();

                    if(c.getCodice().compareTo(r.getCodice()) == 0){
                        //is the same risk
                        if(c.getContingency() != r.getContingency() ||
                            c.getProbabilitaIniziale() != r.getProbabilitaIniziale() ||
                            c.getImpattoIniziale() != r.getImpattoIniziale() ||
                            c.getCostoPotenzialeImpatto() != r.getCostoPotenzialeImpatto()){
                            //if different
                            return true;

                        }
                        found = true;
                    }
                }
            }
        }

        return false;
    }
    //same as above, but for actions
    private boolean compareModificationsActions(List<Azioni> prev, List<Azioni> current, PrintWriter out){
        //XXX test
        Iterator it = current.iterator();

        while(it.hasNext()){
            boolean found = false;
            Azioni a = (Azioni) it.next();
           
            Iterator t = prev.iterator();
                while(t.hasNext() && !found){
                    Azioni c = (Azioni) t.next();
                    
                    if(c.getPrimaryKey().equals(a.getPrimaryKey())){
                        
                        //is the same action
                        if(c.getIntensita() != a.getIntensita()){
                            //if different
                            return true;
                        }
                        found = true;
                    }
                }
        }
        return false;
    }
    //generates valid actionId
    private int generateIdentifier(HttpSession session, AzioniPrimaryKey pk, PrintWriter out) throws Exception{
        LinkedList<AzioniPrimaryKey> listaPkAzioni = (LinkedList<AzioniPrimaryKey>) session.getAttribute("listaPkAzioni");
        if(listaPkAzioni == null){
            listaPkAzioni = new LinkedList<AzioniPrimaryKey>();
        }

        AzioniPrimaryKey t = new AzioniPrimaryKey(pk.getIdentifier(), pk.getIdAzione(), pk.getIdRischio(), pk.getTipo());

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
        /*
        while(listaPkAzioni.contains(t)){
            int old = t.getIdentifier();
            t.setIdentifier(old+1);
        }*/
        //sign used key
        //AzioniPrimaryKey azpk = new AzioniPrimaryKey(pk.getIdentifier(), pk.getIdAzione(), pk.getIdRischio(), pk.getTipo());
        listaPkAzioni.add(t);

        //debug print
        /*
        Iterator it = listaPkAzioni.iterator();
        out.println("LISTA CONTENT");
        while(it.hasNext()){
            AzioniPrimaryKey y = (AzioniPrimaryKey) it.next();
           out.println("-ident: "+y.getIdentifier()+" azione:"+y.getIdAzione()+" rischio:"+y.getIdRischio()+" tipo"+y.getTipo()+" ");
        }
        */
        session.setAttribute("listaPkAzioni", listaPkAzioni);

        return t.getIdentifier();
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
    }out.println("puppa");
*/

    /*SIMILARITY UTILITIES*/
    private void suggestions(Progetto queryDesc, HttpSession session,PrintWriter out) throws Exception {
        //String file = "/media/gutzy/home/narduz/Documenti/Università/Sistemi intelligenti/Progetto/repository03/connectingInterface/similarityconfig/groupsConfig.xml";
        String file = "C:\\Documents and Settings\\Rosetta Stoned\\My Documents\\NetBeansProjects\\0219\\trunk\\projectRiskCbr\\WebContent\\assets\\groupsConfig.xml";
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
    //returns a list of checklists codes of all previously suggested risks. Dummy function
    /*XXXprivate List<Integer> alreadySuggestedCkRisks(){
        try{
            return (List<Integer>) Rischio.executeQuery("select codChecklist from CkRischi where codChecklist <= 10");
        } catch (Exception e) {}

        return new LinkedList();
    }*/

}
