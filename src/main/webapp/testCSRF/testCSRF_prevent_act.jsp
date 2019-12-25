<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<%
String tokenId = request.getParameter("tokenId");
String tokenContent = request.getParameter("tokenContent");
String tokenContentCheck = (String)session.getAttribute(tokenId);

out.println(String.format("tokenId:%s<br/>",tokenId));
out.println(String.format("tokenContent:%s<br/>",tokenContent));
out.println(String.format("tokenContentCheck:%s<br/>",tokenContentCheck));

if(tokenContent!=null && tokenContentCheck!=null && tokenContent.equals(tokenContentCheck)){
	out.println("token驗證成功<br/>");
	session.removeAttribute(tokenId);
}
else{
	out.println("token驗證失敗<br/>");
}
%>
</body>
</html>