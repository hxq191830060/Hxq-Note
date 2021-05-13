package 多线程;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class Call1 {

    public static void main(String[]args){
        ReentrantLock
        for(int i=0;i<10;i++){
            new Thread((Runnable) new Call("线程"+i)).start();
        }
    }
    public static class Call implements Callable<String>{
        String name;
        ReentrantLock
        public Call(String name){
            this.name=name;
        }
        @Override
        public String call() throws Exception {
            System.out.println(name);
            return name;
        }
    }
}
