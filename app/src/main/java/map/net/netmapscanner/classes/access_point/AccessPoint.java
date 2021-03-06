package map.net.netmapscanner.classes.access_point;

import java.io.Serializable;

/**
 * Created by adriano on 05/09/16.
 */
public class AccessPoint implements Serializable {

    private String BSSID;
    private Double RSSI;

    public AccessPoint(String BSSID, Double RSSI) {
        this.BSSID = BSSID;
        this.RSSI = RSSI;
    }


    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }


    public Double getRSSI() {
        return RSSI;
    }

    public void setRSSI(Double RSSI) {
        this.RSSI = RSSI;
    }

}
