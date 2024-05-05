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
        num.setNum(1); // вернули прежнее значение, но sout сработает, т.к. в кеше ключа объекта еще нет такой записи (конструктор заносит пустышки)
        num.doubleValue(); // sout сработал
        num.setNum(5);
        num.doubleValue(); // sout молчит, значение взяли из кеша
        Assertions.assertEquals(3, fr.count);
    }

    @Test
    @DisplayName("test time live in cache")
    public void test_2() throws Exception {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue(); // sout сработал
        num.setNum(5);
        num.doubleValue(); // sout сработал
        num.setNum(1); // вернули прежнее значение, но sout сработает, т.к. в кеше ключа объекта еще нет такой записи (конструктор заносит пустышки)
        num.doubleValue(); // sout сработал, время жизни еще не вышло
        Thread.sleep(500); // заснуть на 0.5 сек
        num.setNum(5);
        num.doubleValue(); // sout молчит, значение взяли из кеша
        Assertions.assertEquals(3, fr.count);
    }

    @Test
    @DisplayName("test SimpleThread clearing cache")
    public void test_3() throws Exception {
        int count = 0;
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.setNum(1);
        num.setDenum(2);
        num.doubleValue(); // sout сработал
        count++;
        for (int i = 0; i < 9; i++) {
            num.setNum(2 + i);
            num.doubleValue(); // sout сработал
            count++;
        }
        // закончили добавлять уникальные значения в кеш
        Thread.sleep(1500); // заснуть на 1.5 сек ждем очистки кеша отдельным потоком
        System.out.println("-------------");
        num.setNum(1); // вернули прежнее значение, что было до очистки
        num.doubleValue(); // sout сработал
        count++;
        Assertions.assertEquals(count, fr.count);
    }
}
