

ws=new WebSocket(wsUrl);
ws.onopen = function()
{
};
ws.onmessage = function(evt)
{
    console.log("收到："+evt)
    var jsondata=JSON.parse(evt.data);
    for (var i=0;i<jsondata.length;i++){
        switch (jsondata[i].msgId) {
            case "s4" :{
                Dos4(jsondata[i].msgBody);
                break;
            }
            case "s5" :{
                Dos5(jsondata[i].msgBody)
                break;
            }
            case "s6" :{
                Dos6(jsondata[i].msgBody)
                break;
            }
            case "s7" :{
                Dos7(jsondata[i].msgBody)
                break;
            }
            case "s8" :{
                Dos8(jsondata[i].msgBody)
                break;
            }
            case "s9" :{
                Dos9(jsondata[i].msgBody)
                break;
            }
            case "s10" :{
                Dos10(jsondata[i].msgBody)
                break;
            }
            case "s11" :{
                Dos11(jsondata[i].msgBody)
                break;
            }
            case "s12" :{
                Dos12(jsondata[i].msgBody)
                break;
            }

        }
    }

};
ws.onclose = function(evt)
{
    console.log("WebSocketClosed!");
};
ws.onerror = function(evt)
{
    console.log("WebSocketError!");
};

function closeWebSocket() {
    websocket.close();
};