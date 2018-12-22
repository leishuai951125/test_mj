package leishuai.lsmj.ws.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/20 3:17
 * @Version 1.0
 */
public interface FinalValue {
    //以下五个变量对应牌的五个状态，与0-4一起，可以构成cardArr的取值范围
    int PENG=5;  //碰
    int DIAN_XIAO=6;  //点笑
    int HUI_TOU_XIAO=7;  //回头笑
    int CHAO_TIAN=8;  //朝天
    int ZI_XIAO=9;  //自笑

    int DISABLE=-1; //禁止
    int NORMAL=10;  //普通

    int PUBLIC_ROOM= -1 ;//公共房
    int PRIVATE_ROOM=1;//私人房
}