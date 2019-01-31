<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="css/game.css">
    <link rel="stylesheet" type="text/css" href="css/player.css">
    <link rel="stylesheet" type="text/css" href="css/yupai.css">
    <title>game</title>
</head>
<body>
<script>
    var params = {
        accountId: '${account.accountId}',
        diFen: '<%= request.getParameter("diFen")%>',
        token: '',
        roomId: '<%= request.getParameter("roomId")%>'
    };
    // var wsUrl = 'ws://localhost:8080/lsmj/websocket/{'+JSON.stringify(params)+"}";
    // var wsUrl = 'ws://192.168.43.35:8080/lsmj/websocket/{'+JSON.stringify(params)+"}";

    var wsUrl = 'ws://localhost:8080/TestGit/lsmj/websocket/{' + JSON.stringify(params) + "}";
    // var wsUrl = 'ws://111.230.108.43:8080/TestGit/lsmj/websocket/{'+JSON.stringify(params)+"}";

    // var wsUrl = 'ws://3308b008.nat123.cc:53780/lsmj/websocket/{'+JSON.stringify(params)+"}";
    var accountId = params.accountId;

</script>
<div id="yemian">
    <div id="yupai-information"></div>
    <div id="showlaizi"></div>
    <div id="fullScreen" onclick=requestFullScreen()>全屏</div>
    <div id="daoZhuan" onclick=daoZhuan()>倒转</div>
    <div id="buyao">不要</div>

    <div id="buttonqu">
        <button id="chupai">出牌</button>
        <button id="hupai">胡</button>
        <button id="pengpai">碰</button>
        <button id="xiaopai">笑</button>
    </div>

    <div id="naiziqu"></div>
    <div id="xiaoqu"></div>

    <div id="leftplayer"></div>
    <div id="left-information"></div>
    <div id="left-chupai"></div>
    <div id="left-pai"></div>

    <div id="rightplayer"></div>
    <div id="right-information"></div>
    <div id="right-chupai"></div>
    <div id="right-pai"></div>

    <div id="acrossplayer"></div>
    <div id="across-information"></div>
    <div id="across-pai"></div>
    <div id="across-chupai"></div>

    <div id="duijushu">
        底分：0 &nbsp;&nbsp; 对局数：0/0 &nbsp;&nbsp;
        余牌：0 &nbsp;&nbsp; 积分：0 &nbsp;&nbsp;
    </div>

    <div id="chupaiqu"></div>

    <div id="chupairen">
        <img id="myChuPai" src="img/time33.png"/>
        <img id="acrossChuPai" src="img/time22.png"/>
        <img id="leftChuPai" src="img/time44.png"/>
        <img id="rightChuPai" src="img/time55.png"/>
    </div>

    <div id="myDiscard"></div>
    <div id="myPengXiao"></div>

</div>

<script src="js/jquery.min.js"></script>
<script src="js/playerinformation.js"></script>
<script src="js/Do.js"></script>
<script src="js/socket.js"></script>
<script src="js/huPai(1).js"></script>
<script src="js/chupai.js"></script>
<script>
    function requestFullScreen() {
    }
    var requestFullScreen = function () {
        var de = document.getElementById("yemian");
        if (de.requestFullscreen) {
            de.requestFullscreen();
        } else if (de.mozRequestFullScreen) {
            de.mozRequestFullScreen();
        } else if (de.webkitRequestFullScreen) {
            de.webkitRequestFullScreen();
        }
        setTimeout(rotateYeMian, 1000);
    }

    function setFontSize(){
        $("*").css("fontSize", window.innerWidth / 100 + "px");
        $("#duijushu").css("fontSize", "1.5em");
    }

    $(function () {
        setFontSize();
        $(window).resize(function () {
            setFontSize();
        });
    })

    function rotateYeMian() {
        var preWidth = window.innerWidth;
        var preHeight = window.innerHeight;
        if (preWidth < preHeight) //需要旋转90度的手机端，如微信和qq
        {
            $("#daoZhuan").hide();
            var sub = (preHeight - preWidth) / 2;
            $("#yemian").css("top", sub + "px");
            $("#yemian").css("left", -sub + "px");
            $("#yemian").css("width", preHeight);
            $("#yemian").css("height", preWidth);
            $("#yemian").css("transform", "rotate(270deg)")
            return true;
        }
        return false;
    }

    function daoZhuan() {
        if (daoZhuan.preV === undefined) {
            $("#yemian").css("transform", "rotate(180deg)");
            daoZhuan.preV = null;
        } else {
            $("#yemian").css("transform", "rotate(0deg)");
            daoZhuan.preV = undefined;
        }
    }
</script>
</body>
</html>
