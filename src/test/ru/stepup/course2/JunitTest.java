package ru.stepup.course2;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JunitTest {
    @Test
    @SneakyThrows(NullPointerException.class)
    public void testFraction() {
        // корректно создан экземпляр
        Assertions.assertDoesNotThrow(() -> new Fraction(1, 2));
        // сеттеры корректно
        Fraction fraction = new Fraction(1, 2);
        Assertions.assertDoesNotThrow(() -> fraction.setNum(5));
        Assertions.assertDoesNotThrow(() -> fraction.setDenum(5));
        // doubleValue корректно
        Assertions.assertDoesNotThrow(() -> fraction.doubleValue());
        // doubleValue корректно, вычисление значения
        Assertions.assertEquals(fraction.doubleValue(), 1.00F);
        // doubleValue исключения не будет хоть и деление на ноль
        fraction.setDenum(0);
        Assertions.assertDoesNotThrow(() -> fraction.doubleValue());
        // multiValue корректно
        Assertions.assertDoesNotThrow(() -> fraction.multiValue());
    }

    @Test
    @SneakyThrows(NullPointerException.class)
    public void testUtils() {
        // Fractionable корректно
        Fraction fr = new Fraction(1, 2);
        Assertions.assertDoesNotThrow(() -> (Fractionable) Utils.cache(fr));
    }

    @Test
    @SneakyThrows(NullPointerException.class)
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
    @SneakyThrows(NullPointerException.class)
    public void testMainApp() {
        // проверка запускающего модуля MainApp
        String[] str = null;
        Assertions.assertDoesNotThrow(() -> MainApp.main(str));
    }
}
