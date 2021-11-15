package com.che300.design.factory;


/**
 * 简单工厂模式
 * 工厂方法模式
 * 抽象工厂模式
 *
 * @author jlliu
 */
public abstract class RouJiaMoStore {

    public abstract RouJiaMo createRouJiaMo(String type);

    /**
     * 根据传入类型卖不同的肉夹馍
     */
    public RouJiaMo sellRouJiaMo(String type) {
        RouJiaMo rouJiaMo = createRouJiaMo(type);
        rouJiaMo.prepare();
        rouJiaMo.fire();
        rouJiaMo.pack();
        return rouJiaMo;
    }
}
