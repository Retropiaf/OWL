package com.app.owl.SoundDetector;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Christiane on 1/17/18.
 */

public class Alert {
    Date time;
    String ResponderId;

    public Alert(){
        this.time = Calendar.getInstance().getTime();
    }


}
