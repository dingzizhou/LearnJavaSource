# Object

Object类是所有Java类的根父类，所有Java类(包括数组)都直接或间接继承Object类，因此都具有该类的方法。

Object中大部分为native方法（本地方法）。（还未了解JNI，以后具体补充）
#
## registerNatives();

```java
    private static native void registerNatives();
    static {
        registerNatives();
    }
```
本地方法。注册本地方法，该方法会在类加载时就进行注册。
#
## getClass();
返回某对象的运行时类。返回的 Class 对象是由所表示类的 static synchronized 方法锁定的对象。 （The returned Class object is the object that is locked by static synchronized methods of the represented class.）（还未理解，对多线程更深入理解的时候补充）
#
## hashcode();
返回这个对象的哈希码。

三个规则：

    1.在Java程序运行期间，不管什么时候调用同一个方法，hashCode一定返回相同的数字，前提是没有修改对象上的 equals 比较中使用的信息。不需要当程序再次执行时保持一致。
    2.如果根据equals方法得出两个对象相同。那么两个对象中的每一个调用 hashCode 方法必须产生相同的整数结果。
    3.如果根据equals方法得出两个对象不相同，那么两个对象调用hashCode方法不一定返回不一样的整数。
#
## equals(Object obj)
```java
    public boolean equals(Object obj) {
        return (this == obj);
    }
```
判断另一个对象是否与该对象相同。

Object 类的 equals 方法实现了对象上最有区别的可能等价关系；也就是说，对于任何非空引用值 x 和 y，当且仅当 x 和 y 引用同一个对象（x == y 的值为 true）时，此方法才返回 true。
请注意，每当重写该方法时，通常都需要重写 hashCode 方法，以维护 hashCode 方法的一般约定，即相等的对象必须具有相等的哈希码。
#
## clone()
创建并返回一个这个对象的复制。想要使用clone方法，需要实现接口Cloneable，没有则会抛出CloneNotSupportedException。所有数组都被认为实现了Cloneable接口。 Object 类本身并没有实现接口 Cloneable，因此在类为 Object 的对象上调用 clone 方法将导致在运行时抛出异常。
该方法执行对象的浅拷贝，而不是深拷贝。（此方法会创建此对象的类的新实例，并使用此对象的相应字段的内容来初始化其所有字段，就像通过赋值一样；字段的内容本身不会被克隆。） 参考test1。
#
## toString()
```java 
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
```

Object中toString的默认实现是返回类的名称加上@加上他的hashcode。 参考test2。

#
## notifiy与wait方法在多线程时再更新。