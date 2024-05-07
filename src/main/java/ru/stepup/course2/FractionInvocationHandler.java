package ru.stepup.course2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FractionInvocationHandler<T> implements InvocationHandler {
    // прокси класс, здесь основная логика прокси
    SingleThreadExt singleThreadExt;
    private T object;

    private int periodCheckEvent; // период сканирования переменной eventNotify доп. потока, берется из параметра periodCheck аннотации @Cache

    private ConcurrentHashMap<StateObj, ConcurrentHashMap<Method, Result>> mapState = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Method, Result> mapRes;

    private StateObj stateObj;
    private Thread thCache;

    public FractionInvocationHandler(T object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object res;
        Method keyMethod = object.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (stateObj == null) {
            stateObj = new StateObj();
            mapState.put(stateObj, new ConcurrentHashMap<>());
        }
        if (keyMethod.isAnnotationPresent(Mutator.class)) {
            stateObj = new StateObj(stateObj, method, args);
            Set<StateObj> keys = mapState.keySet();
            if (!mapState.containsKey(stateObj)) {
                mapState.put(stateObj, new ConcurrentHashMap<>());
            }
            return keyMethod.invoke(object, args);
        }
        if (keyMethod.isAnnotationPresent(Cache.class)) {
            Cache ch = keyMethod.getAnnotation(Cache.class); // получить параметр из кеша
            if (thCache == null) {
                periodCheckEvent = ch.periodCheck();
                singleThreadExt = new SingleThreadExt();
                singleThreadExt.eventNotify = System.currentTimeMillis();
                thCache = new Thread(singleThreadExt);
                thCache.setDaemon(true); // объявляем поток демоном, чтобы по завершении работы основного потока завершился и дочерний поток SingleThread
                thCache.start();
            }
            mapRes = mapState.get(stateObj);
            if (mapRes == null) {
                mapRes = new ConcurrentHashMap<>();
            }
            // после обсуждения с преподавателем в части многопоточки основные замечания:
            // использование поиска ключа через containsKey опасно для мапы, в которой может быть конкуренция по потокам.
            // правильно сразу получать значение (если уже удалено, не страшно, вернет в Result значение null)
            // дальше проверять Result на не равно null, если да, то проверять время и обновлять значение, после чего return
            // если нет, вызываем метод invoke, после чего тот же put в мапу и return
            if (mapRes.containsKey(method)) {
                Result result = mapRes.get(method);
                // <- с включением данного кода дополнительный поток по очистке не проверить в тестах, здесь он срабатывает быстрее
//                if (result.getTimeLeave() < System.currentTimeMillis()) {
//                    result.setRes(null); // искусственно ставим значение null чтобы сделать перезапуск подсчета
//                }
                // -> с включением данного кода дополнительный поток по очистке не проверить в тестах
                result.setTimeLeave(System.currentTimeMillis() + ch.value());
                res = result.getRes();
                if (res != null) {
                    mapRes.put(method, result);
                    mapState.put(stateObj, mapRes);
                    return res;
                }
                res = keyMethod.invoke(object, args);
                result.setRes(res);
                mapRes.put(method, result);
                mapState.put(stateObj, mapRes);
                return res;
            }
            res = keyMethod.invoke(object, args);
            Result result = new Result(System.currentTimeMillis() + ch.value(), res);
            mapRes.put(method, result);
            mapState.put(stateObj, mapRes);
            return res;
        }
        return keyMethod.invoke(object, args);
    }

    private class SingleThreadExt extends Thread {
        volatile long eventNotify;

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                    while (System.currentTimeMillis() - eventNotify < periodCheckEvent) {
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
}