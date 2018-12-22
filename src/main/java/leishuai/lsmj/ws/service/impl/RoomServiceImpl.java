package leishuai.lsmj.ws.service.impl;


import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.bean.Room;
import leishuai.lsmj.ws.service.RoomService;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/18 9:47
 * @Version 1.0
 */
public class RoomServiceImpl implements RoomService,Comparator<Room> {

    //已满的房间集合，官方没有提供hash版的set，只能用map模拟
    static private ConcurrentHashMap<Room,Object> fullRoomSet=new ConcurrentHashMap<>(100);
    static RoomServiceImpl roomComparator=new RoomServiceImpl();
    static private TreeSet<Room> publicRoom[]=new TreeSet[5];
    static {
        for(int i=0;i<5;i++){ //第i个对应底注为i+1的空房间，实际只使用0，1，4，即底注1，2，5
            publicRoom[i]=new TreeSet<Room>(roomComparator);
        }
    }
    static private Long maxRoomId=1L; //注意多线程并发，服务器集群时最好改存redis
    //指定比较器，可以认为初始状态room1在前，room2在后，返回值大于一就交换，以下表示降序排列
    @Override
    public int compare(Room o1, Room o2) { //降序
        if(o1.getHavePalyerNum()>=o2.getHavePalyerNum()) {
            return -1;
        }else {
            return 1;
        }
    }

    public void setMaxRoomId(long maxRoomId2){
        synchronized (maxRoomId){
            maxRoomId=maxRoomId2;
        }
    }

    @Override
    public Room getOnePublicRoom(int diFen) { //获取房间与占位
        Room room=null;
        TreeSet<Room> notFullRoomSet=publicRoom[diFen-1];
        synchronized (notFullRoomSet){  //多线程在notFull中获取一个房间，没有就新建一个
            if(!notFullRoomSet.isEmpty()) {
                room=notFullRoomSet.first();
            }
            if(room==null){  //不能漏，因为房间的创建是第二类更新问题，先读后根据读的情况进行操作
                room=new Room();
                notFullRoomSet.add(room);
            }
            int playNum=room.getHavePalyerNum();
            if(playNum==3){ //加上当前获取房间的人就四个了
//                notFullRoomSet.remove(room);
                notFullRoomSet.pollFirst();
                fullRoomSet.put(room,"");
            }
            room.setHavePalyerNum(playNum+1);//执行此步相当于有玩家占领了该房间的一个位置
        }
        return room;
    }

    @Override
    public void exitRoom(Player player) {

    }

    public static void main(String[] args) {

        TreeSet<Room> treeSet=publicRoom[0];
        Room room=new Room();
        System.out.println(treeSet.size());
        treeSet.add(room);
        System.out.println(treeSet.size());
//        treeSet.remove(room);
        treeSet.pollFirst();
        System.out.println(treeSet.size());
    }
}
