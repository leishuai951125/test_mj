package leishuai.lsmj.ws.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/18 18:28
 * @Version 1.0
 */
public class PlayerState {
    //此数组同时记录了牌、点笑、自笑等等信息，可用长14的map代替，但可能影响效率
    public int []cardArr=new int[28]; //下标0不用，取值0表示没有，1-4表示个数，5-9 看上面常量定义，

    //以下两变量方便核对和恢复
    public boolean isYourResponseCard;//是否该你响应别人的出牌
    public boolean isYourChoseCard;

    //出牌（泛指碰、自笑、回头笑）标志，-1表示禁用，5 表示碰后出牌，依此类推
    public int choseFlag=0;//取值-1，5-10

    public int fuckWho;//点笑时，需要记录被点笑的人，出牌没人要时减掉他的积分

}
