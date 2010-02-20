
<%@ page import="jcolibri.casebase.*" %>
<%@ page import="jcolibri.cbrcore.*" %>
<%@ page import="jcolibri.connector.*" %>


<%@ page import="java.util.*" %>


<%@page import="projectriskcbr.config.Configuration"%>
<%@page import="persistentclasses.Progetto"%>
<%@page import="persistentclasses.SessionObject"%>
<%@page import="org.hibernate.SessionFactory"%>
<%@page import="org.hibernate.Session"%><html>
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

<table border="1" cellpadding="2" cellspacing="2" width="100%">
<tr>
	<td><b>Description</b></td><td><b>Result</b></td><td><b>Solution</b></td><td><b>Justification of Solution</b></td></b>
</tr>
<%
		
  	    Configuration configuration = (Configuration)getServletContext().getAttribute("configuration");
		SessionFactory factory = SessionObject.getStarted();
		
		SessionObject.newTransaction();
		
		Collection<CBRCase> cases = new LinkedList<CBRCase>();
		try {
			// Progetto extends CBRCase, since a Progetto can be a case
			List<Progetto> progetti = (List<Progetto>)Progetto.getCases();
			for(Progetto progetto : progetti) {
				CBRCase cbrCase = new CBRCase();
				cbrCase.setDescription(progetto);
				cases.add(cbrCase);
			}
		} catch(Exception e) {
			out.println("error retrieving cases:" + e.getMessage());
		}
		
		SessionObject.endTransaction();
		
		for(CBRCase c: cases)
		{
			out.println("<tr>");
			out.println("<td>"); out.println(c.getDescription());             out.println("</td>");
		    out.println("<td>"); out.println(c.getResult());                  out.println("</td>");
		    out.println("<td>"); out.println(c.getSolution());                out.println("</td>");
			out.println("<td>"); out.println(c.getJustificationOfSolution()); out.println("</td>");
			
			out.println("</tr>");
			
		}
		
		getServletContext().setAttribute("factory", factory);
		getServletContext().setAttribute("cases", cases);
    
%>   
</table>

<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>
</html>
