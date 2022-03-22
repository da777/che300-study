package com.che300.algorithm;

public class Q35 {

    public static void main(String[] args) {
        int[] nums = new int[]{1, 3};
        int target = 1;
        System.out.println(searchInsert(nums, target));
    }

    public static int searchInsert(int[] nums, int target) {
        int left = 0;
        int right = nums.length;
        int mid = (left + right) / 2;
        while (left < right) {
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
}
