
<%@ page import="jcolibri.casebase.*" %>
<%@ page import="jcolibri.cbrcore.*" %>
<%@ page import="jcolibri.connector.*" %>


<%@ page import="java.util.*" %>

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
       <h1><font face="arial" color="#ffffff"> GAIA - Group for Artificial Intelligence Applications</font></h1>
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
		
  	    CBRCaseBase _caseBase = (CBRCaseBase)getServletContext().getAttribute("casebase");

		java.util.Collection<CBRCase> cases = _caseBase.getCases();
		for(CBRCase c: cases)
		{
			out.println("<tr>");
			
			out.println("<td>"); out.println(c.getDescription());             out.println("</td>");
		    out.println("<td>"); out.println(c.getResult());                  out.println("</td>");
		    out.println("<td>"); out.println(c.getSolution());                out.println("</td>");
			out.println("<td>"); out.println(c.getJustificationOfSolution()); out.println("</td>");
			
			out.println("</tr>");
			
		}
		
		getServletContext().setAttribute("cases", cases);
    
%>   
</table>

<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>

<hr>
<h3>Source Code</h3>
<pre>
	CBRCaseBase _caseBase = (CBRCaseBase) getServletContext().getAttribute("casebase");

	java.util.Collection<CBRCase> cases = _caseBase.getCases();
	for(CBRCase c: cases)
	{
		out.println("tr");
			
		out.println("td"); out.println(c.getDescription());             out.println("td");
		out.println("td"); out.println(c.getResult());                  out.println("td");
		out.println("td"); out.println(c.getSolution());                out.println("td");
		out.println("td"); out.println(c.getJustificationOfSolution()); out.println("td");
			
		out.println("tr");
	}	
	getServletContext().setAttribute("cases", cases);	
</pre>
</body>
</html>
