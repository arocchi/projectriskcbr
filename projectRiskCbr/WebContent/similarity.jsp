
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

<html>
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
		SessionFactory factory = 		(SessionFactory)getServletContext().getAttribute("factory");
		Collection<CBRCase> cases = 	(Collection<CBRCase>)getServletContext().getAttribute("cases");
		Configuration configuration = 	(Configuration)getServletContext().getAttribute("configuration");

		/*
		 * creating the description for the query.
		 * every CBRCase consists of a Description and a Solution.
		 * In our case, the Solution part is null, but we have a Description part.
		 * the Description part is a Progetto instance.
		 */
		Progetto queryDesc = new Progetto();
		queryDesc.setCodice("Nuovo Progetto");
		queryDesc.setValoreEconomico(10000.0);
		// we have R1, R2 and R3 for ingegneria
		LivelloDiRischio ingegneria 	= new LivelloDiRischio(1, 0, 3);
		queryDesc.setIngegneria(ingegneria);
		
		// the query does not have an R3 set
		LivelloDiRischio mercatoCliente = new LivelloDiRischio();
		mercatoCliente.setR1(3);
		mercatoCliente.setR2(0);
		queryDesc.setMercatoCliente(mercatoCliente);
		
		// the query does not have an R1 set
		LivelloDiRischio paese = new LivelloDiRischio();
		paese.setR2(1);
		paese.setR3(3);
		queryDesc.setPaese(paese);
		
		ImpattoStrategico im = new ImpattoStrategico(2);
		queryDesc.setIm(im);
		
		ImpattoStrategico ip = new ImpattoStrategico(0);
		queryDesc.setIp(ip);
		
		// the query must be a CBRCase, the description of whom is the Progetto just defined
		CBRCase query = new CBRCase();
		query.setDescription(queryDesc);

		
		/* NNConfig is a configuration structure for the similarity algorithm
		 * We put NNConfigs in a map so that we can have at any moment about the ConfigurationGroup that generated them
		 */
		Map<NNConfig, ConfigurationGroup> simConfigs = new HashMap<NNConfig, ConfigurationGroup>();
		/*
		 * globalSimConfig is a similarity configuration that gives a global ranking on a project,
		 * that is it's a ranking that is more "general" to the ranking given in each group.
		 * It will be used later with more explanations.
		 */
		NNConfig globalSimConfig = queryDesc.getTotalSimilarityConfig(null);
		
		
		/*
		 * NNConfigurator extracts information from a ConfigurationGroup and stores them
		 * inside a simConfig structure.
		 * Note you must pass the Progetto class to the configureSimilarity function.
		 */
		for(ConfigurationGroup groupConfig : configuration.groups) {
			NNConfig simConfig = new NNConfig();
			NNConfigurator.configureSimilarity(simConfig, groupConfig, Progetto.class);
			simConfigs.put(simConfig, groupConfig);
		}
		
	/* ------------------------- */	
	/* CASES SELECTION IN GROUPS */
	/* ------------------------- */
		
	/* 
	 * We build a list groupsResults, where each Collection of CBRCases (groupResult) corresponds to the top k results
	 * of the similarity evaluation according to settings of each group.
	 * Those k best results (according to the group similarity settings) are then ranked again inside each group
	 * with the globalSimilarity similarity configuration (global ranking)
	 *
	 * RetrievalResult is a structure that contains a numerical evalution of the similarity between the query and a case
	 * trough the getEval method, and the case for which the similarity towards the query has been calculated,
	 * available trough the get_case method.
	 * You can access the corrisponding Progetto instance trough the getDescription method of that case
	 */
	 Map<ConfigurationGroup, Collection<RetrievalResult>> groupsResults = new HashMap<ConfigurationGroup, Collection<RetrievalResult>>();
	
	/* We calculate similarity according to the similarity criterias specified for each group.
	 * Then, we get the top k projects for the group. 
	 */
	for(Map.Entry<NNConfig, ConfigurationGroup> entry : simConfigs.entrySet()) {
		NNConfig simConfig = entry.getKey();
		// we calculate similarity according to the similarity configuratino for this group
		Collection<RetrievalResult> simEval = NNScoringMethod.evaluateSimilarity(cases, query, simConfig);
		Collection<CBRCase> bestEval = SelectCases.selectTopK(simEval, configuration.kProgetto);
		
		// as last, we give a global ranking to the best kProgetto entries of this group
		Collection<RetrievalResult> globallyEvaluatedResul = NNScoringMethod.evaluateSimilarity(bestEval, query, globalSimConfig);

		out.println("<br><br><h3>Most similar cases with: " + query + " for group " + entry.getValue().getName() + "</h3>");
		out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
		out.println("<tr><td><b>Similarity</b></td><td><b>Case</b></td></tr>");
		for(RetrievalResult nse: globallyEvaluatedResul)
			out.println("<tr><td>"+nse.getEval()+"</td><td>"+nse.get_case()+"</td></tr>");
		out.println("</table>");
		
		groupsResults.put(entry.getValue(), globallyEvaluatedResul);
	}
	
	getServletContext().setAttribute("factory", factory);
	getServletContext().setAttribute("cases", cases);
	getServletContext().setAttribute("query", query);
	getServletContext().setAttribute("configuration", configuration);
	getServletContext().setAttribute("simConfigs", simConfigs);
	getServletContext().setAttribute("groupsResults", groupsResults);
%>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>

</html>
