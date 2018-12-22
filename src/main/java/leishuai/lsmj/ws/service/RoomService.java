package leishuai.lsmj.ws.service;


import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.bean.Room;

public interface RoomService {
    void exitRoom(Player player);
    Room getOnePublicRoom(int diFen);
}
