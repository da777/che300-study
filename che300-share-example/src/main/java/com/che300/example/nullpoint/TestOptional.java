package com.che300.example.nullpoint;

import lombok.Data;

import java.util.Optional;

/**
 * @author liujialiang
 */

@Data
public class TestOptional {

    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo();

    }

    public static void start(UserInfo userInfo){
        //错误示例
        String result = userInfo.getAddress().getCity().getZipCode().getZipCode();
        System.out.println(result);

        //第一种方法
        Address address = userInfo.getAddress();
        if (address != null) {
            City city = address.getCity();
            if (city != null) {
                ZipCode zipCode1 = city.getZipCode();
                if (zipCode1 != null) {
                    System.out.println(zipCode1.getZipCode());
                }
            }
        }

        //第二种方法

        String s = Optional.ofNullable(userInfo)
                .map(UserInfo::getAddress)
                .map(Address::getCity)
                .map(City::getZipCode)
                .map(ZipCode::getZipCode)
                .map(String::trim)
                .orElse(null);
        System.out.println(s);
    }

}
