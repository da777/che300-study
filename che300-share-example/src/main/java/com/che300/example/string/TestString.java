package com.che300.example.string;

public class TestString {
    public static void main(String[] args) {
        String s1 = "abc";
        String s2 = new String("abc");
        //输出结果
        System.out.println(s1.equals(s2));
        System.out.println(s1 == s2);

    }
}
