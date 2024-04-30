package ru.stepup.course2;

import java.lang.reflect.Method;
import java.util.HashMap;

public class HasmapThread<T> implements Runnable {

    private T object;

    private Method keyMethod;
    private String keyParam;
    private HashMap<String, Object> hashMap;

    private Object[] args;

    private Object result;

    public HasmapThread(T object, Method keyMethod, String keyParam, HashMap<String, Object> hashMap, Object[] args, Object result) {
        this.object = object;
        this.keyMethod = keyMethod;
        this.keyParam = keyParam;
        this.hashMap = new HashMap<>(hashMap);
        this.args = args;
        this.result = result;
    }

    public HashMap<String, Object> getHashMap() {
        return new HashMap<>(hashMap);
    }

    public Object getResult() {
        return result;
    }

    @Override
    public void run() {
        Object result = null;
        try {
            hashMap = getHashMap();
            if (hashMap.containsKey(keyParam)) {
                result = hashMap.get(keyParam);
            }
            if (result != null) {
                this.result = result;
            } else {
                result = keyMethod.invoke(object, args);
                hashMap.put(keyParam, result);
                this.result = result;
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
