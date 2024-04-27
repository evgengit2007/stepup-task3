package ru.stepup.course2;

public class MainApp {
    public static void main(String[] args) {
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
        num.setNum(1);
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
    }
}
