package leishuai.bean;

/**
 * @Description 一条指令
 * @Author leishuai
 * @Date 2018/12/19 0:50
 * @Version 1.0
 */
public class Suggest {
    String msgId;
    Object msgBody;

    public Object getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(Object msgBody) {
        this.msgBody = msgBody;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


}
