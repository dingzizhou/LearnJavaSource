package org.example.test.utils.HashMap;

import org.junit.Test;

import java.util.TreeMap;


public class test {

    @Test
    public void test1(){
        RBTree<Integer> rbTree = new RBTree<>();
        for (int i = 1;i < 15;i++) {
            rbTree.insertNode(i);
        }
        rbTree.show();
       for(int i = 1;i < 15;i+=2){
           System.out.println("删除i = "+i);
           rbTree.delete(i);
           rbTree.show();
       }
    }
}
