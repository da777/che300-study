package com.che300.algorithm;

public class MaoPao {
    public static void main(String[] args) {
        int[] arr = new int[]{4, 2, 5, 9, 6, 3, 11, 20, 1, 7, 8, 22};
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]+" ");
        }
        swap(arr,0,1);
        System.out.println();
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]+" ");
        }


    }

    static void swap(int[] arr, int left, int right) {
        arr[left] = arr[left] ^ arr[right];
        arr[right] = arr[left] ^ arr[right];
        arr[left] = arr[left] ^ arr[right];
    }


}
