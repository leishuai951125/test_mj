/*
指令格式定义，鉴于js对象与json格式的高度相似，以及书写方便，以下均使用js对象语法书写，
实际通信需要转成对应的json字符串，如果看不懂，可以在浏览器执行JSON.stringify( jsObject );
如执行 JSON.stringify( frontMsg ),可将fronMsg对象转成对应的json字符串
 */
//前台消息格式，因为前台消息都是单指令，所以前台消息格式等同于单条指令的格式
var frontMsg = suggest = {
    msgId: "",
    msgBody: {  //msgBody可以是值、对象、数组，或者三者继续嵌套
    }
}
//后台消息包含多条指令，是一个指令的数组
var backMsg = [
    //一个suggest数组，包含多条指令
    suggest, suggest
]

//==========================
//对c3处理产生的指令或消息如下
// 后台s4指令，为新加入的玩家分配房间后，给当前用户返回房间信息和已有玩家信息
var s4_suggest = {
    msgId: "s4",
    msgBody: [
        {//房间信息
            roomId:23422,
            diFen:2,
            sumTurn:10,
            playedTurn:0,
            selfSeatNo:3
        },//以下为已有玩家信息，包括自己
        {
            seatNo: 1,
            headImgUrl: "",
            accountId: 2,
            username: "赵进化"
        },
        {
            seatNo: 2,
            headImgUrl: "",
            accountId: 1,
            username: "施庄明"
        }
    ]
}
// 后台s5指令，告诉其它玩家有人加入或者退出
var s5_suggest={
    msgId:"s5",
    msgBody://body虽然是一个数组，但数组可以只有一个元素
        {//新人信息
            /*
            enter
            exit
             */
            type:"enter",
            seatNo: 3,
            headImgUrl: "",
            accountId: 2,
            username: "雷帅"
        }

}
//s6 达到四人每人发13张牌
var s6_suggest={
    msgId:"s6",
    msgBody:{
        laiZi:2,
        laiGen:1,
        allCards:[
            //13张牌的编号
        ]
    }
}
// s7 告诉当前出牌人
var s7_suggest={
    msgId:"s7",
    msgBody:{
        seatNo:3,  //出牌人座位号
        /*
        0 不是自己出牌（此时其实可以不用获取这个属性）
        -1 不拿牌自己出牌
        1-27 拿牌出牌
         */
        paiNo:10,
        //最后四张牌，把自己座位号作为下标，可获取自己拿到的牌，只能响应胡或者不胡。
        //且有lastFourCards字段则无上诉两个字段
        lastFourCards:[2,13,25,6],
        yuPaiSum:30,//余牌总数
    }
}
//对c3的处理产生的服务端消息
//****传给新加入玩家的：
var msg=[
    s4_suggest
]
//或者
var msg=[
    s4_suggest,
    s6_suggest,
    s7_suggest
]
//****传给其它玩家的
var msg=[
    s5_suggest
]
//或者
var msg=[
    s5_suggest,
    s6_suggest,
    s7_suggest
]
//==============================

//==============================
//对c4的处理
//验证是否为当前出牌人，不是则忽略消息，无
// 是出牌人把出牌信息发给另外三个人  	s8
var s8_suggest={
    msgId:"s8",
    msgBody:{
        // /*
        // 出牌类型，此属性过时
        // （1）普通出牌：normal
        // （2）点笑出牌：dian_xiao
        // （3）自笑出牌：zi_xiao
        // （4）回头笑出牌：hui_tou_xiao
        //  (5) 朝天
        //  （6）赖子：lai_zi
        //  */
        // type:"normal",
        paiNo:20,
        seatNo:2
    }
}
//出牌不合法，则随机帮其出牌
//=======================


//======================
//c5
// 验证，胡牌合法，发送所有人输赢信息s10。
var s10_suggest={
    msgId:"s10",
    msgBody:[
        //四个座位号对应的当前最新积分
    ]
}
// 加上是否到达房卡次数，可以加每个人的余牌信息 s9	s10 s9
var s9_suggest={
    magId:"s9",
    msgBody:{
        isOver:false,//达到房间上限，显示积分，确认后自动退出房间，私人房和公共房有区别
        /*
        pi_hu
        zhuo_chong
        lian_chong
        hei_mo
         */
        type:"pi_hu",
        seatNoOfHu:[],//胡牌人的座位号
        seatNoOfBeiHu:2,//被的人座位号，自摸的不用管此属性
        yuPai:[ //四个人的余牌
            [],[],[],[]
        ]
    }
}
//正常的消息格式为：
var msg=[
    s10_suggest,s9_suggest
]
// 胡牌不合法，帮其随机出牌
//===========================


//===========================
// c6（自笑、朝天、回头笑）	 验证，通过则修改积分和输赢s10
// 同时指定该人再拿牌出牌（s6.3，s7）
//当前玩家消息
var msg=[
    s10_suggest,s7_suggest //朝天无s6.3
]
//积分信息s10   自笑信息s11发给另外三人	s10 s11
var s11_suggest={
    msgId:"s11",
    msgBody:{
        /*
        zi_xiao
        dian_xiao
        hui_tou_xiao
         */
        type:"zi_xiao",
        paiNo:13,  //牌编号
        seatNo:2  //笑得人座位号
    }
}
//另外三人收到的消息：
var msg=[
    s10_suggest,s11_suggest
]
// 不合法帮忙随机出牌

// 回头笑，通过则在服务端判断有没有人能胡，无则同上
// 回头笑有人胡，则发送回头笑s11和积分8.3，捉冲给每个人s9
var msg=[
    s11_suggest,s10_suggest,s9_suggest
]
//================================


//==================================
// c7 （点笑、胡、碰、不要）
//未达三人只验证响应合法性，非法时当不要处理，均不响应
// 笑得信息发给所有人s11，还有积分信息s10，并指定为出牌人s6.3 s7
var msg=[
    s11_suggest,s10_suggest,s7_suggest
]
//没要的起，指定下一人拿牌并出牌出牌人
var msg=[
    s7_suggest //有的人没有6.3
]
//达到三人合法回复，推送积分s10，捉冲（热冲，连冲）s9
var msg=[
    s10_suggest,s9_suggest
]
//碰的信息发给所有人s12，并指定出牌s7
var s12_suggest={
    msgId:"s12",
    msgBody:{
        paiNo:12,//碰的牌编号
        seatNo:2 //碰的人座位号
    }
}
//=============================


//=========================
// c9（开始下一局）	未达四人仅存，不响应，无
// 每人13张牌，告诉出牌人，以及出牌人拿牌	s6 s6.3 s7
var msg=[
    s6_suggest,s7_suggest
]








