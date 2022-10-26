package org.example.test.lang.String;

import org.junit.Test;

public class test {

    @Test
    public void test1(){
        String str1 = "abc";
        char[] data = {'a','b','c'};
        String str2 = new String(data);
        System.out.println(str1.equals(str2));
    }

    @Test
    public void test2(){
        int[] s = {97,98,99,100};
        String str = new String(s,0,4);
        System.out.println(str);

    }

}
