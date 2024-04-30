package ru.stepup.course2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FractionInvocationHandler<T> implements InvocationHandler {
    static SingleThread singleThread;
    static HasmapThread hashmapThread;
    private T object;

    private volatile HashMap<String, Object> paramHash = new HashMap<>();

    private Thread thCache;
    private Thread thHash;

    public FractionInvocationHandler(T object) {
        this.object = object;
    }

    public void hashClear() {
        paramHash.clear();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String keyParam = ";";
        Method keyMethod = object.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (keyMethod.isAnnotationPresent(Mutator.class)) {
            return keyMethod.invoke(object, args);
        }
        if (keyMethod.isAnnotationPresent(Cache.class)) {
            Cache ch = keyMethod.getAnnotation(Cache.class); // так получить параметр из кеша
            if (thCache == null) {
                singleThread = new SingleThread(this, ch.value(), paramHash);
                thCache = new Thread(singleThread);
                thCache.start();
            } else {
                if (thCache.isAlive()) {
                    thCache.interrupt();
                }
                singleThread = new SingleThread(this, ch.value(), paramHash);
                thCache = new Thread(singleThread);
                thCache.start();
            }
            keyParam = keyParam + keyMethod.getName() + ";";
            // Получить имя и значение полей doubleValue
            // ключ строка вида: ;doubleValue;num=5;denum=2;
            Field fieldNum = object.getClass().getDeclaredField("num");
            fieldNum.setAccessible(true);
            keyParam = keyParam + "num" + "=" + fieldNum.get(object) + ";";
            Field fieldDenum = object.getClass().getDeclaredField("denum");
            fieldDenum.setAccessible(true);
            keyParam = keyParam + "denum" + "=" + fieldDenum.get(object) + ";";
            hashmapThread = new HasmapThread<Object>(object, keyMethod, keyParam, paramHash, args, result);
            thHash = new Thread(hashmapThread);
            thHash.start();
            // последовательное выполнение потоков, в этом случае более логично код потока вынести в тело метода
            thHash.join();
            paramHash = hashmapThread.getHashMap();
            result = hashmapThread.getResult();
            if (result != null) {
                System.out.println("result = " + result);
                return result;
            }
        }
        return keyMethod.invoke(object, args);
    }
}
