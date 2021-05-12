package 多线程;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

    public static void main(String[]args){

        ThreadLocal
        ThreadPoolExecutor threadpool=new ThreadPoolExecutor(3,6,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(2));
        for(int i=0;i<6;i++){
            threadpool.execute(new MyThread("线程"+i));
        }
    }
    public static class MyThread implements Runnable{
        public String name;
        public MyThread(String name){
            this.name=name;
        }
        @Override
        public void run() {
            for(int i=0;i<10;i++){
                System.out.println(Thread.currentThread().getName()+" "+name);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
