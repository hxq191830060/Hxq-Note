import java.util.Arrays;
import java.util.List;

public class JVMInstructionTest implements Runnable {
    public JVMInstructionTest() {
        System.out.println("constructor method");
    }

    private void s() {
        System.out.println("private method");
    }

    static void print() {
        System.out.println("static method");
    }

    void p() {
        System.out.println("instance method");
    }

    public void d(String str) {
        System.out.println("for method handle " + str);
    }

    static void ddd(String str) {
        System.out.println("static method for method handle " + str);
    }

    public static void main(String[] args) throws Throwable {
        /**
         * invoke special
         */
        JVMInstructionTest test = new JVMInstructionTest();
        /**
         * invoke special
         */
        test.s();
        /**
         * invoke virtual
         */
        test.p();
        /**
         * invoke static
         */

        print();
        /**
         * invoke interface
         */
        Runnable r = new JVMInstructionTest();
        r.run();
        /**
         * Java 8中，lambda表达式和默认方法时，底层会生成和使用invoke dynamic
         * invoke dynamic
         */
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        list.stream().forEach(System.out::println);

        Class clazz=JVMInstructionTest.class;
    }

    @Override
    public String toString(){
        return super.toString();
    }
    @Override
    public void run() {
        System.out.println("interface method");
    }
}