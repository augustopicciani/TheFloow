package thefloow.com.thefloow.model.local;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import thefloow.com.thefloow.model.DateConverter;

/**
 * Created by Augusto on 05/05/2018.
 */
@Entity
public class JourneyModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String journeyID;
    private double lati;
    private double longi;

    @TypeConverters(DateConverter.class)
    private Date journeyDate;



    public JourneyModel(String journeyID, double lati, double longi, Date journeyDate){
        this.journeyID = journeyID;
        this.lati = lati;
        this.longi = longi;
        this.journeyDate = journeyDate;
    }



    public double getLati() {
        return lati;
    }

    public double getLongi() {
        return longi;
    }

    public Date getJourneyDate() {
        return journeyDate;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
