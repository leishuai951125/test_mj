package leishuai.lsmj.ws.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import leishuai.lsmj.ws.bean.*;
import leishuai.lsmj.ws.service.AccountService;
import leishuai.lsmj.ws.service.ConnectService;
import leishuai.lsmj.ws.service.ProcessMsg;
import leishuai.lsmj.ws.service.RoomService;
import leishuai.lsmj.ws.service.impl.AccountServiceImpl;
import leishuai.lsmj.ws.service.impl.ConnectServiceImpl;
import leishuai.lsmj.ws.service.impl.RoomServiceImpl;
import leishuai.lsmj.ws.utils.StringUtil;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/17 11:00
 * @Version 1.0
 */
@ServerEndpoint("/lsmj/websocket/{jsonParam}")
//@ServerEndpoint("/lsmj/websocket")
public class WebSocket {
    private static int onlineCount = 0;
    static AccountService accountService = new AccountServiceImpl();
    static RoomService roomService = new RoomServiceImpl();
    static ConnectService connectService = new ConnectServiceImpl();
    //    static ProcessMsg processMsgMap=null;
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

//    static String jsonParamByCreate = null;
//    static {
//        Map map = new HashMap() {{
//            put("accountId", 123);
//            put("roomId", null);
//            put("token", "token");
//            put("diFen", 5);
//        }};
//        jsonParamByCreate = JSON.toJSONString(map);
//    }

    @OnOpen
    public void onOpen(Session session, @PathParam("jsonParam") String jsonParam) throws IOException {
        addOnlineCount();
        long startTime = System.nanoTime() / 100000;
        try {
            System.out.println(jsonParam);
            JSONObject param = JSON.parseObject(jsonParam);
            if (isCheckSuccess(param)) {
                long accountId = param.getLong("accountId");
                Account accountOnGame = accountService.getAccountOnGame(accountId);
                accountOnGame = null; //todo
                if (accountOnGame != null) {//在游戏中，恢复操作和重传操作
                    //上线
                    connectService.onLine(this, session, accountOnGame);
                    //状态恢复
                    connectService.stateRecovery(this, session, accountOnGame);
                } else { //不在游戏中，分配房间
                    Account account = accountService.getAccountBySession(accountId);
                    //上线
                    connectService.onLine(this, session, account);
                    Long roomId = param.getLong("roomId");
                    Integer diFen = param.getInteger("diFen");//底分
                    connectService.intoRoom(player, roomId, diFen);
                    ProcessMsg processMsg = ProcessMsg.map.get("c3");
                    doMsgAndSendMsg(processMsg, null);
                }
            } else {
                throw new LsmjException("验证失败");
            }
        } catch (LsmjException e) {
            System.out.println(e.getName());
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            session.close();
        }
        System.out.println("加入第 " + getOnlineCount() + " 个玩家耗时 " + (System.nanoTime() / 100000 - startTime) + " （毫秒*10）");
    }

    @OnClose   //todo 此处没考虑player在并发时的处理问题
    public void onClose() throws IOException, LsmjException {
        Integer seat=player==null?null:player.getSeatNo();
        System.out.println("==========seatNo:"+seat+"连接关闭");


        if (player != null) {
            Room room = player.getRoom();
            if (room != null) {
                synchronized (room) {
                    if (room.getHavePalyerNum() < 4) {
                        ProcessMsg processMsg = ProcessMsg.map.get("-c3");
                        doMsgAndSendMsg(processMsg, null);
                        roomService.exitRoom(player);
                    }
                }
            }
        }
        player.setSession(null);
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
        Object lockObject = (room == null ) ? new Object() : room;
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
                        System.out.println(player.getSeatNo()+"给"+seatNo+"发送消息");
                        System.out.println(Thread.currentThread());
                        sendMsg(sessionToSendMsg, jsonString);
                    } catch (Exception e) {
                        System.out.println(player.getSeatNo()+"给"+seatNo+"发送消息时发生错误");
                    }
                }
            }
            if(room!=null && room.getRoomState().isOver){
                roomService.destory(room);
            }
        }

        return resultList;
    }

    public static void sendMsg(Session session, String text) throws IOException {
        synchronized (session){
//            session.getAsyncRemote().sendText(text);
            session.getBasicRemote().sendText(text);
        }
        System.out.println("成功发送:" + text);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        Integer seat=player==null?null:player.getSeatNo();
        System.out.println("===========seatNo:"+seat+"连接出错,马上关闭============");
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