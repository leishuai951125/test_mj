/*
说明：除建立连接的c3外，其它客户端消息均使用标准json格式(文中使用js对象语法描述)
 */
//==================
//建立连接  c3
var c3_param = {  //以下参数是接在ws连接地址后的值
    accountId: 12344,
    token: "tokenString",
    roomId: 234535345, //公共房不需要roomId参数
    diFen: 5  //私人房不需要difen
}
//===================
// JSESSIONID 0970F763D7AA4B055C942C6B7EBA2227
// smidV2=20180724113452535c269f59bec4b3640ef470c1bb705300b0bd8f7b4ea0250; UN=qq_27854405; ARK_ID=JSb1e21d44fc3562e09843ee6c42e1701bb1e2; uuid_tt_dd=10_28867322970-1540738112551-390550; _ga=GA1.2.2115094341.1541661218; __yadk_uid=rxzwq0F4I1nM3xRE0wjfDlHTsn3t3IT8; UM_distinctid=1687a89230c1fa-01b5207f01bb86-2711d3e-100200-1687a89230d1bd; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=1788*1*PC_VC!5744*1*qq_27854405!6525*1*10_28867322970-1540738112551-390550; dc_session_id=10_1555237282363.254241; yidun_tocken=9ca17ae2e6ffcda170e2e6eea2c573b387a88cee6db0868bb7c44e879e9ebaf73981b2b8b5ef7fb78ffdaab32af0feaec3b92ab1b3a586cb6982e88eb5c45a838a9ba7d54b8fb1a3b8f93b9cb5a391f5738defee9e; SESSION=4e9eb520-5e81-4d46-834d-d5c3e2ab7210; UserName=qq_27854405; UserInfo=d448c01d7b94486d9545e270fd2e24d0; UserToken=d448c01d7b94486d9545e270fd2e24d0; UserNick=qq_27854405; AU=7EF; BT=1557419762574; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1557415954,1557419268,1557419274,1557419690; c_adb=1; aliyun_webUmidToken=T726CCCCE8DC1507B1702FE5624F8A0DEAF9968BED18B8ACA4CC110701D; aliyun_UAToken=117#9STQja9l9RK6AEYidOK9ncycTEIjjAOIEVwgBnFoEtuBZNFOmcjWERlgOIlA8kcuZQM90EV+ASapmt49BEfuAbgGRBfRBkj6mcfzdd8RASGqZIdFBEN6KQmGOBFRBzjumpvFOdf+ASapmt49B+rQOR04n/VzBzjuC6kYiYVVDi6jMkGdAxSx3UDCI+FLTwzh0CIMOQrdPCuiI6fCHfmUAUmOJdAvToLI5CrbBrOK6WehICZRvxAHET+msLn8sc69O4IzBkVg63ehILZOoxSx0PDnIYfUT3JtkCB+BkMz6WehILwyocQx00tnI+fpTMEhiCm+BkVg63ehICMKoc4Siql4wsMjHpwqFAppWSOSLcOOQXwXPWmMLWelo29PXIvda11dr5RcJJ7iAssPJk4WcwbvQwedA5zkKEJgIbfO5XbVLclRnhLFGaBZQh/bYruvwStC/5XTuDye24vStcRF+M9bHdHwtLiwhsFpOtSsuDggB9X9t+QykeIiOZdMFC6D74i75WRMx9RQ/0XmAATxLrXIb1WJWsG09WUM14uaBoqD3qgJscGdWwHtRn5LASp/jivTo0T12Wryk0ceX4BfIDtgDpBl3PA0a2iF9d4T8WS96O9lalM3KTudGR2XSbTR0pfU2QBJkFGkYOIR1wzLUhl2SVlUj93zl1WpRc/d/mK5fKFOPTF3zY5ZgjApDo85UZ9rKcgnMQyru/gL+IMBg7xat9==; dc_tos=pr8xqb; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1557420996; firstDie=1
// BAIDUID=6CAEF48ECF3584421C092FDAAAE06E95:FG=1; PSTM=1532570155; BIDUPSID=BAD9C2C291EE215724D053370910632A; BD_UPN=12314753; BDUSS=owfkt2R2JKZFpzWUtXT2Zkd3gzZGlmZkVWbkN5YUZMfnpIWDRPcEg1V3I0SzVjQVFBQUFBJCQAAAAAAAAAAAEAAADV8y9cyM~V5rXEts~HxbLQ0akAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKtTh1yrU4dcRV; BDSFRCVID=x7KOJeC62w3vqA79vjOUt039deS6RyTTH6aosu4N5JlczN8JTqm8EG0Ptf8g0Ku-jIDvogKKLgOTHULF_2uxOjjg8UtVJeC6EG0P3J; H_BDCLCKID_SF=tR-D_D8aJK-3Hn7gMtTJq4C85aRP-Tv9WDTm_D_XJUjoJxDw546cqJ3QDNrbKltqLj5n-pPKKR7isfKlXloH0nOL5NQdblTT3mkjbn5Gfn02OP5P0T_5DP4syPRrKMRnWNTrKfA-b4ncjRcTehoM3xI8LNj405OTt2LEoD0KJCt-hCv65nt_5tCjMfcKhI62aKDsXI3cBhcqEIL45Tb-KfoWeqjBWhQJtnA80n5RJfoBSMbSj4QoKnQ3XGoZ2PJEBJbZ-PjEfp5nhMJmb67JDMP0qf6A0n5y523i2n3vQpnl8hQ3DRoWXPIqbN7P-p5Z5mAqKl0MLIOkbRO4-TF5D6OLDf5; MCITY=-%3A; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; H_PS_PSSID=1454_21122_18559_28519_28768_28722_28963_28830_28584_28604; delPer=0; BD_CK_SAM=1; PSINO=3; BD_HOME=1; H_PS_645EC=856fltu9BZAMTRMq0Ays5KnRe33yX1V3oDrZ1aDeAUcs7I92gga3P4pfyR4; ZD_ENTRY=baidu; BDSVRTM=18
//==================
//对s7的处理
// 拿一张并出牌 	c4
var c4_msg = {
    msgId: "c4",
    paiNo: 12
}
// 拿一张自摸 	c5
var c5_msg = {
    msgId: "c5",
    type: "pi_hu",
    matchMethod: [1, 1, 1, 2],//取值1，2，3，对应'顺','对'，'杠'，
    actAs: []  //癞子充当的牌,一赖时此值为空，或者一个元素,多赖为多个元素。黑摸为空
    /*
    pi_hu
    hei_mo
     */
}
// 拿一张自笑、回头笑、朝天 	c6
var c6_msg = {
    msgId: "c6",
    /*
    zi_xiao
    hui_tou_xiao
    注：没有点笑
     */
    type: "zi_xiao",
    paiNo: 10
}
//============================

//=========================
// 对s8 的处理
// 点笑、捉冲、碰、不要 c7
var c7_msg = {
    msgId: "c7",
    /*
      dian_xiao
      zhuo_chong
      peng
      bu_yao
      chi
       */
    type: "dian_xiao",
    //当type为捉冲时增加以下两个字段
    matchMethod: [1, 1, 1, 2],//取值1，2，3，对应'顺','对'，'杠'，
    actAs: [],  //癞子充当的牌,一赖时此值为空，或者一个元素,多赖为多个元素。黑摸为空
    //吃类型时有下面字段
    chiType: 1, //1 吃最左 2 吃中间 3 吃最右
}
//==========================

//=========================
var c8_msg = {
    msgId: "c8",
    type: "pi_hu",
    matchMethod: [1, 1, 1, 2],//取值1，2，3，对应'顺','对'，'杠'，
    actAs: []  //癞子充当的牌,一赖时此值为空，或者一个元素,多赖为多个元素。黑摸为空
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
var c9_msg = {
    msgId: "c9",
    roomId: 23,
    diFen: 2
}
//=================================