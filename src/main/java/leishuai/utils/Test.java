package leishuai.utils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/27 5:39
 * @Version 1.0
 */
public class Test {
    static String jsonString = "{\"beforeGetCard\":10,\"canDisCard\":true,\"disCardNo\":0,\"disCa" +
            "rdSeatNo\":0,\"fuckWho\":-1,\"getCardNoBeforeDis\":15,\"laiGen\":23,\"laiZi\":24,\"" +
            "laiZiAppeared\":false,\"playedTurn\":1,\"playerStates\":[{\"cardArr\":[0,1,0,0,0,0,0," +
            "0,1,0,0,0,0,0,0,1,2,0,0,0,2,1,1,1,1,2,1,0],\"disLiaZiNum\":0,\"getCardTimes\":1,\"jife" +
            "n\":0,\"responseDisCard\":10},{\"cardArr\":[0,1,1,0,1,1,0,1,0,0,1,1,1,0,0,0,1,0,0,1,1,1" +
            ",0,0,1,0,0,0],\"disLiaZiNum\":0,\"getCardTimes\":0,\"jifen\":0,\"responseDisCard\":10},{\"" +
            "cardArr\":[0,1,1,2,0,0,0,0,0,1,0,0,1,1,0,1,0,0,1,0,0,2,0,1,0,0,1,0],\"disLiaZiNum\":0,\"" +
            "getCardTimes\":0,\"jifen\":0,\"responseDisCard\":10},{\"cardArr\":[0,0,1,1,0,0,0,2,1,0,2" +
            ",0,0,1,0,0,0,0,0,1,1,0,1,0,0,1,0,1],\"disLiaZiNum\":0,\"getCardTimes\":0,\"jifen\":0,\"r" +
            "esponseDisCard\":10}],\"responseNum\":0,\"roomId\":0,\"yuPai\":[13,19,17,23,25,6,9,14,27," +
            "6,23,4,17,5,27,6,9,12,26,22,26,18,15,16,14,12,19,24,2,15,17,11,14,13,27,3,6,5,18,11,17," +
            "4,4,8,9,24,8,7,18,10,14,22,1,11,5],\"zhuang\":-1}\n";

    public static void main(String[] args) throws InterruptedException {
//        Map map=new HashMap(10000);
//        Map map1=new Hashtable(10000);
//        StringBuilder stringBuilder=new StringBuilder();
//        StringBuffer stringBuffer=new StringBuffer();
        HashMap map = new HashMap();
        long start;
        start = System.nanoTime() / 1000;
        for (int i = 0; i < 100; i++) {
            map.put(i, i);
//            stringBuilder.append("sjflksjdflkjs");
//            stringBuffer.append("sjflksjdflkjs");
        }
        System.out.println(map);
        System.out.println(System.nanoTime() / 1000 - start);
        start = System.nanoTime() / 1000;
        for (int i = 0; i < 10000; i++) {
//            map1.put(i,i);
//            stringBuilder.append("sjflksjdflkjs");
//            stringBuffer.append("sjflksjdflkjs");
        }
        System.out.println(System.nanoTime() / 1000 - start);
    }
}

class MyThread extends Thread {
    static boolean flag = false;
    CopyOnWriteArrayList<Integer> list;

    MyThread(CopyOnWriteArrayList copyOnWriteArrayList) {
        list = copyOnWriteArrayList;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            list.add(1);
        }
    }
}

/**
 * 0 集合框架的增删改均不是原子操作，有可能产生环状结构，Vector或者hashtable只解决了增删改的原子性，没解决遍历时的增删问题。
 * 1 迭代时的增删问题指：java的设计者希望遍历时，除迭代器自己外，包括本线程在内的任何线程都不要增删数据。最简单做法是遍历（也
 * 叫迭代）对集合上锁，但迭代耗时很长，不划算。
 * 3 迭代器只能保证当前线程遍历过程中没有除当前迭代器以外的增删操作，并非网上所讲运行在单独的线程，
 * 只是迭代器有一个对增删的校验而已,在hasnext（）方法中，发现增删则抛异常。
 * 4 可以看出，锁机制，与迭代器机制，本身毫无关联，唯一的相同点就是能解决多线程中的部分并发问题。
 * <p>
 * 5 map中，相同值的包装类型或者字符串类型认为是同一key，具有相同值的普通类被认为不是同一个key
 * 6 copyOnWrite*,在迭代过程中，不管当前线程还是其它线程的修改，迭代数据均不会发生变化。
 * 而当前线程在迭代过程中的多次修改均发生在同一副本中，类似多版本控制协议。
 * 7 ConcurrentMap 迭代过程中发生修改，和6中的现象相似，不过迭代的基本单位是段，只有
 * 正在被使用的段被修改才会复制出新的段副本
 * 8 copyOnWrite* 中迭代器特点：
 * （1）只用来遍历读取数据，不允许使用迭代器删除。
 * （2）每个迭代器都会拥有一个原数据的一个副本（跟数据库快照读相似）,实际上这个复制只发生在有修改时，无修改直接指向原数据。
 * （3）也就是迭代器并不是指向最新的list（历史读），而所有的非迭代器操作都是对最新list进行操作（当前读），
 */