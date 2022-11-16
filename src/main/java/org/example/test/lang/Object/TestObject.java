package org.example.test.lang.Object;

import org.junit.Test;

class minClass implements Cloneable{
    private String name;
    public minClass(String name){
        this.name= name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
}

class bigClass implements Cloneable{
    private minClass minclass;
    private String name;
    public bigClass(String name,minClass minclass){
        this.minclass = minclass;
        this.name = name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public minClass getMinclass() {
        return minclass;
    }

    public void setMinclass(minClass minclass) {
        this.minclass = minclass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class bigClass_deep implements Cloneable{
    private minClass minClass;
    private String name;
    public bigClass_deep(String name,minClass minClass_deep){
        this.minClass = minClass_deep;
        this.name = name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        bigClass_deep newClass = (bigClass_deep) super.clone();
        newClass.setMinClass((minClass) newClass.getMinClass().clone());
        return newClass;
    }

    public minClass getMinClass() {
        return minClass;
    }

    public void setMinClass(org.example.test.lang.Object.minClass minClass) {
        this.minClass = minClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}


public class TestObject {
    @Test
    public void test_clone() throws CloneNotSupportedException {
        minClass minClass1 = new minClass("minClass");
        bigClass bigClass1 = new bigClass("bigClass",minClass1);
        bigClass bigClass2 = (bigClass) bigClass1.clone();
        // 浅拷贝仅仅复制所拷贝的对象，而不复制他所引用的对象
        System.out.println("bigClass1 == bigClass2:"+bigClass1.equals(bigClass2));
        System.out.println("bigClass1.minClass == bigClass2.minClass:"+bigClass1.getMinclass().equals(bigClass2.getMinclass()));
        // 深拷贝把要复制的对象所引用的对象都复制了一遍。
        bigClass_deep bigClass_deep1 = new bigClass_deep("bigClass_deep",minClass1);
        bigClass_deep bigClass_deep2 = (bigClass_deep) bigClass_deep1.clone();
        System.out.println("bigClass_deep1 == bigClass_deep2:"+bigClass_deep1.equals(bigClass_deep2));
        System.out.println("bigClass_deep1.minClass == bigClass_deep2.minClass:"+bigClass_deep1.getMinClass().equals(bigClass_deep2.getMinClass()));
    }

    @Test
    public void test_Equal(){
        String a = new String("ab");
        String b = new String("ab");
        String aa = "ab";
        String bb = "ab";
        System.out.println(aa == bb);// true
        System.out.println(a == b);// false
        System.out.println(a.equals(b));// true
        System.out.println(aa.equals(bb));// true
        System.out.println(42 == 42.0);// true
    }
}
