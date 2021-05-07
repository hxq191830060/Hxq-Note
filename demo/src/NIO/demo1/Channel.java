package NIO.demo1;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/*
Channel是个接口
常用的Channel类
FileChannel抽象类——FileChannelImp文件
DatagramChannel——UDP
ServerSocketChannel抽象类——ServerSocketChannelImp——TCP
SocketChannel抽象类——SocketChannelImp ——TCP
 */
public class Channel {
    @Test
    //FileChannel写文件
    public void test1() throws IOException {
        FileOutputStream fileOutputStream=new FileOutputStream("src/demo1/hello.txt");
        FileChannel fileChannel=fileOutputStream.getChannel();

        String str="hello world!";
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());

        //切记切记，一定要切换，重置position
        byteBuffer.flip();
        fileChannel.write(byteBuffer);

        fileOutputStream.close();
    }

    @Test
    //FileChannel读文件
    public void test2(){

    }
}
