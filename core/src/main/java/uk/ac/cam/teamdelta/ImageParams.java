package uk.ac.cam.teamdelta;

public class ImageParams {
    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    private double a;
    private double b;
    private double c;

    public void setParams(double a, double b, double c){
        this.a=a;
        this.b=b;
        this.c=c;
    }

    public String getString(){
        return a + ", " + b + ", " + c;
    }
}
