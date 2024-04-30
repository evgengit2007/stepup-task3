package ru.stepup.course2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/*
список тестов:
1. Проверить попадание в историю кеша значений и вывод значения из кеша
2. Проверить работу кеширования до истечения времени жизни в кеше (кеш не должен обнуляться)
3. Проверить работу кеширования после истечения времени жизни в кеше (кеш должен обнулиться)

*/
public class JunitTest {
    @Test
    @DisplayName("test history cache")
    public void test_1() {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); // sout сработал
        num.setNum(5);
        num.doubleValue(); // sout сработал
        num.setNum(1); // вернули прежнее значение
        num.doubleValue(); // sout молчит, значение взяли из кеша
        Assertions.assertEquals(fr.count, 2);
    }

    @Test
    @DisplayName("test time live in cache")
    public void test_2() throws Exception {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); // sout сработал
        num.setNum(5);
        num.doubleValue(); // sout сработал
        Thread.sleep(500); // заснуть на 0.5 сек
        num.setNum(1); // вернули прежнее значение
        num.doubleValue(); // sout молчит, значение взяли из кеша
        Assertions.assertEquals(fr.count, 2);
    }

    @Test
    @DisplayName("test time live expiried in cache")
    public void test_3() throws Exception {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); // sout сработал
        num.setNum(5);
        num.doubleValue(); // sout сработал
        Thread.sleep(1500); // заснуть на 1.5 сек
        num.setNum(1); // вернули прежнее значение
        num.doubleValue(); // sout сработал
        Assertions.assertEquals(fr.count, 3);
    }
}
