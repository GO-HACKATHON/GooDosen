package id.ranuwp.greetink.goodosen.model.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by ranuwp on 3/25/2017.
 */


public class BitmapHelper {

    public static byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        return stream.toByteArray();
    }

    public static Bitmap getBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

}
