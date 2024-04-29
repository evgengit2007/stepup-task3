package ru.stepup.course2;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FractionInvocationHandler<T> implements InvocationHandler {
    static SingleThread singleThread;
    private T object;

    private HashMap<String, Object> paramHash = new HashMap<>();

    private Thread th1;

    public FractionInvocationHandler(T object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        String keyParam = ";";
        Method keyMethod = object.getClass().getMethod(method.getName(), method.getParameterTypes());
//        System.out.println("-----------------------");
//        System.out.println(keyMethod.getName());
        if (keyMethod.isAnnotationPresent(Mutator.class)) {
            return keyMethod.invoke(object, args);
        }
        if (keyMethod.isAnnotationPresent(Cache.class)) {
            Cache ch = keyMethod.getAnnotation(Cache.class); // так получить параметр из кеша
            System.out.println("parameter cache: " + ch.value()); // так получить параметр из кеша
            if (th1 == null) {
                Runnable st1 = () -> {
                    while (!Thread.interrupted()) { // Thread.interrupted() прерывание потока извне
                        try {
                            System.out.println("!!!!!!!");
                            Thread.sleep(ch.value());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("11111111");
                        paramHash.clear();
                        return;
                    }
                };
                th1 = new Thread(st1);
                th1.start();
                System.out.println(th1.toString());
            }
/*
            if (th1 == null) {
                singleThread = new SingleThread(ch.value());
                th1 = new Thread(singleThread);
                th1.start();
            }
*/
            keyParam = keyParam + keyMethod.getName() + ";";
            // Получить имя и значение всех полей
            Field[] field = object.getClass().getDeclaredFields();
            for (Field f : field) {
                f.setAccessible(true);
//                System.out.println(f.getName() + "=" + f.get(object));
                keyParam = keyParam + f.getName() + "=" + f.get(object) + ";";
            }
//            System.out.println("keyParam: " + keyParam);
            if (paramHash.containsKey(keyParam)) {
                result = paramHash.get(keyParam);
                System.out.println("cache result = " + result);
                return result;
            }
            result = keyMethod.invoke(object, args);
            paramHash.put(keyParam, result);
            System.out.println("calculate result = " + result);
            return result;
        }
        return keyMethod.invoke(object, args);
    }
}
