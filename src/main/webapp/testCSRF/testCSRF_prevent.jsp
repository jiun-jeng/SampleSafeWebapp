<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="org.apache.commons.lang.RandomStringUtils" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<%
String tokenId = RandomStringUtils.randomAlphabetic(12);
String tokenContent = RandomStringUtils.randomAlphabetic(12);

session.setAttribute(tokenId, tokenContent);
%>
<form method="post" action="testCSRF_prevent_act.jsp">
	<input type="hidden" name="tokenId" value="<%=tokenId %>">
	<input type="hidden" name="tokenContent" value="<%=tokenContent %>">
	
	<input type="submit" value="送出">
</form>
</body>
</html>