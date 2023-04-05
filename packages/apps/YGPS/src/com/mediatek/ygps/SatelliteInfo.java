
package com.mediatek.ygps;

public class SatelliteInfo {
    int prn;
    float snr;
    float elevation;
    float azimuth;
    boolean usedInFix;
    int color;

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(prn).append(", ");
        builder.append(snr).append(", ");
        builder.append(elevation).append(", ");
        builder.append(azimuth).append(", ");
        builder.append(usedInFix).append("]");
        return builder.toString();
    }
}
