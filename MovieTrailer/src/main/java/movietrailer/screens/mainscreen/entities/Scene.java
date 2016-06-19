package movietrailer.screens.mainscreen.entities;

import org.json.JSONObject;

import java.util.Random;

/**
 * Created by a7medM on 4/23/2016.
 */
public class Scene {
    private String objectId = "";
    private String name;
    private String start;
    private String end;
    private int duration;
    private String hours, min, sec;

    public Scene(JSONObject jsonObject) throws Exception {
        setStart(jsonObject.getString("start"));
        setEnd(jsonObject.getString("end"));
        int duration = new Random().nextInt(5) + 6;
        setDuration(duration);
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        try {
            String[] arr = start.split(",");
            if (arr != null || arr.length > 0) {
                this.start = arr[0];
                String[] arr2 = start.split(":");
                hours = arr2[0];
                min = arr2[1];
                sec = arr2[2];
            } else {
                this.start = start;
            }
        } catch (Exception e) {
            this.start = start;

        }
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }
}