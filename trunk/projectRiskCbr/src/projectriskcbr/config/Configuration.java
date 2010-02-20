package projectriskcbr.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.thoughtworks.xstream.XStream;


public class Configuration {

	
	public List<ConfigurationGroup> groups = new ArrayList<ConfigurationGroup>();
	public String description = new String();
	
	public Integer kProgetto;
	public Integer kRischio;
	public Integer kAzioni;
	
	public Boolean adaptIntensita = false;
	
	public void save(FileOutputStream configFile) {
        XStream xs = new XStream();
        xs.alias("config", Configuration.class);
        xs.alias("group", ConfigurationGroup.class);
        xs.alias("attribute", ConfigurationAttribute.class);
        xs.toXML(this, configFile);
	}
	
	public static Configuration load(FileInputStream configFile) {
        XStream xs = new XStream();
        xs.alias("config", Configuration.class);
        xs.alias("group", ConfigurationGroup.class);
        xs.alias("attribute", ConfigurationAttribute.class);
        Configuration configuration = (Configuration)xs.fromXML(configFile);
        return configuration;
	}
}
