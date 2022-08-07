package leishuai.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 处理完一条消息后返回的处理结果，包含多条指令
 * @Author leishuai
 * @Date 2018/12/17 19:37
 * @Version 1.0
 */
public class ProcessResult {
    int seatNo;//收信人的座位号
    List<Suggest> suggestList = new ArrayList<Suggest>(4);//指令列表

    public int getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public List<Suggest> getSuggestList() {
        return suggestList;
    }

    public void setSuggestList(List<Suggest> suggestList) {
        this.suggestList = suggestList;
    }
}
