
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <base href="<%= request.getContextPath()+"/" %>">
    <title>Title</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0,minimum-scale=1.0,user-scalable=false" />

    <script src="static/js/jquery.min.js"></script>
    <style>
        body {
            background-color: cadetblue;
        }

        table {
            background-color: darkgrey;
        }

        table tr td {
            text-align: center;
        }

        .two_span {
            border: hidden;
        }

        td input, select {
            width: 8em;
            height: 2em;
        }
    </style>
    <script>
        function isPriExist(roomId) { //私人房是否存在
            return $.ajax({
                url:"room/checkPriRoom",
                data:"roomId="+roomId,
                async:false
            }).responseText==="success";
        }
        function intoRoom(diFen) { //进入房间
            console.log(diFen);
            var f = gameForm;
            f.sumTurn.value = null;
            if (diFen !== undefined) {  //存在底分
                if (isNaN(diFen)) {
                    return
                } else {  //进入公共房
                    f.diFen.value = diFen;
                    f.roomId.value = null;
                }
            } else {
                var roomId = $("#roomIdText").val().trim();
                if (isNaN(roomId) || roomId === "") {
                    alert("房号必须是纯数字")
                    return;
                } else {  //尝试进入私人房
                    if (isPriExist(roomId)) {
                        f.diFen.value = null;
                        f.roomId.value = roomId;
                    } else {
                        alert("私人房号不存在")
                        return;
                    }
                }
            }
            f.action="static/room.jsp";
            f.submit();
        }
        function createPriRoom() { //创建私人房
            var f = gameForm;
            // f.roomId.value=null;
            f.diFen.value=$("#diFenSel").val();
            f.sumTurn.value=$("#sumTurnSel").val();
            f.sumPlayer.value=$("#sumPlayerSel").val();
            f.action="room/createPriRoom";
            f.submit();
        }
    </script>
    <script>

    </script>
</head>
<body>

<form method="get" action="static/room.jsp" name="gameForm">
    <input type="hidden" name="diFen" value="1">
    <input type="hidden" name="sumTurn">
    <input type="hidden" name="roomId">
    <input type="hidden" name="sumPlayer">
</form>
<table border="1" style="margin: auto">
    <tr>
        <th colspan="3">进入公共房</th>
    </tr>
    <tr>
        <td><input type="button" value="1分公共房" onclick="intoRoom(1)"></td>
        <td><input type="button" value="2分公共房" onclick="intoRoom(2)"></td>
        <td><input type="button" value="5分公共房" onclick="intoRoom(5)"></td>
    </tr>
    <tr class="two_span">
        <td colspan="3"><br><br></td>
    </tr>
    <tr>
        <th colspan="3">进入私人房</th>
    </tr>
    <tr>
        <td>输入房号:</td>
        <td><input type="text" id="roomIdText"></td>
        <td><input type="button" value="进入私人房" onclick="intoRoom()"></td>
    </tr>
    <tr class="two_span">
        <td colspan="3"><br><br></td>
    </tr>
    <tr>
        <th colspan="3">开设私人房</th>
    </tr>
    <tr>
        <td>底分：</td>
        <!--<td><input type="text"></td>-->
        <td><select id="diFenSel">
            <option value="1">1分</option>
            <option value="2" selected="selected">2分</option>
            <option value="5">5分</option>
        </select></td>
        <td rowspan="3">
            <input id="check" type="button" value="确定" style="height: 3em" onclick="createPriRoom()">
        </td>
    </tr>
    <tr>
        <td>场数：</td>
        <!--<td><input type="text"></td>-->
        <td><select id="sumTurnSel">
            <option value="5">5场</option>
            <option value="10" selected="selected">10场</option>
            <option value="20">20场</option>
        </select></td>
    </tr>
    <tr>
        <td>人数：</td>
        <!--<td><input type="text"></td>-->
        <td><select id="sumPlayerSel">
            <option value="2">2人</option>
            <option value="3" selected="selected">3人</option>
            <option value="4">4人</option>
        </select></td>
    </tr>
</table>
<%--<h4 style="color: white;text-align: center;">--%>
    <%--要获取房间页面，所以必须提交表单，如果是本地应用，不需要经过后台--%>
<%--</h4>--%>
</body>
</html>
