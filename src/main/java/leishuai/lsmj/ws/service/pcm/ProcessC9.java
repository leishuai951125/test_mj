package leishuai.lsmj.ws.service.pcm;

import leishuai.lsmj.ws.bean.PlayerState;
import leishuai.lsmj.ws.bean.RoomState;
import leishuai.lsmj.ws.service.ConnectService;
import leishuai.lsmj.ws.service.ProcessMsg;
import leishuai.lsmj.ws.service.impl.ConnectServiceImpl;
import org.springframework.stereotype.Component;

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
                Long roomId = jsonObject.getLong("roomId");
                Integer diFen = jsonObject.getInteger("diFen");//底分
               if(diFen==null){
                   diFen=5;
               }
                connectService.intoRoom(player,roomId,diFen);
                return ProcessMsg.map.get("c3").processMsg(jsonObject,player);
            }

            //继续原来的房间  todo 未完待续
            RoomState roomState=player.getRoom().getRoomState();
            PlayerState[]playerStates=roomState.playerStates;
            //当前属于不可响应状态，直接返回null
            if(playerStates[player.getSeatNo()].responseFlag != PlayerState.V.RESP_RESTART){
                return null;
            }
            return null;
        }));
    }
}
