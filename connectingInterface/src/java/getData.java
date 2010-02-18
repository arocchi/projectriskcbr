import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
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
        try {
            // Firstly I examine the type parameter
            String type = request.getParameter("type");

            if (type != null) {
                if (type.equals("cliente")){
                    // Calling a method that gives all the customers in the database
                    dataFromDB cl = new dataFromDB(0, out);
                } else if (type.equals("oggettoFornitura")){
                    dataFromDB cl = new dataFromDB(1, out);
                } else if (type.equals("reparto")) {
                    dataFromDB cl = new dataFromDB(2, out);
                } else if (type.equals("risksbygroup")) {
                    dataFromDB cl = new dataFromDB(3, out);
                } else if (type.equals("risksnogroup")) {
                    dataFromDB cl = new dataFromDB(4, out);
                } else if (type.equals("risksbycategory")) {
                    dataFromDB cl = new dataFromDB(5, out);
                } else {
                    out.println("<error>Type not allowed</error>");
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
