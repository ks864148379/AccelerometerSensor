package ibeacondata.bean;

/**
 * Created by LK on 2016/10/31.
 */
public class VisitorBean {
    int  axisX;
    int axisY;

    public VisitorBean(){}

    public VisitorBean(int axisX,int axisY){
        this.axisX = axisX;
        this.axisY = axisY;
    }

    public int getAxisX() {
        return axisX;
    }

    public void setAxisX(int axisX) {
        this.axisX = axisX;
    }

    public int getAxisY() {
        return axisY;
    }

    public void setAxisY(int axisY) {
        this.axisY = axisY;
    }
}
