package com.ir6.ecommerce;

import org.junit.Test;

public class TestCase {

    @Test
    public void contextLoad() {
        String s = "";
        if("sdf".contains(s)) {
            System.out.println("111");
        } else {
            System.out.println("222");
        }
        System.out.println("sdf".indexOf(s));
    }

}
