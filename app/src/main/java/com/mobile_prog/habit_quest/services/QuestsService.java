package com.mobile_prog.habit_quest.services;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobile_prog.habit_quest.interfaces.Callable;
import com.mobile_prog.habit_quest.models.Quest;

import java.util.Vector;

public class QuestsService extends BaseService{
    private static QuestsService instance = null;

    private QuestsService() {
        super("quests", "QuestsService");
    }

    public static QuestsService getInstance() {
        if (instance == null) {
            instance = new QuestsService();
        }

        return instance;
    }

    public void getAll(Callable<Vector<Quest>> callback) {
        db.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Vector<Quest> quests = new Vector<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    quests.add(new Quest(
                            document.getId(),
                            document.getString("type_id"),
                            document.getString("name"),
                            document.getString("description"),
                            document.getString("tips")
                    ));
                }

                callback.call(quests);
            } else {
                Log.w(this.getClass().getName(), "Error getting documents.", task.getException());
                callback.call(null);
            }
        });
    }

    public void getById(String questId, Callable<Quest> callback) {
        db.collection(COLLECTION_NAME).document(questId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Quest quest = new Quest(
                        document.getId(),
                        document.getString("type_id"),
                        document.getString("name"),
                        document.getString("description"),
                        document.getString("tips")
                );

                callback.call(quest);
            } else {
                Log.w(this.getClass().getName(), "Error getting documents.", task.getException());
                callback.call(null);
            }
        });
    }

    public void getByQuestTypeId(String questTypeId, Callable<Vector<Quest>> callback) {
        db.collection(COLLECTION_NAME).whereEqualTo("type_id", questTypeId).get().addOnCompleteListener(task -> {
           Vector<Quest> quests = new Vector<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    quests.add(new Quest(
                            document.getId(),
                            document.getString("type_id"),
                            document.getString("name"),
                            document.getString("description"),
                            document.getString("tips")
                    ));
                }
           } else {
                Log.w(this.getClass().getName(), "Error getting documents.", task.getException());
           }
            callback.call(quests);
        });
    }
}
