package com.che300.design.factory;


public class Start {
    public static void main(String[] args) {
        SuQianStore store = new SuQianStore();
        store.createRouJiaMo("Tian");
        store.sellRouJiaMo("Tian");
    }
}
