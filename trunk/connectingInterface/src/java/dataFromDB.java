/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import persistentclasses.*;
import persistentclasses.attributes.*;
import funzioniprova.*;

/**
 *
 * @author User
 */
public class dataFromDB {
    dataFromDB (int type, PrintWriter out) {

        //parte provvisoria, va sostituita col meccanismo delle sessioni
        try{
            /*
            out.println("PART1");
            SessionObject.getStarted();
            out.println("PART2");
            SessionObject.newTransaction();
            out.println("PART3");
             * */

            Elemento.setIntero(1);
            out.print(Elemento.getIntero());
            switch(type) {

                // Getting the checklist of CLIENTE
                case 0:



                    // HIBERNATE => Select nome FROM cliente
                    /*
                    List clienti = Progetto.executeQuery("select distinct nomeCliente from Progetto");
                    Iterator it = clienti.iterator();
                    int index = 0;
                    while(it.hasNext()) {
                        String cl = (String) it.next();
                        out.println("<nome idName=\""+index+"\">"+cl+"</nome>");
                        index++;
                    }*/
                    /*
                     * OUTPUT (for each record):
                     * out.println("<nome idName="idCliente">nomeCliente</nome>");
                     * out.println("<nome idName="1">nomeCliente1</nome>");
                     */
                    break;

                // Getting the checklist of OGGETTOFORNITURA
                case 1:
                    // The same of above
                    //out.println("<oggettoFornitura idName="1">nomeCliente1</oggettoFornitura>");
                    break;

                // Getting the checklist of REPARTO
                case 2:
                    // The same of above
                    //out.println("<reparto idName="1">nomeCliente1</reparto>");
                    break;
            }
            out.println("PART4");
            SessionObject.endTransaction();
            out.println("PART5");
        } catch (Exception e){out.println(e);}

        //out.println("Hai richiesto: " + type + "\n");
        
        out.println("<name>Franci</name>");
        out.println("<name>Luigi</name>");
        out.println("<name>Maria</name>");
        out.println("<name>Ettore</name>");
        
    }
}
