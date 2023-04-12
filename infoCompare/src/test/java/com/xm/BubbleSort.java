package com.xm;

import java.util.Arrays;

public class BubbleSort {

    /**
     * 冒泡排序1.0
     * @param arr
     */
    public static void sort(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                int temp = 0;
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 冒泡排序2.0
     * @param arr
     */
    public static void sort2(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            boolean isSorted = true;
            for (int j = 0; j < arr.length - i - 1; j++) {
                int temp = 0;
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    //有元素进行交换，所以不是有序的
                    isSorted = false;
                }
            }
            if (isSorted) {
                break;
            }
        }
    }

    /**
     * 冒泡排序3.0
     * @param arr
     */
    public static void sort3(int arr[]) {
        //记录最后一次交换的位置
        int lastChangeIndex = 0;
        //无序序列的边界，每次比较只需要比到这里
        int sortBorder = arr.length - 1;
        for (int i = 0; i < arr.length - 1; i++) {
            //有序标记，每一轮的初始值都是true
            boolean isSorted = true;
            for (int j = 0; j < sortBorder; j++) {
                int temp = 0;
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    isSorted = false;
                    //更新为最后一次交换元素的位置
                    lastChangeIndex = j;
                }
            }
            sortBorder = lastChangeIndex;
            if (isSorted) {
                break;
            }
        }
    }



    public static void main(String[] args) {
        int[] arr = new int[]{2, 1, 8, 6, 9, 5, 4};
        sort(arr);
        System.out.println(Arrays.toString(arr) + " ");
        sort2(arr);
        System.out.println(Arrays.toString(arr) + " ");
        sort3(arr);
        System.out.println(Arrays.toString(arr) + " ");

    }
}
