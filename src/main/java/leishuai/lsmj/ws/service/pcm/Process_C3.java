package leishuai.lsmj.ws.service.pcm;

import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.bean.ProcessResult;
import leishuai.lsmj.ws.bean.Room;
import leishuai.lsmj.ws.bean.Suggest;
import leishuai.lsmj.ws.service.ProcessMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/24 19:56
 * @Version 1.0
 */
public class Process_C3 {
    {
        ProcessMsg.map.put("-c3",((jsonObject, player) -> { //玩家退出
            Room room=player.getRoom();
            if(room.getHavePalyerNum()==4){
                return null;
            }
            Player[] players=room.getPlayers();
            //以下生成消息
            List<ProcessResult> resultList=new ArrayList<ProcessResult>(4);
            Suggest s5_suggest=new Suggest(){{  //玩家退出
                setMsgId("s5");
                setMsgBody(ProcessC3.getPlayerInfo(player,true));
            }};
            for(int i=0;i<4;i++){
                ProcessResult result=null;
                if(players[i]!=null){
                    result=new ProcessResult();
                    result.setSeatNo(i);
                    result.setSuggestList(new ArrayList(){{
                        add(s5_suggest);
                    }});
                    resultList.add(result);
                }
            }
            return resultList;
        }));
    }
}
