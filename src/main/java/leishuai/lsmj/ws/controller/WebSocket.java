package leishuai.lsmj.ws.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import leishuai.lsmj.ws.bean.Account;
import leishuai.lsmj.ws.bean.LsmjException;
import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.bean.ProcessResult;
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
import java.util.List;

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
        if(true){  //todo 开发阶段使用，无条件通过
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

    Account getDevAccount(){
        return new Account(){{
            setAccountId(123);
            setHeadImgUrl("headImgUrl");
            setPassword("password");
            setUsername("username");
        }};
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(new WebSocket().getDevAccount()));
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("jsonParam") String jsonParam) throws IOException {
       long startTime=System.nanoTime()/1000;
        try {
            System.out.println(jsonParam);
            JSONObject param = JSON.parseObject(jsonParam);
            if (isCheckSuccess(param)) {
                long accountId = param.getLong("accountId");
                Account accountOnGame = accountService.getAccountOnGame(accountId);
                if (accountOnGame != null) {//在游戏中，恢复操作和重传操作
                    connectService.stateRecovery(this, session, accountOnGame);
                } else { //进入房间或者分配房间
                    Account account = accountService.getAccountBySession(accountId);
                    account=getDevAccount();  //todo 开发时使用
                    Long roomId = param.getLong("roomId");
                    if (roomId != null) {//房号非空,加入私人房
                        boolean isSuccess = connectService.intoPrivateRoom(this, session, account, roomId);
                        if (!isSuccess) {
                            throw new LsmjException("进入私人房出错,可能是房号不对");
                        }
                    } else {//进去公共房
                        int diFen = param.getInteger("diFen");//底分
                        if (diFen < 1 || diFen > 5) {
                            throw new LsmjException("底分不合法");
                        }
                        connectService.intoPublicRoom(this, session, account, diFen);
                    }
                    ProcessMsg processMsg = ProcessMsg.map.get("c3");
                    doMsgAndSendMsg(processMsg, null);
                }
                System.out.println("新连接加入");
                addOnlineCount();
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
        System.out.println("加入一个玩家耗时"+(System.nanoTime()/1000-startTime)+"微秒");
    }

    @OnClose
    public void onClose() {
        System.out.println("连接关闭");
        subOnlineCount();
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException, LsmjException {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String msgId = jsonObject.getString("msgId");
        ProcessMsg processMsg = ProcessMsg.map.get(msgId);
        doMsgAndSendMsg(processMsg, jsonObject);
    }

    public void doMsgAndSendMsg(ProcessMsg processMsg, JSONObject jsonObject) throws LsmjException, IOException {
       long startTime,stopTime;
        List<ProcessResult> resultList = null;
        if (processMsg != null) {
            synchronized (player.getRoom()){ //同一时刻只有一人操作当前房间
                resultList = processMsg.processMsg(jsonObject, player);
            }
        } else {
            throw new LsmjException("找不到ws消息对应的处理对象");
        }
        if (null != resultList && !resultList.isEmpty()) {
            for (ProcessResult processResult : resultList) {
                int seatNo = processResult.getSeatNo();
                Session sessionToSendMsg = player.getRoom().getPlayers()[seatNo].getSession();
                List list = processResult.getMsgList();
//                startTime=System.nanoTime()/1000;
                String jsonString=JSON.toJSONString(list);
//                stopTime=System.nanoTime()/1000;
//                System.out.println("序列化耗时"+(stopTime-startTime));
                sendMsg(sessionToSendMsg, jsonString);
//                startTime=System.nanoTime()/1000;
//                System.out.println("发送耗时"+(startTime-stopTime));
            }
        }
    }

    public static void sendMsg(Session session, String text) throws IOException {
        session.getBasicRemote().sendText(text);
//        System.out.println("成功发送:" + text);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("连接出错");
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}