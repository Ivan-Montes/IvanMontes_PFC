package com.pfc.support;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public interface FirestoreCallbackListCollection {

    void onCallback(List<QueryDocumentSnapshot> listResult);
}
