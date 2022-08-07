package leishuai.bean;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/17 20:13
 * @Version 1.0
 */
public class LsmjException extends Exception {
    private String name;

    public String getName() {
        return name;
    }

    public LsmjException(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
