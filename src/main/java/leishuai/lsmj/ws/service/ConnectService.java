package leishuai.lsmj.ws.service;


import leishuai.lsmj.ws.bean.Account;
import leishuai.lsmj.ws.bean.LsmjException;
import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.controller.WebSocket;

import javax.websocket.Session;

/**
 * @Description ws连接成功或者断开相关操作
 * @param:
 * @return:
 * @auther: leishuai
 * @date: 2018/12/17 23:20
 */
public interface ConnectService {
    //上线，绑定account，player，ws，session
    void onLine(WebSocket webSocket, Session session, Account account);
    //进入房间，包括私人房和公共房
    boolean intoRoom(Player player,Long roomId,Integer diFen) throws LsmjException;
    void exitRoom(Player player);//退出房间
    void stateRecovery(WebSocket webSocket, Session session, Account account);
}
