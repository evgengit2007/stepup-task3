package ru.stepup.course2;

import java.util.HashMap;

public class SingleThread implements Runnable {

    private int cacheValue;

    public SingleThread(int cacheValue) {
        this.cacheValue = cacheValue;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) { // Thread.interrupted() прерывание потока извне
            try {
                System.out.println("!!!!!!!");
                Thread.sleep(cacheValue);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("11111111");
//            paramHash.clear();
            return;
        }
    }
}
