package id.ranuwp.greetink.goodosen.model.helper;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import id.ranuwp.greetink.goodosen.model.User;

/**
 * Created by ranuwp on 3/25/2017.
 */


public class UserSuggestion implements SearchSuggestion {

    private User user;

    public UserSuggestion(User user){
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getBody() {
        return user.getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
