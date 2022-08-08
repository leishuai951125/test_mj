package leishuai.service.pcm;

import com.alibaba.fastjson.JSONObject;
import leishuai.bean.*;
import leishuai.service.ProcessMsg;
import leishuai.utils.HuPai;
import leishuai.utils.HuPaiByGuide;
import org.apache.tomcat.util.digester.RulesBase;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/23 20:18
 * @Version 1.0
 */
@Component
public class ProcessC6 {
    public interface V {  //以下字符串常量都是与客户端交互时使用
        //笑信息
        String HUI_TOU_XIAO = "hui_tou_xiao";
        String ZI_XIAO = "zi_xiao";
        //捉冲字符串
        String ZHUO_CHONG = "zhuo_chong";
        String LIAN_CHONG = "lian_chong";
    }

    //判断是否被抢，如果被抢求出抢回头笑的人座位号
    private static int[] beRobbed(JSONObject jsonObject, Player player, RoomState roomState) {
        if (Rule.GameMode==Rule.GameMode_GanDengYan && roomState.laiZiAppeared) { //癞子出现，所有人都不能胡
            return null;
        }
        int sumPlayer = player.getRoom().getSumPlayer();
        int robbedNo[] = new int[sumPlayer];
        int lenth = 0;
        //回头笑是否被抢，被抢返回序号
        int paiNo = jsonObject.getInteger("paiNo");
        for (int i = 0; i < sumPlayer; i++) {
            int[] temp = roomState.playerStates[i].cardArr;
            //超过胡牌的癞子数限制，不能胡(超过限制黑摸也不能胡)
            if (temp[roomState.laiZi] > player.getRoom().getMaxLaiZiNum_zhuoChong()) {
                continue;
            }
            //下面都是黑的胡牌
            int[] paiArr = HuPaiByGuide.copyCardArr(temp);
            paiArr[paiNo]++;
            if (HuPai.testHu(paiArr)) { //testHu不会改动数组
                robbedNo[lenth++] = i;
            }
        }
        if (lenth == 0) {
            return null;
        } else {
            //去掉多余的数据
            return Arrays.copyOf(robbedNo, lenth);
        }
    }

    //计算被捉后的积分,可能是连冲，在c6中主要计算抢回头笑
//    beRobbed是捉冲人构成的数组，player是被捉的人
    static void jiFenAfterRobbed(RoomState roomState, Player player, int[] beRobbed, int jiFenReduce) {
        PlayerState selfState = roomState.playerStates[player.getSeatNo()];
        for (int i = 0; i < beRobbed.length; i++) {
            roomState.playerStates[beRobbed[i]].jifen += jiFenReduce;
            selfState.jifen -= jiFenReduce;
        }
    }

    //    type:"zi_xiao",
//    paiNo:13,  //牌编号
//    seatNo:2  //笑得人座位号
    public static Suggest getS11(int seatNo, String type, int paiNo) {//获取笑信息，包括所有的笑类型
        return new Suggest() {{
            setMsgId("s11");
            setMsgBody(new HashMap() {{
                put("type", type);
                put("paiNo", paiNo);
                put("seatNo", seatNo);
            }});
        }};
    }

    //获取自动抢笑后的消息
    private static List<ProcessResult> getRobbedXiaoMsg(RoomState roomState, Player player, int[] beRobbed, int paiNo) {
        List<ProcessResult> resultList = new LinkedList<ProcessResult>();
        int jiFenReduce=0;
        if(Rule.GameMode== Rule.GameMode_GanDengYan){
            jiFenReduce = 3 * player.getRoom().getDiFen();
            //todo 需要计算赖子数
        }else{
            jiFenReduce=10;//晃晃固定 10 番
        }
        jiFenAfterRobbed(roomState, player, beRobbed, jiFenReduce); //计算抢笑后的积分
        Suggest s11_suggest = getS11(player.getSeatNo(), V.HUI_TOU_XIAO, paiNo);//获取有人笑的信息
        Suggest s10_suggest = ProcessC5.getS10(roomState); //积分
        Suggest s9_suggest = getS9_robbedXiao(roomState, player, beRobbed);
        List<Suggest> suggestList = new LinkedList<Suggest>();
        suggestList.add(s11_suggest);
        suggestList.add(s10_suggest);
        suggestList.add(s9_suggest);
        for (int i = 0; i < player.getRoom().getSumPlayer(); i++) {
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;
    }

    //判断笑是否合法，对自笑的判断不严谨，
    private static boolean checkXiao(JSONObject jsonObject, RoomState roomState, Player player) {
        int paiNo = jsonObject.getInteger("paiNo");  //笑的牌编号
        String type = jsonObject.getString("type");   //笑得类型
        PlayerState playerState = roomState.playerStates[player.getSeatNo()];
        int paiNum = playerState.cardArr[paiNo]; //牌数量
        if (paiNo == roomState.laiZi) {
            return false;
        }
        if (V.HUI_TOU_XIAO.equals(type)) {//回头笑的判断
            if (roomState.getCardNoBeforeDis == paiNo && paiNum == PlayerState.V.PENG_AND_ONE) {
                return true;
            }
        } else if (V.ZI_XIAO.equals(type)) { //自笑的判断
            if (paiNum == 4 || paiNum == 3 && paiNo == roomState.laiGen) { //数量正确
                if (roomState.getCardNoBeforeDis == paiNo) { //拿牌是自笑的牌
                    return true;
                }
                //不是自笑的牌，则要求当前拿牌次数为1，且不能是碰后出牌，也不能是漂癞子后的出牌
                else if (playerState.getCardTimes == 1 && roomState.beforeGetCard != RoomState.V.DIS_LAI_ZI &&
                        roomState.beforeGetCard != RoomState.V.PENG) {
                    return true;
                } else if (playerState.getCardTimes == 2 && (roomState.beforeGetCard == RoomState.V.ZI_XIAO ||
                        roomState.beforeGetCard == RoomState.V.DIAN_XIAO)) {
                    return true;
                }
            }
        }
        return false;
    }

    //获取被捉的信息，c6中为抢回头笑
    static Suggest getS9_robbedXiao(RoomState roomState, Player player, int[] beRobbed) {
        int[] seatNoOfHu = beRobbed;
        String type = null;
        if (beRobbed.length == 1) {
            type = V.ZHUO_CHONG;
        } else {
            type = V.LIAN_CHONG;
        }
        int seatNoOfBeiHu = player.getSeatNo();
        return ProcessC5.getS9(player, type, roomState, seatNoOfHu, seatNoOfBeiHu);
    }

    //改变笑之后的积分信息和出牌信息
    private static void changeStatusAfterXiaoBySelf(RoomState roomState, Player player, int paiNo, String type) {
        if (V.HUI_TOU_XIAO.equals(type)) {
            roomState.playerStates[player.getSeatNo()].cardArr[paiNo] = PlayerState.V.HUI_TOU_XIAO;
            roomState.beforeGetCard = RoomState.V.HUI_TOU_XIAO;
            if(!Rule.HasGangShangPao){ //没有杠上炮，立即计算积分
                jiFenAfterXiaoBySelf(roomState, player, 1);//计算输赢
            }
        } else {
            if (paiNo == roomState.laiGen) //朝天类不存在热冲，所以可以修改输赢信息，然后当普通出牌处理
            {
                roomState.beforeGetCard = RoomState.V.NORMAL;
                jiFenAfterXiaoBySelf(roomState, player, 2);//计算大朝天的输赢，翻倍
            } else { //普通的自笑
                if(!Rule.HasGangShangPao){ //没有杠上炮，立即计算积分
                    jiFenAfterXiaoBySelf(roomState, player, 2);//计算输赢
                }
                roomState.beforeGetCard = RoomState.V.ZI_XIAO;
            }
            roomState.playerStates[player.getSeatNo()].cardArr[paiNo] = PlayerState.V.ZI_XIAO;
        }
        roomState.disCardSeatNo = player.getSeatNo();
    }

    private static List<ProcessResult> getXiaoBySelfMsg(RoomState roomState, Player player, int paiNo, String type) {
        changeStatusAfterXiaoBySelf(roomState, player, paiNo, type); //修改自笑或者回头笑后的状态信息
        Suggest s11_suggest = getS11(player.getSeatNo(), type, paiNo);//获取笑的信息
        boolean isGetCard = roomState.laiGen == paiNo ? false : true;
        Suggest s7_suggest[] = ProcessC3.getS7(player.getRoom(), isGetCard);
        List<ProcessResult> resultList = new LinkedList<ProcessResult>();

        Suggest s10_suggest = ProcessC5.getS10(roomState);
        for (int i = 0; i < player.getRoom().getSumPlayer(); i++) {
            List<Suggest> suggestList = new LinkedList<Suggest>();
            suggestList.add(s10_suggest); //无脑更新积分
            suggestList.add(s11_suggest);
            suggestList.add(s7_suggest[i]);
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;
    }

    //计算回头笑、自笑后的输赢（包括大朝天），笑翻倍。
    public static void jiFenAfterXiaoBySelf(RoomState roomState, Player player, int Multiple) {
        int selfNo = player.getSeatNo();
        PlayerState[] playerStates = roomState.playerStates;
        int selfDisLaiZi = playerStates[selfNo].disLiaZiNum;
        for (int i = 0; i < player.getRoom().getSumPlayer(); i++) {
            if (i != selfNo) {
                int disLaiZi=0;
                if(Rule.IsGangFanBei){
                    disLaiZi = playerStates[i].disLiaZiNum + selfDisLaiZi; //一共漂癞子数癞子
                }
                int multipleI = Multiple * (int) Math.pow(2, disLaiZi); //2的disLaiZi次方
                int jiFenReduce = multipleI * player.getRoom().getDiFen();
                playerStates[i].jifen -= jiFenReduce;
                playerStates[selfNo].jifen += jiFenReduce;
            }
        }
    }

    {
        //笑得处理
        ProcessMsg.map.put("c6", ((jsonObject, player) -> {
            RoomState roomState = player.getRoom().getRoomState();
            //此时不允许出牌，或者出牌人不是当前player，则胡罗
            //根据结果进行操作，要加锁，保证了每轮只有一人一次响应,在处理消息时已加锁
            if (!roomState.canDisCard || player.getSeatNo() != roomState.disCardSeatNo) {
                return null;
            }
            roomState.canDisCard = false;

            //数量要看类型，拿牌要看是否是回头笑
            int paiNo = jsonObject.getInteger("paiNo");
            boolean isAbleXiao = checkXiao(jsonObject, roomState, player);//核对数量确定能否笑

            if (!isAbleXiao) {//牌数量不对,或者与拿牌不一致，或者出错，随机出一张
                return ProcessC4.disCardOne(player, roomState, 0);
            }

            //计算不被捉时的积分变化，即出牌前操作造成的积分变化，不含大小朝天，
            //这是上一轮的自笑或者其他行为
            ProcessC7.jiFenBeforeNotRobbed(player.getRoom(), player.getSeatNo());

            String type = jsonObject.getString("type");
            if (V.HUI_TOU_XIAO.equals(type)) {
                int[] beRobbed = beRobbed(jsonObject, player, roomState);//是否被胡,只有回头笑会被胡
                if (beRobbed != null) {
                    return getRobbedXiaoMsg(roomState, player, beRobbed, paiNo);
                }
            }

            //经过上面的操作后，只有可能是自笑（包括朝天），或者不能抢的回头笑，并且已经过合法性验证
            return getXiaoBySelfMsg(roomState, player, paiNo, type);
        }));
    }

}
