package com.che300.design.proxy;

public class Proxy implements Rent{

    private Host host;



    public Proxy(Host host) {
        this.host = host;
    }


    @Override
    public void rent() {
        seeHouse();
        host.rent();
        heTong();
        fee();
    }


    public void seeHouse(){
        System.out.println("中介带看房");
    }

    public void heTong(){
        System.out.println("和中介签合同");
    }

    public void fee(){
        System.out.println("中介收取费用");
    }
}
