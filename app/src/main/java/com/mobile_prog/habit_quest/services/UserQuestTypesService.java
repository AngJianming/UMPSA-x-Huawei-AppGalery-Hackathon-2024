package com.mobile_prog.habit_quest.services;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobile_prog.habit_quest.contexts.AuthContext;
import com.mobile_prog.habit_quest.interfaces.Callable;
import com.mobile_prog.habit_quest.models.UserQuest;
import com.mobile_prog.habit_quest.models.UserQuestType;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class UserQuestTypesService extends BaseService{
    private static UserQuestTypesService instance = null;

    private UserQuestTypesService() {
        super("user_quest_types", "UserQuestTypesService");
    }

    public static UserQuestTypesService getInstance() {
        if (instance == null) {
            instance = new UserQuestTypesService();
        }

        return instance;
    }

    public void getByUser(String userId, Callable<Vector<UserQuestType>> callback) {
        db.collection(COLLECTION_NAME).whereEqualTo("user_id", userId).get().addOnCompleteListener(task -> {
            Vector<UserQuestType> userQuestTypes = new Vector<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userQuestTypes.add(new UserQuestType(
                            document.getId(),
                            document.getString("quest_type_id"),
                            document.getString("user_id"),
                            document.getBoolean("is_done")
                    ));
                }
                callback.call(userQuestTypes);
            } else {
                Log.d(TAG, "Failed when getting quest type documents by user");
                callback.call(userQuestTypes);
            }
        });
    }

    public void getAll(Callable<Vector<UserQuestType>> callback) {
        db.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
            Vector<UserQuestType> userQuestTypes = new Vector<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userQuestTypes.add(new UserQuestType(
                            document.getId(),
                            document.getString("quest_type_id"),
                            document.getString("user_id"),
                            document.getBoolean("is_done")
                    ));
                }
                callback.call(userQuestTypes);
            } else {
                Log.d(TAG, "Failed when getting quest type documents by user");
                callback.call(userQuestTypes);
            }
        });
    }

    public void getByUserAndQuestType(String userId, String questTypeId, boolean isDone, Callable<UserQuestType> callback) {
        db.collection(COLLECTION_NAME).whereEqualTo("quest_type_id", questTypeId).whereEqualTo("user_id", userId).whereEqualTo("is_done", isDone).get().addOnCompleteListener(task -> {
            Vector<UserQuestType> userQuestTypes = new Vector<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userQuestTypes.add(new UserQuestType(
                            document.getId(),
                            document.getString("quest_type_id"),
                            document.getString("user_id"),
                            document.getBoolean("is_done")
                    ));
                }

                if (userQuestTypes.size() == 0) {
                    callback.call(null);
                } else {
                    callback.call(userQuestTypes.get(0));
                }
            } else {
                Log.d(TAG, "Failed when getting quest type documents by user");
                callback.call(null);
            }
        });
    }

    public void getOnGoingQuestByUserAndQuestType(String userId, String questTypeId, Callable<Integer> callback) {
        db.collection(COLLECTION_NAME).whereEqualTo("quest_type_id", questTypeId).whereEqualTo("user_id", userId).whereEqualTo("is_done", false).get().addOnCompleteListener(task -> {
            Vector<UserQuestType> userQuestTypes = new Vector<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userQuestTypes.add(new UserQuestType(
                            document.getId(),
                            document.getString("quest_type_id"),
                            document.getString("user_id"),
                            document.getBoolean("is_done")
                    ));
                }
                callback.call(userQuestTypes.size());
            } else {
                Log.d(TAG, "Failed when getting quest type documents by user");
                callback.call(userQuestTypes.size());
            }
        });
    }

    public void add(String userId, String questTypeId, Callable<String> callback) {
        Map<String, Object> userQuestType = new HashMap<>();
        userQuestType.put("id", "-1");
        userQuestType.put("is_done", false);
        userQuestType.put("quest_type_id", questTypeId);
        userQuestType.put("user_id", userId);

        db.collection(COLLECTION_NAME).add(userQuestType).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference docRef = task.getResult();
                callback.call(docRef.getId());
            } else {
                Log.d(TAG, "Failed when getting quest type documents by user");
                callback.call(null);
            }
        });
    }

    public void delete(String questTypeId, Callable<String> callback){
        db.collection(COLLECTION_NAME).whereEqualTo("quest_type_id", questTypeId).whereEqualTo("is_done",false).whereEqualTo("user_id", AuthContext.getId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();
                    document.getReference().delete();
                    callback.call(id);
                }

            } else {
                Log.w(this.getClass().getName(), "Error getting documents.", task.getException());
                callback.call(null);
            }
        });
    }

    public void checkIfShouldMarkDone(String userQuestTypeId, Callable<Boolean> callback) {
        UserQuestsService.getInstance().getByUserQuestTypeId(userQuestTypeId, userQuests -> {
            boolean isAllDone = true;
            for(UserQuest uq: userQuests) {
                if (!uq.isDone()) isAllDone = false;
            }

            if (isAllDone) {
                db.collection(COLLECTION_NAME).document(userQuestTypeId).update("is_done", true)
                        .addOnSuccessListener(__ -> callback.call(true))
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Failed when marking the quest type as done");
                            callback.call(false);
                        });
            } else {
                callback.call(true);
            }
        });

    }
}
