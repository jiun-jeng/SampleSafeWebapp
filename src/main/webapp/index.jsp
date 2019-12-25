<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="com.m.util.*" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>       
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>

<a href="testRequestUtil/testRequestUtil.jsp">testRequestUtil.jsp</a>
<br/>
<a href="testXssFilter/testXssFilter.jsp">testXssFilter.jsp</a>
<br/>
<a href="testXfsFilter/testXfsFilter.jsp">testXfsFilter.jsp</a>
<br/>
<a href="testCSRF/testCSRF_prevent.jsp">testCSRF_prevent.jsp</a>
<br/>

<!-- [defect2]Cross-Site Scripting[fix] -->
<!-- c:out會跳脫xml -->
el:<c:out value="${param.test}" />
<% 
RequestUtil requestUtil = new RequestUtil(request);
%>
<%=requestUtil.getParameter("template")%>

<!--[defect]File Inclusion-->
<%-- 
<jsp:include page="<%=request.getParameter("template")%>">
--%>
</body>
</html>
