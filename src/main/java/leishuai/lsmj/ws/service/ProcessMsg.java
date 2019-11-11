package leishuai.lsmj.ws.service;

import com.alibaba.fastjson.JSONObject;
import leishuai.lsmj.ws.bean.LsmjException;
import leishuai.lsmj.ws.bean.Player;
import leishuai.lsmj.ws.bean.ProcessResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 处理消息的接口，里面有一个消息id与对应处理函数的映射
 * @auther: leishuai
 * @date: 2018/12/17 20:08
 */
public interface ProcessMsg{
    Map<String,ProcessMsg> map=new HashMap<String,ProcessMsg>(16); //静态资源
    List<ProcessResult> processMsg(JSONObject jsonObject, Player player) throws LsmjException;
}
