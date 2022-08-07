package leishuai.service.impl;

import leishuai.service.RoomIdService;
import leishuai.service.RoomService;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/31 14:00
 * @Version 1.0
 */
@Component
public class RoomIdServiceImpl implements RoomIdService {
    //    static final int MIN_UU_ROOM_ID= 1000*1000; //一百万 7位数
    static final int MIN_UU_ROOM_ID = 10000; //一百万 7位数
    static final int MIN_PRI_ROOM_ID = MIN_UU_ROOM_ID / 10;//10 0000 最小私人房房号，6位数
    static int maxPriRoomId = MIN_UU_ROOM_ID / 10 - 1; //9 9999 最大私人房房号初始值，第一次初始化后值要加上INCR_ID_NUMBER
    static boolean[] priUsedFlags = new boolean[(MIN_UU_ROOM_ID / 10) * 9]; //私人房房号 10 0000 --- 99 9999 ,共90万个
    static List<Integer> avaiPriRoomId = new LinkedList<>();
    static final int INCR_ID_NUMBER = 10;
    static private AtomicLong uuRoomId = new AtomicLong(MIN_UU_ROOM_ID);

    static {
        int firstIncr = INCR_ID_NUMBER * 5;
        int[] arr = new int[firstIncr];
        int minRoomId = maxPriRoomId + 1;
        for (int i = 0; i < firstIncr; i++) {
            arr[i] = minRoomId + i;
        }
        maxPriRoomId += firstIncr;
        for (int i = 0; i < firstIncr; i++) {
            int rand = (int) (Math.random() * firstIncr);
            int temp = arr[i];
            arr[i] = arr[rand];
            arr[rand] = temp;
        }
        for (int i = 0; i < firstIncr; i++) {
            avaiPriRoomId.add(arr[i]);
        }
    }

    static void addAvaiRoomId() {
        int[] arr = new int[INCR_ID_NUMBER];
        int minRoomId = maxPriRoomId + 1;
        for (int i = 0; i < INCR_ID_NUMBER; i++) {
            arr[i] = minRoomId + i;
        }
        maxPriRoomId += INCR_ID_NUMBER;
        for (int i = 0; i < INCR_ID_NUMBER; i++) {
            avaiPriRoomId.add(arr[i]);
        }
    }

    @Override
    public long getPriRoomId() {
        synchronized (avaiPriRoomId) {
            int temp = avaiPriRoomId.remove(0);
            if (avaiPriRoomId.size() < 3) {
                addAvaiRoomId();
            }
            priUsedFlags[temp - MIN_PRI_ROOM_ID] = true;
            return temp;
        }
    }

    @Override
    public void putPriRoomId(long priRoomId) {
        int roomId = (int) priRoomId;
        synchronized (avaiPriRoomId) {
            int temp = roomId - MIN_PRI_ROOM_ID;
            if (priUsedFlags[temp] == true) {
                priUsedFlags[temp] = false;
                avaiPriRoomId.add(roomId);
            }
        }
    }

    public static void main(String[] args) {
        RoomIdService roomIdService = new RoomIdServiceImpl();
        for (int i = 0; i < 1000; i++) {
            System.out.println("i= " + i);
            long roomid = roomIdService.getPriRoomId();
            System.out.println("priRoomId: " + roomid);
            if (Math.random() < 0.96) {
                roomIdService.putPriRoomId(roomid);
//                System.out.println("put: "+(roomid));
            }
        }
    }

    @Override
    public long getUURoomId() {
        return uuRoomId.getAndIncrement();
    }
}
