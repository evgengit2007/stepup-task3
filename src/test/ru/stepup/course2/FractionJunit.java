package ru.stepup.course2;

public class FractionJunit implements Fractionable{
    private int num;
    private int denum;
    public int countStartDouble = 0; // счетчик запуска метода doubleValue

    public FractionJunit(int num, int denum) {
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
    @Cache
    public double doubleValue() {
        System.out.println("invoke double value");
        countStartDouble++;
        return (double) num/denum;
    }

    @Override
    @Cache
    public double multiValue() {
        System.out.println("invoke multi double value");
        return (double) num*denum;
    }
}
