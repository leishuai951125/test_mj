<%--
  Created by IntelliJ IDEA.
  User: leishuai
  Date: 2018/12/30
  Time: 18:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="game.css">
    <link rel="stylesheet" type="text/css" href="player.css">

    <title>game</title>
</head>
<body>
<script>
    var params={accountId:${account.accountId},diFen:"5",token:undefined,roomId:null};
    var ws=null;
    var wsUrl = 'ws://localhost:8080/lsmj/websocket/{'+JSON.stringify(params)+"}";
    // var wsUrl = 'ws://3308b008.nat123.cc:53780/lsmj/websocket/{'+JSON.stringify(params)+"}";
    var accountId=params.accountId;

    function requestFullScreen(de) {}
    // var requestFullScreen=function (de) {
    //     // var de = document.documentElement;
    //     if (de.requestFullscreen) {
    //         de.requestFullscreen();
    //     } else if (de.mozRequestFullScreen) {
    //         de.mozRequestFullScreen();
    //     } else if (de.webkitRequestFullScreen) {
    //         de.webkitRequestFullScreen();
    //     }
    // }
</script>
<div id="yemian" onclick = requestFullScreen(this)>
    <div id="showlaizi"></div>
    <div id="buyao">不要</div>

    <div id="buttonqu">
        <button id="chupai">出牌</button>
        <button id="hupai">胡</button>
        <button id="pengpai">碰</button>
        <button id="xiaopai">笑</button>
    </div>

    <div id="naiziqu"></div>

    <div id="xiaoqu"></div>

    <div id="leftplayer">
    </div>
    <div id="left-information">
        积分：
    </div>
    <div id="left-chupai"></div>
    <div id="left-pai">
        <!-- <img src="img/侧家手牌.png"/>
        <img src="img/侧家手牌.png"/>
        <img src="img/侧家手牌.png"/> -->

    </div>
    <div id="leftchupai"></div>


    <div id="rightplayer">
    </div>
    <div id="right-information">
        积分：
    </div>
    <div id="right-chupai"></div>
    <div id="right-pai"></div>
    <div id="right-chupai"></div>


    <div id="acrossplayer">
    </div>
    <div id="across-information">
        积分：
    </div>
    <div id="across-chupai"></div>
    <div id="across-pai"></div>
    <div id="across-chupai"></div>


    <div id="duijushu">
        底分：
        对局数：
    </div>

    <div id="chupaiqu">

    </div>

    <div id="chupairen">
        <img id="myChuPai" src="img/time33.png"/>
        <img id="acrossChuPai" src="img/time22.png"/>
        <img id="leftChuPai" src="img/time44.png"/>
        <img id="rightChuPai" src="img/time55.png"/>
    </div>

    <div id="myDiscard">

    </div>
    <div id="myPengXiao">

    </div>

</div>

<script src="jquery.min.js"></script>
<script src="playerinformation.js"></script>
<script src="Do.js"></script>
<script src="socket.js"></script>
<script src="huPai(1).js"></script>
<script src="chupai.js"></script>

</body>

</html>
