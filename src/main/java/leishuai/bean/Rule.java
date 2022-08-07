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
    boolean YouLaiCanZhuoChong = true;//有赖子是否能胡别人，前端用
    boolean CanChi = true;//是否能吃
    boolean IsGangFanBei = false; //杠是否跟赖子翻倍，后端用
    boolean FanLeiJia = true; //番数是否累加，false 为累乘

    int GameMode_HuangHuang = 1;
    int GameMode_GanDengYan = 2;
    int GameMode = GameMode_HuangHuang;
}
