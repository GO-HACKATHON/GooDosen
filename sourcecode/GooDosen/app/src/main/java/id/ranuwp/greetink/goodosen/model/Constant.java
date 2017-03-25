package id.ranuwp.greetink.goodosen.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ranuwp on 3/25/2017.
 */

public class Constant {
    public static final String SHARED_PREF = "GOODOSEN";
    public static final String SP_USEr_ID = "USEr_ID";
    public static final String SP_USER_NAME = "USER_NAME";
    public static final String SP_USER_FROM = "USER_FORM";
    public static final String SP_USER_IMAGE_URL = "USER_IMAGE_URL";

    public static SharedPreferences getSharedPreference(Context context){
        return context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
    }
}
