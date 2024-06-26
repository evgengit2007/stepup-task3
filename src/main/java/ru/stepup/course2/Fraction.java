package ru.stepup.course2;

public class Fraction implements Fractionable {
    private int num;
    private int denum;

    public Fraction(int num, int denum) {
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
    @Cache(1000)
    public double doubleValue() {
        System.out.println("invoke double value");
        return (double) num / denum;
    }

    @Override
    @Cache(1000)
    public double multiValue() {
        System.out.println("invoke multi value");
        return (double) num * denum;
    }
}
