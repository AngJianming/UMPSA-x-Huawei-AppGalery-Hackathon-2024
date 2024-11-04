package com.mobile_prog.habit_quest.services;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.mobile_prog.habit_quest.interfaces.Callable;
import com.mobile_prog.habit_quest.models.Quest;
import com.mobile_prog.habit_quest.models.UserQuest;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class UserQuestsService extends BaseService{
    private static UserQuestsService instance = null;

    private UserQuestsService() {
        super("user_quests", "UserQuestsService");
    }

    public static UserQuestsService getInstance() {
        if (instance == null) {
            instance = new UserQuestsService();
        }

        return instance;
    }

    public void getByUserAndQuestType(String userId, String questTypeId, boolean isDone, Callable<Vector<UserQuest>> callback) {
        UserQuestTypesService.getInstance().getByUserAndQuestType(userId, questTypeId, isDone, userQuestType -> {
            if (userQuestType == null) {
                callback.call(new Vector<UserQuest>());
            } else {
                db.collection(COLLECTION_NAME).whereEqualTo("user_quest_type_id", userQuestType.getId()).get().addOnCompleteListener(task -> {
                    Vector<UserQuest> userQuests = new Vector<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userQuests.add(new UserQuest(
                                    document.getId(),
                                    document.getString("user_quest_type_id"),
                                    document.getString("quest_id"),
                                    document.getBoolean("is_done")
                            ));
                        }

                        callback.call(userQuests);
                    } else {
                        Log.w(this.getClass().getName(), "Error getting user quests documents.", task.getException());
                        callback.call(userQuests);
                    }
                });
            }
        });
    }

    public void addForUserQuestType(String userQuestTypeId, String questTypeId, Callable<Void> callback) {
        QuestsService.getInstance().getByQuestTypeId(questTypeId, quests -> {
            WriteBatch batch = db.batch();

            for (Quest quest: quests) {
                Map<String, Object> userQuest = new HashMap<>();
                userQuest.put("id", "-1");
                userQuest.put("is_done", false);
                userQuest.put("quest_id", quest.getId());
                userQuest.put("user_quest_type_id", userQuestTypeId);

                DocumentReference docRef = db.collection(COLLECTION_NAME).document();
                batch.set(docRef, userQuest);
            }

            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.call(null);
                } else {
                    Log.w(TAG, "error when adding user quests");
                    callback.call(null);
                }
            });
        });
    }

    public void deleteByUserQuestType(String userQuestTypeId, Callable<Boolean> callback){
        db.collection(COLLECTION_NAME).whereEqualTo("user_quest_type_id", userQuestTypeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
                callback.call(true);
            } else {
                Log.d(TAG, "Failed when getting quest type documents by user");
                callback.call(false);
            }
        });
    }

    public void markAsDoneById(String userQuestId, Callable<Boolean> callback) {
        db.collection(COLLECTION_NAME).document(userQuestId).update("is_done", true)
            .addOnSuccessListener(__ -> callback.call(true))
            .addOnFailureListener(e -> {
               Log.d(TAG, "Failed when marking the quest as done");
               callback.call(false);
            });
    }

    public void getByUserQuestTypeId(String userQuestTypeId, Callable<Vector<UserQuest>> callback) {
        db.collection(COLLECTION_NAME).whereEqualTo("user_quest_type_id", userQuestTypeId).get().addOnCompleteListener(task -> {
            Vector<UserQuest> userQuests = new Vector<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userQuests.add(new UserQuest(
                            document.getId(),
                            document.getString("user_quest_type_id"),
                            document.getString("quest_id"),
                            document.getBoolean("is_done")
                    ));
                }

                callback.call(userQuests);
            } else {
                Log.w(this.getClass().getName(), "Error getting user quests documents.", task.getException());
                callback.call(userQuests);
            }
        });
    }
}
