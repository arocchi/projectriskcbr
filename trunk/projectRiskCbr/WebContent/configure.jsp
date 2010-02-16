
<%@ page import="jcolibri.casebase.*" %>
<%@ page import="jcolibri.cbrcore.*" %>
<%@ page import="jcolibri.connector.*" %>
<%@ page import="jcolibri.test.*" %>


<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>

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
<p></p>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
<%
	   Connector _connector = null;
	   CBRCaseBase _caseBase = null;
   
       String c  = request.getParameter("connector");
	   String cf = request.getParameter("configfile");
	   String im = request.getParameter("inmemory");
	     
	   if(c.equals("Data Base"))
	   {	   
  	           out.println("Setting persistence: Data Base --> ");
  	           try {
  	        	 	jcolibri.test.database.HSQLDBserver.init();
					_connector = new DataBaseConnector();
					out.println("Ok<br>");
  	           } catch (Exception e) {
  	        	 out.println("Error<br>");
  	           }

			   URL configfile = null;
			   
  	           
			   if(cf.equals("Test4"))
				   try {
					   configfile = jcolibri.util.FileIO.findFile("jcolibri/test/test4/databaseconfig.xml");     	
					   out.println("Initializing with config file --> ");
					   _connector.initFromXMLfile(configfile);
					   out.println("Ok<br>");
	 	  	       } catch (Exception e) {
						out.println("Error<br>");
	 	  	       }
			     
	   }

	   if(im.equals("Lineal Case Base"))
	   {
  	           out.println("Setting in memory organization: Lineal Case Base --> ");
  	           
			   try {
			       _caseBase  = new LinealCaseBase();
				   out.println("Ok<br>");
 	  	       } catch (Exception e) {
					out.println("Error<br>");
 	  	       }

	    }

  	    out.println("Init Case Base --> ");
  	    
		   try {
				_caseBase.init(_connector);				
			    out.println("Ok<br>");
	  	   } catch (Exception e) {
				out.println("Error<br>");
	  	   }

	    
		getServletContext().setAttribute("casebase", _caseBase);	
		
%>
<p><form action="index.html" method="post"><input type="submit" value="Back"></form></p>
</body>
</html>
