package ibeacondata.bean;

/**
 * Created by LK on 2016/10/26.
 */
public class LocationBean {
    public int id;
    private String device_id;
    private String building;
    private String floor;
    private double position_x;
    private double position_y;
    private String spotName;
    private String spotId;

    public LocationBean(){}

    public LocationBean(int id,String device_id,String building,
                        String floor,double position_x,
                        double position_y,String spotName,String spotId){
        this.id = id;
        this.device_id = device_id;
        this.building = building;
        this.floor = floor;
        this.position_x = position_x;
        this.position_y = position_y;
        this.spotName = spotName;
        this.spotId = spotId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public double getPosition_x() {
        return position_x;
    }

    public void setPosition_x(double position_x) {
        this.position_x = position_x;
    }

    public double getPosition_y() {
        return position_y;
    }

    public void setPosition_y(double position_y) {
        this.position_y = position_y;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }
}
