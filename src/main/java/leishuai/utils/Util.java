package leishuai.utils;

import org.junit.Test;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/23 19:55
 * @Version 1.0
 */
public class Util {
    public int[] randomYinShe(){
        int[][]arr=new int[3][9];
        for(int i=0;i<3;i++){
            for(int j=0;j<9;j++){
                arr[i][j]=i*9+j;
            }
        }
        for(int i=0;i<3;i++){
            int random=(int)(Math.random()*2);
            if(random==0){
                for(int j=0;j<5;j++){
                    int temp=arr[i][j];
                    arr[i][j]=arr[i][8-j];
                    arr[i][8-j]=temp;
                }
            }
            //交换同条万
            random=(int)(Math.random()*3);
            int[]temp=arr[i];
            arr[i]=arr[random];
            arr[random]=temp;
        }
        int yinShe[]=new int[28];
        for(int i=0;i<3;i++){
            for(int j=0;j<9;j++){
                yinShe[i*9+j+1]=arr[i][j]+1;
            }
        }
        return yinShe;
    }
    @Test
    public void test2(){
        int testTime=0;
        while (++testTime<=10){
            System.out.println("第"+testTime+"次测试的转换关系：");
            int[]yinShe=randomYinShe();
            for(int i=1;i<28;i++){
                System.out.print(i+":"+yinShe[i]+"  ");
                if(i%9==0){
                    System.out.println();
                }
            }
        }
    }
}
