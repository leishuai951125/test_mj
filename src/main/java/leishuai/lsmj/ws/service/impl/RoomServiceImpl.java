package leishuai.lsmj.ws.service.impl;


import leishuai.lsmj.ws.bean.*;
import leishuai.lsmj.ws.service.AccountService;
import leishuai.lsmj.ws.service.RoomService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/18 9:47
 * @Version 1.0
 */
public class RoomServiceImpl implements RoomService {
    static private AccountService accountService=new AccountServiceImpl();

    //游戏结束后作废的房间对象，只存一百个，重复利用，避免频繁创建和回收房间
    static private Queue<Room> emptyRoomList=new LinkedList<Room>();//最多存100个房间对象

    //已满的房间集合，官方没有提供hash版的并发set，只能用map模拟
    static private ConcurrentHashMap<Room,Object> fullRoomSet=new ConcurrentHashMap<>(1000);
    static private Room publicRoom[]=new Room[5];//房间分配时的temp
    static private Object pubRoomLock[]={//公共房分配时用的锁
            new Object(),new Object(),new Object(),new Object(),new Object()};
    // 不直接拿pub[i]当锁，是因为pub[i]指向的对象会发生变化，也就是多个线程用的并非同一把锁,有可能
    //多个线程同时进入房间分配的同步块，重点是进入之后还有可能操作的是同一对象，所以不行

    static private Long maxRoomId=1L; //注意多线程并发，服务器集群时最好改存redis

//    public void setMaxRoomId(long maxRoomId2){
//        synchronized (maxRoomId){
//            maxRoomId=maxRoomId2;
//        }
//    }

    private Room getEmptyRoomObject(){ //获取一个房间对象
        synchronized (emptyRoomList){
            if(emptyRoomList.size()==0){
                return new Room();
            }else {
                return emptyRoomList.poll();
            }
        }
    }
    private boolean addEmptyRoomObject(Room room){//将作废的房间存起来，方便再用
        synchronized (emptyRoomList){
            if(emptyRoomList.size()>100){
                return false;
            }else {
                return emptyRoomList.offer(room);
            }
        }
    }

    @Override
    public Room getOnePublicRoom(int diFen) { //获取房间与占位
        Room room=null;
        synchronized (pubRoomLock[diFen-1]){  //获取创建房间和移动房间的权力
            room=publicRoom[diFen-1];
            if(room==null){ //创建房间
                room=publicRoom[diFen-1]=getEmptyRoomObject();
            }
            //占有房间的一个位置
            synchronized (room){//有可能集齐之前有其它玩家退出修改数量。
                //因为涉及到pubromm的条件修改，所以只能放在嵌套同步块里
                int playNum=room.getHavePalyerNum();
                if(playNum==3){
                    publicRoom[diFen-1]=null;
                    fullRoomSet.put(room,"");
                    if(fullRoomSet.size()==50){
                        long start=System.nanoTime()/1000;
                        System.out.println(fullRoomSet);
                        System.out.println("time:"+(System.nanoTime()/1000-start));
                    }
                }
                room.setHavePalyerNum(playNum+1);
            }
        }
        return room;
    }

    @Override
    public void destory(Room room) { //解散房间
        synchronized (room){
            Player [] players=room.getPlayers();
            PlayerState[]playerStates=room.getRoomState().playerStates;
            for(int i=0;i<4;i++){
                players[i].setSumJiFen(playerStates[i].jifen); //记录总积分
                Account account=players[i].getAccount();//退出在线账户
                accountService.removeOnGameAccount(account.getAccountId());
                exitRoom(players[i]); //退出房间
            }
            room.getRoomState().recoverDefault(); //还原状态
        }
    }

    @Override
    public void exitRoom(Player player){ //退出房间
        Room room=player.getRoom();
        Player[] players=room.getPlayers();
        //以下退房操作
        synchronized (room){
            int hasPlayer=room.getHavePalyerNum();
            room.setHavePalyerNum(hasPlayer-1);
            players[player.getSeatNo()]=null;

            player.setRoom(null);
            player.setSeatNo(-1);
            if(hasPlayer==0){ //房间空了
                fullRoomSet.remove(room);
                addEmptyRoomObject(room);
            }
        }
    }


}
