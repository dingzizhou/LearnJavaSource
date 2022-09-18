# ArrayList

ArrayList是一个长度可调节的数组，可以包括任何数据（包括null）。与Vector区别为ArrayList非线程安全，Vector线程安全，但Vector效率较低，推荐使用CopyOnWriteArray。

## 类定义
```java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
```

## 成员变量与常量

```java
private static final int DEFAULT_CAPACITY = 10;  
// 无参构造函数初始化后添加数据默认大小为10
private static final Object[] EMPTY_ELEMENTDATA = {};  
// 空数组
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};  
// 同样为空数组，根据用途且别于上面的空数组
transient Object[] elementData; // non-private to simplify nested class access
// Object类型数组，用于存放ArrayList数据，transient表示该变量的值是不包括在序行化的表示中的。
// 原因在于elementData中并不是所有空间都有元素。ArrayList通过实现writeObject与readObject来序列化。
private int size;  
// 数组元素个数，并非elementData大小
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
// 要分配的数组的最大大小。-8主要是因为array保存自己的标头（header words可以这么翻译？），避免超过虚拟机的限制。
```

## 构造函数

### 空参的构造函数
```java
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
```
空参的初始化为一个静态的Object[](private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};)。减少开辟空间的耗时和耗空间。
### 带参数的构造函数
```java
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
```
如果initialCapacity为0时，elementData初始化为EMPTY_ELEMENTDATA,否则初始化一个长度为initialCapacity的elementData。
### 集合的构造函数
```java
    public ArrayList(Collection<? extends E> c) {
        Object[] a = c.toArray();
        if ((size = a.length) != 0) {
            if (c.getClass() == ArrayList.class) {
                elementData = a;
            } else {
                elementData = Arrays.copyOf(a, size, Object[].class);
            }
        } else {
            // replace with empty array.
            elementData = EMPTY_ELEMENTDATA;
        }
    }
```
构造一个包含指定集合的元素并按照迭代器顺序排序的list。使用Arrays.copyOf(elementData, size, Object[].class)多复制一遍是因为bug6260652存在：https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6260652。
意思是在某些情况下调用集合toArray方法返回值不确定，具体产生原因没有探究，具体复现代码如下：
```java

    List<String> list1 = new ArrayList<>();
    list1.add("list1");
    Object[] array1 = list1.toArray();
    System.out.println(array1.getClass().getCanonicalName());// java.lang.Object[]
    array1[0] = new Object(); //正常

    List<String> list2 = Arrays.asList("list2"); 
//      String[] strs = {"list2"};
//      List<String> list2= Arrays.asList(strs);

    Object[] array2 = list2.toArray();
    System.out.println(array2.getClass().getCanonicalName());// java.lang.String[]
    array2[0] = new Object(); // Exception in thread "main" java.lang.ArrayStoreException: java.lang.Object
```

## add(E e)
```java
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }
```
通过测试代码：
```java
ArrayList<String> list = new ArrayList<>();
list.add("111");
```
通过调试，可知调用函数如下：
```java
    public boolean add(E e) {
        // 判断elementData是否可以容下新增元素
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // 向末尾添加元素，并且使元素个数size+1
        elementData[size++] = e;
        return true;
    }
    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }
    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        // 如果初始化的为空数组，则最小开辟的空间为10，否则将返回最小所需要的空间大小minCapacity即size+1
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;// modCount表示列表被修改的次数，在单线程中没啥用，但是可以在多线程中保持列表不在使用时被修改。
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)// 所需最小容量大于elementData时扩容
        grow(minCapacity);
    }
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);// 扩充1.5倍
        if (newCapacity - minCapacity < 0)// newCapacity比minCapacity小的情况：newCapacity超过int范围，以及初始容量为1需要扩容size=1的情况。两种情况看测试代码1。
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```
```java
// 测试代码1

    ArrayList<String> list = new ArrayList<>(1);
    list.add("111");
    list.add("111");

/*
    当添加第二个时，grow函数内的参数具体为：
    private void grow(int minCapacity) { // minCapacity: 2
        // overflow-conscious code
        int oldCapacity = elementData.length; // oldCapacity: 1
        int newCapacity = oldCapacity + (oldCapacity >> 1); // oldCapacity: 1  newCapacity: 1
        if (newCapacity - minCapacity < 0) // 进入该if
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
*/
    
    // 当oldCapacity接近于MAX_VALUE时，根据扩容规则newCapacity会成为负数
    int oldCapacity = Integer.MAX_VALUE -1111;// 2147482536
    int newCapacity = oldCapacity + (oldCapacity >> 1);// -1073743492
```

可以得知，空ArrayList添加第一个元素，内部容量被扩充为10，除此之外每次扩容为旧容量的1.5倍，且添加方法为尾添加。

## add(int index,E element)

```java
    public void add(int index, E element) {
    rangeCheckForAdd(index);// 下标检查
    // 确认容量，与上面一致
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    System.arraycopy(elementData, index, elementData, index + 1, size - index); // 调用系统的复制算法
    elementData[index] = element; // 插入
    size++; // 更新size
    }

    private void rangeCheckForAdd(int index) {
        // 下标比size大或者下标小于0，都会抛出下标越界异常
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```
代码较为简单，其中涉及元素的移动，效率较低。

## addAll(Collection<? extends E> c)

```java
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray(); // 通过toArray函数生成参数集合c的Object数组。
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount // 判断是否需要扩容
        System.arraycopy(a, 0, elementData, size, numNew); // 调用arrycopy将a添加到elementData
        size += numNew; // 更改size
        return numNew != 0;
    }
```

## addAll(int index, Collection<? extends E> c)

```java
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index); // 检查下标是否合法

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount // 同上

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved); // 移动elementData数组，腾出空间给Object[] a

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew; // 复制数组进elementData并修改size
        return numNew != 0;
    }
```
System.arraycopy与copyOf()区别：

1.arraycopy()需要目标数组，将原数组拷贝到你自己定义的数组里，而且可以选择拷贝的起点和长度以及放入新数组中的位置
2.copyOf()是系统自动在内部新建一个数组，并返回该数组。


## clear()
```java
    public void clear() {
        modCount++;

        // clear to let GC do its work
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }
```

clear()方法只是将数组元素置空值，并没有选择直接将elementData=null，而是选择逐个赋null的做法，等待下次系统gc的时候回收。下次添加时减少扩容的操作。

## remove(int index) 
```java 
    public E remove(int index) {
        rangeCheck(index);

        modCount++;
        E oldValue = elementData(index);

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);// 将index后的元素前移覆盖。
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }
```
## remove(Object o)
```java
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true; // 找到第一个匹配的
                }
        }
        return false;
    }
    // 与remove逻辑一直
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                            numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }
```

## get(int index)
```java
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```
代码较简单，直接通过下标返回值。

## set(int index, E element)
```java
    public E set(int index, E element) {
        rangeCheck(index); // 下标检查

        E oldValue = elementData(index); // 提取旧值
        elementData[index] = element; // 更新
        return oldValue; //返回旧值
    }
```


查询方法大多为遍历。