package ru.stepup.course2;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FractionInvocationHandler<T> implements InvocationHandler {
    private T object;

    private HashMap<String, Object> paramHash = new HashMap<>();

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
            keyParam = keyParam + keyMethod.getName() + ";";
            // Получить имя и значение всех полей
            Field[] field = object.getClass().getDeclaredFields();
            for (Field f : field) {
                f.setAccessible(true);
//                System.out.println(f.getName() + "=" + f.get(object));
                keyParam = keyParam + f.getName() + "=" + f.get(object) + ";";
            }
//            System.out.println("keyParam: " + keyParam);
            Cache ch = keyMethod.getAnnotation(Cache.class); // так получить параметр из кеша
//            System.out.println("parameter cache: " + ch.value()); // так получить параметр из кеша
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
