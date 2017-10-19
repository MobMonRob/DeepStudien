package info.hmm.mpu9250data;

import java.io.Serializable;

/**
 * Created by Franz Aufschläger on 14.08.2017.
 */

public class Data implements Serializable{
    public int Ac_x;
    public int Ac_y;
    public int Ac_z;

    public int Gy_x;
    public int Gy_y;
    public int Gy_z;

    public int Ma_x;
    public int Ma_y;
    public int Ma_z;

    public long Ts;
}
