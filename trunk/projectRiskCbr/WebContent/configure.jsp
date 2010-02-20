
<%@ page import="jcolibri.casebase.*" %>
<%@ page import="jcolibri.cbrcore.*" %>
<%@ page import="jcolibri.connector.*" %>
<%@ page import="jcolibri.test.*" %>


<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>


<%@page import="projectriskcbr.config.Configuration"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="projectriskcbr.config.ConfigurationGroup"%>
<%@page import="projectriskcbr.config.ConfigurationAttribute"%><html>
<head></head>
<body>
<table border="0" cellpadding="2" cellspacing="7" width="100%">

  <tr>
  <td>
	<a href="http://gaia.fdi.ucm.es"><img src="img/gaia.gif" border="0"></a>
  </td>
  <td colspan="2" valign="middle" bgcolor="#738EAB">
    <font size="-1">
       <h1><font face="arial" color="#ffffff"> projectRiskCbr - Loading Configuration</font></h1>
	</font>

	<hr color="White">
  </td>
  </tr>
</table>
<p></p>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
<%
		FileInputStream configFile = new FileInputStream(getServletContext().getRealPath("assets/groupsConfig.xml"));

		// loading configuration
		Configuration configuration = Configuration.load(configFile);
		
		System.out.println("Loaded configuration file from assets/groupsConfig.xml");
		System.out.println("Printing basic configuration informations:<br>" +
								"Number of progetti to gather per group: " + configuration.kProgetto + "<br>" +
								"Number of rischi to suggest per group: " + configuration.kRischio + "<br>" +
								"Number of azioni to suggest per risk: " + configuration.kAzioni + "<br>" +
								"Do we have to adapt intensita for each azione?: " + configuration.adaptIntensita + "<br>" +
								"Number of groups in this configuration: " + configuration.groups.size() + "<br>");
		
		System.out.println("Printing basic information for each group:<br>");
		for(ConfigurationGroup group : configuration.groups) {
			System.out.println("Group " + group.getName() + ":<br>");
			for(ConfigurationAttribute attr : group.attributes) {
				System.out.println("-- " + attr.getName() + "\t\t-" + attr.getWeight() + "<br>");	
			}
		}
	    
		// storing configuration into servlet context. It will be used later on
		getServletContext().setAttribute("configuration", configuration);
%>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>
</html>
