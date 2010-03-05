import desktopparser.*;
import persistentclasses.*;
import persistentclasses.attributes.*;

/**
 *
 */
public class SimpleParser {
    public static void main(String[] args)
    {
        DesktopParser dp = new DesktopParser();
        dp.parser("C:\\Documents and Settings\\Rosetta Stoned\\Desktop\\DataBaseNew.xls");
        //dp.parser("/home/narduz/Scaricati/DataBaseNew.xls");
    }
}
