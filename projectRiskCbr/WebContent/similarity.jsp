
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

<head></head>
<body>
<table border="0" cellpadding="2" cellspacing="7" width="100%">

  <tr>
  <td>
	<a href="http://gaia.fdi.ucm.es"><img src="img/gaia.gif" border="0"></a>
  </td>
  <td colspan="2" valign="middle" bgcolor="#738EAB">
    <font size="-1">
       <h1><font face="arial" color="#ffffff"> GAIA - Group for Artificial Intelligence Applications</font></h1>
	</font>

	<hr color="White">
  </td>
  </tr>
</table>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
<%
		
		jcolibri.test.test4.TravelDescription queryDesc = new jcolibri.test.test4.TravelDescription();		
		String holidayType		= new String("Bathing");
		Integer numberOfPersons	= new Integer(2);
		Region r = new Region();
		r.setRegion("Bornholm");
		queryDesc.setHolidayType(holidayType);
		queryDesc.setNumberOfPersons(numberOfPersons);
		queryDesc.setRegion(r);
		CBRQuery query = new CBRQuery();
		query.setDescription(queryDesc);
		
		Integer k = 5;

		CBRCaseBase caseBase = (CBRCaseBase)getServletContext().getAttribute("casebase");
		
		if(caseBase==null) {
			out.println("case base is null");
		}
		
		Collection<CBRCase> allCases = caseBase.getCases();
		
		/* CASES SELECTION IN GROUPS */

		NNConfig simConfig1 = new NNConfig();
		NNConfig simConfig2 = new NNConfig();
		NNConfig simConfig3 = new NNConfig();
		NNConfig globalSimConfig = new NNConfig();
		
		simConfig1.setDescriptionSimFunction(new Average());
		simConfig2.setDescriptionSimFunction(new Average());
		simConfig3.setDescriptionSimFunction(new Average());
		globalSimConfig.setDescriptionSimFunction(new Average());

		Attribute at1 = new Attribute("HolidayType", TravelDescription.class);
		simConfig1.addMapping(at1, new Equal());
		simConfig1.setWeight(at1, 1.0);
		globalSimConfig.addMapping(at1, new Equal());
		
		Attribute at3 = new Attribute("NumberOfPersons", TravelDescription.class);
		simConfig2.addMapping(at3, new Equal());
		simConfig2.setWeight(at3, 1.0);
		globalSimConfig.addMapping(at3, new Equal());
		
		Attribute at9 = new Attribute("Region", TravelDescription.class); 
		simConfig3.addMapping(at9, new Average());
		simConfig3.setWeight(at9, 1.0);
		globalSimConfig.addMapping(at9, new Average());

		Attribute at10 = new Attribute("region", Region.class);
		simConfig3.addMapping(at10, new Equal());
		simConfig3.setWeight(at10, 1.0);
		globalSimConfig.addMapping(at10, new Equal());
		
		globalSimConfig.addMapping(new Attribute("city", Region.class),		new Equal());
		globalSimConfig.addMapping(new Attribute("airport", Region.class),	new Equal());
		globalSimConfig.addMapping(new Attribute("currency", Region.class),	new Equal());
		
		globalSimConfig.addMapping(new Attribute("Transportation", TravelDescription.class), new Equal());
		globalSimConfig.addMapping(new Attribute("Duration", TravelDescription.class), new Interval(31));
		globalSimConfig.addMapping(new Attribute("Season", TravelDescription.class), new Equal());
	
	Collection<RetrievalResult> eval1 = NNScoringMethod.evaluateSimilarity(allCases, query, simConfig1);
	Collection<RetrievalResult> eval2 = NNScoringMethod.evaluateSimilarity(allCases, query, simConfig2);
	Collection<RetrievalResult> eval3 = NNScoringMethod.evaluateSimilarity(allCases, query, simConfig3);
			
	out.println("Most similar cases with: " + query);
	out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
	out.println("<tr><td><b>Similarity</b></td><td><b>Case</b></td></tr>");
	for(RetrievalResult nse: eval1)
		out.println("<tr><td>"+nse.getEval()+"</td><td>"+nse.get_case()+"</td></tr>");
	out.println("</table>");
	
	out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
	out.println("<tr><td><b>Similarity</b></td><td><b>Case</b></td></tr>");
	for(RetrievalResult nse: eval2)
		out.println("<tr><td>"+nse.getEval()+"</td><td>"+nse.get_case()+"</td></tr>");
	out.println("</table>");
		
	out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
	out.println("<tr><td><b>Similarity</b></td><td><b>Case</b></td></tr>");
	for(RetrievalResult nse: eval3)
		out.println("<tr><td>"+nse.getEval()+"</td><td>"+nse.get_case()+"</td></tr>");
	out.println("</table>");
		
	Collection<CBRCase> eval1TopK = SelectCases.selectTopK(eval1, k);
	Collection<CBRCase> eval2TopK = SelectCases.selectTopK(eval2, k);
	Collection<CBRCase> eval3TopK = SelectCases.selectTopK(eval3, k);
	
	Collection<RetrievalResult> eval1Global = NNScoringMethod.evaluateSimilarity(eval1TopK, query, globalSimConfig);
	Collection<RetrievalResult> eval2Global = NNScoringMethod.evaluateSimilarity(eval2TopK, query, globalSimConfig);
	Collection<RetrievalResult> eval3Global = NNScoringMethod.evaluateSimilarity(eval3TopK, query, globalSimConfig);
	
	out.println("<br><br>Most k similar cases with: " + query + "scored using global scoring method");
	out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
	out.println("<tr><td><b>Similarity</b></td><td><b>Case</b></td></tr>");
	for(RetrievalResult nse: eval1Global)
		out.println("<tr><td>"+nse.getEval()+"</td><td>"+nse.get_case()+"</td></tr>");
	out.println("</table>");
	
	out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
	out.println("<tr><td><b>Similarity</b></td><td><b>Case</b></td></tr>");
	for(RetrievalResult nse: eval2Global)
		out.println("<tr><td>"+nse.getEval()+"</td><td>"+nse.get_case()+"</td></tr>");
	out.println("</table>");
		
	out.println("<p><table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" width=\"100%\">");			
	out.println("<tr><td><b>Similarity</b></td><td><b>Case</b></td></tr>");
	for(RetrievalResult nse: eval3Global)
		out.println("<tr><td>"+nse.getEval()+"</td><td>"+nse.get_case()+"</td></tr>");
	out.println("</table>");

%>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>
</html>
