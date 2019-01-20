package leishuai.utils;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/18 11:15
 * @Version 1.0
 */
public class StringUtil {
    public static boolean isNotNull(String s){
        if(s==null || "".equals(s.trim())){
            return false;
        }
        return true;
    }
}
