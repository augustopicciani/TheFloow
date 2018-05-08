package thefloow.com.thefloow.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Augusto on 06/05/2018.
 */

public class Utils {

    public static String generateUniqueID(){
        return UUID.randomUUID().toString();
    }

    public static String getStringDate(Date date){

        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
        return simpleDate.format(date);
    }

    public static String getTimeFromDate(Date date){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date);
    }

    public static String getDateAndTimeFromDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return simpleDateFormat.format(date);
    }
}
