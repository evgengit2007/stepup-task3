package ru.stepup.course2;

import java.lang.reflect.Proxy;

public class Utils {
    public static <T> T cache(T objectVol) {
        ClassLoader classLoader = objectVol.getClass().getClassLoader();
        Class[] interfaces = objectVol.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(classLoader,
                interfaces,
                new FractionInvocationHandler(objectVol));
    }

}
