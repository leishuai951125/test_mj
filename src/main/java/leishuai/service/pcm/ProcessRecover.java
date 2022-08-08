package leishuai.service.pcm;

import leishuai.bean.*;
import leishuai.service.ProcessMsg;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/26 18:22
 * @Version 1.0
 */
@Component
public class ProcessRecover {
    {
        ProcessMsg.map.put("recover", ((jsonObject, player) -> {
            return getRecoverMsg(player);
        }));
    }

    private List<ProcessResult> getRecoverMsg(Player player) {
        List<Suggest> suggestList = new ArrayList<>();
        Suggest s13_suggest = getS13(player);
        suggestList.add(s13_suggest);

        ProcessResult processResult = new ProcessResult();
        processResult.setSeatNo(player.getSeatNo());
        processResult.setSuggestList(suggestList);

        List<ProcessResult> resultList = new LinkedList<ProcessResult>();
        resultList.add(processResult);
        return resultList;
    }

    private static Suggest getS13(Player player) {
        List s13_body = new LinkedList();
        s13_body.add(getRoomMsg(player));
        for (int i = 0; i < player.getRoom().getSumPlayer(); i++) {
            s13_body.add(getPlayerMsg(player.getRoom(), i, player.getSeatNo()));
        }
        Suggest s13_suggest = new Suggest();
        s13_suggest.setMsgId("s13");
        s13_suggest.setMsgBody(s13_body);
        return s13_suggest;
    }

    private static Map getPlayerMsg(Room room, int seatNo, int recoverPlayerSeatNo) {
        Player player = room.getPlayers()[seatNo];
        Account account = player.getAccount();
        PlayerState playerState = room.getRoomState().playerStates[seatNo];
        return new HashMap(10) {{
            put("headImgUrl", account.getHeadImgUrl());
            put("username", account.getUsername());
            put("seatNo", player.getSeatNo());
            put("accountId", account.getAccountId());
            if (seatNo == recoverPlayerSeatNo) {
                put("cardArr", playerState.cardArr);
            } else {
                put("cardArr", getPengAndXiao(playerState.cardArr));
            }
            put("disLiaZiCount", playerState.disLiaZiCount);
            put("disHongZhongCount", playerState.disHongZhongCount);
            put("xiaoFanShu", playerState.getXiaoFanForHuangHuang());
            put("jifen", playerState.jifen);
            put("disCardArr", playerState.disCardArr);
            put("superFlag", playerState.superFlag);
            put("chiArr", playerState.allChi);
        }};
    }

    private static int[] getPengAndXiao(int[] cardArr) {
        int[] pengAndXiao = Arrays.copyOf(cardArr, cardArr.length);
        for (int i = 1; i < 28; i++) {
            if (pengAndXiao[i] > 0 && pengAndXiao[i] <= 4) {
                pengAndXiao[i] = 0;
            } else if (pengAndXiao[i] == PlayerState.V.PENG_AND_ONE) {
                pengAndXiao[i] = PlayerState.V.PENG;
            }
        }
        return pengAndXiao;
    }

    private static int[] getCurrentJiFen(Room room) {
        int sumPlayer = room.getSumPlayer();
        PlayerState[] playerStates = room.getRoomState().playerStates;
        int currentJiFen[] = new int[sumPlayer];//四个玩家本轮积分
        for (int i = 0; i < sumPlayer; i++) {
            currentJiFen[i] = playerStates[i].jifen;
        }
        return currentJiFen;
    }

    private static Map getRoomMsg(Player player) {
        Room room = player.getRoom();
        RoomState roomState = room.getRoomState();
        PlayerState playerState = roomState.playerStates[player.getSeatNo()];
        Map roomMsg = new HashMap(20) {{
            put("selfSeatNo", player.getSeatNo());
            put("roomId", room.getRoomId());
            put("diFen", room.getDiFen());
            put("sumTurn", room.getCanBeUsedTimes());
            put("sumPlayer", room.getSumPlayer());

            put("playedTurn", roomState.playedTurn);
            put("isOver", roomState.isOver);
            put("laiGen", roomState.laiGen);
            put("laiZi", roomState.laiZi);
            put("laiZiApprience", roomState.laiZiAppeared);
            put("yuPaiSum", roomState.yuPai.size());

            put("disCardSeatNo", roomState.disCardSeatNo);
            put("getCardNoBeforeDis", roomState.getCardNoBeforeDis);
            put("canDisCard", roomState.canDisCard);
            put("disCardNo", roomState.disCardNo);
            put("responseFlag", playerState.responseFlag);
            if (playerState.responseFlag == PlayerState.V.RESP_LAST_FOUR_CARD) {
                put("lastFourCards", roomState.yuPai);
            } else if (playerState.responseFlag == PlayerState.V.RESP_RESTART) {
                put("currentJiFen", getCurrentJiFen(room));
                int sumPlayer = room.getSumPlayer();
                PlayerState[] playerStates = room.getRoomState().playerStates;
                int[][] yuPai = new int[sumPlayer][];
                for (int i = 0; i < sumPlayer; i++) {
                    yuPai[i] = playerStates[i].cardArr;
                }
                put("yuPai", yuPai);
            }
        }};
        return roomMsg;
    }
}
