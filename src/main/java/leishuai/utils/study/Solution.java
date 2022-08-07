package leishuai.utils.study;

import java.util.*;

public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().treeSum(new int[]{
                0, 0, 0, 0, 3, -1, -2
        }));
    }

    public List<List<Integer>> treeSum(int[] nums) {
        Arrays.sort(nums);
        Map<Integer, List<Integer>> indexMap = getIndexMap(nums);
//        System.out.println(indexMap);
        List<List<Integer>> rList = new LinkedList();
        Set<List<Integer>> rSet = new HashSet<>();
        int length = nums.length;
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                int k = 0 - nums[j] - nums[i];
                List<Integer> list = indexMap.get(k);
                if (list != null) {
                    for (Integer index : list) {
                        if (index > i && index > j) {
                            List<Integer> r2List = new ArrayList();
                            r2List.add(nums[i]);
                            r2List.add(nums[j]);
                            r2List.add(nums[index]);
                            rSet.add(r2List);
                        }
                    }
                }
            }
        }
        for (List<Integer> list : rSet) {
            rList.add(list);
        }
        return rList;
    }

    private Map<Integer, List<Integer>> getIndexMap(int[] nums) {
        Map<Integer, List<Integer>> indexMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer v = nums[i];
            List<Integer> list = indexMap.get(v);
            if (list == null) {
                list = new LinkedList<Integer>();
                indexMap.put(v, list);
            }
            list.add(i);
        }
        return indexMap;
    }
}
