package leishuai.service.impl;


import com.alibaba.fastjson.JSON;
import leishuai.bean.*;
import leishuai.service.AccountService;
import leishuai.service.RoomIdService;
import leishuai.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/18 9:47
 * @Version 1.0
 */
@Service
public class RoomServiceImpl implements RoomService {
    static private AccountService accountService=new AccountServiceImpl();
    static private RoomService roomService=new RoomServiceImpl();
    static private RoomIdService roomIdService=new RoomIdServiceImpl();

    //游戏结束后作废的房间对象，只存一百个，重复利用，避免频繁创建和回收房间
    static private Queue<Room> emptyRoomList=new LinkedList<Room>();//最多存100个房间对象

    //已满的房间集合，官方没有提供hash版的并发set，只能用map模拟
    static private ConcurrentHashMap<Room,Object> fullRoomSet=new ConcurrentHashMap<>(1000);
    static {
        new Thread(){
            static final long five_minute=5*40*1000;
//            static final long five_minute=20*1000;
            static final long destory_time=4*five_minute;
            @Override
            public void run(){
                System.out.println("回收开始");
                while(true){
                    try {
                        Thread.sleep(five_minute);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long time=System.currentTimeMillis();
                    for(Map.Entry<Room,Object> entry:fullRoomSet.entrySet()){
                        Room room=entry.getKey();
                        RoomState roomState=room.getRoomState();
                        synchronized (room){
                            System.out.println("time: "+time+"  chaTime:"+(time-roomState.updateTime));
                            if(time-roomState.updateTime>=destory_time){
                                System.out.println("roomId: "+room.getRoomId()+" 解散");
                                roomService.destory(room);
                            }
                        }
                    }
                }
            }
        }.start();
    }
    //    static {
//        new Thread(){
//            @Override
//            public void run(){
//                long start=0,stop=0;
//                boolean fileNo=false;
//                BufferedWriter fileWriter=null;
//                File file=null;
//                while (true){
//                    start=System.currentTimeMillis();
//                    try {
//                        fileNo=!fileNo;
//                        file=new File("d:/majiang/majiang"+fileNo+".txt");
//                        fileWriter=new BufferedWriter(new FileWriter(file));
//                        fileWriter.write(fileNo+"");
//                        fileWriter.newLine();
//                        for(Map.Entry<Room,Object> entry:fullRoomSet.entrySet()){
//                            Room room=entry.getKey();
//                            System.out.println("写入"+room);
//                            fileWriter.write(JSON.toJSONString(room));
//                            fileWriter.newLine();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }finally {
//                        try {
//                            fileWriter.close();
////                            file.renameTo(new File("d:/majiang/success.txt"));
//                            System.out.println("关闭文件");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    stop=System.currentTimeMillis();
//                    try {
//                        sleep(5000-stop+start);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
//    }
    //私人房
    static private ConcurrentHashMap<Long,Room> priRoomSet=new ConcurrentHashMap<>(100);

    static private Room publicRoom[]=new Room[5];//房间分配时的容器
    static private Object pubRoomLock[]={//公共房分配时用的锁
            new Object(),new Object(),new Object(),new Object(),new Object()};
    // 不直接拿pub[i]当锁，是因为pub[i]指向的对象会发生变化，也就是多个线程用的并非同一把锁,有可能
    //多个线程同时进入房间分配的同步块，重点是进入之后还有可能操作的是同一对象，所以不行


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
    public Room createPriRoom(Integer diFen, Integer sumTurn,Integer sumPlayer) {
        Room priRoom=getEmptyRoomObject();
        priRoom.setRoomId(roomIdService.getPriRoomId());
        priRoom.setDiFen(diFen);
        priRoom.setCanBeUsedTimes(sumTurn);
        priRoom.setSumPlayer(sumPlayer);
        priRoomSet.put(priRoom.getRoomId(),priRoom);
        return priRoom;
    }

    @Override
    public Room getOnePrivateRoom(Long roomId) {
        Room room=priRoomSet.get(roomId);
        if(room==null){
            return null;
        }
        synchronized (room){
            int playNum=room.getHavePalyerNum();
            if(playNum==room.getSumPlayer()){
                return null;
            }else {
                room.setHavePalyerNum(playNum+1);
                if(playNum==room.getSumPlayer()-1){
                    priRoomSet.remove(roomId);
                    fullRoomSet.put(room,"");
                }
                return room;
            }
        }
    }

    @Override
    public boolean isPriExist(Long roomId) {
        Room room=priRoomSet.get(roomId);
        if(room==null){
            return false;
        }
        synchronized (room){
            return room.getHavePalyerNum()!=room.getSumPlayer(); //未满返回true
        }
    }

    @Override
    public Room getOnePublicRoom(int diFen) { //获取房间与占位,一定有房间和位置
        Room room=null;
        synchronized (pubRoomLock[diFen-1]){  //获取创建房间和移动房间的权力
            room=publicRoom[diFen-1];
            if(room==null){ //创建房间
                room=publicRoom[diFen-1]=getEmptyRoomObject();
                room.setRoomId(roomIdService.getUURoomId());
                room.setDiFen(diFen);
                room.setCanBeUsedTimes(Room.V.PUBLIC_ROOM);
                room.setSumPlayer(3);
            }
            //占有房间的一个位置
            synchronized (room){//有可能集齐之前有其它玩家退出修改数量。
                //因为涉及到pubromm的条件修改，所以只能放在嵌套同步块里
                int playNum=room.getHavePalyerNum();
                room.setHavePalyerNum(playNum+1);
                if(playNum==room.getSumPlayer()-1){
                    publicRoom[diFen-1]=null;
                    fullRoomSet.put(room,"");
                }
            }
        }
        return room;
    }

    @Override
    public void destory(Room room) { //解散房间
        synchronized (room){
            Player [] players=room.getPlayers();
            PlayerState[]playerStates=room.getRoomState().playerStates;
            for(int i=0;i<room.getSumPlayer();i++){
//                players[i].setSumJiFen(playerStates[i].jifen); //记录总积分
//                Account account=players[i].getAccount();//退出在线账户
//                accountService.removeOnGameAccount(account.getAccountId());
//                if(players[i]!=null){
                    exitRoom(players[i]); //退出房间
//                }
            }
            room.getRoomState().playedTurn=0;
            room.getRoomState().isOver=false;
        }
    }

    @Override
    public void exitRoom(Player player){ //退出房间
        Room room=player.getRoom();
        Player[] players=room.getPlayers();
        //以下退房操作
        synchronized (room){
            Account account=player.getAccount();//退出在线账户
            accountService.removeOnGameAccount(account.getAccountId());

            int hasPlayer=room.getHavePalyerNum();
            room.setHavePalyerNum(hasPlayer-1);
            players[player.getSeatNo()]=null;

            player.setRoom(null);
            player.setSeatNo(-1);
            if(hasPlayer==1){ //房间空了
                fullRoomSet.remove(room);
                //私人房可能还要先从私人房列表里删除,并且归还房号
                if(room.getCanBeUsedTimes()!=Room.V.PUBLIC_ROOM){
                    priRoomSet.remove(room.getRoomId());
                    roomIdService.putPriRoomId(room.getRoomId());
                }
                addEmptyRoomObject(room);
            }
        }
    }


}
