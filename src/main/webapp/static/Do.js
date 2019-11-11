// var a ={pai:[],peng:[],xiao:"",seatNo:"",accountNo:"",headImgUrl:"",chuPai:[],userName:"",id};
var myInformation=new Object();
var leftInformation=new Object();
var rightInformation=new Object();
var acrossInformation=new Object();
var roomInformation={sumTurn:"",playedTurn:"",diFen:"",roomId:"",laizi:"",laiGen:""};
//将牌通过数组进行转换，只用于显示。
var zhuanhuan=[-1,"suo1","suo2","suo3","suo4","suo5","suo6","suo7","suo8","suo9","wan1","wan2","wan3","wan4","wan5","wan6","wan7","wan8","wan9","tong1","tong2","tong3","tong4","tong5","tong6","tong7","tong8","tong9"];
rightInformation.jiFen=0;
acrossInformation.jiFen=0;
leftInformation.jiFen=0;
myInformation.jiFen=0;
init();
function init(){
    myInformation.seatNo="";
    myInformation.chuPai=[];
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
    myInformation.canHu={};
    leftInformation.chuPai=[];
    leftInformation.pai=13;
    leftInformation.peng=[];
    leftInformation.xiao=[];
    roomInformation.diFen=5;
    roomInformation.playedTurn=0;
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

//新玩家加入后，再玩家对象中添加基本信息，同时显示玩家头像
 function Dos4(data) {
    var selfSeatNo=data[0].selfSeatNo;
    myInformation.seatNo=selfSeatNo;

     roomInformation.diFen=data[0].diFen;
     roomInformation.playedTurn=data[0].playedTurn;
     roomInformation.roomId=data[0].roomId;
     roomInformation.sumTurn=data[0].sumTurn;
     for (var i = 0; i < data.length; i++) {
         if (data[i].seatNo == selfSeatNo) {
             myInformation.headImgUrl = data[selfSeatNo].headImgUrl;
             myInformation.accountId = data[selfSeatNo].accountId;
             myInformation.userName = data[selfSeatNo].username;

         } else if (data[i].seatNo == ((parseInt(myInformation.seatNo) + 1) % 4)) {
             //rightplayer
             rightInformation.seatNo = data[i].seatNo;
             rightInformation.headImgUrl = data[i].headImgUrl;
             rightInformation.accountId = data[i].accountId;
             rightInformation.userName = data[i].username;
             $("#rightplayer").css("background-image","url("+rightInformation.headImgUrl+")");

         } else if (data[i].seatNo == ((parseInt(myInformation.seatNo) + 2) % 4)) {
             //accrossplayer
             acrossInformation.seatNo = data[i].seatNo;
             acrossInformation.headImgUrl = data[i].headImgUrl;
             acrossInformation.accountId = data[i].accountId;
             acrossInformation.userName = data[i].username;
             $("#acrossplayer").css("background-image","url("+acrossInformation.headImgUrl+")");

         } else if (data[i].seatNo == ((parseInt(myInformation.seatNo) + 3) % 4)) {
             //leftplayer
             leftInformation.seatNo = data[i].seatNo;
             leftInformation.headImgUrl = data[i].headImgUrl;
             leftInformation.accountId = data[i].accountId;
             leftInformation.userName = data[i].username;
             $("#leftplayer").css("background-image","url("+leftInformation.headImgUrl+")");
            }
     }
 }

 //有玩家加入，保存玩家信息，显示玩家
 function Dos5(data) {
     if(data.type=="exit"){
         switch (data.seatNo) {
             case leftInformation.seatNo:{
                 leftInformation.seatNo="";
                 leftInformation.headImgUrl="";
                 leftInformation.accountId="";
                 leftInformation.userName="";
                 $("#leftplayer").css("background-image","url()");
                 break;
                }
             case rightInformation.seatNo:{
                 rightInformation.seatNo="";
                 rightInformation.headImgUrl="";
                 rightInformation.accountId="";
                 rightInformation.userName="";
                 $("#rightplayer").css("background-image","url()");
                 break;
             }
             case  acrossInformation.seatNo:{
                 acrossInformation.seatNo="";
                 acrossInformation.headImgUrl="";
                 acrossInformation.accountId="";
                 acrossInformation.userName="";
                 $("#acrossplayer").css("background-image","url()");
                 break;
             }

         }
     }else{
         if(data.seatNo == ((parseInt(myInformation.seatNo) + 1)% 4)) {
             //rightplayer
             rightInformation.seatNo = data.seatNo;
             rightInformation.headImgUrl = data.headImgUrl;
             rightInformation.accountId = data.accountId;
             rightInformation.userName = data.username;
             $("#rightplayer").css("background-image","url("+rightInformation.headImgUrl+")");

         } else if (data.seatNo == ((parseInt(myInformation.seatNo) + 2) % 4)) {
             //accrossplayer
             acrossInformation.seatNo = data.seatNo;
             acrossInformation.headImgUrl = data.headImgUrl;
             acrossInformation.accountId = data.accountId;
             acrossInformation.userName = data.username;
             $("#acrossplayer").css("background-image","url("+acrossInformation.headImgUrl+")");

         } else if (data.seatNo == ((parseInt(myInformation.seatNo) + 3) % 4)) {
             //leftplayer
             leftInformation.seatNo = data.seatNo;
             leftInformation.headImgUrl = data.headImgUrl;
             leftInformation.accountId = data.accountId;
             leftInformation.userName = data.username;
             $("#leftplayer").css("background-image","url("+leftInformation.headImgUrl+")");
         }
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
     var imglaizi=new Image();
     imglaizi.src="img/"+zhuanhuan[roomInformation.laiGen]+".png";
     imglaizi.style.position="absolute";
     imglaizi.style.width="100%";
     imglaizi.style.height="100%";
     imglaizi.style.left="0%";
     $("#showlaizi").append(imglaizi);
     myCard();
     leftPai();
     rightPai();
     acrossPai()
 }

 function showDuiJuShu() {
     $("#duijushu").html("底分 : "+roomInformation.diFen+ "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp对局数 : "+roomInformation.playedTurn+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp余牌 : "+roomInformation.yuPaiSum+"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp积分 : "+myInformation.jiFen);

 }
function Dos7(data) {
    roomInformation.yuPaiSum=data.yuPaiSum;
    showDuiJuShu();
    if(data.lastFourCards!=undefined){
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

    //出牌人不为自己时，只需要更新当前出牌人图标
    if(data.seatNo==leftInformation.seatNo){
        //出牌人为左边玩家,只需要更新当前出牌人
        chuPaiRen(3);
    }else if (data.seatNo==rightInformation.seatNo){
        //出牌人为右边玩家
        chuPaiRen(1);
    } else if(data.seatNo==acrossInformation.seatNo){
        //出牌人为对面玩家
        chuPaiRen(2);
    }else if (data.seatNo==myInformation.seatNo){
        chuPaiRen(0);
        roomInformation.isMyTurn=true;

        if(data.paiNo>0){

            //自己为出牌人 -1,不拿牌直接出牌、1-27 拿牌编号
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
    switch (data.seatNo){
        case leftInformation.seatNo:
            leftInformation.chuPai.push(disCard);
            leftDaZi(disCard);
            break;
        case rightInformation.seatNo:
            rightInformation.chuPai.push(disCard);
            rightDaZi(disCard);
            break;
        case acrossInformation.seatNo:
            acrossInformation.chuPai.push(disCard)
            acrossDaZi(disCard);
            break;
    }

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
    $("#leftplayer").empty();
    $("#rightplayer").empty();
    $("#acrossplayer").empty();
    $("#myDiscard").empty();
    $("#myPengXiao").empty();
    $("#chupaiqu").empty();
    hideButton();
    $("#leftplayer").css("background-image","url()");
    $("#rightplayer").css("background-image","url()");
    $("#acrossplayer").css("background-image","url()");
    $("#showlaizi").empty();
}
function Dos9(data) {
    if(data.isOver){
    //结束游戏 返回房间界面
    //     alert("余牌显示todo "+data.type+ "点击继续游戏");
        var c9={
            msgId:"c9",
            diFen:roomInformation.diFen
        }
        setTimeout(function () {
            endClear();
            init();
            ws.send(JSON.stringify(c9));
        },4000)

    } else{
        // 初始化值
        alert("重新开始游戏 todo1");
    }
}
function Dos10(data) {
    myInformation.jiFen=data[myInformation.seatNo];
    leftInformation.jiFen=data[leftInformation.seatNo];
    rightInformation.jiFen=data[rightInformation.seatNo];
    acrossInformation.jiFen=data[acrossInformation.seatNo];

    $("#rightplayer").html("积分："+rightInformation.jiFen);
    $("#acrossplayer").html("积分："+acrossInformation.jiFen);
    $("#leftplayer").html("积分："+leftInformation.jiFen);
    showDuiJuShu();
}
//显示笑
function Dos11(data) {
    switch (data.type){
        case "zi_xiao":{
            switch (data.seatNo) {
                case leftInformation.seatNo:{
                    if(data.paiNo==roomInformation.laiGen){
                        leftInformation.peng.push(data.paiNo);
                        leftInformation.pai=leftInformation.pai-3;
                        leftPai();
                    }else{
                        leftInformation.xiao.push(data.paiNo);
                        leftInformation.pai=leftInformation.pai-4;
                        leftPai();
                    }
                    break;
                }
                case rightInformation.seatNo:{
                    if(data.paiNo==roomInformation.laiGen){
                        rightInformation.peng.push(data.paiNo);
                        rightInformation.pai=rightInformation.pai-3;
                        rightPai();
                    }else{
                        rightInformation.xiao.push(data.paiNo);
                        rightInformation.pai=rightInformation.pai-4;
                        rightPai();
                    }
                    break;
                }
                case acrossInformation.seatNo:{
                    if(data.paiNo==roomInformation.laiGen){
                        acrossInformation.peng.push(data.paiNo);
                        acrossInformation.pai=acrossInformation.pai-3;
                        acrossPai();
                    }else{
                        acrossInformation.xiao.push(data.paiNo);
                        acrossInformation.pai=acrossInformation.pai-4;
                        acrossPai();
                    }
                    break;
                }
                case myInformation.seatNo:{
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
                    break;
                }
            }
            break;
        }
        case "hui_tou_xiao":{
            switch (data.seatNo) {
                case leftInformation.seatNo:{
                    leftInformation.peng.splice(leftInformation.peng.indexOf(data.paiNo),1);
                    leftInformation.xiao.push(data.paiNo);
                    leftPai();
                    break;
                }
                case rightInformation.seatNo:{
                    rightInformation.peng.splice(rightInformation.peng.indexOf(data.paiNo),1);
                    rightInformation.xiao.push(data.paiNo);
                    rightPai();
                    break;
                }
                case acrossInformation.seatNo:{
                    acrossInformation.peng.splice(acrossInformation.peng.indexOf(data.paiNo),1);
                    acrossInformation.xiao.push(data.paiNo);
                    acrossPai();
                    break;
                }
                case myInformation.seatNo:{
                    myInformation.peng.splice(myInformation.peng.indexOf(data.paiNo),1);
                    myInformation.xiao.push(data.paiNo);
                    myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
                    myCard();
                    woPeng();
                    break;
                }
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
            
            switch (data.seatNo) {
                case leftInformation.seatNo:{
                    if(data.paiNo==roomInformation.laiGen){
                        leftInformation.peng.push(data.paiNo);
                        leftInformation.pai=leftInformation.pai-3;
                    } else {
                        leftInformation.xiao.push(data.paiNo);
                        leftInformation.pai=leftInformation.pai-3;
                    }
                    leftPai();
                    break;
                }
                case rightInformation.seatNo:{
                    if(data.paiNo==roomInformation.laiGen){
                        rightInformation.peng.push(data.paiNo);
                        rightInformation.pai=rightInformation.pai-3;
                    } else{
                        rightInformation.xiao.push(data.paiNo);
                        rightInformation.pai=rightInformation.pai-3;
                    }

                    rightPai();
                    break;
                }
                case acrossInformation.seatNo:{
                    if(data.paiNo==roomInformation.laiGen) {
                        acrossInformation.peng.push(data.paiNo);
                        acrossInformation.pai=acrossInformation.pai-3;
                    }else{
                        acrossInformation.xiao.push(data.paiNo);
                        acrossInformation.pai=acrossInformation.pai-3;
                    }
                    acrossPai();
                    break;
                }
                case myInformation.seatNo:{
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
                    break;
                }
            }
            break;
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
    switch (data.seatNo) {
        case leftInformation.seatNo:{
            leftInformation.peng.push(data.paiNo);
            // leftInformation.pai.splice(leftInformation.pai.indexOf(data.paiNo),1);
            // leftInformation.pai.splice(leftInformation.pai.indexOf(data.paiNo),1);
            leftInformation.pai-=3;
            leftPai();
            break;
        }
        case rightInformation.seatNo:{
            rightInformation.peng.push(data.paiNo);
            // rightInformation.pai.splice(rightInformation.pai.indexOf(data.paiNo),1);
            // rightInformation.pai.splice(rightInformation.pai.indexOf(data.paiNo),1);
            rightInformation.pai-=3;
            rightPai();
            removeChuPai()
            break;
        }
        case acrossInformation.seatNo:{

            acrossInformation.peng.push(data.paiNo);
            // acrossInformation.pai.splice(acrossInformation.pai.indexOf(data.paiNo),1);
            // acrossInformation.pai.splice(acrossInformation.pai.indexOf(data.paiNo),1);
            acrossInformation.pai-=3;
            acrossPai();
            break;
        }
        case myInformation.seatNo:{
            //添加牌到碰中，删除手牌中两张该牌，重新显示碰、手牌
            myInformation.peng.push(data.paiNo);
            myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
            myInformation.pai.splice(myInformation.pai.indexOf(data.paiNo),1);
            myCard();
            woPeng();
            break;
        }
    }
}