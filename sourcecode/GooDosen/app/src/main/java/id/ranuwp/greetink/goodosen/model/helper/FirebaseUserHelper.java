package id.ranuwp.greetink.goodosen.model.helper;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by ranuwp on 3/25/2017.
 */


public class FirebaseUserHelper {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth.AuthStateListener authStateListener;

    public FirebaseUserHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public boolean isLogin(){
        return firebaseAuth.getCurrentUser() != null;
    }

    public void signOut(){
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    public boolean isEmailVerified(){
        return firebaseAuth.getCurrentUser().isEmailVerified();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public FirebaseUser getFirebaseUser(){
        return firebaseAuth.getCurrentUser();
    }


    public FirebaseStorage getFirebaseStorage() {

        return firebaseStorage;
    }

    public StorageReference getStorageReference() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return storageReference.child("user/"+firebaseUser.getUid()+"/data_result");
    }

    public FirebaseAuth.AuthStateListener getAuthStateListener() {
        return authStateListener;
    }

    public void setAuthStateListener(FirebaseAuth.AuthStateListener authStateListener) {
        this.authStateListener = authStateListener;
    }
}
