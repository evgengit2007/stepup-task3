package ru.stepup.course2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JunitTest {
    @Test
    @DisplayName("cache without")
    public void testWithoutCache() {
        FractionTest fr = new FractionTest(1, 2);
        fr.doubleValue();
        fr.doubleValue();
        Assertions.assertEquals(fr.count, 2);
    }

    @Test
    @DisplayName("cache work once")
    public void testCacheOnce() {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue();
        num.doubleValue();
        Assertions.assertEquals(fr.count, 1);
    }

    @Test
    @DisplayName("cache work twice")
    public void testCacheTwice() {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue();
        num.multiValue();
        Assertions.assertEquals(fr.count, 2);
    }

    @Test
    @DisplayName("Mutator work")
    public void testMutator() {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        num.doubleValue();
        num.doubleValue(); // данные в кеше
        fr.setNum(4); // данные сбросились
        num.doubleValue();
        Assertions.assertEquals(fr.count, 1);
    }

    @Test
    @DisplayName("Correct value double")
    public void testCorrectValueDouble() {
        FractionTest fr = new FractionTest(1, 2);
        Fractionable num = Utils.cache(fr);
        double result = num.doubleValue();
        Assertions.assertEquals(result, 0.5);
    }

    @Test
    @DisplayName("Correct value multi")
    public void testCorrectValueMulti() {
        FractionTest fr = new FractionTest(3, 2);
        Fractionable num = Utils.cache(fr);
        double result = num.multiValue();
        Assertions.assertEquals(result, 6);
    }
}
