package ru.stepup.course2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JunitTest {
    @Test
    @DisplayName("test Mutator")
    public void test_1() {
        FractionJunit fr = new FractionJunit(1,2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); // sout сработал
        num.doubleValue(); // sout молчит
        num.setNum(2);
        num.doubleValue(); // sout молчит
        Assertions.assertEquals(fr.countStartDouble, 2
        );
    }

    @Test
    public void testUtils() {
        // Fractionable корректно
        Fraction fr = new Fraction(1, 2);
        Assertions.assertDoesNotThrow(() -> (Fractionable) Utils.cache(fr));
    }

    @Test
    public void testFractionInvocationHandler() {
        // FractionInvocationHandler корректно
        Fraction fr = new Fraction(1, 2);
        Assertions.assertDoesNotThrow(() -> new FractionInvocationHandler(fr));
    }

    @Test
    public void testInvokeMethod() {
        FractionJunit fr = new FractionJunit(1, 2);
        Fractionable num = Utils.cache(fr);
        // первый вызов doubleValue
        num.doubleValue();
        System.out.println("fr.countStartDouble = " + fr.countStartDouble);
        // повторный вызов doubleValue будет из кэша
        num.doubleValue();
        System.out.println("fr.countStartDouble = " + fr.countStartDouble);
        Assertions.assertEquals(1, fr.countStartDouble);
    }

    @Test
    public void testMainApp() {
        // проверка запускающего модуля MainApp
        String[] str = null;
        Assertions.assertDoesNotThrow(() -> MainApp.main(str));
    }
}
