package leishuai.lsmj.ws.utils;


import leishuai.lsmj.ws.bean.Msg;
import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.bean.ProcessResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/20 0:38
 * @Version 1.0
 */
public class Do {
    static public List<ProcessResult> randomChoseCard(Player player) {
//        int cardArr[]=player.getPlayerState().cardArr;
        int choseCard = 0;
        for (int i = 1; i <= 27; i++) {
//            if(cardArr[i]>0 && cardArr[i]<5){
//                choseCard=i;
//            }
        }
        return choseCard(choseCard, player);
    }

    static private List<ProcessResult> choseCard(int choseCard, Player player) {
        String typeStringArr[] = {"peng", "dian_xiao", "hui_tou_xiao", "chao_tian", "normal"};
//        int cardArr[]=player.getPlayerState().cardArr;
//        cardArr[choseCard]--;
//        int type=player.getPlayerState().beforeChose;
//        player.getPlayerState().beforeChose=PlayerState.NORMAL;

        List<ProcessResult> resultList = new ArrayList<>(4);
        for (int seatI = 0; seatI < 4; seatI++) {
            ProcessResult processResult = new ProcessResult();
            processResult.setSeatNo(seatI);
            processResult.setMsgList(new ArrayList<Msg>() {{
                add(new Msg() {{
                    setMsgId("s8");
                    setMsgBody(new HashMap() {{
                        put("type", "normal");
                        put("paiNo", choseCard);
                    }});
                }});
            }});
        }
        return null;
    }

}
