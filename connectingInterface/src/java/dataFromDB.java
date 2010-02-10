/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.PrintWriter;
/**
 *
 * @author User
 */
public class dataFromDB {
    dataFromDB (int type, PrintWriter out) {

        switch(type) {

            // Getting the checklist of CLIENTE
            case 0:
                // HIBERNATE => Select nome FROM cliente

                /*
                 * OUTPUT (for each record):
                 * out.println("<nome idName="idCliente">nomeCliente</nome>");
                 */
                break;

            // Getting the checklist of OGGETTOFORNITURA
            case 1:
                // The same of above
                break;

            // Getting the checklist of REPARTO
            case 2:
                // The same of above
                break;
        }

        //out.println("Hai richiesto: " + type + "\n");
        out.println("<name>Franci</name>");
        out.println("<name>Luigi</name>");
        out.println("<name>Maria</name>");
        out.println("<name>Ettore</name>");
    }
}
