/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package desktopparser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import persistentclasses.*;
import persistentclasses.attributes.*;
/**
 *
 * @author Rosetta Stoned
 */
public class DesktopParser {
    public DesktopParser() {}

    public String parser(String filename) {
        try {
            // Creating a new excel workbook object from the name file
            Workbook workbook = Workbook.getWorkbook(new File(filename));

            SessionObject.getStarted();
            SessionObject.newTransaction();
            sheetProgetto(workbook);
            SessionObject.endTransaction();
            System.out.println("Progetti salvati");

            SessionObject.newTransaction();
            sheetRischio(workbook);
            SessionObject.endTransaction();
            System.out.println("Rischi salvati");


            SessionObject.newTransaction();
            sheetCausaEffetto(workbook);
            SessionObject.endTransaction();
            System.out.println("Causa ed Effetto aggiunti");

            SessionObject.newTransaction();
            sheetAzioni(workbook);
            SessionObject.endTransaction();
            System.out.println("Azioni aggiunte");

            // Closing the stream
            workbook.close();

        } catch(IOException ioex) {
            System.out.print("Not possible to get the excel file\n");
            ioex.printStackTrace(System.out);
        } catch (JXLException jxlex) {
            jxlex.printStackTrace(System.out);
        } catch(Exception e) {
           System.out.println(e);
        }
        return filename;
    }

    public void sheetProgetto(Workbook workbook) throws Exception{
        // Selecting the correspondent sheet
        Sheet sheet = workbook.getSheet(0);

        // Current cell examined
        Cell current;

        // I need a variable to get the number of consecuive empty rows.
        // I assume that 4 consecutive rows implie the end of file
        int emptyCnt = 0;

        // I need a variable to establish that the first non-empty line (wich contains the descriptions) has been reached
        boolean firstLineFlag = false;
        
        //flag that indicates that the current line is a valid project (note empty line)
        boolean writable;
        // Creating interfacing objects
        Progetto assigned;
        LivelloDiRischio riskLevel = new LivelloDiRischio();
        ImpattoStrategico impact = new ImpattoStrategico();
        
        // Reading unknown number of lines, cause I don't know the exact number of projects
        //start from the 2nd line (i=1), cause the first one contains the index of the table
        for (int i = 0; ; i++)
        {
                // If 4 consecutive lines are empty, the database is fineshed
                if (emptyCnt == 4)
                    break;
                writable = true;
                assigned = new Progetto();
                assigned.setIsCase(true);
                assigned.setIsOpen(true);
                // Readings 40 columns: one for each "Progetto" sheet
                for (int j = 0; j < 39 ; j++) {
                    try {
                        current = sheet.getCell(j, i);
                        // Checking the emptiness of the cell
                        String content = current.getContents();
                        
                        // Empty cell => next row
                        if (content.isEmpty()){
                            // If two consecutive cells in the same row are empty, go to the next line
                            try {
                                Cell currentIncremented = sheet.getCell(j+1, i);
                                if (currentIncremented.getContents().isEmpty()) {
                                    emptyCnt++;
                                    writable = false;
                                    break;
                                }
                            } catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                                // Other columns are not specified
                                break;
                            }
                        }
                        // Non empty content
                        else {
                            // Heading description row, without real data
                            if (firstLineFlag == false) {
                                firstLineFlag = true;
                                writable = false;
                                break;
                            }
                            emptyCnt = 0;
                            // Here I have to give the content read to the object mapped in the database

                            // Giving the content to a particular variable by the column number
                            switch(j) {
                                case 0:
                                    assigned.setCodice(toString(current));
                                    break;

                                case 1:
                                    // Getting a double value and casting it to int
                                    assigned.setReparto(toInt(current));
                                    break;

                                case 2:
                                    assigned.setClasseRischio(toInt(current));
                                    break;

                                case 3:
                                    assigned.setValoreEconomico(toDouble(current));
                                    break;

                                case 4:
                                    assigned.setDurataContratto(toInt(current));
                                    break;

                                case 5:
                                    assigned.setOggettoFornitura(toString(current));
                                    break;

                                case 6:
                                    assigned.setNomeCliente(toString(current));
                                    break;

                                case 7:
                                case 10:
                                case 13:
                                case 16:
                                case 19:
                                case 22:
                                case 25:
                                case 28:
                                case 31:
                                    riskLevel.setR1(toInt(current));
                                    break;

                                case 8:
                                case 11:
                                case 14:
                                case 17:
                                case 20:
                                case 23:
                                case 26:
                                case 29:
                                case 32:
                                    riskLevel.setR2(toInt(current));
                                    break;

                                case 9:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setPaese(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 12:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setMercatoCliente(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 15:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setContratto(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 18:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setComposizionePartnership(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 21:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setIngegneria(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 24:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setApprovvigionamento(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 27:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setFabbricazione(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 30:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setMontaggio(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 33:
                                    riskLevel.setR3(toInt(current));
                                    assigned.setAvviamento(riskLevel);
                                    riskLevel = new LivelloDiRischio();
                                    break;
                                case 34:
                                    assigned.setIm(new ImpattoStrategico(toInt(current)));
                                    break;
                                case 35:
                                    assigned.setIc(new ImpattoStrategico(toInt(current)));
                                    break;
                                case 36:
                                    assigned.setIp(new ImpattoStrategico(toInt(current)));
                                    break;
                                case 37:
                                    assigned.setIa(new ImpattoStrategico(toInt(current)));
                                    break;
                                case 38:
                                    assigned.setIpp(new ImpattoStrategico(toInt(current)));
                                    break;
                            }
                        }
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                        // Other lines are not specified
                        return;
                    }
                }//for (cells)
                if(writable && Progetto.checkAvailable(assigned.getCodice()))
                    assigned.write();
            }//for (lines)
    }

    public void sheetRischio(Workbook workbook) throws Exception{
        // Selecting the correspondent sheet
        Sheet sheet = workbook.getSheet(1);

        // Current cell examined
        Cell current;

        // I need a variable to get the number of consecuive empty rows.
        // I assume that 4 consecutive rows implie the end of file
        int emptyCnt = 0;

        // I need a variable to establish that the first non-empty line (wich contains the descriptions) has been reached
        boolean firstLineFlag = false;

        //flag that indicates that the current line is a valid project (note empty line)
        boolean writable;

        //variable to remember the already inserted description in risk checklist
        LinkedList<Integer> inserted = new LinkedList<Integer>();

        // Creating interfacing objects
        Rischio assigned;

        // Reading unknown number of lines, cause I don't know the exact number of projects
        for (int i = 0; ; i++)
        {
                // If 4 consecutive lines are empty, the database is fineshed
                if (emptyCnt == 4)
                    break;
                writable = true;
                assigned = new Rischio();
                // Readings 40 columns: one for each "Rischio" sheet
                for (int j = 0; j < 24 ; j++) {
                    try {
                        //there are some hidden fields not to considerate
                        if(j==5)
                            j=18;
                        current = sheet.getCell(j, i);
                        // Checking the emptiness of the cell
                        String content = current.getContents();

                        // Empty cell => next row
                        if (content.isEmpty()){
                            // If two consecutive cells in the same row are empty, go to the next line
                            try {
                                Cell currentIncremented = sheet.getCell(j+1, i);
                                if (currentIncremented.getContents().isEmpty()) {
                                    emptyCnt++;
                                    writable = false;
                                    break;
                                }
                            } catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                                // Other columns are not specified
                                break;
                            }
                        }
                        // Non empty content
                        else {
                            // Heading description row, without real data
                            if (firstLineFlag == false) {
                                firstLineFlag = true;
                                writable = false;
                                break;
                            }
                            emptyCnt = 0;
                            // Here I have to give the content read to the object mapped in the database

                            // Giving the content to a particular variable by the column number
                            switch(j) {
                                case 0:
                                    assigned.setIdProgramma(toString(current));
                                    break;
                                case 1:
                                    assigned.setCodice(toString(current));
                                    break;
                                case 2:
                                    assigned.setCodiceChecklist(toInt(current));
                                    break;
                                case 3:
                                    assigned.setStato(new StatoRischio(toString(current)));
                                    break;
                                case 4:
                                    assigned.setCategoria(new CategoriaRischio(toString(current)));
                                    break;
                                case 18:
                                    assigned.setVerificato(toInt(current));
                                    break;
                                case 19:
                                    assigned.setNumeroRevisione(toInt(current));
                                    break;
                                case 20:
                                    assigned.setDescrizione(toString(current));
                                    //creating the checklist description if the risk is not in the checklist
                                    if(!inserted.contains(assigned.getCodiceChecklist()) &&
                                       CkRischi.checkAvailable(assigned.getCodiceChecklist()))
                                    {
                                        //inserting description
                                        CkRischi ckr = new CkRischi();
                                        ckr.setFields(assigned.getCodiceChecklist(), toString(current));
                                        ckr.write();
                                        inserted.add(assigned.getCodiceChecklist());
                                    }
                                    break;
                                case 21:
                                    assigned.setContingency(toDouble(current));
                                    break;
                                case 22:
                                    assigned.setProbabilitaIniziale(toInt(current));
                                    break;
                                case 23:
                                    assigned.setImpattoIniziale(toInt(current));
                                    break;
                            }
                        }
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                        // Other lines are not specified
                        return;
                    }
                }//for (cells)
                if(writable && Rischio.checkAvailable(assigned.getCodice()))
                    assigned.write();
            }//for (lines)
    }

    public void sheetCausaEffetto(Workbook workbook) {
        // Selecting the correspondent sheet
        Sheet sheet = workbook.getSheet(2);
        
        int emptyCnt = 0;
        
        for(int i = 1; emptyCnt<4 ;i++){
            try{
                Cell current = sheet.getCell(0, i);
                // Checking the emptiness of the cell
                String content = current.getContents();

                if(content.isEmpty()){
                    emptyCnt ++;
                    continue;
                }
                //retrieving risk from DB and adding properties

                Rischio r = (Rischio) Rischio.getById(Rischio.class, content);
                if(r!=null)
                {
                    String causa = sheet.getCell(1,i).getContents();
                    r.setCausa(causa);
                    String effetto = sheet.getCell(2,i).getContents();
                    r.setEffetto(effetto);
                    r.update();
                }
                    //XXX System.out.println(content + "\nCAUSA:" + sheet.getCell(1,i).getContents() + "\nEFFETTO:"+sheet.getCell(2,i).getContents());
            } catch (Exception e){
                    //System.out.println(e);
                break;
            }
        }
    }

    public void sheetAzioni(Workbook workbook) throws Exception {
        // Selecting the correspondent sheet
        Sheet sheet = workbook.getSheet(3);

        // Current cell examined
        Cell current;

        // I need a variable to get the number of consecuive empty rows.
        // I assume that 4 consecutive rows implie the end of file
        int emptyCnt = 0;

        // I need a variable to establish that the first non-empty line (wich contains the descriptions) has been reached
        boolean firstLineFlag = false;

        //flag that indicates that the current line is a valid project (note empty line)
        boolean writable;

        // Creating interfacing objects
        Azioni assigned;

        // Reading unknown number of lines, cause I don't know the exact number of projects
        for (int i = 0; ; i++)
        {
                // If 4 consecutive lines are empty, the database is fineshed
                if (emptyCnt == 4)
                    break;
                
                writable = true;
                assigned = new Azioni();
                // Readings 40 columns: one for each "Rischio" sheet
                for (int j = 0; j < 8 ; j++) {
                    try {
                        current = sheet.getCell(j, i);
                        // Checking the emptiness of the cell
                        String content = current.getContents();
                        //XXX
                        //System.out.println(content);
                        // Empty cell => next row
                        if (content.isEmpty()){
                            // If two consecutive cells in the same row are empty, go to the next line
                            try {
                                Cell currentIncremented = sheet.getCell(j+1, i);
                                if (currentIncremented.getContents().isEmpty()) {
                                    emptyCnt++;
                                    writable = false;
                                    break;
                                }
                            } catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                                // Other columns are not specified
                                break;
                            }
                            //se mancano i campi della chiave ignoro la riga
                            if(j==1 || j==2 || j==3)
                            {
                                writable=false;
                                break;
                            }
                        }
                        // Non empty content
                        else {
                            // Heading description row, without real data
                            if (firstLineFlag == false) {
                                firstLineFlag = true;
                                writable = false;
                                break;
                            }
                            emptyCnt = 0;
                            // Here I have to give the content read to the object mapped in the database

                            // Giving the content to a particular variable by the column number
                            switch(j) {
                                case 0:
                                    
                                    break;
                                case 1:
                                    assigned.setPrimaryKey(new AzioniPrimaryKey());
                                    assigned.getPrimaryKey().setIdAzione(toInt(current));
                                    break;
                                case 2:
                                    assigned.getPrimaryKey().setIdRischio(content);
                                    break;
                                case 3:
                                    assigned.getPrimaryKey().setTipo(content.charAt(0));
                                    break;
                                case 4:
                                    assigned.setIntensita(toInt(current));
                                    break;
                                case 5:
                                    assigned.setStato(content);
                                    break;
                                case 6:
                                    assigned.setDescrizione(content);
                                    break;
                                case 7:
                                    assigned.setRevisione(toInt(current));
                                    break;
                            }
                        }
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException ee) {
                        // Other lines are not specified
                        return;
                    }
                }//for (cells)
                if(writable && Azioni.checkAvailable(assigned.getPrimaryKey()))
                    assigned.write();
            }//for (lines)
    }

    public void sheetCkMitigazioni(Workbook workbook) {
        // Selecting the correspondent sheet
        Sheet sheet = workbook.getSheet(4);
    }

    public void sheetCkRecovery(Workbook workbook) {
        // Selecting the correspondent sheet
        Sheet sheet = workbook.getSheet(5);
    }

    public void sheetStorico(Workbook workbook) {
        // Selecting the correspondent sheet
        Sheet sheet = workbook.getSheet(6);
    }

    private String toString(Cell current) {
        return current.getContents();
    }

    private int toInt(Cell current) {
        return (int) (((NumberCell) current).getValue());
    }

    private double toDouble(Cell current) {
        return (double) (((NumberCell) current).getValue());
    }
}