package leishuai.lsmj.ws.service.impl;

import com.alibaba.fastjson.JSONObject;
import leishuai.lsmj.ws.bean.*;
import leishuai.lsmj.ws.service.ProcessMsg;
import leishuai.lsmj.ws.utils.Do;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description 为procell接口中的map添加方法，要加@component
 * 否则静态代码块运行不了
 * @Author leishuai
 * @Date 2018/12/17 19:48
 * @Version 1.0
 */
@Component
public class ProcessC3 {
    private static List<Map> getAllInfo(Player player) { //进入房间时获取房间信息
        Room room = player.getRoom();
        RoomState roomState = room.getRoomState();
        List<Map> allInfo = new ArrayList<Map>(5){{
            add(getRoomInfo(player));  //添加房间信息
        }};
        Player [] allPlayer=player.getRoom().getPlayers();
        for(int i=0;i<4;i++){
            if(allPlayer[i]!=null){
                allInfo.add(getPlayerInfo(allPlayer[i]));  //添加已有玩家信息
            }
        }
        return allInfo;
    }
    private static Map getPlayerInfo(Player player){
        Account account=player.getAccount();
        Map playerInfo=new HashMap(4){{
            put("seatNo",player.getSeatNo());
            put("headImgUrl",account.getHeadImgUrl());
            put("accountId",account.getAccountId());
            put("username",account.getUsername());
        }};
        return playerInfo;
    }

    public static Map getRoomInfo(Player player){
        Room room = player.getRoom();
        RoomState roomState = room.getRoomState();
        Map roomInfo = new HashMap() {{
            put("roomId", room.getRoomId());
            put("diFen", room.getDiFen());
            put("sumTurn", room.getCanBeUsedTimes());
            put("playedTurn", roomState == null ? 0 : roomState.playedTurn);
            put("selfSeatNo",player.getSeatNo());
        }};
        return roomInfo;
    }

    private static int[] creatLaiZi(){  //生成赖根和癞子
        int laiGen=(int)(Math.random()*27)+1; //1-27
        int laiZi=0;
        if(laiGen % 9!=0){
            laiZi=laiGen+1;
        }else {
            laiZi=laiGen-8;
        }
        return new int[]{laiGen,laiZi};
    }
     private static int[] creadAllCards(){
         int allCards[]=new int[108];  //值为1-27
         for(int i=0;i<108;i++){
             allCards[i]=i%27+1;
         }
         for(int i=0;i<108;i++){ //打乱
             int random=(int)(Math.random()*108);
             int temp=allCards[i];
             allCards[i]=allCards[random];
             allCards[random]=temp;
         }
         return allCards;
     }

    private static Map[] getAllPlayCard(RoomState roomState){  //生成牌，并返回生成的手牌信息
        int allCards[]=null;  //值为1-27
        Map []allPlayCard=new Map[4];
        for(int i=0;i<4;i++){
            allPlayCard[i]=new HashMap();
        }
        allCards=creadAllCards();
        int laiGenLaiZi[]=creatLaiZi(); //0赖根，1癞子
//        RoomState roomState=player.getRoom().getRoomState();
        PlayerState[]playerStates=roomState.playerStates;
        for(int i=0;i<4;i++){  //给四个玩家发牌
            int cardArr[]=playerStates[i].cardArr;
            for(int k=0;k<28;k++){ //清空原来的牌
                cardArr[k]=0;
            }
            int returnCardArr[]=new int[13];
            for(int j=0;j<13;j++){
                int temp=allCards[i*13+j];
                cardArr[temp]++;  //存服务器
                returnCardArr[j]=temp;  //发给客户端
            }
            allPlayCard[i].put("allCards",returnCardArr);
            allPlayCard[i].put("laiGen",laiGenLaiZi[0]);
            allPlayCard[i].put("laiZi",laiGenLaiZi[1]);
        }
        List<Integer> roomYupai=roomState.yuPai;
        roomYupai.clear();
        for(int i=52;i<108;i++){ //余牌存room
            roomYupai.add(allCards[i]);
        }
        return allPlayCard;
    }

    static public Msg[] getS6(RoomState roomState) {//发牌，并生成消息
        Msg s6_suggest[]=new Msg[4];  //生成牌
        //指定玩家拿牌
        Map []cardInfo=getAllPlayCard(roomState);
        for(int i=0;i<4;i++){
            Msg msgTemp=new Msg();
            msgTemp.setMsgId("s6");
            msgTemp.setMsgBody(cardInfo[i]);
            s6_suggest[i]=msgTemp;
        }
        return s6_suggest;
    }

    static public Msg[] getS7(RoomState roomState,boolean isGetCard){//拿牌并指定出牌人
        int seatNo=roomState.disCardSeatNo;
        Msg s7_suggest[]=new Msg[4];
        int paiNo=-1;
        if(isGetCard){  //拿牌
            List<Integer> roomYuPai=roomState.yuPai;
            paiNo=roomYuPai.get(0);
            roomYuPai.remove(0);
            roomState.playerStates[seatNo].cardArr[paiNo]++;
        }
        Map disCardBody=new HashMap();
        disCardBody.put("seatNo",seatNo);
        disCardBody.put("paiNo",paiNo);
        Map notDisCardBody=new HashMap();
        notDisCardBody.put("seatNo",seatNo);
        notDisCardBody.put("paiNo",0);
        for(int i=0;i<4;i++){
            if(i==seatNo){ //是出牌人
                s7_suggest[i]=new Msg(){{
                    setMsgId("s7");
                    setMsgBody(disCardBody);
                }};
            }else { //不是出牌人
                s7_suggest[i]=new Msg(){{
                    setMsgId("s7");
                    setMsgBody(notDisCardBody);
                }};
            }
        }
        return s7_suggest;
    }

    static {
        ProcessMsg.map.put("c3",((jsonObject, player) -> {
            List<ProcessResult> resultList=new ArrayList(4);
            for(int i=0;i<4;i++){
                ProcessResult result=new ProcessResult();
                result.setSeatNo(i);
                resultList.add(result);
            }

            Msg s4_suggest=new Msg(){{  //获取已有玩家和房间
                setMsgId("s4");
                setMsgBody(getAllInfo(player)); //获取已有玩家信息和房间信息
            }};
            resultList.get(player.getSeatNo()).getMsgList().add(s4_suggest);

            Msg s5_suggest=new Msg(){{  //新玩家加入
                setMsgId("s5");
                setMsgBody(getPlayerInfo(player));
            }};
            for(int i=0;i<4;i++){
                if(i!=player.getSeatNo()){
                    resultList.get(i).getMsgList().add(s5_suggest);
                }
            }

            if(player.getRoom().getHavePalyerNum()==4){ //游戏开始，给玩家生成牌
                RoomState roomState=player.getRoom().getRoomState();
                Msg s6_suggest[]=getS6(roomState);  //生成牌,及其对应消息
                //指定玩家拿牌
                Msg s7_suggest[]=getS7(roomState,true);
                for(int i=0;i<4;i++){
                    resultList.get(i).getMsgList().add(s6_suggest[i]);
                    resultList.get(i).getMsgList().add(s7_suggest[i]);
                }
            }else {
                Player player1[]=player.getRoom().getPlayers();
                Iterator<ProcessResult> iterator=resultList.iterator();
               for(int i=0;i<4;i++){
                   iterator.next();
                   if(null == player1[i]){
                       iterator.remove();
                   }
               }
            }
            return resultList;
        }));

        //对指令 c4 的处理
        /**
         * // 拿一张并出牌 	c4
         * var c4_msg={
         *     msgId:"c4",
         *     msgBody:{
         *         paiNo:12
         *     }
         * }
         */
////        ProcessMsg.map.put("c4",((jsonObject, player) -> {
////            PlayerState ps=player.getPlayerState();
////            RoomState rs=player.getRoom().getRoomState();
////            if(rs.someOneTurn!=player.getSeatNo()){
////                return null;
////            }
////            rs.someOneTurn=-1; //谁也不出牌
////            String paiNo=jsonObject.getJSONObject("msgBody").getString("paiNo");
////            int choseCard=Integer.parseInt(paiNo);
////            if(ps.cardArr[choseCard]<=0 || ps.cardArr[choseCard]>=5){ //不合法则随机出牌
////                return Do.randomChoseCard(player);
////            }
////            return null;
////
////        }));
        //对指令 c5 的处理
        ProcessMsg.map.put("c5",((jsonObject, player) -> {
          return null;
        }));
    }
}
