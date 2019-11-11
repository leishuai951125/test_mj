package leishuai.lsmj.ws.service.impl;


import leishuai.lsmj.ws.bean.*;
import leishuai.lsmj.ws.controller.WebSocket;
import leishuai.lsmj.ws.service.AccountService;
import leishuai.lsmj.ws.service.ConnectService;
import leishuai.lsmj.ws.service.RoomService;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
     第一次连接，或者重开一句都用这个方法
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
        room = getRoom(player,roomId,diFen);
        if(room==null){
            return false;
        }
        //设置具体的房间信息
        if(roomId==null){ //公共房
            room.setDiFen(diFen);
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
    private Room getRoom(Player player,Long roomId,Integer diFen) throws LsmjException {
        if(roomId!=null){ //获取私人房, todo 房号可能会不合法
            return null;
        }else if(diFen!=null){ //获取公共房，低分可能不合法
            if ( diFen < 1 || diFen > 5) {
                throw new LsmjException("底分不合法");
            }
            return roomService.getOnePublicRoom(diFen);//获取一个有空位的房间
        }else { //获取房间失败
            return null;
        }
    }

    //上线，绑定account，player，ws，session
    @Override
    public void onLine(WebSocket webSocket, Session session, Account account) {
        //        Room room;//房间
//        int seatNo;//座位号
//        Session session;//会话，不需要持久化，因为持久化是为断电恢复使用的，断电后sessino存了也没有作用
//        Account account;//账户信息，该字段不需要持久化,暂时可以不存
//        int account_id;  //账号id
        Player player = account.getPlayer();
        boolean allowMuliplePlayers=true;//允许多玩家登陆
        //加上if也意为者同一个账号只能登陆一次
        if(player==null || allowMuliplePlayers) {//第一次开，player对象不存在
            if(allowMuliplePlayers){//正式环境下去掉
                long id=account.getAccountId();
                String name=account.getUsername();
                account=new Account(){{
                    int imgNo=(int)(Math.random()*12);
                    String url="img/head/"+imgNo+".jpg";
                    setHeadImgUrl(url);
                    setAccountId(id);
                    setUsername(name);
                }};
            }

            //palyer与account绑定
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
