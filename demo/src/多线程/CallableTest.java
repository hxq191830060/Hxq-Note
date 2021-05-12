package 多线程;

import sun.misc.Unsafe;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;

public class CallableTest {

    public static void main(String[]args){
        ExecutorService executor=Executors.newSingleThreadExecutor();
        for(int i=0;i<4;i++){
            Future<Integer>future=executor.submit(new CallableClass(i));
            try {
                System.out.println(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }
    public static class CallableClass implements Callable<Integer>{
        private int num;
        public CallableClass(int num){
            this.num=num;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println(Thread.currentThread().getName()+" 计算开始");
            for(int i=0;i<10;i++){
                num+=1;
                Thread.sleep(100);
            }
            System.out.println(Thread.currentThread().getName()+" 计算结束");
            return num;
        }
    }
}
