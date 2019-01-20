package leishuai.service.pcm;

import com.sun.org.apache.xalan.internal.xslt.Process;
import leishuai.bean.*;
import leishuai.service.ConnectService;
import leishuai.service.ProcessMsg;
import leishuai.service.impl.ConnectServiceImpl;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/4 1:56
 * @Version 1.0
 */
@Component
public class ProcessC9 {
    ConnectService connectService=new ConnectServiceImpl();
    //对重新开始的处理
    {
        ProcessMsg.map.put("c9", ((jsonObject, player) -> {
            if(player==null){
                return null;
            }

            if(player.getRoom()==null){ //分配房间，默认为底分5的公共房
//                Long roomId = jsonObject.getLong("roomId");
                Integer diFen = jsonObject.getInteger("diFen");//底分
               if(diFen==null){
                   diFen=5;
               }
                connectService.intoRoom(player,null,diFen);
                return ProcessMsg.map.get("c3").processMsg(jsonObject,player);
            }

            //继续原来的房间
            RoomState roomState=player.getRoom().getRoomState();
            PlayerState[]playerStates=roomState.playerStates;
            //当前属于不可响应状态，直接返回null
            if(playerStates[player.getSeatNo()].responseFlag != PlayerState.V.RESP_RESTART){
                return null;
            }
            //可响应
            synchronized (player.getRoom()){
                roomState.responseNum++;
                playerStates[player.getSeatNo()].responseFlag = - PlayerState.V.RESP_RESTART; //已响应重新开始
                if(roomState.responseNum == 4){
                    return onceAgain(player);
                }
            }
            return null;
        }));
    }

    private List<ProcessResult> onceAgain(Player player) {
        Room room=player.getRoom();
        Suggest[] s6_suggest=ProcessC3.getS6(room);
        Suggest[] s7_suggest=ProcessC3.getS7(room.getRoomState(),true);
        List<ProcessResult> resultList=new LinkedList<>();
        for(int i=0;i<4;i++){
            List<Suggest> list=new LinkedList<>();
            list.add(s6_suggest[i]);
            list.add(s7_suggest[i]);
            ProcessResult result=new ProcessResult();
            result.setSuggestList(list);
            result.setSeatNo(i);
            resultList.add(result);
        }
        return resultList;
    }
}
