<!DOCTYPE html>
<html lang="en">
<head>
<title>Drumbeat Security</title>
<%@ page import="fi.aalto.drumbeat.security.OrganizationManager"%>
<%@ page import="java.util.List,org.utils.Tuple"%>
</head>
<body>
	<H1>Visited users</H1>
	<ul>
		<%
		    List<Tuple<String, Long>> access_list = OrganizationManager.getAccessList();
		
		    if(access_list.isEmpty())
		%>
		   <li> List empty </li>
		<%
		    
			for (Tuple<String, Long> tuple : access_list) {
		%>
		
		<li><%=tuple.a%></li>
		<%
			}
		%>
	</ul>
</body>
</html>