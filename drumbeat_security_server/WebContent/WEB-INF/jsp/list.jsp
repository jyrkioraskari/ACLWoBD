<!DOCTYPE html>
<html lang="en">
<head>
<title>Drumbeat Security</title>
<%@ page import="fi.aalto.cs.drumbeat.controllers.DrumbeatSecurityController"%>
<%@ page import="java.util.List,org.utils.Tuple"%>
</head>
<body>
	<H1>Visited users</H1>
	<ul>
		<%
		    List<Tuple<String, Long>> access_list = DrumbeatSecurityController.getAccessList();
			for (Tuple<String, Long> tuple : access_list) {
		%>
		
		<li><%=tuple.a%></li>
		<%
			}
		%>
	</ul>
</body>
</html>