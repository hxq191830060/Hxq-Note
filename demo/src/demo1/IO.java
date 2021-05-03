package demo1;

import org.junit.Test;

import java.io.*;
import java.util.stream.Stream;

public class IO {
    @Test
    //FileReader的测试
    public void test1(){
        File file=new File("src/demo1/hello.txt");
        try {
            FileReader reader=new FileReader(file);
            char[]buffer=new char[10];
            int len=reader.read(buffer,2,4);
            System.out.println(len);
            System.out.println();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    //FileWriter的测试
    public void test2(){
        File file=new File("src/demo1/writer.txt");
        try {
            FileWriter writer=new FileWriter(file);
            writer.write(23);
            writer.write("\n");
            writer.write("e123213");
            writer.write("\n");
            writer.write("123456",1,4);
            writer.append("\n");
            writer.append("dasdadasdad");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3(){
        Stream stream=Stream.of(1,3,34,4);
        stream.sequential();
        stream.parallel();
    }

    @Test
    public void test4() throws IOException {
        File file=new File("src/demo1/hello.txt");
        RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
        randomAccessFile.seek(2);
        randomAccessFile.writeBytes("123");
    }

    @Test
    public void test5() throws IOException, ClassNotFoundException {
        FileOutputStream outputStream=new FileOutputStream("src/demo1/hello.txt");
        byte[]bytes=new byte[]{'a','b','e','1','2','3'};
        outputStream.write(bytes,3,2);
    }

    @Test
    public void test6() throws IOException {
        FileReader reader=new FileReader("src/demo1/hello.txt");
        System.out.println((char)reader.read());
    }

    @Test
    public void test7() throws IOException {
    }

    @Test
    public void test8(){

    }
}
