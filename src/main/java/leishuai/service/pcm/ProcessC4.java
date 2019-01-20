package leishuai.service.pcm;

import leishuai.bean.*;
import leishuai.service.ProcessMsg;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/22 23:56
 * @Version 1.0
 */
@Component
public class ProcessC4 {
    //核对出牌合法性，不合法返回一张合法出牌
    private static int checkDisCard(int paiNo, Player player, RoomState roomState) {
//        int paiNo=jsonObject.getInteger("paiNo");
        int paiArr[] = roomState.playerStates[player.getSeatNo()].cardArr;
        //如果不合法，随机选一张
        if (paiArr[paiNo] <= 0 || paiArr[paiNo] > 4 && paiArr[paiNo] != PlayerState.V.PENG_AND_ONE) { //不合法则随机出牌
            for (int i = 1; i < 28; i++) {
                //选一张不是癞子的牌
                if (paiArr[i] == PlayerState.V.PENG_AND_ONE ||
                        paiArr[i] > 0 && paiArr[i] < 4 && paiArr[i] != roomState.laiZi) {
                    paiNo = i;
                    break;
                }
            }
        }
        return paiNo;
    }

    //修改出牌后的变化，包括飘癞子和不漂癞子
    static void changeStatusAfterDisCard(Player player, RoomState roomState, int paiNo) {
        //修改状态信息
        roomState.disCardNo = paiNo;//当前出牌人出的是paiNo
        roomState.playerStates[player.getSeatNo()].cardArr[paiNo]--;//减掉一张牌
        roomState.playerStates[player.getSeatNo()].disCardArr.add(paiNo); //增加一张出牌
        if (paiNo == roomState.laiZi) { //出的是癞子，则癞子数加一
            //计算不被捉时的积分变化，即出牌前操作造成的积分变化，不含大小朝天，
            ProcessC7.jiFenBeforeNotRobbed(player.getRoom(), player.getSeatNo());

            roomState.laiZiAppeared = true;
            jifenAfterDisLaiZi(player, roomState);//与下一句顺序不能交换
            roomState.playerStates[player.getSeatNo()].disLiaZiNum++;
            //重新指定为出牌人
            roomState.disCardSeatNo = player.getSeatNo();
            roomState.beforeGetCard = RoomState.V.DIS_LAI_ZI;
        } else { //不是癞子，将其它三人设置成可响应出牌状态,或者说未响应状态
            for (int i = 0; i < 4; i++) {
                if (i != player.getSeatNo()) {
                    roomState.playerStates[i].responseFlag = PlayerState.V.RESP_OTHER_DISCARD;
                }
            }
            roomState.responseNum = 0;
        }
    }

    //计算漂癞子后的输赢
    private static void jifenAfterDisLaiZi(Player disCardPlayer, RoomState roomState) {
        ProcessC6.jiFenAfterXiaoBySelf(roomState, disCardPlayer, 1);//计算大朝天的输赢，翻倍
    }

    //生成出牌的信息
    private static Suggest getS8(Player player, RoomState roomState, int paiNo) {
        Map s8_body = new HashMap();
        s8_body.put("seatNo", player.getSeatNo());
        s8_body.put("paiNo", paiNo);
        if (paiNo == roomState.laiZi) {
            s8_body.put("type", "lai_zi");
        }
        Suggest s8_suggest = new Suggest() {{
            setMsgId("s8");
            setMsgBody(s8_body);
        }};
        return s8_suggest;
    }

    public static List<ProcessResult> getChuPaiMsg(Player player, RoomState roomState, int paiNo) {
        Suggest s8_suggest = getS8(player, roomState, paiNo);
        Suggest s10_suggest = ProcessC5.getS10(roomState);
        Suggest s7_suggest[] = null;
        if (paiNo == roomState.laiZi) {
            s7_suggest = ProcessC3.getS7(roomState, true);
        }
        List<ProcessResult> resultList = new ArrayList<ProcessResult>(4);
        for (int i = 0; i < 4; i++) {
            List<Suggest> suggestList = new LinkedList<Suggest>();
            suggestList.add(s8_suggest);
            suggestList.add(s10_suggest);
            if (paiNo == roomState.laiZi) {
                suggestList.add(s7_suggest[i]);
            }
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(suggestList);
            resultList.add(result);
        }
        return resultList;
    }

    {
        ProcessMsg.map.put("c4", ((jsonObject, player) -> {
            RoomState roomState = player.getRoom().getRoomState();
            //此时不允许出牌，或者出牌人不是当前player，则胡罗
            //根据结果进行操作，要加锁，保证了每轮只有一人一次响应
            if (!roomState.canDisCard || player.getSeatNo() != roomState.disCardSeatNo) {
                return null;
            }
            roomState.canDisCard = false;
            int paiNo = jsonObject.getInteger("paiNo");
            //出牌不合法会被转成随机出牌
            return disCardOne(player, roomState, paiNo);
        }));
    }

    static public List<ProcessResult> disCardOne(Player player, RoomState roomState, int paiNo) {
        paiNo = checkDisCard(paiNo, player, roomState);
        //更改出牌后的状态
        changeStatusAfterDisCard(player, roomState, paiNo);
        return getChuPaiMsg(player, roomState, paiNo);
    }

}
