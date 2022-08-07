// var a ={pai:[],peng:[],xiao:"",seatNo:"",accountNo:"",headImgUrl:"",chuPai:[],userName:"",id};
var myInformation=new Object();
var leftInformation=new Object();
leftInformation.idString="#leftplayer";
var rightInformation=new Object();
rightInformation.idString="#rightplayer";
var acrossInformation=new Object();
acrossInformation.idString="#acrossplayer";
var roomInformation={sumTurn:"",playedTurn:"",diFen:"",roomId:"",laizi:"",laiGen:""};
//将牌通过数组进行转换，只用于显示。
var zhuanhuan=[-1,"suo1","suo2","suo3","suo4","suo5","suo6","suo7","suo8","suo9","wan1","wan2","wan3","wan4","wan5","wan6","wan7","wan8","wan9","tong1","tong2","tong3","tong4","tong5","tong6","tong7","tong8","tong9","自己手牌"];
rightInformation.jiFen=0;
acrossInformation.jiFen=0;
leftInformation.jiFen=0;
myInformation.jiFen=0;
roomInformation.allPlayer=new Array(4);
init();
function init(){
    if(roomInformation.sumTurn==1){
        myInformation.seatNo=null;
        myInformation.userName=null;
        leftInformation.seatNo=null;
        leftInformation.userName=null;
        rightInformation.seatNo=null;
        rightInformation.userName=null;
        acrossInformation.seatNo=null;
        acrossInformation.userName=null;
    }
    myInformation.chuPai=[];
    myInformation.pai=[];
    myInformation.xuanPai=-1;
    //赖子是否出现，初始化为false。
    roomInformation.laiZiApprience=false;

    myInformation.peng=[];
    myInformation.xiao=[];
    myInformation.laiPai="";
    myInformation.notAdminXiao=[];
    myInformation.notAdminPeng=[];
    myInformation.canXiaoNo=-1;
    myInformation.canPengNo="";
    myInformation.canHu=null;
    // myInformation.jiFen=0;
    leftInformation.chuPai=[];
    leftInformation.pai=13;
    leftInformation.peng=[];
    leftInformation.xiao=[];
    // roomInformation.diFen=5;
    // roomInformation.playedTurn=0;
    roomInformation.yuPaiSum=0;
    rightInformation.chuPai=[];
    rightInformation.pai=13;
    rightInformation.peng=[];
    rightInformation.xiao=[];

    acrossInformation.chuPai=[];
    acrossInformation.pai=13;
    acrossInformation.xiao=[];
    acrossInformation.peng=[];
}

//  处理其他玩家出牌 0要不起 1朝天 2碰 3点笑
function canPengXiao(chuDePai){
    if(chuDePai==roomInformation.laizi){
        return 0;
    }
    var times=0;

    for(var i=0;i<myInformation.pai.length;i++) {
        if (chuDePai == myInformation.pai[i]) {
            times++;
        }
    }
    if (times == 2) {
        if(chuDePai==roomInformation.laiGen){
            // roomInformation.isMyTurn=true;
            myInformation.canXiaoNo=chuDePai;
            return 1;
        } else{
            if(myInformation.notAdminPeng.indexOf(chuDePai)>=0){
                return 0;
            }
            // roomInformation.isMyTurn=true;
            myInformation.canPengNo=chuDePai;
            return 2;
        } 
    } else if (times == 3) {
        // roomInformation.isMyTurn=true;
        myInformation.canXiaoNo=chuDePai;
        myInformation.canPengNo=chuDePai;
        return 3;
    } else return 0;
}
function canXiao() { //判断回头笑、点笑、闷笑
    //长度28，第一位不需要
    var b=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
    for(var i=0;i<myInformation.pai.length;i++){
        b[myInformation.pai[i]]++;
    }
    // b[laiPai]++;
    for (var j=1;j<b.length;j++){
        // 当用户手上牌有四张，并且不在黑名单里面时，将能笑的这颗牌存储起来，方便后面显示以及生成消息
        if(b[j]==4&&((myInformation.notAdminXiao.indexOf(j)<0))&&(j!=roomInformation.laizi)){
            myInformation.canXiaoNo=j;
            return j;
        } else if(b[j]==3&&(myInformation.notAdminXiao.indexOf(j)<0)&&j==roomInformation.laiGen){
            myInformation.canXiaoNo=j;
            return j;
        }
    }
    return -1;
}
function setOtherPlayer(sumPlayer,selfSeatNo) {
    switch (sumPlayer) {
        case 4: {
            roomInformation.allPlayer[(selfSeatNo + 1 + sumPlayer) % sumPlayer] = rightInformation;
            roomInformation.allPlayer[(selfSeatNo + 2 + sumPlayer) % sumPlayer] = acrossInformation;
            roomInformation.allPlayer[(selfSeatNo + 3 + sumPlayer) % sumPlayer] = leftInformation;
            break
        }
        case 3: {
            roomInformation.allPlayer[(selfSeatNo + 1 + sumPlayer) % sumPlayer] = rightInformation;
            roomInformation.allPlayer[(selfSeatNo + 2 + sumPlayer) % sumPlayer] = leftInformation;
            break
        }
        case 2: {
            roomInformation.allPlayer[(selfSeatNo + 1 + sumPlayer) % sumPlayer] = acrossInformation;
            break
        }
    }
}
//新玩家加入后，再玩家对象中添加基本信息，同时显示玩家头像
 function Dos4(data) {
     roomInformation.diFen = data[0].diFen;
     roomInformation.playedTurn = data[0].playedTurn;
     roomInformation.roomId = data[0].roomId;
     roomInformation.sumTurn = data[0].sumTurn;
     var sumPlayer = roomInformation.sumPlayer = data[0].sumPlayer;
     var selfSeatNo = myInformation.seatNo = data[0].selfSeatNo;
     showRoomId(roomInformation.roomId);
     setOtherPlayer(sumPlayer,selfSeatNo);

     for (var i = 1; i < data.length; i++) {
         var seatNo = data[i].seatNo;
         if (seatNo == selfSeatNo) {
             myInformation.headImgUrl = data[i].headImgUrl;
             myInformation.accountId = data[i].accountId;
             myInformation.userName = data[i].username;
         } else {
             roomInformation.allPlayer[seatNo].seatNo = data[i].seatNo;
             roomInformation.allPlayer[seatNo].headImgUrl = data[i].headImgUrl;
             roomInformation.allPlayer[seatNo].accountId = data[i].accountId;
             roomInformation.allPlayer[seatNo].userName = data[i].username;
             var idString = roomInformation.allPlayer[seatNo].idString;
             $(idString).css("background-image", "url(" + data[i].headImgUrl + ")");
         }
     }
 }

 //有玩家加入，保存玩家信息，显示玩家
 function Dos5(data) {
     var seatNo=data.seatNo;
     var idString=roomInformation.allPlayer[seatNo].idString;
     if(data.type=="exit"){
         roomInformation.allPlayer[seatNo].seatNo="";
         roomInformation.allPlayer[seatNo].headImgUrl="";
         roomInformation.allPlayer[seatNo].accountId="";
         roomInformation.allPlayer[seatNo].userName="";
         $(idString).css("background-image","url()");
     }else {
         roomInformation.allPlayer[seatNo].seatNo = data.seatNo;
         roomInformation.allPlayer[seatNo].headImgUrl = data.headImgUrl;
         roomInformation.allPlayer[seatNo].accountId = data.accountId;
         roomInformation.allPlayer[seatNo].userName = data.username;
         $(idString).css("background-image", "url(" + data.headImgUrl + ")");
     }
 }

 //接收13张牌、癞子、癞根、已玩局数，保存到myInformation.pai中
 function Dos6(data) {
    {
        //  对房间对象，玩家对象部分值进行初始化
        myInformation.xiao.length=0;
        myInformation.chuPai.length=0;
        myInformation.peng.length=0;
        leftInformation.peng.length=0;
        leftInformation.xiao.length=0;
        leftInformation.chuPai.length=0;
        rightInformation.xiao.length=0;
        rightInformation.peng.length=0;
        rightInformation.chuPai.length=0;
        acrossInformation.peng.length=0;
        acrossInformation.xiao.length=0;
        acrossInformation.chuPai.length=0
        roomInformation.laiZiApprience=false;
     }
     myInformation.pai=data.allCards;
     roomInformation.laizi=data.laiZi;
     roomInformation.laiGen=data.laiGen;
     roomInformation.playedTurn=data.playedTurn;
     leftInformation.pai=13;
     acrossInformation.pai=13;
     rightInformation.pai=13;
     showLaiGen();
     for(var i=0;i<roomInformation.sumPlayer;i++){
         if(i==myInformation.seatNo){ //展示自己的牌
             myCard();
         }else{ //展示别人的牌
             roomInformation.allPlayer[i].showPai();
         }
     }
 }

function Dos7(data) {
    roomInformation.yuPaiSum=data.yuPaiSum;
    showDuiJuShu();

    if(data.lastFourCards!=undefined ){ //最后四张
        moPai(data.lastFourCards[myInformation.seatNo]);
        myInformation.canHu=huPai3.test2(myInformation.pai,null,roomInformation.laizi,roomInformation);
        if(myInformation.canHu!=null){
            var c8_msg={
                msgId:"c8",
                type:myInformation.canHu.type,
                matchMethod:myInformation.canHu.matchMethod,
                actAs:myInformation.canHu.actAs
            }
            ws.send(JSON.stringify(c8_msg));
        }else {
            var c8_msg={
                msgId:"c8",
                type:"not_hu",
            }
            ws.send(JSON.stringify(c8_msg));
        }
        return;
    }

    //不是最后四张
    var seatNo=data.seatNo;
    //出牌人不为自己时，只需要更新当前出牌人图标
    if(seatNo!=myInformation.seatNo){
        chuPaiRen(roomInformation.allPlayer[seatNo].idString)
    }else{ //自己出牌
        chuPaiRen("#myplayer");
        roomInformation.isMyTurn=true;
        if(data.paiNo>0){
            //自己为出牌人 -1,不拿牌直接出牌（碰）、1-27 拿牌编号
            //进牌区显示该牌
            moPai(data.paiNo);
            myInformation.laiPai=data.paiNo;
            myInformation.canHu=huPai3.test2(myInformation.pai,null,roomInformation.laizi,roomInformation);
            // 清空碰的黑名单
            myInformation.notAdminPeng.length=0;
                //回头笑
                if(myInformation.peng.indexOf(data.paiNo)>=0){
                    myInformation.canXiaoNo=data.paiNo;
                    $("#xiaopai").css("display","block");
                    $("#buyao").css("display","block");
                }
                //闷笑
                if(canXiao()>0){
                    $("#xiaopai").css("display","block");
                    $("#buyao").css("display","block");
                }
                // 胡牌
                if(myInformation.canHu!=null){
                    $("#buyao").css("display","block");
                    $("#hupai").css("display","block");
                }
        }
        $("#chupai").css("display","block");
    }


}

// 玩家出牌处理
function Dos8(data) {
    var disCard=data.paiNo; //出的牌编号
    if(data.paiNo==roomInformation.laizi){
        roomInformation.laiZiApprience=true;
    }

    if(data.seatNo==myInformation.seatNo){ //自己的出牌，只显示
        var index=myInformation.pai.indexOf(disCard);
        myInformation.pai.splice(index,1);
        myInformation.chuPai.push(disCard);
        myCard();
        woChuPai(disCard)
        return;
    }

    var seatNo=data.seatNo;
    roomInformation.allPlayer[seatNo].chuPai.push(disCard);
    roomInformation.allPlayer[seatNo].daZi(disCard);

    //对其他玩家出牌处理 0要不起 1小朝天 2碰 3点笑
    myInformation.canHu=huPai3.test2(myInformation.pai,data.paiNo,roomInformation.laizi,roomInformation);
    var canPengXiaoV=canPengXiao(disCard);
    if(data.paiNo==roomInformation.laizi){
        var c7={
            msgId:"c7",
            type:"bu_yao"
        };
        ws.send(JSON.stringify(c7));
        return;
    }
    if(myInformation.canHu!=null){
        $("#buyao").css("display","block");
        $("#hupai").css("display","block");
    }
    if(canPengXiaoV==1){
        $("#buyao").css("display","block");
        $("#xiaopai").css("display","block");
    }
    if(canPengXiaoV==3){
        $("#buyao").css("display","block");
        $("#xiaopai").css("display","block");
        $("#pengpai").css("display","block");

    }
    if (canPengXiaoV==2){
        $("#buyao").css("display","block");
        $("#pengpai").css("display","block");
    }
    if(canPengXiaoV==0 &&myInformation.canHu==null){
        var c7={
            msgId:"c7",
            type:"bu_yao"
        };
        ws.send(JSON.stringify(c7));
    }
}

function endClear(){
    $("#left-pai").empty()
    $("#left-chupai").empty();
    $("#right-pai").empty();
    $("#right-chupai").empty();
    $("#across-pai").empty();
    $("#across-chupai").empty();
    // $("#leftplayer").empty();
    // $("#rightplayer").empty();
    // $("#acrossplayer").empty();

    $("#myDiscard").empty();
    $("#myPengXiao").empty();
    $("#chupaiqu").empty();
    hideButton();
    //私人房不需要情况头像
    if(roomInformation.sumTurn==1){
        $("#leftplayer").css("background-image","url()");
        $("#rightplayer").css("background-image","url()");
        $("#acrossplayer").css("background-image","url()");
    }
    $("#showlaizi").empty();
}

function Dos9(data) {
    var yuPaiData = [];
    for(var i=0;i<roomInformation.sumPlayer;i++){
        var playerPai=[];
        var pengAndOne=16;
        for(var j=1;j<28;j++){
            if(data.yuPai[i][j]>0&&data.yuPai[i][j]<5){
                for(var k=0;k<data.yuPai[i][j];k++){
                    playerPai.push(j);
                }
            }else if(data.yuPai[i][j]==pengAndOne){
                playerPai.push(j);
            }
        }
        yuPaiData.push(playerPai);
    }
    showYuPai(data.type,data.seatNoOfHu,data.seatNoOfBeiHu,yuPaiData,data.currentJiFen);
}

function showYuPai(type,seatNoOfHu,seatNoOfBeiHu,paiData,currentJiFen){
    $("#yupai-information").empty();
    $("#yupai-information").show();
    var huType=["","","",""];
    switch(type){
        case "pi_hu":{
            huType[seatNoOfHu]="屁模";
            break;
        }
        case "zhuo_chong":
        case "lian_chong":{
            for(var m=0;m<seatNoOfHu.length;m++){
                huType[seatNoOfHu[m]]="捉冲";
            }
            huType[seatNoOfBeiHu]="被胡了";
            break;
        }
        case "hei_mo":{
            huType[seatNoOfHu]="黑摸";
            break;
        }
        case "he_ju":{
            huType[0]="合局";
            break;
        }
        default:{break;}
    }
    for(var j = 0; j < paiData.length; j++){
        var username=document.createElement("div");
        var PlayedTurn=document.createElement("div");
        var yupaiInformation = document.getElementById("yupai-information");
        username.style.position="absolute";
        username.style.width = "40%";
        username.style.height = "4%";
        username.style.left="1%";
        username.style.top=24*j+2+"%";
        username.style.fontSize="1.2em";
        username.style.textAlign="left";
        if(j==myInformation.seatNo){
            username.append(myInformation.userName+" 本轮积分 : "+currentJiFen[j]+ " " +huType[myInformation.seatNo]);
        }else {
            var palyerInfo=roomInformation.allPlayer[j];
            username.append(palyerInfo.userName+" 本轮积分 : "+currentJiFen[j]+ " " +huType[palyerInfo.seatNo]);
        }

        yupaiInformation.appendChild(username);
        for (var i = 0; i < paiData[j].length; i++) {

            var image = new Image();
            //设置图片样式
            image.src="img/"+zhuanhuan[paiData[j][i]]+".png";
            image.style.width = "100%";
            image.style.height = "90%";
            image.style.position="absolute";
            image.style.left=0;
            image.style.top="-10%";

            var pai=document.createElement("div");
            // 设置背景样式
            pai.style.position="absolute";

            pai.style.width = "7%";
            pai.style.height = "18%";
            pai.style.left=6.5*i+"%";
            pai.style.top=24*j+6+"%";

            pai.style.background="url(img/麻将牌.png)";
            pai.style.backgroundRepeat="no-repeat";
            pai.style.backgroundSize="100% 100%";
            // pai.id="mychupai"+myInformation.chuPai.length;

            pai.append(image);
            $("#yupai-information").append(pai);
        }
    }

    var continueButton=document.createElement("button");
    continueButton.id="continue";

    continueButton.onclick=function(){
        if((roomInformation.playedTurn==roomInformation.sumTurn)&&(roomInformation.sumTurn>1)){
            //私人房结束，返回大厅
            if(confirm("看清积分了吗？")==true){
                window.location.href = "hall.jsp";
            }else{
                return;
            }
        }
        var c9=null;
        if(roomInformation.sumTurn>1){
            c9={
                msgId:"c9",
                diFen:roomInformation.diFen
            }
        } else{
            c9={
                msgId:"c9",
                diFen:roomInformation.diFen
            }
        }
        setTimeout(function () {
            endClear();
            init();
            ws.send(JSON.stringify(c9));
        },1000);
        $("#yupai-information").css("display","none");
    };
    $("#yupai-information").append(continueButton);

    if(roomInformation.sumTurn>1){
        if(roomInformation.playedTurn<roomInformation.sumTurn){
            $("#continue").html("继续游戏");
        }else{
            $("#continue").html("返回大厅");
        }
    }else{
        $("#continue").html("继续游戏");
        var exitRoom=document.createElement("button");
        $("#yupai-information").append(exitRoom);

        exitRoom.id="exitRoom";
        $("#exitRoom").html("返回大厅");
        exitRoom.onclick=function() {
            window.location.href = "hall.jsp";
        }
    }
}

function Dos10(data) {
    for(var i=0;i<roomInformation.sumPlayer;i++){
        if(i == myInformation.seatNo){
            myInformation.jiFen=data[myInformation.seatNo];
            myInformation.jiFen=data[myInformation.seatNo];
            showDuiJuShu();
        }else {
            var playerInfo=roomInformation.allPlayer[i];
            playerInfo.jiFen=data[playerInfo.seatNo];
            var idString=playerInfo.idString;
            $(idString).html("积分："+playerInfo.jiFen);
        }
    }
}

//显示笑
function Dos11(data) {
    var seatNo=data.seatNo;
    switch (data.type){
        case "zi_xiao":{
            if(seatNo!=myInformation.seatNo){ //别人
                var playerInfo=roomInformation.allPlayer[seatNo];
                if(data.paiNo==roomInformation.laiGen){
                    playerInfo.peng.push(data.paiNo);
                }else {
                    playerInfo.xiao.push(data.paiNo);
                }
                playerInfo.pai=playerInfo.pai-3;
                playerInfo.showPai();
            }else {  //自己
                if(data.paiNo==roomInformation.laiGen){
                    myInformation.peng.push(data.paiNo);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myCard();
                    woPeng();
                }else{
                    myInformation.xiao.push(data.paiNo);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myCard();
                    woPeng();
                }
            }
            break;
        }

        case "hui_tou_xiao":{
            if(seatNo!=myInformation.seatNo){
                var playerInfo=roomInformation.allPlayer[seatNo];
                playerInfo.peng.splice(playerInfo.peng.indexOf(data.paiNo),1);
                playerInfo.xiao.push(data.paiNo);
                playerInfo.showPai();
            }else{
                myInformation.peng.splice(myInformation.peng.indexOf(data.paiNo),1);
                myInformation.xiao.push(data.paiNo);
                myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                myCard();
                woPeng();
            }
            break;
        }
        //点笑需要清除上一个玩家出牌区打的字
        case "dian_xiao":{
            //清除上一个人打的字
            if($("#myChuPai").css("display")=="block"){
                removeChuPai("mychupai");
            } else if($("#leftChuPai").css("display")=="block"){
                removeChuPai("leftchupai");
            }else if($("#rightChuPai").css("display")=="block"){
                removeChuPai("rightchupai");
            }else if($("#acrossChuPai").css("display")=="block"){
                removeChuPai("acrosschupai");
            }
            if(seatNo!=myInformation.seatNo){
                var playerInfo=roomInformation.allPlayer[seatNo];
                if(data.paiNo==roomInformation.laiGen){
                    playerInfo.peng.push(data.paiNo);
                    playerInfo.pai=playerInfo.pai-3;
                } else {
                    playerInfo.xiao.push(data.paiNo);
                    playerInfo.pai=playerInfo.pai-3;
                }
                playerInfo.showPai();
            }else{
                if(data.paiNo==roomInformation.laiGen){
                    myInformation.peng.push(data.paiNo);
                }else{
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myInformation.xiao.push(data.paiNo);
                }
                myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                myCard();
                woPeng();
            }
        }
    }
}
// 显示碰
function Dos12(data){
    //清除上一个人打的字
    if($("#myChuPai").css("display")=="block"){
        removeChuPai("mychupai");
    } else if($("#leftChuPai").css("display")=="block"){
        removeChuPai("leftchupai");
    }else if($("#rightChuPai").css("display")=="block"){
        removeChuPai("rightchupai");
    }else if($("#acrossChuPai").css("display")=="block"){
        removeChuPai("acrosschupai");
    };
    var seatNo=data.seatNo;
    if(seatNo!=myInformation.seatNo){
        var playerInfo=roomInformation.allPlayer[seatNo];
        playerInfo.peng.push(data.paiNo);
        // leftInformation.pai.splice(leftInformation.pai.indexOf(data.paiNo),1);
        // leftInformation.pai.splice(leftInformation.pai.indexOf(data.paiNo),1);
        playerInfo.pai-=3;
        playerInfo.showPai();
    }else{
        //添加牌到碰中，删除手牌中两张该牌，重新显示碰、手牌
        myInformation.peng.push(data.paiNo);
        myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
        myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
        myCard();
        woPeng();
    }
}

function Dos13(data){
    endClear();
    //赋值房间信息
    roomInformation.roomId = data[0].roomId;
    roomInformation.diFen = data[0].diFen;
    roomInformation.sumTurn = data[0].sumTurn;
    roomInformation.playedTurn = data[0].playedTurn;
    roomInformation.laizi = data[0].laiZi;
    roomInformation.laiGen = data[0].laiGen;
    roomInformation.laiZiApprience = data[0].laiZiApprience;
    roomInformation.yuPaiSum = data[0].yuPaiSum;
    var sumPlayer=roomInformation.sumPlayer=data[0].sumPlayer;
    var selfSeatNo=myInformation.seatNo = data[0].selfSeatNo;
    setOtherPlayer(sumPlayer,selfSeatNo);
    showLaiGen();
    var jiFenArr=[];
    for(var i = 1; i<data.length; i++){
        jiFenArr.push(data[i].jifen);
        var seatNo=data[i].seatNo;
        if(seatNo == myInformation.seatNo){
            myInformation.headImgUrl = data[i].headImgUrl;
            myInformation.accountId = data[i].accountId;
            myInformation.userName = data[i].username;
            //将后台牌数组转换为前台对应格式
            for (var j = 1; j < data[i].cardArr.length; j++) {
                switch (data[i].cardArr[j]) {
                    case 1: {
                        myInformation.pai.push(j);
                        break;
                    }
                    case 2: {
                        myInformation.pai.push(j);
                        myInformation.pai.push(j);
                        break;
                    }
                    case 3: {
                        for (var m = 0; m < 3; m++) {
                            myInformation.pai.push(j);
                        }
                        break;
                    }
                    case 4: {
                        for (var m = 0; m < 4; m++) {
                            myInformation.pai.push(j);
                        }
                        break;
                    }
                    //peng
                    case 15: {
                        myInformation.peng.push(j);
                        break;
                    }
                    //peng and one
                    case 16: {
                        myInformation.peng.push(j);
                        myInformation.pai.push(j);
                        break;
                    }
                    //点笑
                    case 17: {
                        if (j == roomInformation.laiGen) {
                            myInformation.peng.push(j);
                        } else {
                            myInformation.xiao.push(j);
                        }
                        break;
                    }
                    //回头笑
                    case 18: {
                        myInformation.xiao.push(j);
                        break;
                    }
                    //自笑
                    case 19: {
                        if (j == roomInformation.laiGen) {
                            myInformation.peng.push(j);
                        } else {
                            myInformation.xiao.push(j);
                        }
                        break;
                    }
                    default : {
                        break;
                    }
                }
            }
            if (data[i].superFlag == false) {
                //将手上的自笑加入黑名单
                var b=[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
                for(var aa=0;aa<myInformation.pai.length;aa++){
                    b[myInformation.pai[aa]]++;
                }
                for (var num=1;num<b.length;num++){
                    if(b[num]==4||(b[num]==3&&b[num]==roomInformation.laiGen)){
                        myInformation.notAdminXiao.push(num);
                    }
                }
            }
            //显示自己手牌、碰、笑、打的字
            myCard();
            woPeng();
            for (var a = 0; a < data[i].disCardArr.length; a++) {
                myInformation.chuPai.push(data[i].disCardArr[a]);
                woChuPai(data[i].disCardArr[a]);
            }
        }else{
            var playerInfo=roomInformation.allPlayer[seatNo];
            playerInfo.seatNo = data[i].seatNo;
            playerInfo.headImgUrl = data[i].headImgUrl;
            playerInfo.accountId = data[i].accountId;
            playerInfo.userName = data[i].username;
            playerInfo.pai=13;
            var idString=playerInfo.idString;
            $(idString).css("background-image", "url(" + data[i].headImgUrl + ")");
            //将后台牌数组转换为前台对应格式
            for (var j = 1; j < data[i].cardArr.length; j++) {
                switch (data[i].cardArr[j]) {
                    //peng
                    case 15:
                    //peng and one
                    case 16: {
                        playerInfo.peng.push(j);
                        playerInfo.pai = playerInfo.pai - 3;
                        break;
                    }

                    //回头笑
                    case 18: {
                        playerInfo.xiao.push(j);
                        playerInfo.pai = playerInfo.pai - 3;
                        break;
                    }
                    //点笑
                    case 17:
                    //自笑
                    case 19: {
                        if (j == roomInformation.laiGen) {
                            playerInfo.peng.push(j);
                        } else {
                            playerInfo.xiao.push(j);
                        }
                        playerInfo.pai = playerInfo.pai - 3;
                        break;
                    }
                }
            }
            //显示自己手牌、碰、笑、打的字
            playerInfo.showPai();
            for (var a = 0; a < data[i].disCardArr.length; a++) {
                playerInfo.chuPai.push(data[i].disCardArr[a]);
                playerInfo.daZi(data[i].disCardArr[a]);
            }
        }
    }

    Dos10(jiFenArr);

    data[0].paiNo=data[0].getCardNoBeforeDis;
    data[0].seatNo=data[0].disCardSeatNo;

    //出牌人可以出牌
    if(data[0].canDisCard){
        //该我出牌
        if(data[0].disCardSeatNo==myInformation.seatNo){
            var index=myInformation.pai.indexOf(data[0].getCardNoBeforeDis);
            if(index != -1){
                myInformation.pai.splice(index,1);
            }
            myCard();
            woPeng();
        }
        Dos7(data[0]);
    } else{
        switch(data[0].responseFlag){
            case 31:{
                Dos7(data[0]);
                data[0].paiNo=data[0].disCardNo;
                var seatNo=data[0].disCardSeatNo;
                var playerInfo=roomInformation.allPlayer[seatNo];
                playerInfo.chuPai.splice(playerInfo.chuPai.length-1,1);

                Dos8(data[0]);
                break;
            }
            case 32:{
                myInformation.pai.splice(myInformation.pai.indexOf(data[0].lastFourCards[myInformation.seatNo]),1);
                Dos7(data[0]);
                break;
            }
            case 33:{
                Dos9(data[0]);
                break;
            }
        }
    }
    if(data[0].disCardSeatNo == myInformation.seatNo){
        chuPaiRen("#myplayer");
    }else {
        var playerInfo=roomInformation.allPlayer[data[0].disCardSeatNo];
        chuPaiRen(playerInfo.idString)
    }
}
