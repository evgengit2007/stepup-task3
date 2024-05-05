package ru.stepup.course2;

public class Result {
    private long timeLeave;
    private Object res;

    public Result() {
    }

    public Result(long timeLeave, Object res) {
        this.timeLeave = timeLeave;
        this.res = res;
    }

    @Override
    public String toString() {
        return "Result{" +
                "timeLeave=" + timeLeave +
                ", res=" + res +
                '}';
    }

    public Object getRes() {
        return res;
    }

    public long getTimeLeave() {
        return timeLeave;
    }

    public void setRes(Object res) {
        this.res = res;
    }

    public void setTimeLeave(long timeLeave) {
        this.timeLeave = timeLeave;
    }
}
