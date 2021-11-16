package com.che300.design.builder;

public class TestBuilder {
    public static void main(String[] args) {
        Computer computer = new Computer.Builder("AMD", "1TB")
                .setDisplay("三星")
                .setUsbCount(5)
                .setKeyboard("机械键盘").build();

    }
}
