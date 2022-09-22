package com.pfc.db;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pfc.pojos.User;
import com.pfc.support.FirestoreCallbackBool;
import com.pfc.support.FirestoreCallbackListCollection;
import com.pfc.support.FirestoreCallbackUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;


public class FireDbLittleHelper implements DbLittleHelper{

    private final String TAG = "FireDbLittleHelper";
    private final FirebaseFirestore db;

    public FireDbLittleHelper(){
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void registerNewUser(@NonNull Map<String, Object> mapNewUser) {
        db.collection("Users")
                .document(Objects.requireNonNull(mapNewUser.get("Email")).toString())
                .set(mapNewUser);
    }

    @Override
    public void getUser(String email, FirestoreCallbackUser callback) {

        User user = new User();

        db.collection("Users").document(email).get()
                .addOnSuccessListener( (obj) -> {
                        user.setEmail(email);
                        user.setCity((String)obj.get("City"));
                        user.setPhone((String)obj.get("Phone"));
                        user.setAvatar((String) obj.get("Avatar"));
                        callback.onCallback(user);
        });
    }


    @Override
    public void updateCity(String mail, String city, FirestoreCallbackBool callback) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(mail);
        docRef.update("City", city)
                .addOnSuccessListener( v -> callback.onCallback(true))
                .addOnFailureListener( e -> callback.onCallback(false));
    }

    @Override
    public void updatePhone(String mail, String phone, FirestoreCallbackBool callback) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(mail);
        docRef.update("Phone", phone)
                .addOnSuccessListener( v -> callback.onCallback(true))
                .addOnFailureListener( e -> callback.onCallback(false));
    }

    @Override
    public void deleteUser(@NonNull FirebaseUser user, FirestoreCallbackBool firestoreCallbackBool) {
        //I´ve created a latch because my session was erases before I could eliminate all user's documents. That shows up an error related to security/rules database document .
        CountDownLatch latch = new CountDownLatch(2);
        deleteU(user,firestoreCallbackBool,latch);
        deleteUD(user,latch);
        deleteUR(user,latch);
    }

    private void deleteU(FirebaseUser user, FirestoreCallbackBool firestoreCallbackBool, CountDownLatch latch){
        new Thread(() -> {

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            user.delete()
                        .addOnCompleteListener(task -> firestoreCallbackBool.onCallback(task.isSuccessful()));


        }).start();
    }

    private void deleteUD(FirebaseUser user, CountDownLatch latch){
        new Thread(() -> {

            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Objects.requireNonNull(user.getEmail()));
            docRef.delete()
                    .addOnCompleteListener( (t) -> {
                        if (t.isSuccessful()){
                            Log.w(TAG,"deleteRestUserData isSuccessful true");
                        }else{
                            Log.w(TAG,"deleteRestUserData isSuccessful false");
                        }
                        latch.countDown();
                    });

        }).start();
    }

    private void deleteUR(FirebaseUser user, CountDownLatch latch){
        new Thread(() -> FirebaseFirestore.getInstance()
                .collection("Requests")
                .whereEqualTo("Requestor", user.getUid())
                .get()
                .addOnCompleteListener( t -> {
                    if (t.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot:t.getResult()){
                            //Log.w(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            documentSnapshot.getReference().delete();
                        }
                    }
                    latch.countDown();
                }
                )).start();
    }

    @Override
    public void changePassword(@NonNull FirebaseUser user, String newPass, FirestoreCallbackBool firestoreCallbackBool) {
        user.updatePassword(newPass)
                .addOnCompleteListener( (t) -> firestoreCallbackBool.onCallback(t.isSuccessful()));

    }

    @Override
    public void addAvatarDownloadLink(FirebaseUser user, Uri downloadUrl) {

        if ( user.getEmail() != null && !user.getEmail().isEmpty() ){
            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(user.getEmail());

            docRef.update("Avatar", downloadUrl )
                    .addOnSuccessListener( l -> Log.w(TAG, "addAvatarDownloadLink OnComplete true"))
                    .addOnFailureListener( e -> Log.w(TAG,"addAvatarDownloadLink addOnFailureListener true"));
        }

    }
//https://firebase.google.com/docs/firestore/query-data/get-data?hl=es&authuser=0#java
    @Override
    public void getCollection(String collection, FirestoreCallbackListCollection callback) {

        db.collection(collection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<QueryDocumentSnapshot> listResult = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listResult.add(document);
                            }
                            callback.onCallback(listResult);
                        } else {
                            Log.e(TAG, Objects.requireNonNull(task.getException()).getMessage()
                                    + " : " + task.getException().getCause());
                        }
                    }
                });
    }

    @Override
    public void getCollectionWarn(String collection, FirestoreCallbackListCollection callback) {

        db.collection(collection)
                .orderBy("FechaInicio")
                .get()
                .addOnCompleteListener( task -> {
                        if (task.isSuccessful()) {
                            List<QueryDocumentSnapshot> listResult = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listResult.add(document);
                            }
                            callback.onCallback(listResult);
                        } else {
                            Log.e(TAG, Objects.requireNonNull(task.getException()).getMessage()
                                    + " : " + task.getException().getCause());
                        }
                });
    }

    @Override
    public void addRequest(Map<String, Object> mapNewRequest, FirestoreCallbackBool firestoreCallbackBool) {

        db.collection("Requests").add(mapNewRequest)
                .addOnSuccessListener( r -> firestoreCallbackBool.onCallback(true))
                .addOnFailureListener( r -> firestoreCallbackBool.onCallback(false));

    }

}//End
