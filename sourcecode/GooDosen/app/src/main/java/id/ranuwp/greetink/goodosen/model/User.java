package id.ranuwp.greetink.goodosen.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ranuwp on 3/25/2017.
 */

public class User {
    private String id;
    private String image_url;
    private String name;
    private String from;
    private boolean follow;
    private LatLng last_location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public LatLng getLast_location() {
        return last_location;
    }

    public void setLast_location(LatLng last_location) {
        this.last_location = last_location;
    }
}
