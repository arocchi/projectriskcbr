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
        //dp.parser("C:\\Users\\nihil\\Desktop\\projSisInt\\DataBaseNew.xls");
        dp.parser("/home/narduz/Scaricati/DataBaseNew.xls");
    }
}
