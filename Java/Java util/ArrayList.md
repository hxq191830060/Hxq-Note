1. **属性**  
   
   * **private static final int DEFAULT_CAPACITY = 10** 
     
     默认容量
   
   * **private static final Object[] EMPTY_ELEMENTDATA = {}**
     
     空数组
   
   * **transient Object[] elementData**
     
     存储元素的数组
   
   * **private int size**
     
     ArrayList中的元素个数

2. **构造方法**  
   
   * 传入容量   ，会初始化elementData
     
     ```
     this.elementData = new Object[initialCapacity];
     ```
   
   * 不传入容量，elementData初始化为空数组

3. **扩容方法**
   
   * 判断size+1是否>数据数组的长度,如果没有，就返回，不做任务操作
   
   * 如果有，进行扩容
   
   * 旧容量=数据数组的长度，新容量=旧容量+旧容量>>1(新容量为旧容量的1.5倍)
     
     用新容量创建一个新数组，将旧数组中的元素放入新数组

4. **add**
   
   * 判断size+1是否在容量允许范围内，不在的话扩容
   * elementData[size++]放入新元素

5. **remove**
* 移出index处的元素

* 将index后的元素全部都向前移动一个位置

* 将elementData[--size]=null