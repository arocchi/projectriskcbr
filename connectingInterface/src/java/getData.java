import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.SessionFactory;
import persistentclasses.SessionObject;

/**
 * @author User
 */
public class getData extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //standard output
        PrintWriter out = response.getWriter();

        //type parameter
        String type = request.getParameter("type");
        //control if set
        if(type == null || type.isEmpty()){
            out.println("No type parameter set");
            out.close();
            return;
        }

        try {
            Thread.currentThread().sleep(600);
        } catch (InterruptedException ex) {
            Logger.getLogger(getData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //checking session
        HttpSession session = request.getSession();
        Boolean activesession = (Boolean) session.getAttribute("activesession");
        if(type.equals("give_nullrequest")){//opening new session
            if(activesession!=null){
                //previous session already opened
                //deleting data from the previous session and opening a new one
                session.invalidate();
                session = request.getSession();
                session.setMaxInactiveInterval(10000);
            }
            //setting active the current session
            session.setAttribute("activesession", true);
            activesession = true;
            //building sessionfactory and storing it into session
            SessionFactory sf = SessionObject.getStarted();
            session.setAttribute("sessionfactory", sf);
        }
        
        try {
            
            //integer of the operation to execute
            int typenum=-1;
            
            //if requesting data without an active session, giving error
            if(activesession==null)
                out.println("<error>Invalid session</error>");
            
            //I examine the type parameter

            
            else if (type != null) {
                /*ONLY OPEN SESSION. DO NOTHING*/
                if(type.equals("give_nullrequest")) out.println("nullrequest done");

                /*TYPES TO CREATE A NEW PROJECT*/
                else if (type.equals("take_cliente"))               typenum = 0; //gives all the customers in the database
                else if (type.equals("take_oggettofornitura"))      typenum = 1; //gives all the items "oggettoFornitura"
                else if (type.equals("take_reparto"))               typenum = 2; //gives all the "reparto" felds in DB
                else if (type.equals("give_newproject"))            typenum = 101; //user gives data about the new project created
                else if (type.equals("take_risksbygroup"))          typenum = 3; //gives all risks suggested by similarity
                else if (type.equals("take_risksnogroup"))          typenum = 4; //gives all the common risks there are not present in any group
                else if (type.equals("take_risksbycategory"))       typenum = 5; //gives all the other risks, not suggested before
                else if (type.equals("give_risksbycategory"))       typenum = 102; //user gives codchecklist for all the selected risks that were not suggested before
                else if (type.equals("take_selectedrisksbycategory"))typenum = 13; //I give to user the risks corrensponding to the codes previously read
                else if (type.equals("give_allrisksforproject"))    typenum = 103; //user gives all the risks added to the project
                else if (type.equals("take_actionsbyrisk"))         typenum = 6; //gives all adapted actions for the previously selected risks
                else if (type.equals("give_allactionsforproject"))  typenum = 104; //user gives alla actions to add to the project
                else if (type.equals("take_digest"))                typenum = 7; //gives a summary of all the fields of new project created
                else if (type.equals("give_confirm"))               typenum = 105; //gives data="true" or "false" for confirmation or discard
                else if (type.equals("take_newidrischio"))          typenum = 14; //generates a new id for risk to give to user
                 
                    
                /*TYPES TO MODIFY A PROJECT */
                else if (type.equals("take_openedprojects"))        typenum = 8; //gives all the projects that can be modified (open projects)
                else if (type.equals("give_whatopenedproject"))     typenum = 106; //user gives the identifier of the project to modify, I store project id into session
                else if (type.equals("take_whatopenedproject"))     typenum = 15; //I give user the project previously stored
                else if (type.equals("give_actionsbyutilization"))  typenum = 112; //user gives me codchecklist for a risk and type of action. i store them and build response for the next request
                else if (type.equals("take_actionsbyutilization"))  typenum = 9; //gives all actions for a risk. Two categories: alreadyused + stillnotused
                else if (type.equals("give_mx"))                    typenum = 107; //user gives all the changes to the selected project, data="true" or "false" for confirmation or not

                /*TYPES TO ADD NEW RISK*/
                else if (type.equals("take_allchkrisks")) typenum = 10; //gives codchecklist + description for all risks
                else if (type.equals("give_newchkrisk")) typenum = 108; //user gives a description for the new risk to create
                
                /*TYPES TO MODIFY A RISK IN CHECKLIST*/
                else if (type.equals("give_updatechkrisk")) typenum = 109; //user gives codchecklist of thwe risk to update, and the new description

                /*TYPES TO ADD E NEW ACTION*/
                else if (type.equals("take_allchkactions")) typenum = 11; //gives two categories of checklist actions: mitigation and recovery
                else if (type.equals("give_newchkaction")) typenum = 110; //user gives description and type ('r' or 'm') of the new action

                /*TYPES TO MODIFY AN ACTION IN CHECKLIST*/
                else if (type.equals("give_updatechkaction")) typenum = 111; //user gives codchecklist, type('r' or 'm') and new description for the action

                /*CLOSING SESSION*/
                else if (type.equals("closesession")) typenum = 12; //deletes all data from session

                /*CLOSE PROJECT*/
                else if(type.equals("closeproject")) typenum = 999;
                /*TESTS*/
                else if(type.equals("test")) typenum = 666;

                /*TESTING TYPES*/

                /*NO TYPE*/
                else out.println("<error>Type not allowed</error>");

                /*DEBUG PURPOSES ONLY*/
                String debug = request.getParameter("debug");
                if(debug != null && debug.equals("yes")){
                    out.println(request.toString());
                }
                /*CALLING THE PROCEDURE*/
                if(typenum != -1){
                    dataFromDB cl = new dataFromDB(typenum, out, session, request);
                }
            }
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
