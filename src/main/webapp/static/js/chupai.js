$("#buyao").click(function () {

    hideButton();
    if (myInformation.canXiaoNo > 0) {
        myInformation.notAdminXiao.push(myInformation.canXiaoNo);
        myInformation.canXiaoNo = -1;
    }
    if (myInformation.canPengNo > 0) {
        myInformation.notAdminPeng.push(myInformation.canPengNo);
        myInformation.canPengNo = -1;
    }
    if (roomInformation.isMyTurn == true) {
        $("#chupai").css("display", "block")
        //我出牌点击不要
        $("#pengpai").css("display", "none");
        $("#hupai").css("display", "none");
        $("#xiaopai").css("display", "none");
        // $("#chupai").css("display","none");
        if (($("#myChuPai").css("display")) == "none") {
            var c7 = {msgId: "c7", type: "bu_yao"};
            ws.send(JSON.stringify(c7));
        }

    } else {
        //别人出牌我不要
        var c7 = {msgId: "c7", type: "bu_yao"};
        ws.send(JSON.stringify(c7));
    }

});

$("#chupai").click(function () {
    // 用户选中牌并点击出牌允许出牌
    if (roomInformation.isMyTurn == true && myInformation.xuanPai >= 0) {
        roomInformation.isMyTurn = false;
        var c4 = {
            msgId: "c4",
            paiNo: myInformation.pai[myInformation.xuanPai]
        }
        //自己打出赖子后不让胡牌
        if (myInformation.pai[myInformation.xuanPai] == roomInformation.laizi) {
            myInformation.laiZiApprience = true;
        }
        myInformation.xuanPai = -1;
        ws.send(JSON.stringify(c4));
        hideButton();
    }
});

function hideButton() {
    $("#pengpai").css("display", "none")
    $("#hupai").css("display", "none")
    $("#xiaopai").css("display", "none")
    $("#chupai").css("display", "none")
    $("#buyao").css("display", "none");
    $("#chiyou").css("display", "none");
    $("#chizuo").css("display", "none");
    $("#chizhong").css("display", "none");
}

//点击碰之后隐藏胡、笑、碰
$("#pengpai").click(function () {
    hideButton();
    $("#chupai").css("display", "block");
    var c7 = {msgId: "c7", type: "peng", paiNo: myInformation.canPengNo}
    ws.send(JSON.stringify(c7));
});

$("#chizuo").click(function () {
    hideButton();
    $("#chupai").css("display", "block");
    var c7 = {msgId: "c7", type: "chi", chiType: 1}
    ws.send(JSON.stringify(c7));
});
$("#chizhong").click(function () {
    hideButton();
    $("#chupai").css("display", "block");
    var c7 = {msgId: "c7", type: "chi", chiType: 2}
    ws.send(JSON.stringify(c7));
});
$("#chiyou").click(function () {
    hideButton();
    $("#chupai").css("display", "block");
    var c7 = {msgId: "c7", type: "chi", chiType: 3}
    ws.send(JSON.stringify(c7));
});

$("#hupai").click(function () {
    console.log(myInformation.canHu);

    if ((myInformation.pai.length % 3) == 2) {
        var c5 = {
            msgId: "c5",
            type: myInformation.canHu.type,
            matchMethod: myInformation.canHu.matchMethod,
            actAs: myInformation.canHu.actAs
        }
        ws.send(JSON.stringify(c5));
    } else {
        var c7 = {
            msgId: "c7",
            type: myInformation.canHu.type,
            matchMethod: myInformation.canHu.matchMethod,
            actAs: myInformation.canHu.actAs
        }
        ws.send(JSON.stringify(c7));
    }

});

$("#xiaopai").click(function () {
    hideButton();
    var number = 0;
    for (var i = 0; i < myInformation.pai.length; i++) {
        if (myInformation.pai[i] == (myInformation.canXiaoNo)) {
            number++;
        }
    }

    if (number == 1) {

        var c6 = {
            msgId: "c6",
            type: "hui_tou_xiao",
            paiNo: myInformation.canXiaoNo

        }
        ws.send(JSON.stringify(c6));

    } else if (number == 2) {
        //小朝天
        var c7 = {
            msgId: "c7",
            type: "dian_xiao",
            paiNo: myInformation.canXiaoNo
        }
        ws.send(JSON.stringify(c7));
    } else if (number == 3) {
        //大朝天
        if (myInformation.canXiaoNo == roomInformation.laiGen) {
            var c6 = {
                msgId: "c6",
                type: "zi_xiao",
                paiNo: myInformation.canXiaoNo
            }
            ws.send(JSON.stringify(c6));
        } else {
            //点笑
            var c7 = {
                msgId: "c7",
                type: "dian_xiao",
                paiNo: myInformation.canXiaoNo
            }
            ws.send(JSON.stringify(c7));
        }
    } else if (number == 4) {
        //闷笑
        var c6 = {
            msgId: "c6",
            type: "zi_xiao",
            paiNo: myInformation.canXiaoNo
        }
        ws.send(JSON.stringify(c6));
    }
});

function compartor(param1, param2) {
    return param1 - param2;
}

// 显示我的手牌
function myCard() {
    //将赖子取出、再排序、再赖子加入数组
    var laizi = 0;
    var hongzhong = 0;
    var temp = myInformation.pai.length
    for (var a = 0; a < temp;) {
        if (myInformation.pai[a] == roomInformation.laizi) {
            myInformation.pai.splice(a, 1);
            laizi++;
            temp--;
        } else if (myInformation.pai[a] == Rule.HongZhongPoint) {
            myInformation.pai.splice(a, 1);
            hongzhong++;
            temp--;
        }
        else {
            a++;
        }
    }
    //逆序
    myInformation.pai.sort(compartor);
    myInformation.pai.reverse();
    //加红中到左侧
    for (var b = 0; b < hongzhong; b++) {
        myInformation.pai.push(Rule.HongZhongPoint);
    }
    //加赖子到左侧
    for (var b = 0; b < laizi; b++) {
        myInformation.pai.push(roomInformation.laizi);
    }

    $("#chupaiqu").empty();
    for (var i = 0; i < myInformation.pai.length; i++) {
        if (parseInt(myInformation.pai[i]) >= 0) {
            var image = new Image();
            image.src = "img/" + zhuanhuan[myInformation.pai[i]] + ".png";
            image.id = i;
            image.ondblclick = function () {
                doubleClick(this.id)
            };
            image.onclick = function () {
                clickPai(this.id)
            };
            image.style.width = "12%";
            image.style.height = "140%";
            image.style.right = (11.6 * i + 23.2) + "%";
            image.style.backgroundImage = 'url(img/自己手牌.png)';

            $("#chupaiqu").append(image);
        }
    }
}

// 选中牌
function clickPai(id) {
    if ($("#" + id).position().top < 0) {
        $("#" + id).animate({top: "0"}, 0);
        $("#" + id).css("width", "12%");
        $("#" + id).css("height", "140%");
        myInformation.xuanPai = -1;
    } else {
        $("#" + id).animate({top: "-40%"}, 0);
        $("#" + id).siblings().animate({top: "0"}, 0);
        $("#" + id).siblings().animate({width: "12%"}, 0);
        $("#" + id).siblings().animate({height: "140%"}, 0);
        $("#" + id).css("width", "14%");
        $("#" + id).css("height", "180%");
        $("#" + id).css("z-index", "1");
        $("#" + id).siblings().css("z-index", "0");
        myInformation.xuanPai = id;
    }

}

//双击牌事件 id为该牌在数组中下标
function doubleClick(id) {
    if (roomInformation.isMyTurn == true) {
        var c4_msg = {
            msgId: "c4",
            paiNo: myInformation.pai[id]
        }
        roomInformation.isMyTurn = false;
        ws.send(JSON.stringify(c4_msg));

    }
    hideButton();
}

// 左边玩家手牌
function leftPai() {
    $("#left-pai").empty();
    for (var i = 0; i < leftInformation.pai; i++) {
        var image = new Image();
        image.src = "img/leftpai.png";
        image.style.top = 8 * i + "%";
        $("#left-pai").append(image);
    }
    var newImg = function (imgUrl) {
        //设置图片样式
        var image = new Image();
        image.src = imgUrl
        image.style.width = "78%";
        image.style.height = "92%";
        image.style.top = "-10%";
        image.style.position = "absolute";
        image.style.left = "16%";
        image.style.transform = "rotate(90deg)";
        return image
    }
    //左边碰
    for (var i = 0; i < leftInformation.peng.length; i++) {
        for (var b = 0; b < 3; b++) {
            var image = newImg("img/" + zhuanhuan[leftInformation.peng[i]] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "98%";
            pai.style.height = "15%";
            pai.style.position = "absolute";
            pai.style.left = "30%";
            pai.style.top = ((leftInformation.pai * 8) + (b * 9.5)) + (28 * i) + 6 + "%";
            pai.style.background = "url(img/侧家出牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#left-pai").append(pai);
        }
    }
    // 左边笑
    for (var j = 0; j < leftInformation.xiao.length; j++) {
        for (var b = 0; b < 3; b++) {
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "98%";
            pai.style.height = "15%";
            pai.style.position = "absolute";
            pai.style.left = "30%";
            pai.style.top = ((leftInformation.pai * 8) + (b * 9.5)) + leftInformation.peng.length * 28 + (28 * j) + 6 + "%";
            var paiNo=leftInformation.xiao[j]
            if(Rule.AnGangHide && leftInformation.hideAnGangSet.has(paiNo)){
                pai.style.background = "url(img/侧家杠牌.png)";
            }else{
                pai.style.background = "url(img/侧家出牌.png)";
                var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
                pai.append(image);
            }
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            $("#left-pai").append(pai);
        }
        //顶上多一个字
        var pai = document.createElement("div");
        // 设置背景样式
        pai.style.width = "96%";
        pai.style.height = "14%";
        pai.style.position = "absolute";
        pai.style.left = "30%";
        pai.style.top = ((leftInformation.pai * 8) + leftInformation.peng.length * 28 + (28 * j) + 13.5) + "%";
        var paiNo=leftInformation.xiao[j]
        if(Rule.AnGangHide && leftInformation.hideAnGangSet.has(paiNo)){
            pai.style.background = "url(img/侧家杠牌.png)";
        }else{
            pai.style.background = "url(img/侧家出牌.png)";
            var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
            pai.append(image);
        }
        pai.style.backgroundRepeat = "no-repeat";
        pai.style.backgroundSize = "100% 100%";
        $("#left-pai").append(pai);
    }
    //左边吃
    for (var i = 0; i < leftInformation.chiArr.length; i++) {
        for (var b = 0; b < 3; b++) {
            var paiNo = leftInformation.chiArr[i].paiArr[b]
            var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "98%";
            pai.style.height = "15%";
            pai.style.position = "absolute";
            pai.style.left = "30%";
            pai.style.top = ((leftInformation.pai * 8) + (leftInformation.peng.length + leftInformation.xiao.length) * 28 + (b * 9.5)) + (28 * i) + 6 + "%";
            pai.style.background = "url(img/侧家出牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#left-pai").append(pai);
        }
    }
}

function rightPai() {
    $("#right-pai").empty();
    // 右边玩家手牌
    for (var i = 0; i < rightInformation.pai; i++) {
        var image = new Image();
        image.src = "img/侧家手牌.png";
        // image.style.width="30%";
        // image.style.height="8%";
        image.style.top = 8 * i + "%";
        image.style.left = 0;
        $("#right-pai").append(image);
    }
    var newImg = function (imgUrl) {
        //设置图片样式
        var image = new Image();
        image.src = imgUrl
        image.style.width = "78%";
        image.style.height = "92%";
        image.style.top = "-10%";
        image.style.position = "absolute";
        image.style.left = "3%";
        image.style.transform = "rotate(270deg)";
        return image
    }
    //右边碰
    for (var i = 0; i < rightInformation.peng.length; i++) {
        for (var b = 0; b < 3; b++) {
            var image = newImg("img/" + zhuanhuan[rightInformation.peng[i]] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "98%";
            pai.style.height = "15%";
            pai.style.position = "absolute";
            pai.style.left = "-15%";

            pai.style.top = ((rightInformation.pai * 8) + (b * 9.5)) + (28 * i) + 6 + "%";
            pai.style.background = "url(img/侧家出牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#right-pai").append(pai);
        }
    }
    // 右边笑
    for (var j = 0; j < rightInformation.xiao.length; j++) {
        for (var b = 0; b < 3; b++) {
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "98%";
            pai.style.height = "15%";
            pai.style.position = "absolute";
            pai.style.left = "-15%";
            pai.style.top = ((rightInformation.pai * 8) + (b * 9.5)) + rightInformation.peng.length * 28 + (28 * j) + 6 + "%";
            var paiNo = rightInformation.xiao[j]
            if(Rule.AnGangHide && rightInformation.hideAnGangSet.has(paiNo)){
                pai.style.background = "url(img/侧家杠牌.png)";
            }else{
                pai.style.background = "url(img/侧家出牌.png)";
                var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
                pai.append(image);
            }
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            $("#right-pai").append(pai);
        }

        var pai = document.createElement("div");
        // 设置背景样式
        pai.style.width = "96%";
        pai.style.height = "14%";
        pai.style.position = "absolute";
        pai.style.left = "-13%";
        pai.style.top = ((rightInformation.pai * 8) + rightInformation.peng.length * 28 + (28 * j) + 13) + "%";
        var paiNo = rightInformation.xiao[j]
        if(Rule.AnGangHide && rightInformation.hideAnGangSet.has(paiNo)){
            pai.style.background = "url(img/侧家杠牌.png)";
        }else{
            pai.style.background = "url(img/侧家出牌.png)";
            var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
            pai.append(image);
        }
        pai.style.backgroundRepeat = "no-repeat";
        pai.style.backgroundSize = "100% 100%";
        $("#right-pai").append(pai);
    }
    //右边边吃
    for (var i = 0; i < rightInformation.chiArr.length; i++) {
        for (var b = 0; b < 3; b++) {
            var paiNo = rightInformation.chiArr[i].paiArr[2-b]
            var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "98%";
            pai.style.height = "15%";
            pai.style.position = "absolute";
            pai.style.left = "-15%";
            pai.style.top = ((rightInformation.pai * 8) + (rightInformation.peng.length + rightInformation.xiao.length) * 28 + (b * 9.5)) + (28 * i) + 6 + "%";
            pai.style.background = "url(img/侧家出牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#right-pai").append(pai);
        }
    }
}

// 对面玩家手牌
function acrossPai() {

    $("#across-pai").empty();
    for (var i = 0; i < acrossInformation.pai; i++) {
        var image = new Image();
        image.src = "img/对家手牌.png";
        // image.style.width="30%";     
        // image.style.height="8%"; 
        image.style.left = 5.55 * i + 25 + "%";
        $("#across-pai").append(image);
    }
    var newImg = function (imgUrl,notTransform) {
        //设置图片样式
        var image = new Image();
        image.src = imgUrl
        image.style.width = "90%";
        image.style.height = "92%";
        image.style.top = "0%";
        image.style.position = "absolute";
        image.style.left = "3%";
        if(!notTransform){
            image.style.transform = "rotate(180deg)";
        }
        return image
    }
    //对面碰
    for (var i = 0; i < acrossInformation.peng.length; i++) {
        for (var b = 0; b < 3; b++) {
            var image = newImg("img/" + zhuanhuan[acrossInformation.peng[i]] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "5%";
            pai.style.height = "80%";
            pai.style.position = "absolute";
            pai.style.top = "18%";

            pai.style.left = (5.55 * acrossInformation.pai) + 25 + (b * 4.5) + (13.5 * i) + "%";

            pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#across-pai").append(pai);
        }
    }
    // 对面笑
    for (var j = 0; j < acrossInformation.xiao.length; j++) {
        for (var b = 0; b < 3; b++) {

            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "5%";
            pai.style.height = "80%";
            pai.style.position = "absolute";
            pai.style.top = "18%";
            pai.style.left = acrossInformation.peng.length * 13.5 + (5.55 * acrossInformation.pai) + 25 + (b * 4.5) + (13.5 * j) + "%";
            var paiNo=acrossInformation.xiao[j]
            if(Rule.AnGangHide && acrossInformation.hideAnGangSet.has(paiNo)){
                image= newImg("img/自己杠牌.png",true)
            }else{
                pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
                var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
                pai.append(image);
            }
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            $("#across-pai").append(pai);
        }

        var pai = document.createElement("div");
        // 设置背景样式
        pai.style.width = "5%";
        pai.style.height = "80%";
        pai.style.position = "absolute";
        pai.style.top = "0%";
        pai.style.left = acrossInformation.peng.length * 13.5 + (5.55 * acrossInformation.pai) + 29.5 + (13.5 * j) + "%";
        var paiNo=acrossInformation.xiao[j]
        if(Rule.AnGangHide && acrossInformation.hideAnGangSet.has(paiNo)){
            image= newImg("img/自己杠牌.png",true)
        }else{
            pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
            var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
            pai.append(image);
        }
        pai.style.backgroundRepeat = "no-repeat";
        pai.style.backgroundSize = "100% 100%";
        $("#across-pai").append(pai);
    }

    //对面吃
    for (var i = 0; i < acrossInformation.chiArr.length; i++) {
        for (var b = 0; b < 3; b++) {
            var paiNo = acrossInformation.chiArr[i].paiArr[2-b]
            var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.width = "5%";
            pai.style.height = "80%";
            pai.style.position = "absolute";
            pai.style.top = "18%";
            pai.style.left = (acrossInformation.peng.length + acrossInformation.xiao.length) * 13.5 + (5.55 * acrossInformation.pai) + 25 + (b * 4.5) + (13.5 * i) + "%";
            pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#across-pai").append(pai);
        }
    }

}


// 显示上家打的字 先添加到数组中、再显示
function leftDaZi(zi) {


    // leftInformation.chuPai.push(zi);

    var image = new Image();
    //设置图片样式
    image.src = "img/" + zhuanhuan[zi] + ".png";
    image.style.width = "78%";
    image.style.height = "92%";
    image.style.top = "-8%";
    image.style.position = "absolute";
    image.style.left = "15%";
    image.style.transform = "rotate(90deg)";
    // image.style.backgroundImage='url(img/自己出牌对家出牌对家碰牌.png)';
    /* clip:rect(0px,0px,0px,0px); */

    var pai = document.createElement("div");
    // 设置背景样式
    pai.style.position = "absolute";

    pai.style.width = "35%";
    pai.style.height = "22%";
    pai.style.background = "url(img/侧家出牌.png)";
    pai.style.backgroundRepeat = "no-repeat";
    pai.style.backgroundSize = "100% 100%";
    pai.id = "leftchupai" + leftInformation.chuPai.length;
    // pai.style.top="30%";
    pai.append(image);

    if (leftInformation.chuPai.length <= 10) {
        pai.style.left = "60%";
        pai.style.top = (leftInformation.chuPai.length * 14 - 44) + "%";
        pai.style.zIndex = "1";
    } else {
        pai.style.left = "28%";
        // pai.style.zIndex=1;

        pai.style.top = ((leftInformation.chuPai.length - 11) * 14 - 30) + "%";
    }

    $("#left-chupai").append(pai);

}

// 显示右边玩家打一张字
function rightDaZi(zi) {
    var image = new Image();
    //设置图片样式
    image.src = "img/" + zhuanhuan[zi] + ".png";
    image.style.width = "78%";
    image.style.height = "92%";
    image.style.top = "-10%";
    image.style.position = "absolute";
    image.style.left = "3%";
    image.style.transform = "rotate(270deg)";
    // image.style.backgroundImage='url(img/自己出牌对家出牌对家碰牌.png)';
    /* clip:rect(0px,0px,0px,0px); */

    var pai = document.createElement("div");
    // 设置背景样式
    pai.style.position = "absolute";

    pai.id = "rightchupai" + rightInformation.chuPai.length;
    pai.style.width = "35%";
    pai.style.height = "22%";
    pai.style.background = "url(img/侧家出牌.png)";
    pai.style.backgroundRepeat = "no-repeat";
    pai.style.backgroundSize = "100% 100%";
    // pai.style.top="30%";
    pai.append(image);

    if (rightInformation.chuPai.length <= 10) {
        pai.style.left = "25%";
        pai.style.bottom = (rightInformation.chuPai.length * 14 - 30) + "%";
        pai.style.zIndex = 20 - rightInformation.chuPai.length;
    } else {
        pai.style.left = "58%";
        // pai.style.zIndex=1;
        pai.style.zIndex = 20 - rightInformation.chuPai.length;
        pai.style.bottom = ((rightInformation.chuPai.length - 10) * 14 - 30) + "%";
    }
    $("#right-chupai").append(pai);
}

// 显示对家打一张字
function acrossDaZi(zi) {

    // acrossInformation.chuPai.push(zi);
    var image = new Image();
    //设置图片样式
    image.src = "img/" + zhuanhuan[zi] + ".png";
    image.style.width = "80%";
    image.style.height = "80%";
    image.style.top = "-10%";
    image.style.position = "absolute";
    image.style.left = "7%";
    // image.style.backgroundImage='url(img/自己出牌对家出牌对家碰牌.png)';
    /* clip:rect(0px,0px,0px,0px); */

    var pai = document.createElement("div");
    // 设置背景样式
    pai.style.position = "absolute";
    if (acrossInformation.chuPai.length <= 10) {
        pai.style.right = acrossInformation.chuPai.length * 15.3 - 70 + "%";
    } else {
        pai.style.top = "63%";
        pai.style.right = (acrossInformation.chuPai.length - 11) * 15.3 - 54.7 + "%";
    }
    pai.style.width = "16%";
    pai.style.height = "88%";
    pai.id = "acrosschupai" + acrossInformation.chuPai.length;
    pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
    pai.style.backgroundRepeat = "no-repeat";
    pai.style.backgroundSize = "100% 100%";
    pai.append(image);
    $("#across-chupai").append(pai);

}

//显示自己摸上来的一张牌，并将摸得牌加入到手牌数组中
function moPai(moDePai) {

    myInformation.pai.push(moDePai);
    var image = new Image();
    image.src = "img/" + zhuanhuan[moDePai] + ".png";
    image.id = myInformation.pai.length - 1;
    image.style.right = "10%";
    image.style.width = "12%";
    image.style.height = "140%";
    image.style.backgroundImage = 'url(img/自己手牌.png)';
    if (moDePai == roomInformation.laizi) {

        var yellowPai = document.createElement("div");
        yellowPai.style.position = "absolute";
        yellowPai.style.width = "11.6%";
        yellowPai.style.height = "130%"
        yellowPai.style.backgroundColor = "yellow";
        yellowPai.style.right = "10.3%";
        yellowPai.style.top = "8%";
        yellowPai.style.borderRadius = "6%";
        yellowPai.style.opacity = "0.3";
        yellowPai.ondblclick = function () {
            doubleClick(myInformation.pai.indexOf(moDePai))
        };
        yellowPai.onclick = function () {
            clickPai(myInformation.pai.indexOf(moDePai))
        };
        $("#chupaiqu").append(image);
        $("#chupaiqu").append(yellowPai);

    } else {
        image.ondblclick = function () {
            doubleClick(this.id)
        };
        image.onclick = function () {
            clickPai(this.id)
        };
        $("#chupaiqu").append(image);
    }
}

//页面中间指示当前出牌人
function chuPaiRen(idString) {
    // idString取值为
    // "#leftplayer";     // "#rightplayer";
    // "#acrossplayer";   // "#myplayer";
    $("#myChuPai").css("display", "none");
    $("#acrossChuPai").css("display", "none");
    $("#leftChuPai").css("display", "none");
    $("#rightChuPai").css("display", "none");
    var id = idString.split("p")[0] + "ChuPai";
    $(id).css("display", "block");
}

// 显示自己出的牌
function woChuPai(daPai) {
    // myInformation.chuPai.push(myInformation.pai[dapai]);
    var image = new Image();
    //设置图片样式
    image.src = "img/" + zhuanhuan[daPai] + ".png";
    image.style.width = "80%";
    image.style.height = "80%";
    image.style.top = "-10%";
    image.style.position = "absolute";
    image.style.left = "7%";
    // image.style.backgroundImage='url(img/自己出牌对家出牌对家碰牌.png)';
    /* clip:rect(0px,0px,0px,0px); */

    var pai = document.createElement("div");
    // 设置背景样式
    pai.style.position = "absolute";
    if (myInformation.chuPai.length <= 10) {
        pai.style.left = myInformation.chuPai.length * 9.5 - 9.5 + "%";
    } else {
        pai.style.top = "45%";
        pai.style.left = (myInformation.chuPai.length - 11) * 9.5 + "%";
    }
    pai.style.width = "10%";
    pai.style.height = "69%";
    pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
    pai.style.backgroundRepeat = "no-repeat";
    pai.style.backgroundSize = "100% 100%";
    pai.id = "mychupai" + myInformation.chuPai.length;

    pai.append(image);
    $("#myDiscard").append(pai);
}

function woPeng() {
    var newImg = function (imgUrl) {
        //设置图片样式
        var image = new Image();
        image.src = imgUrl
        image.style.width = "80%";
        image.style.height = "80%";
        image.style.top = "-5%";
        image.style.position = "absolute";
        image.style.left = "7%";
        return image
    }
    for (var i = 0; i < myInformation.xiao.length; i++) {
        for (var b = 0; b < 3; b++) {
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.position = "absolute";
            pai.style.top = "100%";
            pai.style.left = ((i * 35) + (b * 11)) + "%";
            pai.style.width = "11.5%";
            pai.style.height = "53%";
            var paiNo=myInformation.xiao[i]
            if(Rule.AnGangHide && myInformation.hideAnGangSet.has(paiNo)){
                pai.style.background = "url(img/自己杠牌.png)";
            }else{
                pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
                var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
                pai.append(image);
            }
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            $("#myPengXiao").append(pai);
        }
        {
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.position = "absolute";
            pai.style.top = "89.5%";
            pai.style.left = (i * 35) + 11 + "%";
            pai.style.width = "11.5%";
            pai.style.height = "51.5%";
            var paiNo=myInformation.xiao[i]
            if(Rule.AnGangHide && myInformation.hideAnGangSet.has(paiNo)){
                pai.style.background = "url(img/自己杠牌.png)";
            }else{
                pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
                var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
                pai.append(image);
            }
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            $("#myPengXiao").append(pai);
        }
    }
    for (var i = 0; i < myInformation.peng.length; i++) {
        for (var b = 0; b < 3; b++) {
            var image = newImg("img/" + zhuanhuan[myInformation.peng[i]] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.position = "absolute";

            pai.style.top = "100%";
            pai.style.left = ((i * 35) + (b * 11)) + myInformation.xiao.length * 35 + "%";

            pai.style.width = "11.5%";
            pai.style.height = "53%";
            pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#myPengXiao").append(pai);
        }
    }
    //我的吃
    for (var i = 0; i < myInformation.chiArr.length; i++) {
        for (var b = 0; b < 3; b++) {
            var paiNo = myInformation.chiArr[i].paiArr[b]
            var image = newImg("img/" + zhuanhuan[paiNo] + ".png")
            var pai = document.createElement("div");
            // 设置背景样式
            pai.style.position = "absolute";
            pai.style.top = "100%";
            pai.style.left = ((i * 35) + (b * 11)) + (myInformation.xiao.length + myInformation.peng.length) * 35 + "%";
            pai.style.width = "11.5%";
            pai.style.height = "53%";
            pai.style.background = "url(img/自己出牌对家出牌对家碰牌.png)";
            pai.style.backgroundRepeat = "no-repeat";
            pai.style.backgroundSize = "100% 100%";
            pai.append(image);
            $("#myPengXiao").append(pai);
        }
    }
}

function removeChuPai(wanjia) {   //todo
    switch (wanjia) {
        case "mychupai": {
            $("#" + wanjia + myInformation.chuPai.length).remove();
            myInformation.chuPai.splice(myInformation.chuPai.length - 1, 1);
            break;
        }
        case "leftchupai": {
            $("#" + wanjia + leftInformation.chuPai.length).remove();
            leftInformation.chuPai.splice(leftInformation.chuPai.length - 1, 1);
            break;
        }
        case "rightchupai": {
            $("#" + wanjia + rightInformation.chuPai.length).remove();
            rightInformation.chuPai.splice(rightInformation.chuPai.length - 1, 1);
            break;
        }
        case "acrosschupai": {
            $("#" + wanjia + acrossInformation.chuPai.length).remove();
            acrossInformation.chuPai.splice(acrossInformation.chuPai.length - 1, 1);
            break;
        }
    }
}

function showLaiGen() {
    var imglaizi = new Image();
    imglaizi.src = "img/" + zhuanhuan[roomInformation.laiGen] + ".png";
    imglaizi.style.position = "absolute";
    imglaizi.style.width = "100%";
    imglaizi.style.height = "100%";
    imglaizi.style.left = "0%";
    $("#showlaizi").append(imglaizi);
    var LaiGenText = document.createElement("div");
    LaiGenText.style.position = "absolute";
    LaiGenText.style.top = "-20%";
    LaiGenText.style.left = "25%";
    LaiGenText.append("赖 根");
    LaiGenText.style.fontSize = "1.2em";
    $("#showlaizi").append(LaiGenText);
}

function showRoomId(roomId) {
    showDuiJuShu();
}

function showDuiJuShu() {
    $("#duijushu").html("底分 : " + roomInformation.diFen + "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp对局数 : " + roomInformation.playedTurn + "/" + roomInformation.sumTurn + "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp余牌 : " + roomInformation.yuPaiSum + "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp积分 : " + myInformation.jiFen + " &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp房间号 : " + roomInformation.roomId);
}

// 雷帅加
leftInformation.showPai = leftPai;
rightInformation.showPai = rightPai;
acrossInformation.showPai = acrossPai;
leftInformation.daZi = leftDaZi;
rightInformation.daZi = rightDaZi;
acrossInformation.daZi = acrossDaZi;
