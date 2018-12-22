package leishuai.lsmj.http.service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/13 18:42
 * @Version 1.0
 */
public class jdsl {
    public static void main(String[] args) throws IOException {
        ObjectOutputStream inputStream=new ObjectOutputStream(new FileOutputStream("d:/du.txt"));
        inputStream.writeObject("sdjflksdjfl");
        inputStream.flush();
        System.out.println();
    }
    static void doN(Map jsonObject){

    }
}
