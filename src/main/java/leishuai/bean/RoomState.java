package leishuai.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * @Description * 游戏周期：生成牌，(发牌阶段)，指示出牌（某人出牌），收到出牌，处理出牌或者三人响应出牌，处理三人响应，指示下一人出牌
 * 断电只存一类还原点，即生成指示出牌的消息后，同时存消息和此时的状态。一个房间对应一个还原点，若无还原点，说明不需要还原。
 * 断网需存状态，以及当前是否处于响应出牌的阶段（服务端发出需要响应的指令，到收到响应消息）
 * @Author leishuai
 * @Date 2018/12/18 18:30
 * @Version 1.0
 */
public class RoomState {
    public interface V {
        int PENG = 15;  //碰
        int PENG_AND_ONE = 16;//碰的同时留有一张
        int DIAN_XIAO = 17;  //点笑，包括小朝天
        int HUI_TOU_XIAO = 18;  //回头笑
        int ZI_XIAO = 19;  //自笑，包括大朝天
        int DIS_LAI_ZI = 20;//漂癞子，用于标识一种出牌前操作
        int DIS_HONG_ZHONG= 21;//漂癞子，用于标识一种出牌前操作
        int NORMAL = 10;  //普通,用于标识一种出牌前操作

        int NO_CARD = -1;//不拿牌出牌
        int NOT_YOU_DISCARD = 0;//不是你出牌
        int NONE_BEFUCK = -1; //没人被笑，点笑中，fuck的一个取值。（小朝天，已经被处理了）
    }

    //以下变量记录着关联关系
    public long roomId; //既是与room关联的外键，也是roomState的主键
    public long updateTime = 0;//更新时间
    public PlayerState[] playerStates = new PlayerState[4];//记录每个玩家在游戏开始后期间的专属数据

    {
        for (int i = 0; i < 4; i++) {
            playerStates[i] = new PlayerState();
        }
    }

    public boolean isOver = false;//是否结束

    //以下变量在发牌阶段修改（此前大多数据需要还原成默认值）
    public int playedTurn = 0;//已玩的轮数
    public int zhuang = 0;
    public int laiGen = 0;
    public int laiZi = 0;
    public List<Integer> yuPai = new LinkedList<Integer>();

    //以下变量在生成出牌指令前修改
    public int disCardSeatNo = 0;//当前出牌人座位号
    public int beforeGetCard = V.NORMAL;//当前出牌人的拿牌前操作，用于判断是否有热冲，只需要记录点笑、回头笑、自笑，其它都是normal，大小朝天也不用记录
    public int fuckWho = V.NONE_BEFUCK;//出牌人拿牌前是点笑时，需要记录被点笑的人，出牌没人要时减掉他的积分，小朝天立马减掉，记录none_befuck

    //以下信息在生成出牌指令时修改
    // 包阔yuPai
    public int getCardNoBeforeDis = V.NO_CARD;//出牌前拿的牌
    public boolean canDisCard = false;//服务器已发出指令（无论是否成功），到收到出牌信息期间为true

    //以下变量在服务端收到出牌信息后修改
    //包括canDisCard
    public int disCardNo = 0;//当前出牌人出的牌
    public boolean laiZiAppeared = false; //是否有人出过癞子
    public int responseNum = 0;//响应当前出牌的人数，最多三个（房间总人数少一）

    //以下信息在服务端收到其它玩家对出的牌的响应之后修改，然后要么结束，要么重回指定出牌人前操作
    //包括responseNum

    //发牌时的房间状态清空
    public void recoverDefault() { //重新设置为默认值,在发牌时会调用（或者在发送完结束消息，并重开时调用）
        for (int i = 0; i < 4; i++) {
            playerStates[i].recoverDefault();
        }
        isOver = false;//是否结束，可以去掉
//        playedTurn = 0;//已玩的轮数  ,解散时清零
//        zhuang = 0;
//        laiGen = 0;
//        laiZi = 0;
        yuPai.clear();
        //以下变量在生成出牌指令前修改
        disCardSeatNo = 0;//当前出牌人座位号
        beforeGetCard = V.NORMAL;//当前出牌人的拿牌前操作，用于判断是否有热冲，只需要记录点笑、回头笑、自笑，其它都是normal，大小朝天也不用记录
        fuckWho = V.NONE_BEFUCK;//出牌人拿牌前是点笑时，需要记录被点笑的人，出牌没人要时减掉他的积分，小朝天立马减掉，记录none_befuck
        //以下信息在生成出牌指令时修改
        // 包阔yuPai
        getCardNoBeforeDis = V.NO_CARD;//出牌前拿的牌
        canDisCard = false;//服务器已发出指令（无论是否成功），到收到出牌信息期间为true
        //以下变量在服务端收到出牌信息后修改
        //包括canDisCard
        disCardNo = 0;//当前出牌人出的牌
        laiZiAppeared = false; //是否有人出过癞子
        responseNum = 0;//响应当前出牌的人数，最多三个（房间总人数少一）
    }
}
