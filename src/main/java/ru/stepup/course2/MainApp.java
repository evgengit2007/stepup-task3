package ru.stepup.course2;

public class MainApp {
    public static void main(String[] args) throws Exception {
        Fraction fr = new Fraction(1, 2);
        System.out.println("num = 1, denum = 2");
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
        System.out.println("set num = 5");
        num.setNum(5);
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
        System.out.println("set num = 1, sleep 500");
        Thread.sleep(500);
        num.setNum(1);
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
        System.out.println("set num = 5, sleep 1500");
        num.setNum(5);
        Thread.sleep(1500);
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.multiValue(); // sout сработал
        num.multiValue(); // sout молчит
    }
}
