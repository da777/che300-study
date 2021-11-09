package com.che300.example.string;

/**
 * @author liujialiang
 */
public class QuestionString {
    public static void main(String[] args) {
        String x = "abc";
        String y = "ab" + "c";
        System.out.println(x == y);

        String s1 = "ab";
        String s2 = "abc";
        String s3 = s1 + "c";
        System.out.println(s2 == s3);
    }
}
