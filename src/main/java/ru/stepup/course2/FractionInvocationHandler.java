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
//        Field[] field = object.getClass().getDeclaredFields();
//        for (Field f : field) {
//            f.setAccessible(true);
//            try {
//                System.out.println(f.get(object));
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        stateObj = new StateObj();
//        Method[] method = object.getClass().getMethods();
//        for (Method m : method) {
//            System.out.println(m.getAnnotation(Mutator.class));
//            if (m.isAnnotationPresent(Mutator.class)) {
//                System.out.println(m.getName());
//                try {
//                    Field f = object.getClass().getField("num");
//                    f.setAccessible(true);
//                    System.out.println(f);
//                } catch (NoSuchFieldException e) {
//                    throw new RuntimeException(e);
//                }
//                stateObj = new StateObj(stateObj, m, List.of(1).toArray());
//            }
//            mapState.put(stateObj, new HashMap<>());
//        }
//        stateObj = new StateObj();
//        mapRes = new HashMap<>();
//        Method[] met = object.getClass().getMethods();
//        mapRes.put()
//        mapState.put(stateObj, mapRes);
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
//            System.out.println("1 " + stateObj.getMapValues());
            Set<StateObj> keys = mapState.keySet();
//            for (StateObj st : keys) {
//                System.out.println("2 " + mapState);
//                mapState.remove(st);
//                System.out.println("22 " + mapState);
//            }
            if (!mapState.containsKey(stateObj)) {
                mapState.put(stateObj, new ConcurrentHashMap<>());
//                System.out.println("3 " + mapState.get(stateObj));
            }
            return keyMethod.invoke(object, args);
        }
        if (keyMethod.isAnnotationPresent(Cache.class)) {
            Cache ch = keyMethod.getAnnotation(Cache.class); // получить параметр из кеша
//            System.out.println("mapState = " + mapState);
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
//                    System.out.println("res 70 = " + res);
                    return res;
                }
                res = keyMethod.invoke(object, args);
                result.setRes(res);
                mapRes.put(method, result);
                mapState.put(stateObj, mapRes);
//                System.out.println("res 77 = " + res);
                return res;
            }
            res = keyMethod.invoke(object, args);
            Result result = new Result(System.currentTimeMillis() + ch.value(), res);
            mapRes.put(method, result);
            mapState.put(stateObj, mapRes);
//            System.out.println("res 84 = " + res);
            return res;
        }
        return keyMethod.invoke(object, args);
    }

    private class SingleThreadExt extends Thread {
        volatile long eventNotify;

        @Override
        public void run() {
//            System.out.println("SingleThread, start");
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
//            System.out.println("SingleThread, start clear cache");
//            System.out.println("SingleThread, mapState = " + mapState);
//            System.out.println("SingleThread, current time = " + System.currentTimeMillis());
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
//            System.out.println("SingleThread end, mapState = " + mapState);
        }
    }
}