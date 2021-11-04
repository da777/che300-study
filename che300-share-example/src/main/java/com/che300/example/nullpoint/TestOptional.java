package com.che300.example.nullpoint;

import lombok.Data;

/**
 * @author liujialiang
 */

@Data
public class TestOptional {

    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo();
        String zipCode = userInfo.getAddress().getCity().getZipCode().getZipCode();

        System.out.println(zipCode);

        if (userInfo != null) {
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
        }
    }
}
