package leishuai.utils.study;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/3/28 15:48
 * @Version 1.0
 */
public class StackStudy {
    public static void main(String[] args) {

        Integer integerArr[]={1,5,3,4,7,6,9,2};
//        Node<Integer> head=createTree2(integerArr);
//        Node<Integer> head=createTree1(integerArr);
//        ff(head);
//        System.out.println();
//        ceng(head);
//        System.out.println();
        fast_sort(integerArr,0,integerArr.length-1);
        System.out.println(Arrays.toString(integerArr));
    }

    static void fast_sort(Integer[] arr,int start,int stop){
        if(start>=stop){
            return;
        }
        int i=start;
        int j=stop;
        while (i<j){
            while (i<j&&arr[start]<=arr[j]){
                j--;
            }
            while (i<j&&arr[start]>=arr[i]){
                i++;
            }
            if(i<j){
                int temp=arr[j];
                arr[j]=arr[i];
                arr[i]=temp;
            }
        }
        int temp=arr[j];
        arr[j]=arr[start];
        arr[start]=temp;
        fast_sort(arr,start,i-1);
        fast_sort(arr,i+1,stop);
    }
    private static void ceng(Node<Integer> head) {
        Queue<Node<Integer>> queue=new LinkedList<>();
        queue.offer(head);
        while (queue.isEmpty()!=true){
            Node<Integer> integerNode=queue.poll();
            System.out.print(integerNode.value+"  ");
            if(integerNode.left!=null){
                queue.offer(integerNode.left);
            }
            if(integerNode.right!=null){
                queue.offer(integerNode.right);
            }
        }
    }

    private static Node<Integer> createTree1(Integer[] integerArr) {
        Node<Integer> head=null;
        for(int i=0;i<integerArr.length;i++){
            head=insert(head,integerArr[i]);
        }
        return head;
    }

    //引用回传，空则插入并返回
    private static Node<Integer> insert(Node<Integer> head, Integer integer) {
        if(head==null){
            return new Node<>(integer);
        }
        if(head.value>integer){ //左子树
            head.left=insert(head.left,integer);
        }else {
            head.right=insert(head.right,integer);
        }
        return head;
    }

    private static Node<Integer> createTree2(Integer[] integerArr) {
        Node<Integer> head=new Node<>(integerArr[0]);
        Node<Integer> point=null;
        for(int i=1;i<integerArr.length;i++){
            point=head;
            Integer iTemp=integerArr[i];
            while (true){
                if(point.value<iTemp){
                    if(point.right==null){
                        point.right=new Node<>(iTemp);
                        break;
                    }else {
                        point=point.right;
                    }
                }else {
                    if(point.left==null){
                        point.left=new Node<>(iTemp);
                        break;
                    }else {
                        point=point.left;
                    }
                }
            }
        }
        return head;
    }

    private static void ff(Node<Integer> point) {
        if(point==null){
            return;
        }
        ff(point.left);
        System.out.print(point.value+"  ");
        ff(point.right);
    }
}
class Node<T>{
    Node(T t){
        value=t;
        left=right=null;
    }
    T value;
    Node<T> left,right;
}
