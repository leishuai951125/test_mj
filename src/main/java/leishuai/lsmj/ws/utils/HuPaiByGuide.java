package leishuai.lsmj.ws.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import leishuai.lsmj.ws.bean.PlayerState;
import leishuai.lsmj.ws.bean.RoomState;
import org.junit.Test;
import java.util.*;

//改1：统计每种花色数量时把pai[i]/3,改成(pai[i]-1)/3
//改2：getCardList时，把sort（pai,0,lenth-1）改成 pai，0，lenth
public class HuPaiByGuide {
    public interface V{
        int PI_HU=2;
        int HEI_MO=4;
        int ZHUO_CHONG=2;
        String PI_HU_S="pi_hu";
        String HEI_MO_S="hei_mo";
        String ZHUO_CHONG_S="zhuo_chnog";
    }
    private static HuPaiByGuide huPaiByGuide=new HuPaiByGuide();
    private boolean printFlag=false; //是否打印匹配信息，主要用于调试
    private boolean printResult=false; //打印匹配结果
    private class CardNode{ //用来存牌某种牌的信息和数量
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
    private class PiPei{ //存储匹配信息
        char type; //匹配类型 '杠’ ‘顺’ ‘将’
        int [] onePiPei;  //一次匹配的牌，有2-3张
        @Override
        public String toString() {
            return "PiPei{" +
                    "type=" + type +
                    ", onePiPei=" + Arrays.toString(onePiPei) +
                    '}';
        }
    }
    private int piPeiDeep=0;
    private int []matchMethod=null;

    private static HuPaiByGuide getInstance(int [] matchMethod){
        huPaiByGuide.matchMethod=matchMethod;
        huPaiByGuide.piPeiDeep=0;
        return huPaiByGuide;
    }

    private boolean nowMatchGang(List<CardNode> list,int cardSum){ //当前用杠进行匹配，看能匹配多少次
        if(printFlag){
            System.out.println("尝试杠");
        }
        if(3==cardSum){ //刚好只有3张牌，返回1
            return true;
        }
        //超过3张牌，将第一个节点减3，为0 则从链表中，生成新链表方便继续匹配
        CardNode nodeCopy=null;
        //nodeCopyd的作用是如果发生删除，则留个备份，用于计算完剩余牌的匹配次数后将链表还原成
        // 划掉杠之前的状态，原因是，除了要计算当前划掉对子时的匹配总数，还可能需要计算当前划掉顺子的匹配总数
        list.get(0).number-=3;
        if(0==list.get(0).number){
            nodeCopy=list.get(0);
            list.remove(0);
        }

        //删除完一杠后,计算剩下牌可以匹配几次，实际上是递归调用
        boolean SY_isHu=isMatchAble(list,cardSum-3);
        //计算完后将链表状态还原
        if(nodeCopy!=null){
            list.add(0,nodeCopy);
        }
        list.get(0).number+=3;

        return SY_isHu;
    }

    private boolean nowMatchDuiZi(List<CardNode> list,int cardSum){ //当前用对子进行匹配，看能匹配多少次
        if(0==cardSum%3){
            //一共只剩下3整数倍数量的牌，说明对子已经被匹配过了，只能有一对将，所以当前不能再用对子匹配了
            return false;
        }
        if(printFlag){
            System.out.println("尝试对子");
        }
        if(2==cardSum){ //刚好只有两张牌，返回1
            return true;
        }
        //超过两张牌，将第一个节点减2，为0 则从链表中
        CardNode nodeCopy=null;
        list.get(0).number-=2;
        if(0==list.get(0).number){
            nodeCopy=list.get(0);
            list.remove(0);
        }

        //删除完一对后,计算剩下牌可以匹配几次，实际上是递归调用
        boolean SY_isHu=isMatchAble(list,cardSum-2);
        //计算完后将链表状态还原
        if(nodeCopy!=null){
            list.add(0,nodeCopy);
        }
        list.get(0).number+=2;
        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    }

    private boolean nowMatchShunZi(List<CardNode> list,int cardSum){  //当前用顺子进行匹配，看能匹配多少次
        if(list.size()<3){  //不足三种牌，无法匹配，返回0次
            return false;
        }
        for(int i=0;i<2; i++){
            //前三种牌的花色不同，或者点数不连续，都不能匹配，返回0次
            if(list.get(i).pattern!=list.get(i+1).pattern){
                return false;
            }else if(list.get(i).figure!=list.get(i+1).figure-1){
                return false;
            }
        }
        //经过上面判断，说明可以划掉顺子
        if(printFlag){
            System.out.println("尝试顺子");
        }

        if(3==cardSum){  //刚好只有三张牌，则直接返回1
            return true;
        }
        //当前超过3张牌，则将前三个节点数量减一，0个的去掉，方便做下一轮匹配
        // 在遍历中可能有删除操作，所以用迭代器遍历
        CardNode [] deleteCopyArr=new CardNode[3];//初始化均为null，用于记录被删除的节点,方便还原
        Iterator<CardNode> iterator=list.iterator();
        for(int index=0;index<3;index++) {  //对前三个元素数量减一，为0 则删除
            CardNode cnTemp=iterator.next();
            cnTemp.number--;
            if(cnTemp.number==0){
                deleteCopyArr[index]=cnTemp;
                iterator.remove();
            }
        }
        //删除完顺子后,计算剩下牌可以匹配几次，实际上是递归调用
        boolean SY_isHu=isMatchAble(list,cardSum-3);
        //还原链表
        for(int i=0;i<3;i++){
            if(deleteCopyArr[i]!=null){
                list.add(i,deleteCopyArr[i]);
            }
            list.get(i).number++;
        }
        //当前函数的返回值应该是剩余牌匹配次数+1
        return SY_isHu;
    }


    /**
     * @param:
     * @auther: leishuai
     * @date: 2018/12/12 3:13
     */
    private boolean isMatchAble(List<CardNode> list,int cardSum){  //递归求是否可胡
        //定义：把相同花色和点数的牌称为一种牌，每种牌占一个节点
        int firstNodeNumber=list.get(0).number;
        int guide=matchMethod[piPeiDeep];
        piPeiDeep++;
        if(2<=firstNodeNumber && guide==2){  //第一种牌至少有两张
            return nowMatchDuiZi(list,cardSum);
        }
        //第一种牌至少有三张，可以分别计算划掉对子和顺子的次数，并取最大者,注意计算顺子在最后
        if(3<=firstNodeNumber && guide==3){
            return nowMatchGang(list,cardSum);
        }
        return nowMatchShunZi(list,cardSum);
    }

    private List<CardNode> getCardList2(int []cardArr){ //在带计数的牌数组中获取牌节点链表
        List<CardNode> list=new LinkedList<CardNode>();
        int sum=0;
        for(int i=1;i<28;i++){
            if(cardArr[i]==RoomState.V.PENG_AND_ONE){
                cardArr[i]=1;
            }
            if(cardArr[i]<=8 && cardArr[i]>0){
                CardNode cardNode=new CardNode();
                cardNode.number=cardArr[i];
                cardNode.figure=(i-1)%9+1;
                cardNode.pattern=(i-1)/9;
                list.add(cardNode);
            }
        }
        return list;
    }

     static private int[] parseIntArr(JSONObject jsonObject,String key){
         Object []aa=null;
         try{
             aa=jsonObject.getJSONArray(key).toArray();
        }catch (Exception e){
            return null;
        }
        int[]matchMethod=new int[aa.length];
        for(int i=0;i<matchMethod.length;i++){
            matchMethod[i]=(Integer)aa[i];
        }
        return matchMethod;
    }

    public static int[] copyCardArr(int []cardArr){
        int []cardArr2 = new int[28]; //复制牌数组
        for (int i = 0; i < 28; i++) {
            if (cardArr[i] == RoomState.V.PENG_AND_ONE) {
                cardArr2[i] = 1;
            } else if (cardArr[i] >= 1 && cardArr[i] <= 4) {
                cardArr2[i] = cardArr[i];
            }
        }
        return cardArr2;
    }
    //没有癞子个数的校验，因为不知道游戏最多允许几个癞子,注意判断时不能影响原来的cardArr数组
    static public int isHu(JSONObject jsonObject,int []cardArr,int laiZi){ //0不糊，2屁胡，4黑摸
        int[]matchMethod=parseIntArr(jsonObject,"matchMethod");
        if(matchMethod==null){
            return 0;
        }
        int sum=0;
        HuPaiByGuide huPaiByGuide=HuPaiByGuide.getInstance(matchMethod);
        int cardArr2[]=copyCardArr(cardArr);
        List<CardNode> nodeList=huPaiByGuide.getCardList2(cardArr2);
        for(CardNode cardNode:nodeList){
            sum+=cardNode.number;
        }
        String type=jsonObject.getString("type");
        boolean isMatch=huPaiByGuide.isMatchAble(nodeList,sum);
        if(V.ZHUO_CHONG_S.equals(type) ){ //捉冲
            if(isMatch){
                return V.ZHUO_CHONG;
            }else {
                return 0;
            }
        }
        if(  V.HEI_MO_S.equals(type) && isMatch){ //黑摸
            return V.HEI_MO;
        }
        int []actAs=parseIntArr(jsonObject,"actAs");//不能黑摸时，这个值不能为空
        if(actAs==null || cardArr[laiZi]<actAs.length){
            return 0;
        }
        cardArr2 = copyCardArr(cardArr);
        //把癞子替换成牌
        cardArr2[laiZi]-=actAs.length;
        for(int i=0;i<actAs.length;i++){
            ++cardArr2[actAs[i]];
        }
        huPaiByGuide=HuPaiByGuide.getInstance(matchMethod);
        nodeList=huPaiByGuide.getCardList2(cardArr2);
        if(huPaiByGuide.isMatchAble(nodeList,sum)){
            return V.PI_HU;
        }
        return 0;
    }
}

class TestHuPaiByGuide{
    public static int[] createTestPaiArr(){
        int []paiArr2={27, 26, 25, 24, 23, 22, 20, 20, 5, 4,3};
//        int []paiArr2={27, 26, 25, 24, 23, 22, 20, 20, 5, 4,3};
//        int []paiArr2={0,0,2,2,1,2,2,2,6,0,2};
        int []paiArr=new int[28];
//        for(int i=0;i<paiArr2.length;i++){
//            paiArr[i]=paiArr2[i];
//        }
        for(int i=0;i<paiArr2.length;i++){
            paiArr[paiArr2[i]]++;
        }
        return paiArr;
    }
    public static   String createJsonObject(){
//        int []matchMethod={1,1,1,1,2};
        int []matchMethod={1,2,1,1};
//        int []actAs={1,8};
        int []actAs={};
        Map map=new HashMap();
        map.put("type","hei_mo");
        map.put("matchMethod",matchMethod);
        map.put("actAs",actAs);
        return JSON.toJSONString(map);
    }

    public static void main(String[] args) throws InterruptedException {
        int []paiArr=createTestPaiArr();
        JSONObject jsonObject=JSON.parseObject(createJsonObject());
        for(int i=0;i<1;i++){
            long startTime=System.nanoTime()/1000;
            System.out.println(HuPaiByGuide.isHu(jsonObject,paiArr,10));
            System.out.println("耗时（单位：微妙）："+(System.nanoTime()/1000-startTime));
//            Thread.sleep(1000);
        }
    }
    @Test
    public void test2() throws InterruptedException {
        int []paiArr=createTestPaiArr();
        JSONObject jsonObject=JSON.parseObject(createJsonObject());
        for(int i=0;i<100;i++){
            long startTime=System.nanoTime()/1000;
            System.out.println(HuPaiByGuide.isHu(jsonObject,paiArr,10));
            System.out.println("耗时（单位：微妙）："+(System.nanoTime()/1000-startTime));
//            Thread.sleep(1000);
        }
    }
    @Test
    public void test(){
        int []arr={1,2,3,4};
        String s= JSON.toJSONString(arr);
        JSONArray jsonArray=JSONArray.parseArray(s);
        Integer b[]={};
        jsonArray.toArray(b);
        System.out.println(Arrays.toString(b));
    }
}




