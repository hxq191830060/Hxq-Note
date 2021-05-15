```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    
    //默认大小
    private static final int DEFAULT_CAPACITY = 10;

    //默认的空数组
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
}

```

* 调用无参构造，内部的数组是