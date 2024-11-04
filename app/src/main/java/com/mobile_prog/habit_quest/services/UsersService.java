package com.mobile_prog.habit_quest.services;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.huawei.hms.support.account.result.AuthAccount;
import com.mobile_prog.habit_quest.interfaces.Callable;
import com.mobile_prog.habit_quest.models.User;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class UsersService extends BaseService{

    private static UsersService instance = null;
    private final Semaphore semaphore = new Semaphore(1);
    private final AtomicReference<User> user = new AtomicReference<>();

    private UsersService() {
        super("users", "UsersService");
    }


    public static UsersService getInstance() {
        if (instance == null) {
            instance = new UsersService();
        }

        return  instance;
    }

    public void getAll(Callable<Vector<User>> callback) {
        db.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
             Vector<User> users = new Vector<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    user.setId(document.getId());
                    users.add(user);
                }

                callback.call(users);
            } else {
                Log.d(TAG,"Failed when getting user documents");
                callback.call(users);
            }
        });
    }

    public void addUserIfNotExist(AuthAccount account, Callable callback) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(account.getUnionId());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (!doc.exists()) {
                    addUser(account, callback);
                } else {
                    callback.call(null);
                }
            } else {
                Log.d(TAG, "Document does not exists");
            }
        });
    }

    private void addUser(AuthAccount account, Callable callback) {
        User user = new User(account.getUnionId(), account.getDisplayName(), account.getEmail(), 0, 0, account.getAvatarUriString());
        db.collection(COLLECTION_NAME).document(user.getId()).set(user).addOnSuccessListener(unused -> {
            callback.call(null);
        }) .addOnFailureListener(e -> Log.d(TAG, "Failed when adding user, exception: " + e.getMessage()));
    }

    public void getUser(String id, Callable<User> callback) {
        db.collection(COLLECTION_NAME).document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                callback.call(doc.toObject(User.class));
            } else {
                Log.d(TAG, "Document does not exists");
                callback.call(null);
            }
        });
    }

    public void increaseExpAndLevel(String userId, int amount, Callable<Boolean> callback) {
        db.collection(COLLECTION_NAME).document(userId).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               DocumentSnapshot doc = task.getResult();
               long currentExp = (Long) doc.get("exp");
               long currentLevel = (Long) doc.get("level");
               long newExp = currentExp + amount;
               long newLevel = currentLevel;

               final long EXP_THRESHOLD = currentLevel * 150;
               if (newExp > EXP_THRESHOLD) {
                   newLevel = currentLevel + 1;
                   newExp = newExp - EXP_THRESHOLD;
               }

               callback.call(true);
               db.collection(COLLECTION_NAME).document(userId).update("level", newLevel, "exp", newExp)
                   .addOnSuccessListener(__ -> callback.call(true))
                   .addOnFailureListener(__ -> callback.call(false));
           } else {
               Log.d(TAG, "Document does not exists");
               callback.call(false);
           }
        });
    }
}
