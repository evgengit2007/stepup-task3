package ru.stepup.course2;

public class MainApp {
//    static SingleThread st1;
    public static void main(String[] args) throws Exception {
//        st1 = new SingleThread("sss", null);
//        Thread th1 = new Thread(st1);
//        th1.start();
        Fraction fr = new Fraction(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
        System.out.println("set num = 5");
        System.out.println("set denum = 20");
        num.setNum(5);
        num.setDenum(20);
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
        System.out.println("set num = 1");
        Thread.sleep(50);
        num.setNum(1);
        System.out.println("1-------------");
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
        System.out.println("set num = 5");
        num.setNum(5);
        Thread.sleep(500);
        System.out.println("2-------------");
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
//        th1.interrupt();
    }
}
