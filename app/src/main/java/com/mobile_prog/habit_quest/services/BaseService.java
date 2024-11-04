package com.mobile_prog.habit_quest.services;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BaseService {
    protected FirebaseFirestore db;
    protected String COLLECTION_NAME;
    protected String TAG;

    protected BaseService(String collectionName, String tag) {
        db = FirebaseFirestore.getInstance();
        COLLECTION_NAME = collectionName;
        TAG = tag;
    }
}
