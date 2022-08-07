package leishuai.utils.study;

import org.apache.ibatis.session.Configuration;

import java.util.Arrays;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/2/24 0:20
 * @Version 1.0
 */
public class 牛牛 {
    static int print[] = new int[5];
    static int B = 0;
    static int min = 0;
    static int[] result = new int[20];
    static int minLenth = 10000;
    static int minRoute[] = null;

    public static void main(String[] args) {
        Configuration configuration;
//        ff2(4,2);
        int arr[] = {3, 4, 2, 1, 3, 1, 2, 3, 4, 5, 3, 6, 3, 2, 5, 5, 2, 6, 4, 2};
        kk(arr, 0, 0);
        System.out.println(Arrays.toString(minRoute));
    }

    private static void kk(int[] arr, int deep, int index) {
        int lenth = arr.length;
        result[deep] = arr[index];
        if (index == lenth - 1) {
            if (deep < minLenth) {
                minRoute = Arrays.copyOf(result, deep + 1);
            }
            for (int i = 0; i <= deep; i++) {
                System.out.print(result[i] + " ");
            }
            System.out.println();
        }
        for (int i = 1; i <= arr[index] && i + index < lenth; i++) {
            kk(arr, deep + 1, i + index);
        }
    }

    private static void ff2(int i, int i1) {
        B = i1;
        ff(i, i1);
    }

    private static void ff(int a, int b) {  //组合问题，C a取b
        if (a < b || b < 1) {
            return;
        }
        print[b] = a;
        if (b == 1) {
            for (int i = B; i > 0; i--) {
                System.out.print(print[i]);
            }
            System.out.println();
        }
        ff(a - 1, b - 1);
        if (a > b) {
            ff(a - 1, b);
        }
    }
}
