package leishuai.bean;

import com.alibaba.fastjson.annotation.JSONField;

import javax.websocket.Session;

/**
 * @Description 玩家类，在游戏中的账户所具有的属性
 * 断网处理：拉取整个房间数据，然后查看是否有需要响应的命令，一个是自己出牌，一个是响应别人的出牌
 * 自己出牌再发条出牌命令（不拿牌），响应出牌同理
 *
 * 服务器断网处理，服务器保存最容易处理的节点，如某人出牌操作的保存
 * @Author leishuai
 * @Date 2018/12/17 10:59
 * @Version 1.0
 */
public class Player {  //玩家不需要单独做持久化
    /**
     * 如果把玩家的持久化从房间持久化中剥离出来，可添加如下字段，
     * 而且一下三个字段会把房间、玩家、账号三张表关联起来，另外两张表不需要添加关联字段了
     * int player_id;  //玩家id
     * int room_id;   //房间id
     */
    int seatNo=-1;//座位号
    long accountId;  //账号id，这个是用来持久化的,并关联玩家和账户，间接关联玩家、房间、账号三个属性
    int sumJiFen=0; //总积分
    @JSONField(serialize = false)
    Room room;//房间
    @JSONField(serialize = false)
    Session session;//会话，不需要持久化，因为持久化是为断电恢复使用的，断电后sessino存了也没有作用
    @JSONField(serialize = false)
    Account account;//账户信息，该字段不需要持久化，房间恢复时需要获取账户信息，所以要存

    public int getSumJiFen() {
        return sumJiFen;
    }

    public void setSumJiFen(int sumJiFen) {
        this.sumJiFen = sumJiFen;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
