<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<form method="post" action="testXssFilter_act.jsp">
<input type="text" name="test" size="60" value="<div><b>'1中文'</b><script>alert('1中文');</script></div>" />
<input type="submit" value="送出" />
</form>
</body>
</html>