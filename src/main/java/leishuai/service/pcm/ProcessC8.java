package leishuai.service.pcm;

import leishuai.bean.*;
import leishuai.service.ProcessMsg;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/4 1:55
 * @Version 1.0
 */
@Component
public class ProcessC8 {
    interface V {
        String HE_JU_STRING = "he_ju";
    }

    {
        //对最后四张的处理
        ProcessMsg.map.put("c8", ((jsonObject, player) -> {
            RoomState roomState = player.getRoom().getRoomState();
            PlayerState[] playerStates = roomState.playerStates;
            //当前属于不可响应状态，直接返回null
            if (playerStates[player.getSeatNo()].responseFlag != PlayerState.V.RESP_LAST_FOUR_CARD) {
                return null;
            }
            //验证通过
            //计算第一个拿牌人，出牌前操作造成的积分变化，不含大小朝天，
            if (player.getSeatNo() == roomState.disCardSeatNo) {
                ProcessC7.jiFenBeforeNotRobbed(player.getRoom(), player.getSeatNo());
            }

            int huMultiple = ProcessC5.checkSelfHu(jsonObject, player);//胡的倍数，0表示不能胡，2屁胡，4黑摸
            synchronized (player.getRoom()) {
                roomState.responseNum++;
                playerStates[player.getSeatNo()].responseFlag = huMultiple;
                if (roomState.responseNum == player.getRoom().getSumPlayer()) {
                    return getC8_Response(player.getRoom());
                }
            }
            return null;
        }));
    }

    private static List<ProcessResult> getC8_Response(Room room) {
        RoomState roomState = room.getRoomState();
        int huSeatNo = getHuSeatByOrder(room);
        if (huSeatNo != -1) { //有人胡
            Player player = room.getPlayers()[huSeatNo];
            roomState.zhuang = huSeatNo;//能胡则为庄
            int huMultiple = roomState.playerStates[huSeatNo].responseFlag;
            ProcessC5.jiFenAfterSelfHu(player, roomState, huMultiple);//计算自摸后的输赢
            List<ProcessResult> resultList = ProcessC5.getHuBySelfMsg(player, roomState, huMultiple);
            return resultList;
        }
        return getHeJuMsg(room);//和局
    }

    private static List<ProcessResult> getHeJuMsg(Room room) {
        Player player = room.getPlayers()[0];
        Suggest s9_suggest = ProcessC5.getS9(player, V.HE_JU_STRING, room.getRoomState(), null, -1);
        Suggest s10_sugget = ProcessC5.getS10(room.getRoomState());
        List<ProcessResult> resultList = new ArrayList<ProcessResult>(4);
        for (int i = 0; i < room.getSumPlayer(); i++) {
            ProcessResult result = new ProcessResult();
            result.setSeatNo(i);
            result.setSuggestList(new ArrayList<Suggest>(2) {{
                add(s10_sugget);
                add(s9_suggest);
            }});
            resultList.add(result);
        }
        return resultList;
    }

    //根据拿牌次序获取胡牌人,-1 没人胡
    private static int getHuSeatByOrder(Room room) {
        RoomState roomState = room.getRoomState();
        PlayerState[] playerStates = roomState.playerStates;
        int disCardSeatNo = roomState.disCardSeatNo;
        for (int i = 0; i < room.getSumPlayer(); i++) {
            int seatNo = (disCardSeatNo + i) % room.getSumPlayer();
            int responseValue = playerStates[seatNo].responseFlag;
            if (responseValue == 2 || responseValue == 4) {
                return seatNo;
            }
        }
        return -1;
    }
}