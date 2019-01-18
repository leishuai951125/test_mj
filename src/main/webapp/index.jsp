<%--
  Created by IntelliJ IDEA.
  User: leishuai
  Date: 2018/12/30
  Time: 7:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<!DOCTYPE html>--%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <base href="<%= request.getContextPath()+"/" %>">
    <style>
        *{
            font-family: "Microsoft YaHei UI";
        }
        body{
            background-color: ghostwhite;
        }
        form{
            width: 200px;
            height: 200px;
            background-color: dimgrey;
            text-align: center;
            line-height: 45px;
        }
        form div input{
            height: 2em;
        }
        form div input{
            width: 8em;
            /*color: red;*/
            font-weight: bold;
            height: 2em;
        }
        *{
            margin: auto;
        }
        .tishi{
            color: red;
            font-size: 1.5em;
        }
        #error{
            font-size: 16px;
            line-height: 1em;
            color: red;
        }
    </style>
</head>
<body>
<form action="user/login" method="post">
    <div>
        <span class="tishi">账号:</span>
        <input value="15671582806" type="text" name="accountId">
    </div>
    <div>
        <span class="tishi">密码:</span>
        <input value="15671582806" type="password" name="password">
    </div>
    <span id="error"><br>${error}</span>
    <br>
    <div><input type="submit" value="开始游戏"></div>
</form>
</body>
</html>
