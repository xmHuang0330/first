package demo;

import org.junit.jupiter.api.Test;

import java.io.File;

public class demo01 {

    /**
     * 冒泡排序
     */
    @Test
    public void bubblingSort() {
        int[] arr = {1, 2, 7, 54, 64, 22, 21, 5,5, 30};
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    /**
     * 选择排序
     */
    @Test
    public void selectSort() {
        int[] arr = {55, 44, 34, 52, 77, 21, 12, 10, 8, 99, 13};
        for (int i = 0; i < arr.length; i++) {
            //选择一个数
            int min = i;
            //默认i之前的数都是已经排好序的了，所以从i开始
            for (int j = i; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[min];
            arr[min] = temp;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "  ");
        }
    }

}
