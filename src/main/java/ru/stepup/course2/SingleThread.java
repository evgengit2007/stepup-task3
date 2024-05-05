package ru.stepup.course2;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SingleThread implements Runnable {
    // не используется, но оставлен в проекте на всякий случай
    // отдельный поток по очистке кеша от старых значений
    // зависит от основного потока (демон), чтобы по завершении основного потока завершиться тоже
    // вызывает очистку кеша при достижении записей в кеше больше 5
    // сам процесс очистки кеша сделан через копирование оригинальной мапы, очистки копии, затем восстановлении

    volatile long eventNotify;
    private ConcurrentHashMap<StateObj, ConcurrentHashMap<Method, Result>> mapState;

    public SingleThread(ConcurrentHashMap<StateObj, ConcurrentHashMap<Method, Result>> mapState) {
        this.mapState = mapState;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                while (System.currentTimeMillis() - eventNotify < 1000) {
                    java.lang.Thread.onSpinWait();
                }
                processEvent();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void processEvent() {
        Result result;
        ConcurrentHashMap<StateObj, ConcurrentHashMap<Method, Result>> mapStateClear = new ConcurrentHashMap<>(mapState); // делаем копию текущей мапы для последующей очистки
        ConcurrentHashMap<StateObj, ConcurrentHashMap<Method, Result>> mapStateNew = new ConcurrentHashMap<>(); // делаем чистую мапу, куда будем писать новые значения
        mapState = mapStateNew; // назначаем ссылку с чистой мапой на текущую, новые значения будут писаться в чистую мапу
        if (!mapStateClear.isEmpty()) { // если есть записи
            Set<StateObj> mapKey = new HashSet<>(mapStateClear.keySet());
            for (StateObj st : mapKey) {
                ConcurrentHashMap<Method, Result> stMapRes = new ConcurrentHashMap<>(mapStateClear.get(st));
                Set<Method> met = new HashSet<>(stMapRes.keySet());
                for (Method key : met) {
                    result = stMapRes.get(key);
                    if (result == null || (result.getTimeLeave() < System.currentTimeMillis())) { // попали в удаление
                        stMapRes.remove(key);
                    }
                }
                if (stMapRes.isEmpty()) {
                    mapStateClear.remove(st);
                } else {
                    mapStateClear.put(st, stMapRes);
                }
            }
        }
        ConcurrentHashMap<StateObj, ConcurrentHashMap<Method, Result>> mapStateAggr = new ConcurrentHashMap<>(mapState); // создаем объединенную мапу для мапы Clear и мапы New
        mapStateAggr.putAll(mapStateClear);
        mapState = mapStateAggr;
        eventNotify = System.currentTimeMillis();
    }
}
