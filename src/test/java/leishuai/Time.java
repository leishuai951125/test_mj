package leishuai.leishuai;

import org.junit.Test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by leishuai on 2018/7/22.
 */
public class Time {


//        Date d=new Date();
//        System.out.println(d);
//        System.out.println(d.toInstant());
//        System.out.println(d.toLocaleString()); //DateFormat.format(Date date)
//        Timestamp d2;
//        System.out.println(d2);
//        System.out.println(d2.toInstant());
//        System.out.println(d2.toLocaleString());

    @Test
    public void test() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yy/MM/dd hh:mm:ss:SSSSSS");
        System.out.println(dateFormat.format(timestamp));
        System.out.println(dateFormat.format(date));
//        Date date=new Date();
//        Timestamp ts=new Timestamp(System.currentTimeMillis());
//        System.out.println(date);
//        System.out.println(ts);
//        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println("处理后：");
//        System.out.println(dateFormat.format(date));
//        System.out.println(dateFormat.format(ts));
//        System.out.println("====================");

//        String dateString="2018/1/2 23:23:23";
//        ts=Timestamp.valueOf(dateString);
//        try {
//            date=dateFormat.parse(dateString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


    }
}
