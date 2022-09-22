package com.pfc.db;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.pfc.support.FirestoreCallbackBool;
import com.pfc.support.FirestoreCallbackListCollection;
import com.pfc.support.FirestoreCallbackUser;

import java.util.Map;

public interface DbLittleHelper {

     void registerNewUser(Map<String, Object> mapNewUser);
     void getUser(String email, FirestoreCallbackUser callback);
     void updateCity(String mail, String city, FirestoreCallbackBool callback);
     void updatePhone(String mail, String phone, FirestoreCallbackBool callback);
     void deleteUser(FirebaseUser user, FirestoreCallbackBool firestoreCallbackBool);
     void changePassword(FirebaseUser user, String newPass, FirestoreCallbackBool firestoreCallbackBool);
     void addAvatarDownloadLink(FirebaseUser user, Uri downloadUrl);
     void getCollection(String collection, FirestoreCallbackListCollection callback);
     void getCollectionWarn(String collection, FirestoreCallbackListCollection callback);
     void addRequest(Map<String, Object> mapNewRequest, FirestoreCallbackBool callback);

}
