package ru.stepup.course2;

public class FractionTest implements Fractionable {
    // класс, созданный для проведения тестов
    private int num;
    private int denum;
    public int count = 0;

    public FractionTest(int num, int denum) {
        this.num = num;
        this.denum = denum;
    }

    @Mutator
    public void setNum(int num) {
        this.num = num;
    }

    @Mutator
    public void setDenum(int denum) {
        this.denum = denum;
    }

    @Override
    @Cache(1000L)
    public double doubleValue() {
        System.out.println("invoke double value");
        count++;
        return (double) num / denum;
    }

    @Override
    @Cache(1000L)
    public double multiValue() {
        System.out.println("invoke multi value");
        count++;
        return (double) num * denum;
    }
}
