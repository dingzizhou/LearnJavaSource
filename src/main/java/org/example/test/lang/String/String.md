## String
String实现了java.io.Serializable(可序列化)、Comparable<String>(可比较)、CharSequence(字符串基本操作)三个接口
#
## 成员变量与常量
```java
//存储字符串
private final char value[];
//哈希值缓存
private int hash;
//序列化
private static final long serialVersionUID = -6849794470754667710L;
//也跟序列化有关
private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
```
#
## 构造函数

String有很多构造函数，提供了通过String，StringBuilder，StringBuffer，char数组，int数组（CodePoint），byte数组（需要指定编码）进行初始化。

```java 
    public String() {
        this.value = "".value;
    }
```
无参构造，由于字符串不可变，所以不需要使用此构造器。
```java
    public String(String original) {
        this.value = original.value;
        this.hash = original.hash;
    }
````
创建一个参数的副本，除了特殊用途不需要使用此构造器。
```java
    //字符串
    public String(char value[]) {
        this.value = Arrays.copyOf(value, value.length);
    }

    public String(char value[], int offset, int count) {
        // 检查是否越界
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            // count == 0 offset合法则为空
            if (offset <= value.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        this.value = Arrays.copyOfRange(value, offset, offset+count);
    }
```
都利用Arrays.copy来复制char数组。

```java
    // 代码点(Unicode)构造String test2
    public String(int[] codePoints, int offset, int count) {
        // 检查是否越界
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= codePoints.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > codePoints.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }

        final int end = offset + count;

        // 计算数组大小
        // Pass 1: Compute precise size of char[]
        int n = count;
        for (int i = offset; i < end; i++) {
            int c = codePoints[i];
            if (Character.isBmpCodePoint(c))
                continue;
            else if (Character.isValidCodePoint(c))
                n++;
            else throw new IllegalArgumentException(Integer.toString(c));
        }

        // Pass 2: Allocate and fill in char[]
        final char[] v = new char[n];
        // 运用Character类将代码点转化为字符串
        for (int i = offset, j = 0; i < end; i++, j++) {
            int c = codePoints[i];
            if (Character.isBmpCodePoint(c))
                v[j] = (char)c;
            else
                Character.toSurrogates(c, v, j++);
        }

        this.value = v;
    }
```
从代码点初始化String在后面版本的JDK有比较大的改变。
```java 
    // 已经弃用的两个
    @Deprecated
    public String(byte ascii[], int hibyte, int offset, int count) {
        checkBounds(ascii, offset, count);
        char value[] = new char[count];

        if (hibyte == 0) {
            for (int i = count; i-- > 0;) {
                value[i] = (char)(ascii[i + offset] & 0xff);
            }
        } else {
            hibyte <<= 8;
            for (int i = count; i-- > 0;) {
                value[i] = (char)(hibyte | (ascii[i + offset] & 0xff));
            }
        }
        this.value = value;
    }
    @Deprecated
    public String(byte ascii[], int hibyte) {
        this(ascii, hibyte, 0, ascii.length);
    }

    // 检查是否越界
    private static void checkBounds(byte[] bytes, int offset, int length) {
        if (length < 0)
            throw new StringIndexOutOfBoundsException(length);
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        if (offset > bytes.length - length)
            throw new StringIndexOutOfBoundsException(offset + length);
    }
    public String(byte bytes[], int offset, int length, String charsetName)
            throws UnsupportedEncodingException {
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        checkBounds(bytes, offset, length);
        this.value = StringCoding.decode(charsetName, bytes, offset, length);
    }
    public String(byte bytes[], int offset, int length, Charset charset) {
        if (charset == null)
            throw new NullPointerException("charset");
        checkBounds(bytes, offset, length);
        this.value =  StringCoding.decode(charset, bytes, offset, length);
    }
    public String(byte bytes[], String charsetName)
            throws UnsupportedEncodingException {
        this(bytes, 0, bytes.length, charsetName);
    }
    public String(byte bytes[], Charset charset) {
        this(bytes, 0, bytes.length, charset);
    }
    public String(byte bytes[], int offset, int length) {
        checkBounds(bytes, offset, length);
        this.value = StringCoding.decode(bytes, offset, length);
    }
    public String(byte bytes[]) {
        this(bytes, 0, bytes.length);
    }
```