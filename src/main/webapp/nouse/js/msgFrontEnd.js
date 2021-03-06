/*
说明：除建立连接的c3外，其它客户端消息均使用标准json格式(文中使用js对象语法描述)
 */
//==================
//建立连接  c3
var c3_param={  //以下参数是接在ws连接地址后的值
    accountId:12344,
    token:"tokenString",
    roomId:234535345, //公共房不需要roomId参数
    diFen:5  //私人房不需要difen
}
//===================

//==================
//对s7的处理
// 拿一张并出牌 	c4
var c4_msg={
    msgId:"c4",
    paiNo:12
}
// 拿一张自摸 	c5
var c5_msg={
    msgId:"c5",
    type:"pi_hu",
    matchMethod:[1,1,1,2],//取值1，2，3，对应'顺','对'，'杠'，
    actAs:[]  //癞子充当的牌,一赖时此值为空，或者一个元素,多赖为多个元素。黑摸为空
        /*
        pi_hu
        hei_mo
         */
}
// 拿一张自笑、回头笑、朝天 	c6
var c6_msg={
    msgId:"c6",
        /*
        zi_xiao
        hui_tou_xiao
        注：没有点笑
         */
    type:"zi_xiao",
    paiNo:10
}
//============================

//=========================
// 对s8 的处理
// 点笑、捉冲、碰、不要 c7
var c7_msg={
    msgId:"c7",
    type:"dian_xiao",
    //当type为捉冲时增加以下两个字段
    matchMethod:[1,1,1,2],//取值1，2，3，对应'顺','对'，'杠'，
        /*
        dian_xiao
        zhuo_chong
        peng
        bu_yao
         */
}
//==========================

//=========================
var c8_msg={
    msgId:"c8",
    type:"pi_hu",
    matchMethod:[1,1,1,2],//取值1，2，3，对应'顺','对'，'杠'，
    actAs:[]  //癞子充当的牌,一赖时此值为空，或者一个元素,多赖为多个元素。黑摸为空
    /*
    pi_hu
    hei_mo
    not_hu
     */
}
//==========================


//=================================
//对s9的处理
//重新在原房间开始新的一局 c9
var c9_msg={
    msgId:"c9",
    roomId:23,
    diFen:2
}
//=================================