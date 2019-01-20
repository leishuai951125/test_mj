package leishuai.service;


import leishuai.bean.Player;
import leishuai.bean.Room;

public interface RoomService {
    void destory(Room room);
    void exitRoom(Player player);
    Room getOnePublicRoom(int diFen);
    Room getOnePrivateRoom(Long roomId);
    boolean isPriExist(Long roomId);
    Room createPriRoom(Integer diFen,Integer sumTurn);
}
