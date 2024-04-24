package ru.stepup.course2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FractionInvocationHandler<T> implements InvocationHandler {
    private T object;
    private HashMap<Method, Object> hashMap = new HashMap<>();

    public FractionInvocationHandler(T object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        Method key = object.getClass().getMethod(method.getName(), method.getParameterTypes());
        // перебираем аннотации в последовательности Mutator, потом Cache
        // теперь если даже разработчик поставит во Fraction на методе doubleValue обе аннотации,
        // или заменит Cache на Mutator, то наша прокси будет считать правильно, правда без кэширования,
        // т.к. первой отработает Mutator
        if (key.isAnnotationPresent(Mutator.class)) {
            hashMap.clear(); // сбросить все кэширования для методов с аннотацией Cache
            return key.invoke(object, args);
        }
        if (key.isAnnotationPresent(Cache.class)) {
            if (hashMap.containsKey(key)) {
                result = hashMap.get(key);
                return result;
            }
            result = (Double) key.invoke(object, args);
            hashMap.put(key, result);
            return result;
        }
        return key.invoke(object, args);
    }
}
