<%--
  Created by IntelliJ IDEA.
  User: leishuai
  Date: 2019/1/26
  Time: 20:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <base href="<%= request.getContextPath()+"/"%>">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0,minimum-scale=1.0,user-scalable=false"/>
</head>
<body>
<form action="user/register">
    <table border="1" style="margin: auto">
        <tr>
            <td>账号（纯数字）</td>
            <td><input name="accountId" value="15671582806"></td>
        </tr>
        <tr>
            <td>用户名</td>
            <td><input name="username"></td>
        </tr>
        <tr>
            <td colspan="2" style="text-align: center;">
                <input type="submit" value="提交" style="width: 10em">
            </td>
        </tr>
    </table>
</form>
</body>
</html>
