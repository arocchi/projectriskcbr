
<%@ page import="jcolibri.casebase.*" %>
<%@ page import="jcolibri.cbrcore.*" %>
<%@ page import="jcolibri.connector.*" %>
<%@ page import="jcolibri.test.test4.*" %>
<%@ page import="jcolibri.method.retrieve.*" %>
<%@ page import="jcolibri.method.retrieve.NNretrieval.*" %>
<%@ page import="jcolibri.method.retrieve.NNretrieval.similarity.global.*" %>
<%@ page import="jcolibri.method.retrieve.NNretrieval.similarity.local.*" %>
<%@page import="jcolibri.method.retrieve.selection.SelectCases"%>

<%@page import="jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage"%>

<%@page import="projectriskcbr.config.Configuration"%>
<%@page import="projectriskcbr.config.ConfigurationGroup"%>
<%@page import="projectriskcbr.config.ConfigurationAttribute"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>


<%@ page import="java.util.*" %>


<%@page import="org.hibernate.SessionFactory"%>
<%@page import="persistentclasses.Progetto"%>
<%@page import="persistentclasses.attributes.LivelloDiRischio"%>
<%@page import="persistentclasses.attributes.ImpattoStrategico"%>
<%@page import="projectriskcbr.config.NNConfigurator"%>
<%@page import="projectriskcbr.config.SelfNNConfigurator"%>


<%@page import="persistentclasses.utils.RischioSuggester"%>
<%@page import="persistentclasses.Rischio"%><html>
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
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
<%
		SessionFactory factory = 											(SessionFactory)getServletContext().getAttribute("factory");
		Collection<CBRCase> cases = 										(Collection<CBRCase>)getServletContext().getAttribute("cases");
		CBRCase query =	 													(CBRCase)getServletContext().getAttribute("query");
		Configuration configuration = 										(Configuration)getServletContext().getAttribute("configuration");
		Map<NNConfig, ConfigurationGroup> simConfigs = 						(Map<NNConfig, ConfigurationGroup>)getServletContext().getAttribute("simConfigs");
		Map<ConfigurationGroup, Collection<RetrievalResult>> groupsResults =(Map<ConfigurationGroup, Collection<RetrievalResult>>)getServletContext().getAttribute("groupsResults");

	/* ----------------- */
	/* RISKS EXTRACTION  */
	/* ----------------- */
	Map<ConfigurationGroup, Collection<RischioSuggester>>  rischioSuggestersByGroup = new HashMap<ConfigurationGroup, Collection<RischioSuggester>>();
	
	// for each group, we will now extract all risks, and get the most relevant ones
	for(Map.Entry<ConfigurationGroup, Collection<RetrievalResult>> entry : groupsResults.entrySet()) {
		Collection<RischioSuggester> rischioSuggesters = RischioSuggester.getTopKSuggesters(query, entry.getValue(), configuration.kRischio);
		rischioSuggestersByGroup.put(entry.getKey(), rischioSuggesters);
		
		out.println("Best risks for query: " + query + " in group " + entry.getKey().getName());
		out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
		out.println("<tr><td><b>Risk CheckList Id</b></td><td><b>Risk</b></td></tr>");
		for(RischioSuggester suggester: rischioSuggesters) {
			Rischio rischio = suggester.getSuggestion();
			out.println("<tr><td>" + suggester.getRiskId()+"</td><td>" + rischio + "</td></tr>");
		}
		out.println("</table>");
		
	}
	 
	
	getServletContext().setAttribute("factory", factory);
	getServletContext().setAttribute("cases", cases);
	getServletContext().setAttribute("query", query);
	getServletContext().setAttribute("configuration", configuration);
	getServletContext().setAttribute("simConfigs", simConfigs);
	getServletContext().setAttribute("groupsResults", groupsResults);
	getServletContext().setAttribute("rischioSuggestersByGroup", rischioSuggestersByGroup);
	
%>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>

</html>
