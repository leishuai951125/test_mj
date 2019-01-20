package leishuai.service.impl;


import leishuai.bean.*;
import leishuai.controller.WebSocket;
import leishuai.service.AccountService;
import leishuai.service.ConnectService;
import leishuai.service.RoomService;

import javax.websocket.Session;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/17 23:29
 * @Version 1.0
 */
public class ConnectServiceImpl implements ConnectService {
    static RoomService roomService = new RoomServiceImpl();
    static AccountService accountService=new AccountServiceImpl();
    /**
     第一次连接，或者重开一局都用这个方法
     */
    @Override
    public boolean intoRoom(Player player,Long roomId, Integer diFen) throws LsmjException { //生成玩家和房间
//        long roomId;//持久化用
//        int creatorId; //创建者的account账户id
//        int diFen=5;//底分 1，2，5
//        int canBeUsedTimes=V.PUBLIC_ROOM;//能被使用的次数，默认值为V.PUBLIC_ROOM -1 表示公共房，不限次数
//        int havePalyerNum=0;//已有玩家人数，分配房间时用,为 0 表示未使用
//        Player[] players=new Player[4];  //若房间以json字符串方式持久化，则存此字段，否则不存。
//        RoomState roomState=null;
        Room room = null;//获取一个有空位的房间
        room = getRoom(roomId,diFen);
        if(room==null){
            return false;
        }

        Player[] players = room.getPlayers();
        int seatNo = -1;
        synchronized (room) {  //看具体能占几号位
            for (int i = 0; i < 4 && seatNo == -1; i++) {
                if (players[i] == null) {
                    seatNo = i;
                }
            }
            //玩家与房间绑定
            players[seatNo] = player;

            player.setRoom(room);
            player.setSeatNo(seatNo);
        }
        //玩家集齐，游戏可以开始，全部加入在线玩家
        boolean full=true; //房间是否已满，不能用room.getHavePalyerNum()
        for(int i=seatNo+1;i<4;i++){
            if(players[i]==null){
                full=false;
                break;
            }
        }
        if(full){//加入在线玩家列表
            for(int i=0;i<4;i++){
                Account account=players[i].getAccount();
                accountService.putOnGameAccount(account.getAccountId(),account);
            }
        }
        return true;
    }

    //获取一个可用的房间和座位号
    private Room getRoom(Long roomId,Integer diFen) throws LsmjException {
        if(roomId!=null){ //获取私人房, todo 房号可能会不合法
            Room priRoom=roomService.getOnePrivateRoom(roomId);
            if(priRoom==null){
                throw new LsmjException("房间已满或者不存在");
            }
            return priRoom;
        }else if(diFen!=null){ //获取公共房，低分可能不合法
            if ( diFen < 1 || diFen > 5) {
                throw new LsmjException("底分不合法");
            }
            return roomService.getOnePublicRoom(diFen);//获取一个有空位的房间
        }else { //获取房间失败
            return null;
        }
    }

    private Account copyAccount(Account account){  //复制account，多登陆且换头像时使用
        long id=account.getAccountId();
        String name=account.getUsername();
        account=new Account(){{ //换个头像，并创建ws对象
            int imgNo=(int)(Math.random()*12);
            String url="img/head/"+imgNo+".jpg";
            setHeadImgUrl(url);
            setAccountId(id);
            setUsername(name);
        }};
        return account;
    }

    //上线，绑定account，player，ws，session
    @Override
    public void onLine(WebSocket webSocket, Session session, Account account) {
        //palyer的成员如下：
        //与room关联的   Room room;  int seatNo;
        //与ws关联的   Session session;
        //与account关联的     Account account;     int account_id;
        Player player = account.getPlayer();
        boolean allowMuliplePlayers=true; //允许同一账号多登陆，如果为true，可多称为多个玩家，但不能进行游戏状态恢复
        //账户第一次建ws连接时player为空，此时创建palyer对象
        //如果希望同一账户可以称为多用户，每次建ws创建palyer对象，否则无需创建，进行绑定即可方便做数据恢复。
        if(player==null || allowMuliplePlayers) {//第一次开，或者希望多登陆
            if(allowMuliplePlayers){
                //同一账户多使用时，换头像，为了保留之前前几个玩家的头像，必须复制出新的账户对象
                //主要方便测试阶段使用，正式环境下可去  todo
//                account=copyAccount(account);
            }

            //创建player，并与account双向绑定
            player = new Player();
            player.setAccount(account);
            player.setAccountId(account.getAccountId());
            account.setPlayer(player);
        }

        //player绑定session和ws
        player.setSession(session);
        webSocket.setPlayer(player);
    }

    /**
     * @Description TODO 游戏状态恢复
     * @auther: leishuai
     * @date: 2018/12/18 13:18
     */
    @Override
    public void stateRecovery(WebSocket webSocket, Session session, Account account) {
    }

}
