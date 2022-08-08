package leishuai.service.pcm;

import com.alibaba.fastjson.JSONObject;
import leishuai.bean.*;
import leishuai.service.ProcessMsg;
import leishuai.utils.HuPaiByGuide;
import org.springframework.stereotype.Component;


import java.util.*;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/25 5:20
 * @Version 1.0
 */
@Component
public class ProcessC7 {
    interface V {
        String DIAN_XIAO = "dian_xiao";
        String ZHUO_CHONG = "zhuo_chong";
        String ZHUO_CHONG_PI_HU = "zhuo_chong_pi_hu";
        String PENG = "peng";
        String BU_YAO = "bu_yao";
        String CHI = "chi";
    }

    {

//        ProcessC7 processC7=(ProcessC7) AopContext.currentProxy();
//        System.out.println("======================"+processC7);

        //对指令 c7 ，响应出牌的处理，c7三人响应，没加锁，所以要小心处理。先验证，
        // 再存储，最后通过响应人数判断是否进行处理（最后这一步加锁）
        ProcessMsg.map.put("c7", ((jsonObject, player) -> {
            RoomState roomState = player.getRoom().getRoomState();
            PlayerState[] playerStates = roomState.playerStates;
            //当前属于不可响应状态，直接返回null
            if (playerStates[player.getSeatNo()].responseFlag != PlayerState.V.RESP_OTHER_DISCARD) {
                return null;
            }

            int responseValue = c7_isLawful(jsonObject, player, roomState);//不合法自动转成不要
            synchronized (player.getRoom()) {
                roomState.responseNum++;
                playerStates[player.getSeatNo()].responseFlag = responseValue;
                if (roomState.responseNum == player.getRoom().getSumPlayer() - 1) {
                    return getC7_Response(player.getRoom());
                }
            }
            //没有达到三次则仅存储响应结果
            return null;
        }));
    }


    //先看三人响应中有没有捉冲，有则计算输赢，并生成消息
    //没有人捉冲，则先看看出牌前有没有自笑和回头笑，有则需要更新积分
    //再看有没有点笑，有点笑，则指定出牌人，朝天则计算输赢并指定出牌
    //看有没有碰，有则指定出牌人
    //三家都不要，则指定下一出牌人
    private static List<ProcessResult> getC7_Response(Room room) {
        RoomState roomState = room.getRoomState();
        int disCardSeatNo = roomState.disCardSeatNo;//当前的出牌人
        int zhuoChongSeats[] = getZhuoChongSeats(room, disCardSeatNo); //传disCardSeatNo的原因是避免把自己以前的响应统计进去
        if (zhuoChongSeats != null) {//有人捉冲
            return getRobbedMsg(room, disCardSeatNo, zhuoChongSeats);
        }
        jiFenBeforeNotRobbed(room, disCardSeatNo);//计算不被捉时的积分变化，即出牌前操作造成的积分变化，不含大小朝天，
        int dianXiaoSeat = getDianXiaoSeat(room, disCardSeatNo); //点笑的人座位号
        if (dianXiaoSeat != -1) { //有人点笑
            return getDianXiaoMsg(room, disCardSeatNo, dianXiaoSeat);
        }
        int pengSeat = getPengSeat(room, disCardSeatNo);
        if (pengSeat != -1) {//有人碰
            return getPengMsg(room, disCardSeatNo, pengSeat);
        }
        //吃的处理
        int nextSeatNo = (disCardSeatNo + 1) % room.getSumPlayer(); //下一个玩家的座位
        int chiType = getChiType(room, nextSeatNo);
        if (chiType != PlayerState.V.BU_YAO) {//下家吃
            return getChiMsg(room, disCardSeatNo, nextSeatNo, chiType);
        }
        return getNormalMsg(room, disCardSeatNo);
    }

    private static List<ProcessResult> getNormalMsg(Room room, int disCardSeatNo) {
        RoomState roomState = room.getRoomState();
        //修改出牌人
        changeStatusWhenNothing(room, disCardSeatNo);
        List<ProcessResult> resultList = new LinkedList<ProcessResult>();
        Suggest s10_suggest = ProcessC5.getS10(roomState); //积分
        Suggest s7_suggest[] = ProcessC3.getS7(room, true);
        for (int i = 0; i < room.getSumPlayer(); i++) {
            List<Suggest> suggestList = new LinkedList<Suggest>();
            suggestList.add(s10_suggest);
            suggestList.add(s7_suggest[i]);
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;

    }

    //修改三家都不要时的出牌人
    private static void changeStatusWhenNothing(Room room, int disCardSeatNo) {
        RoomState roomState = room.getRoomState();
        int next = 0;
        if (disCardSeatNo == room.getSumPlayer() - 1) {
            next = 0;
        } else {
            next = disCardSeatNo + 1;
        }
        roomState.disCardSeatNo = next;
        roomState.beforeGetCard = RoomState.V.NORMAL;
    }

    private static List<ProcessResult> getPengMsg(Room room, int disCardSeatNo, int pengSeat) {
        RoomState roomState = room.getRoomState();
        changeStatusWhenPeng(room, pengSeat);//修改出牌人
        List<ProcessResult> resultList = new LinkedList<ProcessResult>();
        Suggest s10_suggest = ProcessC5.getS10(roomState); //积分
        Suggest s12_suggest = getS12(pengSeat, roomState.disCardNo);//获取碰的信息
        Suggest s7_suggest[] = ProcessC3.getS7(room, false);

        for (int i = 0; i < room.getSumPlayer(); i++) {
            List<Suggest> suggestList = new LinkedList<Suggest>();
            suggestList.add(s10_suggest);
            suggestList.add(s12_suggest);
            suggestList.add(s7_suggest[i]);
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;
    }

    private static List<ProcessResult> getChiMsg(Room room, int disCardSeatNo, int chiSeatNo, int chiType) {
        RoomState roomState = room.getRoomState();
        changeStatusWhenChi(room, chiSeatNo, chiType, roomState.disCardNo);//修改出牌人
        List<ProcessResult> resultList = new LinkedList<ProcessResult>();
        Suggest s10_suggest = ProcessC5.getS10(roomState); //积分
        Suggest s14_suggest = getS14(chiSeatNo, roomState.disCardNo, chiType);//获取吃的信息
        Suggest s7_suggest[] = ProcessC3.getS7(room, false);
        for (int i = 0; i < room.getSumPlayer(); i++) {
            List<Suggest> suggestList = new LinkedList<Suggest>();
            suggestList.add(s10_suggest);
            suggestList.add(s14_suggest);
            suggestList.add(s7_suggest[i]);
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;
    }

    private static Suggest getS12(int pengSeat, int disCardNo) {
        Suggest s12_suggest = new Suggest();
        s12_suggest.setMsgId("s12");
        Map body = new HashMap();
        body.put("paiNo", disCardNo);
        body.put("seatNo", pengSeat);
        s12_suggest.setMsgBody(body);
        return s12_suggest;
    }

    static int[] getPaiArrByChiType(int disCardNo, int chiType) {
        int[] paiArr = null;
        switch (chiType) {
            case PlayerState.V.CHIZUO: {
                paiArr = new int[]{disCardNo, disCardNo + 1, disCardNo + 2};
                break;
            }
            case PlayerState.V.CHIZHONG: {
                paiArr = new int[]{disCardNo - 1, disCardNo, disCardNo + 1};
                break;
            }
            case PlayerState.V.CHIYOU: {
                paiArr = new int[]{disCardNo - 2, disCardNo - 1, disCardNo};
                break;
            }
        }
        return paiArr;
    }

    private static Suggest getS14(int pengSeat, int disCardNo, int chiType) {
        Suggest s14_suggest = new Suggest();
        s14_suggest.setMsgId("s14");
        Map body = new HashMap();
        body.put("paiNo", disCardNo);
        body.put("seatNo", pengSeat);
        body.put("chiType", chiType - 20); //前后端相差20
        body.put("paiArr", getPaiArrByChiType(disCardNo, chiType));
        s14_suggest.setMsgBody(body);
        return s14_suggest;
    }

    //更新出牌人,和碰信息
    private static void changeStatusWhenPeng(Room room, int pengSeat) {
        RoomState roomState = room.getRoomState();
        //移除原出牌人的出牌信息
        List<Integer> disCardArr = roomState.playerStates[roomState.disCardSeatNo].disCardArr;//原出牌人的出牌数组
        disCardArr.remove(disCardArr.size() - 1);
        //修改碰的人牌信息，以及出牌前操作
        int disCard = roomState.disCardNo;
        int sum = roomState.playerStates[pengSeat].cardArr[disCard];
        //如果有三张，结果为PENG_AND_ONE
        roomState.playerStates[pengSeat].cardArr[disCard] = PlayerState.V.PENG + sum - 2;
        //出牌人要变了
        roomState.disCardSeatNo = pengSeat;
        roomState.beforeGetCard = RoomState.V.PENG;
    }

    //更新出牌人,和碰信息
    private static void changeStatusWhenChi(Room room, int chiSeatNo, int chiType, int disCardNo) {
        int[] paiArr = getPaiArrByChiType(disCardNo, chiType);
        RoomState roomState = room.getRoomState();
        //移除原出牌人的出牌信息
        List<Integer> disCardArr = roomState.playerStates[roomState.disCardSeatNo].disCardArr;//原出牌人的出牌数组
        disCardArr.remove(disCardArr.size() - 1);
        for (int i = 0; i < paiArr.length; i++) {
            if (paiArr[i] != disCardNo) {
                roomState.playerStates[chiSeatNo].cardArr[paiArr[i]]--;
            }
        }
        room.getRoomState().playerStates[chiSeatNo].allChi.add(new PlayerState.Chi(chiType, paiArr));
        //出牌人要变了
        roomState.disCardSeatNo = chiSeatNo;
        roomState.beforeGetCard = chiType;
    }

    private static int getPengSeat(Room room, int disCardSeatNo) {
        RoomState roomState = room.getRoomState();
        for (int i = 0; i < room.getSumPlayer(); i++) {
            if (i != disCardSeatNo && roomState.playerStates[i].responseFlag == RoomState.V.PENG) {
                return i;
            }
        }
        return -1;
    }

    private static int getChiType(Room room, int nextSeatNo) {
        int flag = room.getRoomState().playerStates[nextSeatNo].responseFlag;
        if (flag == PlayerState.V.CHIZUO || flag == PlayerState.V.CHIZHONG || flag == PlayerState.V.CHIYOU) {
            return flag;
        }
        return PlayerState.V.BU_YAO;
    }


    //disCardSeatNo被笑的人，dianxiaoSeat是笑的人，修改积分和出牌人
    private static void changeStatusWhenDianXiao(Room room, int beFuck, int dianXiaoSeat) {
        RoomState roomState = room.getRoomState();
        //移除原出牌人的出牌信息
        List<Integer> disCardArr = roomState.playerStates[roomState.disCardSeatNo].disCardArr;//原出牌人的出牌数组
        disCardArr.remove(disCardArr.size() - 1);

        PlayerState[] playerStates = roomState.playerStates;
        //更新点笑信息
        int disCard = roomState.disCardNo;
        roomState.playerStates[dianXiaoSeat].cardArr[disCard] = PlayerState.V.DIAN_XIAO;
        //出牌人要变了
        roomState.disCardSeatNo = dianXiaoSeat;
        roomState.beforeGetCard = RoomState.V.DIAN_XIAO;

        if (roomState.disCardNo == roomState.laiGen)//小朝天，计算输赢，fuckwho记录为none
        {
            Player fuckerPlayer = room.getPlayers()[dianXiaoSeat];//点笑的玩家，不是被点笑的人
            jiFenAfterXiaoByOther(roomState, fuckerPlayer, beFuck);
            roomState.beforeGetCard = RoomState.V.PENG; //小朝天可以当碰处理
            roomState.fuckWho = RoomState.V.NONE_BEFUCK; //没人被艹
        } else { //普通笑，记录笑得信息
            if(!Rule.HasGangShangPao){ //没有杠上炮，直接计算积分
                Player fuckerPlayer = room.getPlayers()[dianXiaoSeat];//点笑的玩家，不是被点笑的人
                jiFenAfterXiaoByOther(roomState, fuckerPlayer, beFuck);
            }
            roomState.fuckWho = beFuck;
        }
    }

    //修改点笑后的积分和其它信息，并返回消息 todo
    private static List<ProcessResult> getDianXiaoMsg(Room room, int disCardSeatNo, int dianXiaoSeat) {
        RoomState roomState = room.getRoomState();
        changeStatusWhenDianXiao(room, disCardSeatNo, dianXiaoSeat);//修改积分和出牌人
        List<ProcessResult> resultList = new LinkedList<ProcessResult>();
        Suggest s10_suggest = ProcessC5.getS10(roomState); //积分
        Suggest s11_suggest = ProcessC6.getS11(dianXiaoSeat, V.DIAN_XIAO, roomState.disCardNo);//获取笑的信息
        boolean isGetCard = false;
        if (roomState.disCardNo != roomState.laiGen) {//不是小朝天
            isGetCard = true;
        }
        Suggest s7_suggest[] = ProcessC3.getS7(room, isGetCard);

        for (int i = 0; i < room.getSumPlayer(); i++) {
            List<Suggest> suggestList = new LinkedList<Suggest>();
            suggestList.add(s10_suggest);
            suggestList.add(s11_suggest);
            suggestList.add(s7_suggest[i]);
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;
    }

    //获取点笑的人座位号
    private static int getDianXiaoSeat(Room room, int disCardSeatNo) {
        RoomState roomState = room.getRoomState();
        for (int i = 0; i < room.getSumPlayer(); i++) {
            if (i != disCardSeatNo &&
                    roomState.playerStates[i].responseFlag == PlayerState.V.DIAN_XIAO) {
                return i;
            }
        }
        return -1;
    }

    //计算不被捉时的积分变化，包括出牌前操作（点笑、回头笑、自笑，不含大小朝天）
    //disCardSeatNo 是当前出牌人，也是杠或者笑的执行者
    static void jiFenBeforeNotRobbed(Room room, int disCardSeatNo) {
        if(!Rule.HasGangShangPao){ //没有杠上炮就不用计算，因为在杠的时候已经记录了
            return;
        }
        Player disCardPlayer = room.getPlayers()[disCardSeatNo];//当前出牌人
        RoomState roomState = room.getRoomState();
        //计算当前出牌人的出牌前操作造成的积分修改
        if (roomState.beforeGetCard == RoomState.V.ZI_XIAO) { //大朝天的类型已改成normal
            ProcessC6.jiFenAfterXiaoBySelf(roomState, disCardPlayer, 2);//计算大朝天的输赢，翻倍
        } else if (roomState.beforeGetCard == RoomState.V.HUI_TOU_XIAO) { //回头笑
            ProcessC6.jiFenAfterXiaoBySelf(roomState, disCardPlayer, 1);//计算大朝天的输赢，翻倍
        } else if (roomState.beforeGetCard == RoomState.V.DIAN_XIAO) { //点笑，翻倍，朝天不计算
            jiFenAfterXiaoByOther(roomState, disCardPlayer, roomState.fuckWho);
        }
    }

    //计算点笑、小朝天后的输赢，笑翻倍,fuckerPlayer是笑的人，beFuck是被笑的人，小朝天中此值为none_befuck
    public static void jiFenAfterXiaoByOther(RoomState roomState, Player fuckerPlayer, int beFuck) {
        if (beFuck == RoomState.V.NONE_BEFUCK) {
            return;
        }
        int selfNo = fuckerPlayer.getSeatNo();
        PlayerState[] playerStates = roomState.playerStates;
        int selfDisLaiZi = playerStates[selfNo].disLiaZiCount;
        int disLaiZi = 0;//一共漂癞子数癞子
        if(Rule.IsGangFanBei){ //杠翻倍
            disLaiZi=playerStates[beFuck].disLiaZiCount + selfDisLaiZi;
        }
        int multipleI = (int) Math.pow(2, disLaiZi); //2的disLaiZi次方
        int jiFenReduce = multipleI * fuckerPlayer.getRoom().getDiFen();
        playerStates[beFuck].jifen -= jiFenReduce;
        playerStates[selfNo].jifen += jiFenReduce;
    }


    //获取被捉后的消息
    private static List<ProcessResult> getRobbedMsg(Room room, int disCardSeatNo, int[] zhuoChongSeats) {
        RoomState roomState = room.getRoomState();
        jiFenAfterZhuoChong(room, disCardSeatNo, zhuoChongSeats);
        List<ProcessResult> resultList = new LinkedList<ProcessResult>();
        Suggest s10_suggest = ProcessC5.getS10(roomState); //积分
        Suggest s9_suggest = ProcessC6.getS9_robbedXiao(roomState, room.getPlayers()[disCardSeatNo], zhuoChongSeats);
        List<Suggest> suggestList = new LinkedList<Suggest>();
        suggestList.add(s10_suggest);
        suggestList.add(s9_suggest);
        for (int i = 0; i < room.getSumPlayer(); i++) {
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;
    }


    //出牌后的捉冲，更新积分信息
    private static void jiFenAfterZhuoChong(Room room, int disCardSeatNo, int[] zhuoChongSeats) {
        RoomState roomState = room.getRoomState();
        Player player = room.getPlayers()[disCardSeatNo];//当前被捉的人，也是出牌的人
        if(Rule.GameMode==Rule.GameMode_HuangHuang){
            //累加模式，晃晃的玩法
            PlayerState beFuckPlayer=room.getRoomState().playerStates[disCardSeatNo];
            for(int i=0;i<zhuoChongSeats.length;i++){
                int seatNo=zhuoChongSeats[i];
                PlayerState fucker = room.getRoomState().playerStates[seatNo];
                int respFlag=fucker.responseFlag;
                int jiFenReduce=0;
                if(respFlag==PlayerState.V.ZHUO_CHONG){
                    jiFenReduce+=2+fucker.getFanCount()+beFuckPlayer.getFanCount();
                }else if(respFlag==PlayerState.V.ZHUO_CHONG_PI_HU){
                    jiFenReduce+=1+fucker.getFanCount()+beFuckPlayer.getFanCount();
                }else{
                    continue;
                }
                jiFenReduce*=room.getDiFen();
                ProcessC6.jiFenAfterRobbed(roomState, player, new int[]{seatNo}, jiFenReduce);
            }
            return;
        }

        //累乘模式，干瞪眼里面的玩法
        int beiShu = 1;
        if(Rule.HasGangShangPao){ //有杠上炮需要计算打牌前的动作，以来到底里面的玩法，晃晃走不到这里
            if (roomState.beforeGetCard == RoomState.V.DIAN_XIAO) { //点笑被捉，3
                beiShu = 3;
            } else if (roomState.beforeGetCard == RoomState.V.HUI_TOU_XIAO) { //回头笑被捉，5
                beiShu = 5;
            } else if (roomState.beforeGetCard == RoomState.V.ZI_XIAO) {//自笑被捉，8
                beiShu = 8;
            } else { //普通被捉
                beiShu = 2;
            }
        }
        int jiFenReduce =0;
        jiFenReduce = beiShu * player.getRoom().getDiFen();    //todo 需要管打了几个赖子
        ProcessC6.jiFenAfterRobbed(roomState, player, zhuoChongSeats, jiFenReduce); //计算抢笑后的积分
    }

    //获取捉冲人的数组
    private static int[] getZhuoChongSeats(Room room, int disCardSeatNo) {
        RoomState roomState = room.getRoomState();
        int[] zhuoChongSeats = new int[room.getSumPlayer()];
        int lenth = 0;
        for (int i = 0; i < room.getSumPlayer(); i++) {
            int respFlag=roomState.playerStates[i].responseFlag;
            boolean isZhuoChong= respFlag == PlayerState.V.ZHUO_CHONG || respFlag == PlayerState.V.ZHUO_CHONG_PI_HU;
            if (i != disCardSeatNo && isZhuoChong) {
                zhuoChongSeats[lenth++] = i;
            }
        }
        if (lenth == 0) {
            return null;
        } else {
            return Arrays.copyOf(zhuoChongSeats, lenth);
        }
    }


    //    msgId:"c7",
//    type:"dian_xiao",
//    //当type为捉冲时增加以下两个字段
//    matchMethod:[1,1,1,2],//取值1，2，3，对应'顺','对'，'杠'，
//    actAs:[]  //癞子充当的牌,一赖时此值为空，或者一个元素,多赖为多个元素。黑摸为空
//    dian_xiao
//    zhuo_chong
//    peng
//    bu_yao
    //点笑、碰、捉冲是否合法
    private int c7_isLawful(JSONObject jsonObject, Player player, RoomState roomState) {
        String type = jsonObject.getString("type");
        if (V.BU_YAO.equals(type)) { //不要无条件为真
            return PlayerState.V.BU_YAO;
        }
        int disCardNo = roomState.disCardNo;
        int[] cardArr = roomState.playerStates[player.getSeatNo()].cardArr;
        if (V.DIAN_XIAO.equals(type)) {  //笑
            if (cardArr[disCardNo] == 3 || disCardNo == roomState.laiGen && cardArr[disCardNo] == 2) {
                return PlayerState.V.DIAN_XIAO;
            }
        } else if (V.PENG.equals(type) && (cardArr[disCardNo] == 2 || cardArr[disCardNo] == 3)) { //碰
            return PlayerState.V.PENG;
        } else if (V.CHI.equals(type)) { //todo 吃牌没检查，因为没时间做精细
            int chiType = jsonObject.getInteger("chiType");
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>() {
                {
                    put(1, PlayerState.V.CHIZUO);
                    put(2, PlayerState.V.CHIZHONG);
                    put(3, PlayerState.V.CHIYOU);
                }
            };
            Integer v = map.get(chiType);
            if (v == null) {
                return PlayerState.V.BU_YAO;
            } else {
                return v;
            }
        }
        int paiNo = disCardNo;

        //-----剩下的只有捉冲------

        if (cardArr[roomState.laiZi] > player.getRoom().getMaxLaiZiNum_zhuoChong()) {
            //手上赖子多也不允许胡
            return PlayerState.V.BU_YAO;
        }

        if(Rule.GameMode==Rule.GameMode_HuangHuang){
            if(V.ZHUO_CHONG.equals(type)){
                return PlayerState.V.ZHUO_CHONG;
            }else if(V.ZHUO_CHONG_PI_HU.equals(type)){
                return PlayerState.V.ZHUO_CHONG_PI_HU;
            }
        }else{ //干瞪眼
            if(roomState.laiZiAppeared){ //干瞪眼见到赖子就不能胡
                return PlayerState.V.BU_YAO;
            }
            int paiArr[] = HuPaiByGuide.copyCardArr(cardArr);
            paiArr[paiNo]++;
            if (HuPaiByGuide.isHu(jsonObject, paiArr, 0) == HuPaiByGuide.V.ZHUO_CHONG) {//
                return PlayerState.V.ZHUO_CHONG;
            }
        }
        return PlayerState.V.BU_YAO;
    }
}
