
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
		SessionFactory factory = (SessionFactory)getServletContext().getAttribute("factory");
		factory.close();
	
%>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>

</html>
