package leishuai.bean;

/**
 * @Description 游戏房间,房间的持久化只有两个字段，roomId和room对象的json字符串（包含四个玩家）
 * 为了减少持久化次数，玩家不需要单独做持久化操作，随房间一起持久化
 * 游戏周期：生成牌：发牌，指示出牌，某人出牌，处理出牌或者三人响应出牌，处理三人响应，指示下一人出牌
 *
 * @Author leishuai
 * @Date 2018/12/17 10:57
 * @Version 1.0
 */
public class Room {  //游戏开始前具有的属性
    public interface V{ //常量
         int PUBLIC_ROOM= 1 ;//公共房
    }
    long roomId;//持久化用
    int creatorId; //创建者的account账户id
    int diFen=5;//底分 1，2，5
    int maxLaiZiNum_ziMo=1;//胡牌时允许的最大癞子数,1 赖
    int maxLaiZiNum_zhuoChong=0;//胡牌时允许的最大癞子数,1 赖
    int canBeUsedTimes=V.PUBLIC_ROOM;//能被使用的次数，默认值为V.PUBLIC_ROOM -1 表示公共房，不限次数
    int havePalyerNum=0;//已有玩家人数，分配房间时用,为 0 表示未使用
    Player[] players=new Player[4];  //若房间以json字符串方式持久化，则存此字段，否则不存。
    RoomState roomState=new RoomState();  //记录集齐四人后发好牌完后具有的公共信息，因为此时才有恢复的必要

    public int getMaxLaiZiNum_ziMo() {
        return maxLaiZiNum_ziMo;
    }

    public void setMaxLaiZiNum_ziMo(int maxLaiZiNum_ziMo) {
        this.maxLaiZiNum_ziMo = maxLaiZiNum_ziMo;
    }

    public int getMaxLaiZiNum_zhuoChong() {
        return maxLaiZiNum_zhuoChong;
    }

    public void setMaxLaiZiNum_zhuoChong(int maxLaiZiNum_zhuoChong) {
        this.maxLaiZiNum_zhuoChong = maxLaiZiNum_zhuoChong;
    }

    public int getDiFen() {
        return diFen;
    }

    public void setDiFen(int diFen) {
        this.diFen = diFen;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getCanBeUsedTimes() {
        return canBeUsedTimes;
    }

    public void setCanBeUsedTimes(int canBeUsedTimes) {
        this.canBeUsedTimes = canBeUsedTimes;
    }

    public RoomState getRoomState() {
        return roomState;
    }

    public void setRoomState(RoomState roomState) {
        this.roomState = roomState;
    }

    public int getHavePalyerNum() {
        return havePalyerNum;
    }

    public void setHavePalyerNum(int havePalyerNum) {
        this.havePalyerNum = havePalyerNum;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }
}
