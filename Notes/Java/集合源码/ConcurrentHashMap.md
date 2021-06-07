**JDK1.8**

**类似于HashMap**

**hash函数**

```
h=key.hashCode();
return (h ^ (h >>> 16)) & HASH_BITS;
```



**tableSizeFor()**

**保证数组的capacity一定为2的n次方**



**构造方法**

初始化数组的capacity,不会初始化table



**put**

①如果table为空，初始化table

②计算hash值，取得索引 i=hash&(数组长度-1)

③如果table[i]为null，通过CAS操作存入节点

​    如果table[i]不为null，通过sychonized给table[i]上锁