package leishuai.lsmj.ws.service.impl;


import leishuai.lsmj.ws.bean.Account;
import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.bean.Room;
import leishuai.lsmj.ws.bean.RoomState;
import leishuai.lsmj.ws.controller.WebSocket;
import leishuai.lsmj.ws.service.ConnectService;
import leishuai.lsmj.ws.service.RoomService;

import javax.websocket.Session;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/17 23:29
 * @Version 1.0
 */
public class ConnectServiceImpl implements ConnectService {
    static RoomService roomService = new RoomServiceImpl();
    @Override
    public void intoPublicRoom(WebSocket webSocket, Session session, Account account, int diFen) { //生成玩家和房间
        //        Room room;//房间
//        int seatNo;//座位号
//        Session session;//会话，不需要持久化，因为持久化是为断电恢复使用的，断电后sessino存了也没有作用
//        Account account;//账户信息，该字段不需要持久化,暂时可以不存
//        int account_id;  //账号id
        Player player = new Player() {{
            setSession(session);
            setAccount(account);
            setAccountId(account.getAccountId());
        }};
        webSocket.setPlayer(player);
        account.setPlayer(player);
//        long roomId;//持久化用
//        int creatorId; //创建者的account账户id
//        int diFen=5;//底分 1，2，5
//        int canBeUsedTimes=V.PUBLIC_ROOM;//能被使用的次数，默认值为V.PUBLIC_ROOM -1 表示公共房，不限次数
//        int havePalyerNum=0;//已有玩家人数，分配房间时用,为 0 表示未使用
//        Player[] players=new Player[4];  //若房间以json字符串方式持久化，则存此字段，否则不存。
//        RoomState roomState=null;
        Room room = roomService.getOnePublicRoom(diFen);//进入房间,未持久化
        //设置具体的房间信息
        synchronized (room) {  //此时房间为临界资源
            room.setDiFen(diFen);
            Player[] players = room.getPlayers();
            int seatNo = -1;
            for (int i = 0; i < 4 && seatNo == -1; i++) {
                if (players[i] == null) {
                    players[i] = player;
                    seatNo = i;
                }
            }
            player.setRoom(room);
            player.setSeatNo(seatNo);
            room.getPlayers()[seatNo]=player;
        }

    }


    /**
     * @Description TODO 游戏状态恢复
     * @auther: leishuai
     * @date: 2018/12/18 13:18
     */
    @Override
    public void stateRecovery(WebSocket webSocket, Session session, Account account) {
    }

    /**
     * @Description TODO 进入私人房
     * @auther: leishuai
     * @date: 2018/12/18 13:18
     */
    @Override
    public boolean intoPrivateRoom(WebSocket webSocket, Session session, Account account, Long roomId) {
        return false;
    }
}
