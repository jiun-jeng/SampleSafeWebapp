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
<%
RequestUtil requestUtil = new RequestUtil(request);
requestUtil.setCharacterEncoding("utf-8");
out.println("requestUtil.getParameter:"+requestUtil.getParameter("test"));

out.println("<br/>");

String dirtyInput = "<div><b>'1中文'</b><script>alert('1中文');</script></div>";
request.setAttribute("dirtyInput", dirtyInput);
out.println("requestUtil.getAttribute:"+requestUtil.getAttribute("dirtyInput"));

out.println("<br/>");

request.getSession().setAttribute("dirtyInput", dirtyInput);
out.println("requestUtil.getSessionAttribute:"+requestUtil.getSessionAttribute("dirtyInput"));

out.println("<br/>");
out.println("requestUtil.getHeader:"+requestUtil.getHeader("Accept"));

out.println("<br/>");
out.println("requestUtil.getRequestURI:"+requestUtil.getRequestURI());

out.println("<br/>");
out.println("requestUtil.getQueryString:"+requestUtil.getQueryString());

//requestUtil.getRequestDispatcher("/testRequestUtil.jsp").include(request, response);

%>
<br/>
<%-- 有xss 
el:${param.test}
--%>
<br/><%-- 無xss --%>
c:out:<c:out value="${param.test}" />
</body>
</html>