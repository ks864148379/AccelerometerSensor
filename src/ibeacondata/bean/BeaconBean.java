package ibeacondata.bean;

/**
 * Created by LK on 2016/9/29.
 */
public class BeaconBean {
    public int id;
    public String device_id;//手机imei
    public String mac_id;
    public String uuid;
    public int major;
    public int minor;
    public int rssi;
    public double distance;
    public long collectime;
    public int flag;
    public String time;

    public BeaconBean(){}

   /* public BeaconBean(int id, String device_id, String mac_id,
                      String uuid, int major, int minor,
                      int rssi, Double distance, long collectime,int flag){
        this.id =id;
        this.device_id=device_id;
        this.mac_id = mac_id;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.distance = distance;
        this.collectime = collectime;
        this.flag = flag;
    }
*/
    public BeaconBean(int id, String device_id, String mac_id,
                      String uuid, int major, int minor,
                      int rssi, Double distance, long collectime, int flag, String time){
        this.id =id;
        this.device_id=device_id;
        this.mac_id = mac_id;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.distance = distance;
        this.collectime = collectime;
        this.flag = flag;
        this.time = time;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac_id() {
        return mac_id;
    }

    public void setMac_id(String mac_id) {
        this.mac_id = mac_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getCollectime() {
        return collectime;
    }

    public void setCollectime(long collectime) {
        this.collectime = collectime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof BeaconBean)){
            return false;
        }else {
            BeaconBean thatBeaconBean = (BeaconBean) that;
            return thatBeaconBean.mac_id.equals(this.mac_id);
        }
    }


}
