package leishuai.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/18 18:28
 * @Version 1.0
 */

public class PlayerState {
    public interface V {
        int PENG = 15;  //碰
        int PENG_AND_ONE = 16;//碰的同时留有一张
        int DIAN_XIAO = 17;  //点笑，包括小朝天
        int HUI_TOU_XIAO = 18;  //回头笑
        int ZI_XIAO = 19;  //自笑，包括大朝天

        int BU_YAO = 10;//不要，标识响应,以上常量也可以做响应标志，也可以做牌信息标志
        int ZHUO_CHONG = 20;//捉冲
        int CHIZUO = 21;//吃最左
        int CHIZHONG = 22;//吃中间
        int CHIYOU = 23;//吃最右
        int RESP_OTHER_DISCARD = 31;//可响应别人出牌，也可以称为未响应状态，以上常量均为不可响应状态
        int RESP_LAST_FOUR_CARD = 32;//可响应最后四张是否能胡
        int RESP_RESTART = 33;//可响应重新开始
    }

    public static class Chi {
        int chiType; //21，22，23
        int[] paiArr;//length 3

        public Chi(int chiType, int[] paiArr) {
            this.chiType = chiType;
            this.paiArr = Arrays.copyOf(paiArr, paiArr.length);
        }
    }

    public List<Chi> allChi = new ArrayList<Chi>();
    //以下变量在发牌阶段修改（此前大多数据需要还原成默认值）
    //此数组同时记录了牌、点笑、自笑等等信息，可用长14的map代替，但可能影响效率
    public static int cardArrLength = 40;
    public int[] cardArr = new int[cardArrLength]; //下标0不用，取值0表示没有，1-4表示个数，15-19 看上面常量定义，

    //以下信息在生成出牌指令时修改
    public int getCardTimes = 0;//拿牌的次数，用于自笑和回头笑的合法验证

    //以下变量在服务端收到出牌信息时修改
    public int disLiaZiNum = 0;//漂癞子数
    public int jifen = 0;
    public List<Integer> disCardArr = new ArrayList<Integer>(20);
    public boolean superFlag = false; //特权

    //以下信息在服务端收到其它玩家对出的牌的响应之后修改
    //包括jifen，cardArr（碰笑时）
    public int responseFlag = V.BU_YAO;//响应类型，与是否可以响应，用于验证响应与记录，以及用作恢复的依据

    public void recoverDefault() {
        for (int i = 0; i < cardArrLength; i++) {
            cardArr[i] = 0;
        }
        //以下信息在生成出牌指令时修改
        getCardTimes = 0;//拿牌的次数，用于自笑和回头笑的合法验证
        //以下变量在服务端收到出牌信息时修改
        disLiaZiNum = 0;//漂癞子数
        jifen = 0;
        //以下信息在服务端收到其它玩家对出的牌的响应之后修改
        //包括jifen，cardArr（碰笑时）
        responseFlag = V.BU_YAO;//响应类型
        disCardArr.clear();
    }
}
