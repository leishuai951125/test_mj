ws = new WebSocket(wsUrl);
ws.onopen = function () {
    $(function () {
        for (var i = 1; i <= 9; i++) {
            new Image().src = "img/suo" + i + ".png";
            new Image().src = "img/tong" + i + ".png";
            new Image().src = "img/wan" + i + ".png";
        }
        for (var i = 1; i <= 4; i++) {
            new Image().src = "img/time" + i + i + ".png";
        }
    });
};
ws.onmessage = function (evt) {
    var jsondata = JSON.parse(evt.data);
    for (var i = 0; i < jsondata.length; i++) {
        switch (jsondata[i].msgId) {
            case "s4" : {
                Dos4(jsondata[i].msgBody);
                break;
            }
            case "s5" : {
                Dos5(jsondata[i].msgBody)
                break;
            }
            case "s6" : {
                Dos6(jsondata[i].msgBody)
                break;
            }
            case "s7" : {
                Dos7(jsondata[i].msgBody)
                break;
            }
            case "s8" : {
                Dos8(jsondata[i].msgBody)
                break;
            }
            case "s9" : {
                Dos9(jsondata[i].msgBody)
                break;
            }
            case "s10" : {
                Dos10(jsondata[i].msgBody)
                break;
            }
            case "s11" : {
                Dos11(jsondata[i].msgBody)
                break;
            }
            case "s12" : {
                Dos12(jsondata[i].msgBody)
                break;
            }
            case "s13" : {
                Dos13(jsondata[i].msgBody)
                break;
            }
            case "s14" : {
                Dos14(jsondata[i].msgBody)
                break;
            }
        }
    }

};
ws.onclose = function (evt) {
    console.log("WebSocketClosed!");
    alert("连接断开，无法游戏")
};
ws.onerror = function (evt) {
    console.log("WebSocketError!");
};

function closeWebSocket() {
    websocket.close();
};