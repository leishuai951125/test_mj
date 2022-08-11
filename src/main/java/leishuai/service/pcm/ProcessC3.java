package leishuai.service.pcm;

import leishuai.bean.*;
import leishuai.service.ProcessMsg;
import org.springframework.stereotype.Component;

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

    static Map getPlayerInfo(Player player, boolean isEixt) {
        Account account = player.getAccount();
        Map playerInfo = new HashMap(5) {{
            put("seatNo", player.getSeatNo());
            if (!isEixt) {
                put("type", "enter");
                put("headImgUrl", account.getHeadImgUrl());
                put("accountId", account.getAccountId());
                put("username", account.getUsername());
            } else {
                put("type", "exit");
            }
        }};
        return playerInfo;
    }

    public static Map getRoomInfo(Player player) {
        Room room = player.getRoom();
        RoomState roomState = room.getRoomState();
        Map roomInfo = new HashMap() {{
            put("roomId", room.getRoomId());
            put("diFen", room.getDiFen());
            put("sumTurn", room.getCanBeUsedTimes());
            put("playedTurn", roomState == null ? 0 : roomState.playedTurn);
            put("selfSeatNo", player.getSeatNo());
            put("sumPlayer", room.getSumPlayer());
        }};
        return roomInfo;
    }

    private static int[] creatLaiZi(int laiGen) {  //生成赖根和癞子
//        int laiGen=(int)(Math.random()*27)+1; //1-27
        int laiZi = 0;
        if (laiGen % 9 != 0) {
            laiZi = laiGen + 1;
        } else {
            laiZi = laiGen - 8;
        }
        return new int[]{laiGen, laiZi};
    }

    private static int[] creadAllCards(int sumCard) {
        int maxPoint = sumCard / 4; //最大的点数
        int count = Rule.DaiHongzhong ? sumCard + 4 : sumCard;
        int allCards[] = new int[count];
        //发 sumCard 张牌
//        for (int j = 0; j < maxPoint; j++) {
//            for (int i = 0; i < 4; i++) {
//                int index = j * 4 + i;
//                allCards[index] = j + 1;//j+1 是点数
//            }
//        }
        //这种方法更散
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < maxPoint; j++) {
                int index = i * maxPoint + j;
                allCards[index] = j + 1;//j+1 是点数
            }
        }
        //发红中
        if (Rule.DaiHongzhong) {
            for (int i = 0; i < 4; i++) {
                allCards[sumCard + i] = Rule.HongZhongPoint; //红中固定 28 点
            }
        }

        int notRandCount=0; //不打乱的数量，测试时不为 0
        //打乱
        for (int i = notRandCount; i < count; i++) { //测试代码
            int random = (int) (Math.random() * (count-notRandCount))+notRandCount;
            int temp = allCards[i];
            allCards[i] = allCards[random];
            allCards[random] = temp;
        }
        return allCards;
    }

    private static Map[] getAllPlayCard(Room room) {  //生成牌，并返回生成的手牌信息
        RoomState roomState = room.getRoomState();
        PlayerState[] playerStates = roomState.playerStates;
        int sumPlayer = room.getSumPlayer();
        int sumCard = 27*4;

        if (sumPlayer != 4) {
            if(Rule.GameMode==Rule.GameMode_HuangHuang){
                sumCard = 27*4; //todo 改动
            }else{
                sumCard = 18*4;
            }
        }

        int allCards[] = null;
        Map[] allPlayCard = new Map[sumPlayer];
        for (int i = 0; i < sumPlayer; i++) {
            allPlayCard[i] = new HashMap();
        }

        allCards = creadAllCards(sumCard);

        //保证最后一张不是红中
        for (int i = allCards.length - 1; i > 0; i--) {
            if (allCards[i] != Rule.HongZhongPoint) { //不是红中
                int temp = allCards[i];
                allCards[i] = allCards[allCards.length - 1];
                allCards[allCards.length - 1] = temp;
            }
        }

        int laiGenLaiZi[] = creatLaiZi(allCards[allCards.length - 1]); //0赖根，1癞子

        roomState.laiGen = laiGenLaiZi[0];
        roomState.laiZi = laiGenLaiZi[1];
        roomState.playedTurn++;

        Player[] players = room.getPlayers();
        //私人房的第一局历史积分清零，palyer记录历史积分
        if (roomState.playedTurn == 1 && room.getCanBeUsedTimes() != Room.V.PUBLIC_ROOM) {
            for (int i = 0; i < sumPlayer; i++) {
                players[i].setSumJiFen(0);
            }
        }

        for (int i = 0; i < sumPlayer; i++) {
            playerStates[i].jifen = players[i].getSumJiFen();
        }

        for (int i = 0; i < sumPlayer; i++) {  //给四个玩家发牌
            int cardArr[] = playerStates[i].cardArr; //服务端玩家的牌
            int returnCardArr[] = new int[13];
            for (int j = 0; j < 13; j++) {
                int temp = allCards[i * 13 + j]; //temp 是牌的点数
                cardArr[temp]++;  //存服务器
                returnCardArr[j] = temp;  //发给客户端
            }
            allPlayCard[i].put("allCards", returnCardArr);
            allPlayCard[i].put("laiGen", laiGenLaiZi[0]);
            allPlayCard[i].put("laiZi", laiGenLaiZi[1]);
            allPlayCard[i].put("playedTurn", roomState.playedTurn);
        }
        List<Integer> roomYupai = roomState.yuPai;
        roomYupai.clear();
        for (int i = 13 * sumPlayer; i < allCards.length - 1; i++) { //余牌存room  todo 调试
            roomYupai.add(allCards[i]);
        }
        return allPlayCard;
    }

    static public Suggest[] getS6(Room room) {//发牌，并生成消息
//        RoomState roomState=room.getRoomState();
        room.getRoomState().recoverDefault(); //清空原本的房间信息
        int sumPlayer = room.getSumPlayer();

        Suggest s6_suggest[] = new Suggest[sumPlayer];  //生成牌

        Map[] cardInfo = getAllPlayCard(room);
        for (int i = 0; i < sumPlayer; i++) {
            Suggest suggestTemp = new Suggest();
            suggestTemp.setMsgId("s6");
            suggestTemp.setMsgBody(cardInfo[i]);
            s6_suggest[i] = suggestTemp;
        }
        return s6_suggest;
    }

    //出牌人的指定在调用这一方法之前
    static public Suggest[] getS7(Room room, boolean isGetCard) {//拿牌并指定出牌人
        RoomState roomState = room.getRoomState();
        int sumPlayer = room.getSumPlayer();
        int[] lastFourCards = null;

        int seatNo = roomState.disCardSeatNo;
        Suggest s7_suggest[] = new Suggest[sumPlayer];
        int paiNo = RoomState.V.NO_CARD;
        List<Integer> roomYuPai = roomState.yuPai;
        if (isGetCard) {  //拿牌操作
            if (roomState.yuPai.size() == sumPlayer) { //最后四张
                lastFourCards = new int[sumPlayer];
                for (int i = 0; i < sumPlayer; i++) { //四人都拿牌
                    paiNo = roomYuPai.get(i);
                    roomState.playerStates[i].cardArr[paiNo]++;
                    lastFourCards[i] = paiNo;
                }
            } else {
                roomState.playerStates[seatNo].getCardTimes++;
                paiNo = roomYuPai.get(0);
                roomYuPai.remove(0);
                //碰后拿牌也是++
                roomState.playerStates[seatNo].cardArr[paiNo]++;
            }
        }

        Map disCardBody = new HashMap();
        if (lastFourCards != null) {
            disCardBody.put("lastFourCards", lastFourCards);
        } else {
            disCardBody.put("seatNo", seatNo);
            disCardBody.put("paiNo", paiNo);
        }
        disCardBody.put("yuPaiSum", roomYuPai.size());
        Map notDisCardBody = new HashMap();
        if (lastFourCards != null) {
            notDisCardBody.put("lastFourCards", lastFourCards);
        } else {
            notDisCardBody.put("seatNo", seatNo);
            notDisCardBody.put("paiNo", paiNo);
        }
        notDisCardBody.put("yuPaiSum", roomYuPai.size());
        for (int i = 0; i < sumPlayer; i++) {
            if (i == seatNo) { //是出牌人
                s7_suggest[i] = new Suggest() {{
                    setMsgId("s7");
                    setMsgBody(disCardBody);
                }};
            } else { //不是出牌人
                s7_suggest[i] = new Suggest() {{
                    setMsgId("s7");
                    setMsgBody(notDisCardBody);
                }};
            }
        }
        if (lastFourCards != null) { //四人出牌
            for (int i = 0; i < sumPlayer; i++) {
                roomState.playerStates[i].responseFlag = PlayerState.V.RESP_LAST_FOUR_CARD;
                roomState.responseNum = 0;
            }
        } else { //一人出牌
            roomState.canDisCard = true;
            roomState.getCardNoBeforeDis = paiNo;
        }

        return s7_suggest;
    }

    public static Suggest getS4(Player player) {
        Room room = player.getRoom();
        List<Map> allInfo = new ArrayList<Map>(5) {{
            add(getRoomInfo(player));  //添加房间信息
        }};
        Player[] allPlayer = player.getRoom().getPlayers();
        for (int i = 0; i < room.getSumPlayer(); i++) {
            if (allPlayer[i] != null && allPlayer[i].getSeatNo() != -1) {
                allInfo.add(getPlayerInfo(allPlayer[i], false));  //添加已有玩家信息
            }
        }
        Suggest s4_suggest =
                new Suggest() {{  //获取已有玩家和房间
                    setMsgId("s4");
                    setMsgBody(allInfo); //获取已有玩家信息和房间信息
                }};
        return s4_suggest;
    }

    {
        ProcessMsg.map.put("c3", ((jsonObject, player) -> {
            System.out.println("room:" + player.getRoom() + "  seatNo:" + player.getSeatNo());
            int sumPlayer = player.getRoom().getSumPlayer();
            List<ProcessResult> resultList = new ArrayList(sumPlayer);
            for (int i = 0; i < sumPlayer; i++) {
                ProcessResult result = new ProcessResult();
                result.setSeatNo(i);
                resultList.add(result);
            }

            Suggest s4_suggest = getS4(player);
            resultList.get(player.getSeatNo()).getSuggestList().add(s4_suggest);

            Suggest s5_suggest = getS5(player, false);

            for (int i = 0; i < sumPlayer; i++) {
                if (i != player.getSeatNo()) {
                    resultList.get(i).getSuggestList().add(s5_suggest);
                }
            }

            if (player.getRoom().getHavePalyerNum() == sumPlayer) { //游戏开始，给玩家生成牌
                RoomState roomState = player.getRoom().getRoomState();
                Suggest s6_suggest[] = getS6(player.getRoom());  //生成牌,及其对应消息
                //指定玩家拿牌并出牌,第一次默认为庄。
                roomState.disCardSeatNo = roomState.zhuang;
                roomState.beforeGetCard = RoomState.V.NORMAL;
                Suggest s7_suggest[] = getS7(player.getRoom(), true);
                for (int i = 0; i < sumPlayer; i++) {
                    resultList.get(i).getSuggestList().add(s6_suggest[i]);
                    resultList.get(i).getSuggestList().add(s7_suggest[i]);
                }
//                System.out.println(JSONObject.toJSONString(roomState));
                long start = System.nanoTime() / 1000;
//                System.out.println(JSONObject.toJSONString(player.getRoom()));
                System.out.println("json: " + (System.nanoTime() / 1000 - start));

            } else {
                Player player1[] = player.getRoom().getPlayers();
                Iterator<ProcessResult> iterator = resultList.iterator();
                for (int i = 0; i < sumPlayer; i++) {
                    iterator.next();
                    if (null == player1[i]) {
                        iterator.remove();
                    }
                }
            }

            return resultList;
        }));

    }

    public static Suggest getS5(Player player, boolean isEixt) {
        Account account = player.getAccount();
        Map playerInfo = new HashMap(5) {{
            put("seatNo", player.getSeatNo());
            if (!isEixt) {
                put("type", "enter");
                put("headImgUrl", account.getHeadImgUrl());
                put("accountId", account.getAccountId());
                put("username", account.getUsername());
            } else {
                put("type", "exit");
            }
        }};
        return new Suggest() {{  //新玩家加入
            setMsgId("s5");
            setMsgBody(getPlayerInfo(player, false));
        }};
    }

}
