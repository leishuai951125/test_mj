package leishuai.service.pcm;

import com.alibaba.fastjson.JSONObject;
import leishuai.bean.*;
import leishuai.service.ProcessMsg;
import leishuai.service.RoomService;
import leishuai.service.impl.RoomServiceImpl;
import leishuai.utils.HuPaiByGuide;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/23 2:26
 * @Version 1.0
 */
@Component
public class ProcessC5 {
    static RoomService roomService = new RoomServiceImpl();

    public interface V {
        int NOT_HU = 0;//不能胡
        int PI_HU = 2;//屁胡
        int HEI_MO = 4;//黑摸
        String PI_HU_STRING = "pi_hu";
        String HEI_MO_STRING = "hei_mo";
    }

    static int checkSelfHu(JSONObject jsonObject, Player player) {//0不胡，2屁胡，4黑摸
        String type = jsonObject.getString("type");
        RoomState roomState = player.getRoom().getRoomState();
        int cardArr[] = roomState.playerStates[player.getSeatNo()].cardArr;
        if (cardArr[roomState.laiZi] > player.getRoom().getMaxLaiZiNum_ziMo()) { //癞子超出
            return V.NOT_HU;
        }
        int bieShu = HuPaiByGuide.isHu(jsonObject, cardArr, roomState.laiZi);
        if (V.HEI_MO_STRING.equals(type) && bieShu == V.HEI_MO) {
            return V.HEI_MO;
        } else if (V.PI_HU_STRING.equals(type) && bieShu == V.PI_HU) {
            return V.PI_HU;
        } else {
            return V.NOT_HU;
        }
    }

    static void jiFenAfterSelfHu(Player player, RoomState roomState, int huMultiple) {
        int selfNo = player.getSeatNo();
        PlayerState[] playerStates = roomState.playerStates;
        int selfDisLaiZi = playerStates[selfNo].disLiaZiCount;
        if (selfDisLaiZi + playerStates[selfNo].cardArr[roomState.laiZi] == 4) { //胡的人共有四个癞子
            huMultiple = 2 * huMultiple;
        }
        for (int i = 0; i < player.getRoom().getSumPlayer(); i++) {
            if (i != selfNo) {
                int disLaiZi = playerStates[i].disLiaZiCount + selfDisLaiZi; //一共漂癞子数癞子
                int multipleI = huMultiple * (int) Math.pow(2, disLaiZi); //2的disLaiZi次方
                int jiFenReduce = multipleI * player.getRoom().getDiFen();
                playerStates[i].jifen -= jiFenReduce;
                playerStates[selfNo].jifen += jiFenReduce;
            }
        }
    }

    //            msgId:"s10",
//    msgBody:[
//            //四个座位号对应的当前最新积分
//            ]
    public static Suggest getS10(RoomState roomState) {
        PlayerState playerState[] = roomState.playerStates;
        int[] s10_body = new int[4];
        for (int i = 0; i < 4; i++) {
            s10_body[i] = playerState[i].jifen;
        }
        return new Suggest() {{
            setMsgId("s10");
            setMsgBody(s10_body);
        }};
    }

    //获取自摸后的消息
    public static List<ProcessResult> getHuBySelfMsg(Player player, RoomState roomState, int huMultiple) {
        Suggest s10_sugget = getS10(roomState);
//        String type= huMultiple == 2 ? V.PI_HU_STRING : V.HEI_MO_STRING;//倍数决定类型
        String type = null;
        if (huMultiple == 2) {
            type = V.PI_HU_STRING;
        } else {
            type = V.HEI_MO_STRING;
        }
        Suggest s9_suggest = getS9_ziMo(player, roomState, type);
        List<ProcessResult> resultList = new ArrayList<ProcessResult>(4);
        for (int i = 0; i < player.getRoom().getSumPlayer(); i++) {
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(new ArrayList<Suggest>(2) {{
                //必须先10后9
                add(s10_sugget);
                add(s9_suggest);
            }});
            resultList.add(result);
        }
        return resultList;
    }

    // 加上是否到达房卡次数，可以加每个人的余牌信息 s9	s10 s9
//    var s9_suggest={magId:"s9",
//    msgBody:{
//        isOver:false,//达到房间上限，显示积分，确认后自动退出房间，私人房和公共房有区别
////        pi_hu  zhuo_chong lian_chong hei_mo
//                type:"pi_hu",
//                seatNoOfHu:[],//胡牌人的座位号
//        seatNoOfBeiHu:2,//被的人座位号，自摸的不用管此属性
//                yuPai:[ //四个人的余牌
//            [],[],[],[]]
//    }
//}
    private static Suggest getS9_ziMo(Player player, RoomState roomState, String type) {
        //不想传player给getS9，所以写在这里
        int[] seatNoOfHu = {player.getSeatNo()};
        int seatNoOfBeiHu = -1;
        return getS9(player, type, roomState, seatNoOfHu, seatNoOfBeiHu);
    }

    public static Suggest getS9(Player player, String type, RoomState roomState, int[] seatNoOfHu, int seatNoOfBeiHu) {
        PlayerState[] playerStates = roomState.playerStates;
        Player[] players = player.getRoom().getPlayers();
        int sumPlayer = player.getRoom().getSumPlayer();

        if (seatNoOfHu != null) {  //不是和局，则更换庄家
            if (seatNoOfHu.length == 1) {
                roomState.zhuang = seatNoOfHu[0];
            } else {
                roomState.zhuang = seatNoOfBeiHu;
            }
        }

        //发送完消息后还要解散房间
        roomState.isOver = roomState.playedTurn == player.getRoom().getCanBeUsedTimes();
        if (!roomState.isOver) {
            roomState.responseNum = 0;
            for (int i = 0; i < sumPlayer; i++) {
                playerStates[i].responseFlag = PlayerState.V.RESP_RESTART;
            }
        }

        int[][] yuPai = new int[sumPlayer][];
        for (int i = 0; i < sumPlayer; i++) {
            yuPai[i] = playerStates[i].cardArr;
        }

        int currentJiFen[] = new int[sumPlayer];//四个玩家本轮积分
        for (int i = 0; i < sumPlayer; i++) {
            int temp = playerStates[i].jifen;
            //计算本轮积分并存储
            currentJiFen[i] = temp - players[i].getSumJiFen();
            //积分记录到玩家中
            players[i].setSumJiFen(temp);
        }

        Map s9_body = new HashMap(5) {{
            put("isOver", roomState.isOver);
            put("type", type);
            put("seatNoOfHu", seatNoOfHu);
            put("yuPai", yuPai);
            put("seatNoOfBeiHu", seatNoOfBeiHu);
            put("currentJiFen", currentJiFen);
        }};

        return new Suggest() {{
            setMsgId("s9");
            setMsgBody(s9_body);
        }};
    }

    {
        //对指令 c5 的处理,拿一张自摸
        // 拿一张自摸 	c5
//        var c5_msg={
//        msgId:"c5",
//        type:"pi_hu"
//        matchMethod:[1,1,1,2],//取值1，2，3，对应'顺','对'，'杠'，
//        actAs:[]
        /*
        pi_hu
        hei_mo
         */
        ProcessMsg.map.put("c5", ((jsonObject, player) -> {
            RoomState roomState = player.getRoom().getRoomState();
            //此时不允许出牌，或者出牌人不是当前player，则胡罗
            //根据结果进行操作，要加锁，保证了每轮只有一人一次响应
            if (!roomState.canDisCard || player.getSeatNo() != roomState.disCardSeatNo) {
                return null;
            }
            roomState.canDisCard = false;

            long start = System.nanoTime() / 100000;
            int huMultiple = checkSelfHu(jsonObject, player);//胡的倍数，0表示不能胡，2屁胡，4黑摸
            System.out.println(System.nanoTime() / 100000 - start);
            if (huMultiple == 0) {  //不能胡，则随机出牌
                return ProcessC4.disCardOne(player, roomState, 0);
            }
            //计算不被捉时的积分变化，即出牌前操作造成的积分变化，不含大小朝天，
            ProcessC7.jiFenBeforeNotRobbed(player.getRoom(), player.getSeatNo());

            roomState.zhuang = player.getSeatNo();//能胡则为庄
            jiFenAfterSelfHu(player, roomState, huMultiple);//计算自摸后的输赢
            List<ProcessResult> resultList = getHuBySelfMsg(player, roomState, huMultiple);
            return resultList;
        }));
    }
}
