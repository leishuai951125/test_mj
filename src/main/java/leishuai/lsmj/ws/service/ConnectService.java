package leishuai.lsmj.ws.service;


import leishuai.lsmj.ws.bean.Account;
import leishuai.lsmj.ws.controller.WebSocket;

import javax.websocket.Session;

/**
 * @Description ws连接成功后的处理
 * @param:
 * @return:
 * @auther: leishuai
 * @date: 2018/12/17 23:20
 */
public interface ConnectService {
    void intoPublicRoom(WebSocket webSocket, Session session, Account account, int difen);
    void stateRecovery(WebSocket webSocket, Session session, Account account);
    boolean intoPrivateRoom(WebSocket webSocket, Session session, Account account, Long roomId);
}
