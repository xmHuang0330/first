package demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class test {

    @Test
    void test01() {
        int[] arr = {-1, -2, -4, -2, 4, -6};
        int res = arr[0];
        int sum = 0;
        for (int i :
                arr) {
            if (sum > 0)
                sum += i;
            else
                sum = i;
            res = Math.max(res, sum);
        }
        System.out.println(res);
    }

    /**
     * 旋转数组
     */
    @Test
    public void rotate() {
        int[] nums = {1,6,4,2,89,56,78,88,99,80};
        int k = 11;
        int length = nums.length;
        k %= length;
        reverse(nums,0,length-1);
        reverse(nums,0,k-1);
        reverse(nums,k,length -1);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + "    ");
        }
    }

    void reverse(int[] nums,int start,int end){
        while(start < end){
            int temp = nums[start];
            nums[start++] = nums[end];
            nums[end--] = temp;
        }
    }

    @Test
    public void test02() {
        List<Double> doubles = new ArrayList<>();
        doubles.add(0, 2d);
        doubles.add(1, 2d);
        doubles.add(2, 2d);
        doubles.add(1, 9d);
        System.out.println(doubles.size());
        System.out.println(doubles.get(1));

    }
}
