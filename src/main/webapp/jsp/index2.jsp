<%--
  Created by IntelliJ IDEA.
  User: leishuai
  Date: 2018/12/22
  Time: 16:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

hello leishuai
<div id="print">
</div>
<input id="sendC4" value="sendC4"  type="button" onclick="sendC4Fun()"/>
<input id="sendC5" value="sendC5"  type="button" onclick="sendC5Fun()"/>
</body>
</html>
<script>
    function sendC4Fun() {
        websocket.send(JSON.stringify({
            msgId:"c4",
            paiNo:12
        }))
    }
    function sendC5Fun() {
        websocket.send(JSON.stringify({
            msgId:"c5",
            type:"hei_mo",
            matchMethod:[1,1,1,2],//取值1，2，3，对应'顺','对'，'杠'，
            actAs:[]  //癞子充当的牌,一赖时此值为空，或者一个元素,多赖为多个元素。黑摸为空
        }))
    }
    function $(id) {
        return document.getElementById(id);
    }
    var paramObject={
        accountId:123,
        token:"jf",
        roomId:null,
        diFen:5
    }
    var jsonParam=JSON.stringify(paramObject);
    var address ="ws://localhost:8081/lsmj/websocket/{"+jsonParam+"}";
    /*
    jmeter使用的地址
    /lsmj/websocket/%7B%22accountId%22:123,%22token%22:%22jf%22,%22roomId%22:null,%22diFen%22:5%7D
     */
    console.log(address)
    var websocket = null;
    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket=new WebSocket(address);
    }else {
        alert("当前浏览器不支持websocket")
    }
    websocket.onerror = function () {
    }

    //连接成功建立的回调方法
    websocket.onopen = function () {
        $("print").innerHTML="连接成功";
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        //setMessageInnerHTML(event.data);
        var s=event.data;
        $("print").innerHTML=s;
    }

    //连接关闭的回调方法
    websocket.onclose = function () {
        $("print").innerHTML+="<h1>连接断开，无法游戏</h1><h1>请检查网络连接</h1>";
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }
</script>