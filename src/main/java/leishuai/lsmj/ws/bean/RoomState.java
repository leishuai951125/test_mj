package leishuai.lsmj.ws.bean;

import java.util.ArrayList;
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
    interface V {
        int PENG = 5;  //碰
        int DIAN_XIAO = 6;  //点笑
        int HUI_TOU_XIAO = 7;  //回头笑
        int CHAO_TIAN = 8;  //朝天
        int ZI_XIAO = 9;  //自笑
    }
    public long roomId; //既是与room关联的外键，也是roomtate的主键
    //存座位号，或者-1
    public int disCardSeatNo;//当前出牌人座位号
    public boolean canDisCard;//服务器已发出指令（无论是否成功），到收到出牌信息期间为true
    public int playedTurn;//已玩的轮数
    public List<Integer> yuPai=new LinkedList<Integer>();
    public PlayerState[] playerStates=new PlayerState[4];
    {
        for(int i=0;i<4;i++){
            playerStates[i]=new PlayerState();
        }
    }
    public int beforeDisCard;//出牌前操作

}
