package com.leishuai;

import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Best{
    public static void main(String[] args) {
        Configuration configuration;
//        new T().start();
//        new T().run();
        List<Integer> list=new LinkedList<Integer>();
        list.add(11);
        list.add(12);
        list.add(13);
        list.add(14);
        list.remove(2);
        System.out.println(list.get(2));
    }
}
class T extends Thread{
    @Override
    public void run() {
        int i=1/0;
    }
}