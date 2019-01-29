package leishuai.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import leishuai.bean.*;
import leishuai.service.AccountService;
import leishuai.service.ConnectService;
import leishuai.service.ProcessMsg;
import leishuai.service.RoomService;
import leishuai.service.impl.AccountServiceImpl;
import leishuai.service.impl.ConnectServiceImpl;
import leishuai.service.impl.RoomServiceImpl;
import leishuai.utils.StringUtil;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/17 11:00
 * @Version 1.0
 */
@ServerEndpoint("/lsmj/websocket/{jsonParam}")
public class WebSocket {
    private static int onlineCount = 0;
    static AccountService accountService = new AccountServiceImpl();
    static RoomService roomService = new RoomServiceImpl();
    static ConnectService connectService = new ConnectServiceImpl();
    private Player player;

    public WebSocket() {
    }

    boolean isCheckSuccess(JSONObject param) {
        if (true) {  //todo 开发阶段使用，无条件通过
            return true;
        }
        Long accountId = param.getLong("accountId");
        String token = param.getString("token");
        if (accountId == null || !StringUtil.isNotNull(token)) {
            return false;
        }
        String tokenInSession = accountService.getAccountToken(accountId);
        if (tokenInSession != null && tokenInSession.equals(token)) { //验证通过
            return true;
        }
        return false;
    }

    private boolean isFull(Room room) {
        Player[] players = room.getPlayers();
        boolean full = true; //房间是否已满，不能用room.getHavePalyerNum()
        for (int i = 0; i < room.getSumPlayer(); i++) {
            if (players[i] == null) {
                full = false;
                break;
            }
        }
        return full;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("jsonParam") String jsonParam) throws IOException, LsmjException {
        addOnlineCount();
        long startTime = System.nanoTime() / 100000;
        System.out.println(jsonParam);
        JSONObject param = JSON.parseObject(jsonParam);
        if (isCheckSuccess(param)) {
            long accountId = param.getLong("accountId");
            Account accountOnGame = accountService.getAccountOnGame(accountId);
//                accountOnGame = null; //// TODO: 2019/1/18
            if (accountOnGame != null) { //在线，或者说在房间中
                connectService.onLine(this, session, accountOnGame); //上线
                Room room = accountOnGame.getPlayer().getRoom();
                synchronized (room){
                    if(isFull(room)){ //齐了
                        ProcessMsg processMsg = ProcessMsg.map.get("recover"); //恢复
                        doMsgAndSendMsg(processMsg, null);
                        System.out.println("状态恢复");
                    }else { //没齐
                        ProcessMsg processMsg = ProcessMsg.map.get("c3"); //加入房间的信息发给每个人
                        doMsgAndSendMsg(processMsg, null);
                    }
                }
            }else { //不在房间中
                Account account = accountService.getAccountBySession(accountId);
                connectService.onLine(this, session, account);//上线
                synchronized (player) {  //进入房间，包括公共房和私人房
                    connectService.intoRoom(player, param.getLong("roomId"), param.getInteger("diFen"));
                    ProcessMsg processMsg = ProcessMsg.map.get("c3"); //加入房间的信息发给每个人
                    doMsgAndSendMsg(processMsg, null);
                }
            }
        } else {
            throw new LsmjException("验证失败");
        }
        System.out.println("加入第 " + getOnlineCount() + " 个玩家耗时 " + (System.nanoTime() / 100000 - startTime) + " （毫秒*10）");
    }

    @OnClose   //todo 此处没考虑player在并发时的处理问题
    public void onClose() throws IOException, LsmjException {
        Integer seat = player == null ? null : player.getSeatNo();
        System.out.println("==========seatNo:" + seat + "连接关闭");
        if (player != null) {
            Room room = player.getRoom();
            if (room != null) {
                synchronized (room) {
                    if (room.getHavePalyerNum() < room.getSumPlayer() && player.isExitFlagWhenSessionClose()) {
                        ProcessMsg processMsg = ProcessMsg.map.get("-c3");
                        doMsgAndSendMsg(processMsg, null);
                        roomService.exitRoom(player);
                    }
                }
            }
            player.setExitFlagWhenSessionClose(true);
        }
//        player.setSession(null);
        player = null;
        subOnlineCount();
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException, LsmjException {
        System.out.println("收到 seatNo" + player.getSeatNo() + " msg: " + message);
        long startTime = System.nanoTime() / 100000;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String msgId = jsonObject.getString("msgId");
        ProcessMsg processMsg = ProcessMsg.map.get(msgId);
        doMsgAndSendMsg(processMsg, jsonObject);
        System.out.println("处理一条消息耗时 " + (System.nanoTime() / 100000 - startTime) + " (毫秒*10)");
    }

    //处理并生成新消息，并发送，发送后再返回消息列表，方便再另作处理。
    public List<ProcessResult> doMsgAndSendMsg(ProcessMsg processMsg, JSONObject jsonObject) throws LsmjException, IOException {
        long startTime, stopTime;
        List<ProcessResult> resultList = null;
        //锁对象，当消息为 c7 时不加锁或者，其余全部都对房间加锁
        Room room = player.getRoom();
        Object lockObject = (room == null) ? new Object() : room;
        synchronized (lockObject) {
            if (processMsg != null) {
                //同一时刻只有一人操作当前房间
                resultList = processMsg.processMsg(jsonObject, player);
            } else {
                throw new LsmjException("找不到ws消息对应的处理对象");
            }
            if (null != resultList && !resultList.isEmpty()) {
                for (ProcessResult processResult : resultList) {
                    int seatNo = processResult.getSeatNo();
                    Session sessionToSendMsg = player.getRoom().getPlayers()[seatNo].getSession();
                    List list = processResult.getSuggestList();
                    String jsonString = JSON.toJSONString(list);
                    try {
                        System.out.println(player.getSeatNo() + "给" + seatNo + "发送消息");
//                        System.out.println(Thread.currentThread());
                        sendMsg(sessionToSendMsg, jsonString);
                    } catch (Exception e) {
                        System.out.println(player.getSeatNo() + "给" + seatNo + "发送消息时发生错误");
                    }
                }
            }
            if (room != null && room.getRoomState().isOver) {
                roomService.destory(room);
            }
//            startTime=System.nanoTime()/100000;
//            System.out.println(JSON.toJSONString(room.getRoomState()));
//            System.out.println("json: "+ (System.nanoTime()/100000-startTime));
        }
        return resultList;
    }

    public static void sendMsg(Session session, String text) throws IOException {
        synchronized (session) {
//            session.getAsyncRemote().sendText(text);
            session.getBasicRemote().sendText(text);
        }
        System.out.println("成功发送:" + text);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        Integer seat = player == null ? null : player.getSeatNo();
        System.out.println("===========seatNo:" + seat + "连接出错,马上关闭============");
        error.printStackTrace();
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ++onlineCount;
    }

    public static synchronized void subOnlineCount() {
        --onlineCount;
    }

    public synchronized Player getPlayer() {
        return player;
    }

    public synchronized void setPlayer(Player player) {
        this.player = player;
    }
}