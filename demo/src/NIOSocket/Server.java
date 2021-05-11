package NIOSocket;

import sun.nio.ch.ThreadPool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class Server  {

    public static void main(String[]args){
        //0启动服务器线程
        new Thread(new ReactorTash()).start();
    }

    public static class ReactorTash implements Runnable{

        private Selector selector=null;

        public ReactorTash(){
            try {
                //1打开ServerSocketChannel，用于监听Client的连接，它是所有Client连接的父Channel
                ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();

                //2监听接口，设置链接为非阻塞模式
                serverSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getByName("localhost"),8083));
                serverSocketChannel.configureBlocking(false);

                //3创建多路复用器Selector，并启动
                selector=Selector.open();

                //4将ServerSocketChannel注册到多路复用器Selector上,监听ACCEPT时间
                SelectionKey key=serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void run() {
            while(true){
                try {
                    selector.select(1000);
                    Set<SelectionKey>selectionKeys=selector.selectedKeys();
                    Iterator<SelectionKey>it=selectionKeys.iterator();
                    SelectionKey key=null;

                    while(it.hasNext()){
                        key=it.next();
                        it.remove();
                        try{
                            if(key.isValid()){
                                //处理Accept请求
                                if(key.isAcceptable()){
                                    //多路复用器监听到有新的Client接入，处理新的连接请求，完成TCP三次握手，建立连接
                                    ServerSocketChannel ssc=(ServerSocketChannel) key.channel();
                                    SocketChannel sc=ssc.accept();
                                    //设置Client链路为非阻塞模式
                                    sc.configureBlocking(false);
                                    sc.socket().setReuseAddress(true);
                                    //将新接入的Client连接注册到多路复用器Selector上，监听读操作，读取Client传递的信息
                                    sc.register(selector,SelectionKey.OP_READ);
                                }
                                //处理Read请求
                                if(key.isReadable()){
                                    SocketChannel sc=(SocketChannel) key.channel();
                                    ByteBuffer readBuffer=ByteBuffer.allocate(1024);
                                    int readBytes=sc.read(readBuffer);


                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
