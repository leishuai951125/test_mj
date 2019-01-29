package leishuai.service.pcm;

import leishuai.bean.Player;
import leishuai.bean.ProcessResult;
import leishuai.bean.Room;
import leishuai.bean.Suggest;
import leishuai.service.ProcessMsg;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/24 19:56
 * @Version 1.0
 */
@Component
public class Process_C3 {
    {
        ProcessMsg.map.put("-c3",((jsonObject, player) -> { //玩家退出
            Room room=player.getRoom();
            if(room.getHavePalyerNum()==room.getSumPlayer()){
                return null;
            }
            Player[] players=room.getPlayers();
            //以下生成消息
            List<ProcessResult> resultList=new ArrayList<ProcessResult>(4);
            Suggest s5_suggest=new Suggest(){{  //玩家退出
                setMsgId("s5");
                setMsgBody(ProcessC3.getPlayerInfo(player,true));
            }};
            for(int i=0;i<room.getSumPlayer();i++){
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
