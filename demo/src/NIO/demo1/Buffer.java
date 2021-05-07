package NIO.demo1;

import org.junit.Test;

import java.nio.IntBuffer;

public class Buffer {
    @Test
    //buffer使用
    public void test1(){
        //创建一个Buffer ,大小为5，可以存放5个int
        IntBuffer intBuffer=IntBuffer.allocate(5);
        /*
        Buffer内有一个数组hb——用来存储数据,数组大小就是Capactiry
        buffer的四大属性
        Capacity:容量，可以容纳的最大数据量，创建时确定，不可修改
        limit:buffer存储的终点,不能对>=limit的位置进行读写,可修改(初始化时=Capacity)
        postion：表示下一个要被读或写的元素的索引,每次读写buffer都会改变position的值(初始值为0)
        Mark：表记，调用mark()——会让mark=position，调用reset()——会让postion=mark(初始值为-1)

        这里,创建了一个容量为5的buffer——Capacity=5,limit=5
         */


        //向Buffer中存放数据——put()
        //put(int number)——hb[position]=number,position+=1;
        intBuffer.put(10);
        int[]nums=new int[]{11,12,13,14};
        intBuffer.put(nums);

        //获取buffer的最大容量
        //System.out.println(intBuffer.capacity());


        //从buffer中读取数据
        //读写切换，Buffer可读可写,通过flip()切换模式
        //flip()让limit=position,postion=0;
        // 创建Buffer的时候，默认为写，写了n个数据后，position=n,调用flip(),切换为读,limit=position,是为了防止读的时候读到无元素位置
        intBuffer.flip();

        //buffer里面维持了个数组hb用来存储数据,还维持了个position表示下一个要读写元素的索引
        //当前position>=limit,hasRemaining()返回false,否则返回true
        //get(),返回hb[position],position+=1
        //get(int index); 与position无关,返回hb[index]
        intBuffer.get();
        intBuffer.get(2);

    }
}
