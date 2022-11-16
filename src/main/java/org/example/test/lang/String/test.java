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
    @Test
    public void test_append(){
        String[] arr = {"he", "llo", "world"};
        String s = "";
        for (int i = 0; i < arr.length; i++) {
            s += arr[i];
        }
        System.out.println(s);
        StringBuilder s1 = new StringBuilder();
        for (String value : arr) {
            s1.append(value);
        }
        System.out.println(s1);
    }

}
