package ru.stepup.course2;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FractionInvocationHandler<T> implements InvocationHandler {
    private T object;
    private HashMap<Method, CacheObj> cacheHash = new HashMap<>();

    private HashMap<Field, Object> paramInt = new HashMap<>();

    private class CacheObj<T> {
        private T objectId;
        private Object result;

        public CacheObj(T objectId, Object result) {
            this.objectId = objectId;
            this.result = result;
        }

        @Override
        public String toString() {
            return "CacheObj{" +
                    "object=" + object +
                    ", result=" + result +
                    '}';
        }
    }

    public FractionInvocationHandler(T object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        CacheObj<Object> cacheObj;
        Method key = object.getClass().getMethod(method.getName(), method.getParameterTypes());
        // перебираем аннотации в последовательности Mutator, потом Cache
        // теперь если даже разработчик поставит во Fraction на методе doubleValue обе аннотации,
        // или заменит Cache на Mutator, то наша прокси будет считать правильно, правда без кэширования,
        // т.к. первой отработает Mutator

        // получить значение одного конкретного поля
/*
        Field field = object.getClass().getDeclaredField("num");
        field.setAccessible(true);
        System.out.println("num = " + field.get(object));
*/

        // Получить значение всех полей
        Field[] field1 = object.getClass().getDeclaredFields();
        for (Field f: field1) {
            f.setAccessible(true);
            paramInt.put(f, object);
            System.out.println(f.getName() + " = " + f.get(object));
        }

        if (key.isAnnotationPresent(Mutator.class)) {
            cacheHash.clear(); // сбросить все кэширования для методов с аннотацией Cache
            return key.invoke(object, args);
        }
        if (key.isAnnotationPresent(Cache.class)) {

            Cache ch = key.getAnnotation(Cache.class); // так получить параметр из кеша
            System.out.println(ch.value()); // так получить параметр из кеша

/*
            result = key.invoke(object, args);
            CacheObj cacheObj = new CacheObj<>(object, result);
            cacheHash.put(key, cacheObj);
*/

            if (cacheHash.containsKey(key)) {
                cacheObj = cacheHash.get(key);
                Field[] fieldObj1 = cacheObj.objectId.getClass().getDeclaredFields();
                for (Field f: fieldObj1) {
                    f.setAccessible(true);
                    System.out.println("cacheObj, " + f.getName() + " = " + f.get(cacheObj.objectId));
                }
                return cacheObj.result;
            }
            result = key.invoke(object, args);
            cacheObj = new CacheObj<Object>(object, result);
            cacheHash.put(key, cacheObj);
//            Field field = cacheObj.objectId.getClass().getDeclaredField("num");
//            field.setAccessible(true);
//            System.out.println("objectId, num = " + field.get(cacheObj.objectId));

            Field[] fieldObj2 = cacheObj.objectId.getClass().getDeclaredFields();
            for (Field f: fieldObj2) {
                f.setAccessible(true);
                System.out.println("cacheObj, " + f.getName() + " = " + f.get(cacheObj.objectId));
            }
            return cacheObj.result;
        }
        return key.invoke(object, args);
    }
}
