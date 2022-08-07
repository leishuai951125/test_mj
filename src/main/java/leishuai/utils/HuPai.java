package leishuai.utils;

import leishuai.bean.RoomState;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

//改1：统计每种花色数量时把pai[i]/3,改成(pai[i]-1)/3
//改2：getCardList时，把sort（pai,0,lenth-1）改成 pai，0，lenth
public class HuPai {
    boolean printFlag = false; //是否打印匹配信息，主要用于调试
    boolean printResult = false; //打印匹配结果

    private class CardNode { //用来存牌某种牌的信息和数量
        //把相同点数和花色的牌称为一种牌，这个节点的作用就是纪录每种牌的数量
        int figure;//点数 1-9
        int pattern;//花色 0-2
        int number;//数量 1-4

        @Override
        public String toString() {
            return "CardNode{" +
                    "figure=" + figure +
                    ", pattern=" + pattern +
                    ", number=" + number +
                    '}';
        }
    }

    class PiPei { //存储匹配信息
        char type; //匹配类型 '杠’ ‘顺’ ‘将’
        int[] onePiPei;  //一次匹配的牌，有2-3张

        @Override
        public String toString() {
            return "PiPei{" +
                    "type=" + type +
                    ", onePiPei=" + Arrays.toString(onePiPei) +
                    '}';
        }
    }

    private PiPei[] piPeiArr = new PiPei[10];//最多可匹配29张牌
    private int piPeiDeep = -1; //匹配的所在的层数

    private boolean nowMatchGang(List<CardNode> list, int cardSum) { //当前用杠进行匹配，看能匹配多少次
        if (printFlag) {
            System.out.println("尝试杠");
        }
        piPeiArr[piPeiDeep] = new PiPei() {{  //记录一次匹配
            type = '杠';
//            onePiPei=new int[3];
//            CardNode temp=list.get(0);
//            for(int i=0;i<3;i++){
//                onePiPei[i]=temp.figure+temp.pattern*9;
//            }
        }};
        if (3 == cardSum) { //刚好只有3张牌，返回1
            return true;
        }
        //超过3张牌，将第一个节点减3，为0 则从链表中，生成新链表方便继续匹配
        CardNode nodeCopy = null;
        //nodeCopyd的作用是如果发生删除，则留个备份，用于计算完剩余牌的匹配次数后将链表还原成
        // 划掉杠之前的状态，原因是，除了要计算当前划掉对子时的匹配总数，还可能需要计算当前划掉顺子的匹配总数
        list.get(0).number -= 3;
        if (0 == list.get(0).number) {
            nodeCopy = list.get(0);
            list.remove(0);
        }

        //删除完一杠后,计算剩下牌可以匹配几次，实际上是递归调用
        boolean SY_isHu = isMatchAble(list, cardSum - 3);
        //计算完后将链表状态还原
        if (nodeCopy != null) {
            list.add(0, nodeCopy);
        }
        list.get(0).number += 3;

        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    }

    private boolean nowMatchDuiZi(List<CardNode> list, int cardSum) { //当前用对子进行匹配，看能匹配多少次
        if (0 == cardSum % 3) {
            //一共只剩下3整数倍数量的牌，说明对子已经被匹配过了，只能有一对将，所以当前不能再用对子匹配了
            return false;
        }
        if (printFlag) {
            System.out.println("尝试对子");
        }
        piPeiArr[piPeiDeep] = new PiPei() {{  //记录一次匹配
            type = '将';
//            onePiPei=new int[2];
//            CardNode temp=list.get(0);
//            for(int i=0;i<2;i++){
//                onePiPei[i]=temp.figure+temp.pattern*9;
//            }
        }};
        if (2 == cardSum) { //刚好只有两张牌，返回1
            return true;
        }

        //超过两张牌，将第一个节点减2，为0 则从链表中
        CardNode nodeCopy = null;
        //nodeCopyd的作用是如果发生删除，则留个备份，用于计算完剩余牌的匹配次数后将链表还原成 ——》
        // ——》划掉对子之前的状态，原因是，除了要计算当前划掉对子时的匹配总数，还可能需要计算当前划掉顺子的匹配总数
        list.get(0).number -= 2;
        if (0 == list.get(0).number) {
            nodeCopy = list.get(0);
            list.remove(0);
        }

        //删除完一对后,计算剩下牌可以匹配几次，实际上是递归调用
        boolean SY_isHu = isMatchAble(list, cardSum - 2);
        //计算完后将链表状态还原
        if (nodeCopy != null) {
            list.add(0, nodeCopy);
        }
        list.get(0).number += 2;

        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    }

    private boolean nowMatchShunZi(List<CardNode> list, int cardSum) {  //当前用顺子进行匹配，看能匹配多少次
        if (list.size() < 3) {  //不足三种牌，无法匹配，返回0次
            return false;
        }

        for (int i = 0; i < 2; i++) {
            //前三种牌的花色不同，或者点数不连续，都不能匹配，返回0次
            if (list.get(i).pattern != list.get(i + 1).pattern) {
                return false;
            } else if (list.get(i).figure != list.get(i + 1).figure - 1) {
                return false;
            }
        }
        //经过上面判断，说明可以划掉顺子
        if (printFlag) {
            System.out.println("尝试顺子");
        }

        piPeiArr[piPeiDeep] = new PiPei() {{  //记录一次匹配
            type = '顺';
//            onePiPei=new int[3];
//            Iterator<CardNode> iterator=list.iterator();
//            for(int i=0;i<3;i++){
//                CardNode temp=iterator.next();
//                onePiPei[i]=temp.figure+temp.pattern*9;
//            }
        }};


        if (3 == cardSum) {  //刚好只有三张牌，则直接返回1
            return true;
        }

        //当前超过3张牌，则将前三个节点数量减一，0个的去掉，方便做下一轮匹配
        // 在遍历中可能有删除操作，所以用迭代器遍历
        CardNode[] deleteCopyArr = new CardNode[3];//初始化均为null，用于记录被删除的节点,方便还原
        Iterator<CardNode> iterator = list.iterator();
        for (int index = 0; index < 3; index++) {  //对前三个元素数量减一，为0 则删除
            CardNode cnTemp = iterator.next();
            cnTemp.number--;
            if (cnTemp.number == 0) {
                deleteCopyArr[index] = cnTemp;
                iterator.remove();
            }
        }
        //删除完顺子后,计算剩下牌可以匹配几次，实际上是递归调用
        boolean SY_isHu = isMatchAble(list, cardSum - 3);
        //还原链表
        for (int i = 0; i < 3; i++) {
            if (deleteCopyArr[i] != null) {
                list.add(i, deleteCopyArr[i]);
            }
            list.get(i).number++;
        }
        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    }

    //把牌封装成一个有序链表，把具有相同花色和点数的牌称为一种牌，
    // 每一种牌占链表中的一个节点，并保存每种牌的数量
    List<CardNode> getCardList(int[] pai, int lenth) {
        Arrays.sort(pai, 0, lenth);//升序
        List<CardNode> list = new LinkedList<CardNode>();
        for (int i = 0; i < lenth; i++) {
            if (i == 0 || pai[i] != pai[i - 1]) {
                CardNode cnTemp = new CardNode();
                cnTemp.figure = (pai[i] - 1) % 9 + 1;
                cnTemp.pattern = (pai[i] - 1) / 9;
                cnTemp.number = 1;
                list.add(cnTemp);
            } else {
                ((LinkedList<CardNode>) list).getLast().number++;
            }
        }
        return list;
    }


    //花色数量校验，应该有两个花色是3整数倍，一个花色除3余2，
    // 不满足则return false，实现方式不唯一
    private boolean patternCheck(int[] pai, int lenth) {
        int patternNumber[] = {0, 0, 0};
        for (int i = 0; i < lenth; i++) { //汇总每个花色的数量
            int pattern = (pai[i] - 1) / 9;
            patternNumber[pattern]++;
        }
        int patternCheck = 0;  //花色校验，值为2 则花色数量正确
        for (int i = 0; i < 3; i++) {  //计算花色数量对不对
            int mod = patternNumber[i] % 3;
            if (mod != 0 && mod != 2) {
                patternCheck += 10;
            } else {
                patternCheck += mod;
            }
        }
        if (patternCheck == 2) {
            return true;
        }
        return false;
    }

    /**
     * 没有癞子时的胡牌检测，可以true，不行false
     * 需要匹配n+1次说明可以胡牌，即lenth/3+1
     *
     * @param pai   用数字0-26表示
     * @param lenth lenth=3*n+2
     * @return
     */
    public boolean noNaiTest(final int[] pai, final int lenth) {
        if (2 != lenth % 3 || pai.length < lenth) { //数量不对
            return false;
        }
        if (false == patternCheck(pai, lenth)) {  //每种花色的数量不对
            return false;
        }
        //以上两个判断其实都可以去掉，但加上再多数情况下会快一点
        List<CardNode> cardNodeList = getCardList(pai, lenth);
        piPeiDeep = -1;
        boolean isMatchAble = isMatchAble(cardNodeList, lenth); //计算可以匹配的次数
        if (isMatchAble && printResult) {
            for (int i = 0; i <= lenth / 3; i++) {
                System.out.println(piPeiArr[i]);
            }
        }
        return isMatchAble;
    }

    /**
     * @param:
     * @auther: leishuai
     * @date: 2018/12/12 3:13
     */
    private boolean isMatchAble(List<CardNode> list, int cardSum) {  //递归求是否可胡
        //定义：把相同花色和点数的牌称为一种牌，每种牌占一个节点
        int firstNodeNumber = list.get(0).number;
        boolean oneMatchIsAble = false;
        if (2 <= firstNodeNumber) {  //第一种牌至少有两张
            piPeiDeep++;
            oneMatchIsAble = nowMatchDuiZi(list, cardSum);
            piPeiDeep--;
            if (oneMatchIsAble) {
                return true;
            }
        }
        //第一种牌至少有三张，可以分别计算划掉对子和顺子的次数，并取最大者,注意计算顺子在最后
        if (3 <= firstNodeNumber) {
            piPeiDeep++;
            oneMatchIsAble = nowMatchGang(list, cardSum);
            piPeiDeep--;
            if (oneMatchIsAble) {
                return true;
            }
        }
        piPeiDeep++;
        oneMatchIsAble = nowMatchShunZi(list, cardSum);
        piPeiDeep--;
        return oneMatchIsAble;
    }


    public static boolean testHu(int[] cardArr) {
        HuPai huPai = new HuPai();
        int[] pai = getPaiFromCardArr(cardArr);
        return huPai.noNaiTest(pai, pai.length);
    }

    private static int[] getPaiFromCardArr(int[] cardArr) {
        int[] cardArr2 = HuPaiByGuide.copyCardArr(cardArr);
        List<Integer> paiList = new LinkedList<Integer>();
        for (int i = 1; i < 28; i++) {
            if (cardArr2[i] == RoomState.V.PENG_AND_ONE || cardArr2[i] == 1) {
                paiList.add(i);
            } else if (cardArr2[i] > 0 && cardArr2[i] <= 4) {
                for (int j = 0; j < cardArr2[i]; j++) {
                    paiList.add(i);
                }
            }
        }
        int pai[] = new int[paiList.size()];
        Iterator<Integer> iterator = paiList.iterator();
        for (int i = 0; i < pai.length; i++) {
            pai[i] = iterator.next();
        }
        return pai;
    }

}

class TestHuPai {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            long s = System.nanoTime() / 1000;
            System.out.println(HuPai.testHu(createTestPaiArr()));
            System.out.println(System.nanoTime() / 1000 - s);
            Thread.sleep(100);
        }
    }

    public static int[] createTestPaiArr() {
        int[] paiArr2 = {0, 3, 1, 1, 1, 1, 2, 1, 1, 0, 1, 1, 6};
        int[] paiArr = new int[28];
        for (int i = 0; i < paiArr2.length; i++) {
            paiArr[i] = paiArr2[i];
        }
        return paiArr;
    }

    @Test
    public void test2() {  //
        List<Integer> paiList = Arrays.stream(
                new Integer[]{1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 9}
        ).collect(Collectors.toList());  //数组转集合
        int paiArr[] = new int[paiList.size() + 1];
        HuPai huPai3 = new HuPai();
        for (int test = 1; test < 28; test++) {
            paiList.add(test);
            Object[] pai = paiList.toArray();
            for (int i = 0; i < paiArr.length; i++) {
                paiArr[i] = (Integer) pai[i];
            }
            if (huPai3.noNaiTest(paiArr, paiArr.length)) {
                System.out.println("可胡" + test);
            }
            paiList.remove(13);
        }
    }


    @Test
    public void test1() {  //测试胡牌检测是否正常与时间打印
        final int times = 100;
        int pai[] = {
                1, 1, 1, 9, 9,
                4, 4, 4, 6, 7,
                8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 11, 13
        };
//        int pai[]={
//                1,1,1,2,2,3,3,4,9,9,9
//        };
        HuPai huPai = new HuPai();
        List list = huPai.getCardList(pai, pai.length);
        for (Object o : list) {
            System.out.println(o);
        }
        for (int i : pai) {
            System.out.print(i + " ");
        }
        System.out.println();

        long timeSum = 0;
        boolean isHu = false;
//        huPai.printFlag=true;
        for (int i = 0; i < times; i++) {
            if (i == 0) {
                huPai.printResult = true;
//                printResult=false;
            } else {
                huPai.printResult = false;
            }
            long startTime = System.nanoTime();

            isHu = huPai.noNaiTest(pai, pai.length);
            long time = (System.nanoTime() - startTime) / 1000;
            timeSum += time;
            System.out.println("no:" + i + "  time:" + time + "微妙");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("sum:" + timeSum / 1000 + "毫秒  avgs:" + timeSum / times + "微秒");
        System.out.println(isHu);
    }
}