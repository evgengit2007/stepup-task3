package ru.stepup.course2;

import java.util.HashMap;

public class SingleThread implements Runnable {

    FractionInvocationHandler fractionInvocationHandler;

    private Object object;
    private int cacheValue;

    private HashMap<String, Object> hashMap;

    public SingleThread(Object object, int cacheValue, HashMap<String, Object> hashMap) {
        this.object = object;
        this.cacheValue = cacheValue;
        this.hashMap = new HashMap<>(hashMap);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) { // Thread.interrupted() прерывание потока извне
            try {
                Thread.sleep(cacheValue);
            } catch (InterruptedException e) {
                return;
            }
            fractionInvocationHandler = (FractionInvocationHandler) object;
            fractionInvocationHandler.hashClear();
            return;
        }
    }
}
