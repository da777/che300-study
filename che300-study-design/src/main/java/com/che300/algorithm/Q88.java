package com.che300.algorithm;

import java.util.Arrays;

public class Q88 {


    public static void main(String[] args) {
        int[] nums1 = new int[]{1};
        int m = 1;
        int[] nums2 = new int[]{};
        int n = 0;
        merge(nums1, m, nums2, n);

    }

    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        if (n == 0) {
            return;
        }
        int[] arr = new int[nums1.length];
        int index = 0;
        int i = 0, j = 0;
        int current;
        while (index < arr.length) {
            if (i == m){
                current = nums2[j++];
            }else if (j == n){
                current = nums1[i++];
            }else if (nums1[i] <= nums2[j]) {
                current = nums1[i++];
            } else {
                current = nums2[j++];
            }
            arr[index++] = current;
        }
        for (int a = 0; a < arr.length; a++) {
            nums1[a] = arr[a];
        }
    }

}
