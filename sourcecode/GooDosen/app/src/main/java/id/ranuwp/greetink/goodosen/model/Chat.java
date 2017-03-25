package id.ranuwp.greetink.goodosen.model;

import java.util.Date;

import id.ranuwp.greetink.goodosen.model.User;

/**
 * Created by ranuwp on 3/25/2017.
 */


public class Chat {
    private User user;
    private Date datetime;
    private boolean notification;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
