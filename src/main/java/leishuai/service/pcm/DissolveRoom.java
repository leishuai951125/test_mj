package leishuai.service.pcm;

import leishuai.service.ProcessMsg;
import leishuai.service.RoomService;
import leishuai.service.impl.RoomServiceImpl;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/26 18:22
 * @Version 1.0
 */
@Component
public class DissolveRoom {
    static RoomService roomService = new RoomServiceImpl();
    {
        //解散房间
        ProcessMsg.map.put("dissolveRoom", ((jsonObject, player) -> {
            roomService.destory(player.getRoom());
            return null;
        }));
    }
}
