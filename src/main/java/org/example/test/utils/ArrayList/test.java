package org.example.test.utils.ArrayList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
public class test {
    /**
     * 初始化和添加函数测试
     */
    @Test
    public void test_add() {
        // 有初始大小添加元素
        ArrayList<String> list1 = new ArrayList<>(1);
        list1.add("2");
        list1.add("22"); // 扩容算法的特殊情况
//        // 测试扩容算法溢出
//        int oldCapacity = Integer.MAX_VALUE -1111;
//        int newCapacity = oldCapacity + (oldCapacity >> 1);
//        System.out.println(oldCapacity);
//        System.out.println(newCapacity);
        list1.add("222");

        // 空参添加元素
        ArrayList<String> list2 = new ArrayList<>();
        list2.add("1");
        list2.add("11");
        list2.add(1,"111"); // 置顶位置插入调试
        System.out.println(Arrays.toString(list2.toArray()));

        // addAll方法
        System.out.println(list2);
        list2.addAll(list1);
        System.out.println(list2);
        list2.addAll(1,list1);
        System.out.println(list2);
    }

    /**
     * 删除
     */
    @Test
    public void test_del(){
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0;i < 10;i++){
            list.add(i);
        }
        System.out.println(list);
    }
}
