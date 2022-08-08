package leishuai.bean;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2022/8/7 23:57
 * @Version 1.0
 */
public interface Rule {
    boolean DaiHongzhong = true;//发牌是否带红中，后端用
    int HongZhongPoint = 28;
    boolean DisLaiZiYouJiang =false;//打癞子是否有奖
    int MaxLaiZiNum_zhuoChong=1;//捉人最大赖子数
    boolean HasGangShangPao=false; //是否有杠上炮
    boolean IsGangFanBei = false; //杠是否跟赖子翻倍，后端用
    //番数
    int FanMode_LeiJia=1;
    int FanMode_LeiCheng=2;
    int FanMode=FanMode_LeiJia;//番数是否累加，false 为累乘
    //游戏类型
    int GameMode_HuangHuang = 1;
    int GameMode_GanDengYan = 2;
    int GameMode = GameMode_HuangHuang;
}
