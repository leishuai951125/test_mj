package leishuai.utils.study;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/1/31 16:21
 * @Version 1.0
 */
public class Conhashmap {
    public static void main(String[] args) {
        ConcurrentHashMap<Long,Long> hashMap=new ConcurrentHashMap();
        for(long i=0;i<10;i++){
            hashMap.put(i,i+100);
        }
        Long ll=new Long(5);
        System.out.println(hashMap.get(ll));
        System.out.println(hashMap.remove(3));
    }
}
