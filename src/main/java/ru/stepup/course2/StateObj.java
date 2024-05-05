package ru.stepup.course2;

import java.util.*;
import java.lang.reflect.Method;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class StateObj {
    // @EqualsAndHashCode ������ ����� ��� �������� ������� � �����������, ������� ��� ����, ����� ������ �� ���������
    private Map<Method, List<Object>> mapValues = new HashMap<>();

    public StateObj(StateObj oldState, Method method, Object[] args) {
        mapValues.putAll(oldState.mapValues);
        mapValues.put(method, Arrays.asList(args));
    }

    public StateObj() {
    }

    // ������������ ��� �������
    public Map<Method, List<Object>> getMapValues() {
        return mapValues;
    }
}
