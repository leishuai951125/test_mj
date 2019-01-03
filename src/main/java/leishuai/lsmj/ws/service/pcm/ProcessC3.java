package leishuai.lsmj.ws.service.pcm;

import com.alibaba.fastjson.JSONObject;
import leishuai.lsmj.ws.bean.*;
import leishuai.lsmj.ws.service.ProcessMsg;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.*;

/**
 * @Description 为process接口中的map添加方法，要加@component
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
                allInfo.add(getPlayerInfo(allPlayer[i],false));  //添加已有玩家信息
            }
        }
        return allInfo;
    }
     static Map getPlayerInfo(Player player,boolean isEixt){
        Account account=player.getAccount();
        Map playerInfo=new HashMap(4){{
            put("seatNo",player.getSeatNo());
            if(!isEixt){
                put("type","enter");
                put("headImgUrl",account.getHeadImgUrl());
                put("accountId",account.getAccountId());
                put("username",account.getUsername());
            }else {
                put("type","exit");
            }
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

    private static int[] creatLaiZi(int laiGen){  //生成赖根和癞子
//        int laiGen=(int)(Math.random()*27)+1; //1-27
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

    private static Map[] getAllPlayCard(Room room){  //生成牌，并返回生成的手牌信息
        RoomState roomState=room.getRoomState();
        int allCards[]=null;  //值为1-27
        Map []allPlayCard=new Map[4];
        for(int i=0;i<4;i++){
            allPlayCard[i]=new HashMap();
        }
        allCards=creadAllCards();
        int laiGenLaiZi[]=creatLaiZi(allCards[107]); //0赖根，1癞子
        roomState.laiGen=laiGenLaiZi[0];
        roomState.laiZi=laiGenLaiZi[1];
//        RoomState roomState=player.getRoom().getRoomState();
        PlayerState[]playerStates=roomState.playerStates;
        if(room.getCanBeUsedTimes()==Room.V.PUBLIC_ROOM){ //公共房永远为第一局
            roomState.playedTurn=1;
        }else {
            roomState.playedTurn++;
        }
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
            allPlayCard[i].put("playedTurn",roomState.playedTurn);
        }
        List<Integer> roomYupai=roomState.yuPai;
        roomYupai.clear();
        for(int i=52;i<107;i++){ //余牌存room
            roomYupai.add(allCards[i]);
        }
        return allPlayCard;
    }

    static public Suggest[] getS6(Room room) {//发牌，并生成消息
//        RoomState roomState=room.getRoomState();
        Suggest s6_suggest[]=new Suggest[4];  //生成牌
        //指定玩家拿牌
        Map []cardInfo=getAllPlayCard(room);
        for(int i=0;i<4;i++){
            Suggest suggestTemp =new Suggest();
            suggestTemp.setMsgId("s6");
            suggestTemp.setMsgBody(cardInfo[i]);
            s6_suggest[i]= suggestTemp;
        }
        return s6_suggest;
    }

    //出牌人的指定在调用这一方法之前
    static public Suggest[] getS7(RoomState roomState, boolean isGetCard){//拿牌并指定出牌人
        int seatNo=roomState.disCardSeatNo;
        Suggest s7_suggest[]=new Suggest[4];
        int paiNo=RoomState.V.NO_CARD;
        List<Integer> roomYuPai=roomState.yuPai;
        if(isGetCard){  //拿牌操作
            paiNo=roomYuPai.get(0);
            roomYuPai.remove(0);
            //碰后拿牌也是++
            roomState.playerStates[seatNo].cardArr[paiNo]++;
        }
        Map disCardBody=new HashMap();
        disCardBody.put("seatNo",seatNo);
        disCardBody.put("paiNo",paiNo);
        disCardBody.put("yuPaiSum",roomYuPai.size());
        Map notDisCardBody=new HashMap();
        notDisCardBody.put("seatNo",seatNo);
        notDisCardBody.put("paiNo",RoomState.V.NOT_YOU_DISCARD);
        notDisCardBody.put("yuPaiSum",roomYuPai.size());
        for(int i=0;i<4;i++){
            if(i==seatNo){ //是出牌人
                s7_suggest[i]=new Suggest(){{
                    setMsgId("s7");
                    setMsgBody(disCardBody);
                }};
            }else { //不是出牌人
                s7_suggest[i]=new Suggest(){{
                    setMsgId("s7");
                    setMsgBody(notDisCardBody);
                }};
            }
        }
        roomState.canDisCard=true;
        roomState.getCardNoBeforeDis=paiNo;
        roomState.playerStates[seatNo].getCardTimes++;
        return s7_suggest;
    }

     {
        ProcessMsg.map.put("c3",((jsonObject, player) -> {
            System.out.println("room:"+player.getRoom()+"  seatNo:"+player.getSeatNo());
            List<ProcessResult> resultList=new ArrayList(4);
            for(int i=0;i<4;i++){
                ProcessResult result=new ProcessResult();
                result.setSeatNo(i);
                resultList.add(result);
            }


            Suggest s4_suggest=new Suggest(){{  //获取已有玩家和房间
                setMsgId("s4");
                setMsgBody(getAllInfo(player)); //获取已有玩家信息和房间信息
            }};
            resultList.get(player.getSeatNo()).getSuggestList().add(s4_suggest);

            Suggest s5_suggest=new Suggest(){{  //新玩家加入
                setMsgId("s5");
                setMsgBody(getPlayerInfo(player,false));
            }};
            for(int i=0;i<4;i++){
                if(i!=player.getSeatNo()){
                    resultList.get(i).getSuggestList().add(s5_suggest);
                }
            }

            if(player.getRoom().getHavePalyerNum()==4){ //游戏开始，给玩家生成牌
                RoomState roomState=player.getRoom().getRoomState();
                Suggest s6_suggest[]=getS6(player.getRoom());  //生成牌,及其对应消息
                //指定玩家拿牌并出牌,第一次默认为庄。
                roomState.disCardSeatNo=roomState.zhuang;
                roomState.beforeGetCard=RoomState.V.NORMAL;
                Suggest s7_suggest[]=getS7(roomState,true);
                for(int i=0;i<4;i++){
                    resultList.get(i).getSuggestList().add(s6_suggest[i]);
                    resultList.get(i).getSuggestList().add(s7_suggest[i]);
                }
//                System.out.println(JSONObject.toJSONString(roomState));
                long start=System.nanoTime()/1000;
//                System.out.println(JSONObject.toJSONString(player.getRoom()));
                System.out.println("json: "+(System.nanoTime()/1000-start));

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

    }
}
