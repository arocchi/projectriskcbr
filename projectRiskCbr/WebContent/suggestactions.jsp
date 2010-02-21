
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
<%@page import="persistentclasses.Rischio"%>
<%@page import="persistentclasses.utils.AzioniSuggester"%>
<%@page import="persistentclasses.Azioni"%><html>
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
		SessionFactory factory = 															(SessionFactory)getServletContext().getAttribute("factory");
		Collection<CBRCase> cases = 														(Collection<CBRCase>)getServletContext().getAttribute("cases");
		CBRCase query =	 																	(CBRCase)getServletContext().getAttribute("query");
		Configuration configuration = 														(Configuration)getServletContext().getAttribute("configuration");
		Map<NNConfig, ConfigurationGroup> simConfigs = 										(Map<NNConfig, ConfigurationGroup>)getServletContext().getAttribute("simConfigs");
		Map<ConfigurationGroup, Collection<RetrievalResult>> groupsResults =				(Map<ConfigurationGroup, Collection<RetrievalResult>>)getServletContext().getAttribute("groupsResults");
		Map<ConfigurationGroup, Collection<RischioSuggester>> rischioSuggestersByGroup = 	(Map<ConfigurationGroup, Collection<RischioSuggester>>)getServletContext().getAttribute("rischioSuggestersByGroup");

	/* ------------------- */
	/* ACTIONS EXTRACTION  */
	/* ------------------- */
	
	// a rather complex data structure to save all actionsSuggesters in all risks by all groups
	Map<Map.Entry<ConfigurationGroup,RischioSuggester>, Collection<AzioniSuggester>>  azioniSuggestersByGroup = new HashMap<Map.Entry<ConfigurationGroup,RischioSuggester>, Collection<AzioniSuggester>>();
	
	// for each group, we will now extract all actions for each risk, and get the most relevant ones
	for(Map.Entry<ConfigurationGroup, Collection<RischioSuggester>> entry : rischioSuggestersByGroup.entrySet()) {
		for(RischioSuggester rischioSuggester : entry.getValue()) {
			Collection<AzioniSuggester> azioniSuggesters = AzioniSuggester.getTopKSuggesters(rischioSuggester, entry.getValue(), configuration.kAzioni);
			
			out.println("<br><br><h3>Best risks for risk: " + rischioSuggester.getRiskId() + " in group " + entry.getKey().getName() + "</h3>");
			out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
			out.println("<tr><td><b>Action CheckList Id</b></td><td><b>Action</b></td></tr>");
			for(AzioniSuggester suggester: azioniSuggesters) {
				Azioni azione = suggester.getSuggestion(configuration.adaptIntensita);
				out.println("<tr><td>" + suggester.getActionId()+"</td><td>" + azione + "</td></tr>");
			}
			out.println("</table>");
						
			// saving actions in the complex data structure.
			Map.Entry<ConfigurationGroup, RischioSuggester> suggesterKey;
			HashMap<ConfigurationGroup, RischioSuggester> suggesterKeyMap = new HashMap<ConfigurationGroup, RischioSuggester>();
			suggesterKeyMap.put(entry.getKey(), rischioSuggester);
			suggesterKey = suggesterKeyMap.entrySet().iterator().next();
			azioniSuggestersByGroup.put(suggesterKey, azioniSuggesters);			
		}
	}
	 
	
	getServletContext().setAttribute("factory", factory);
	getServletContext().setAttribute("cases", cases);
	getServletContext().setAttribute("query", query);
	getServletContext().setAttribute("configuration", configuration);
	getServletContext().setAttribute("simConfigs", simConfigs);
	getServletContext().setAttribute("groupsResults", groupsResults);
	getServletContext().setAttribute("rischioSuggestersByGroup", rischioSuggestersByGroup);
	getServletContext().setAttribute("azioniSuggestersByGroup", azioniSuggestersByGroup);
	
%>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>

</html>
