package com.mobile_prog.habit_quest.services;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mobile_prog.habit_quest.interfaces.Callable;
import com.mobile_prog.habit_quest.models.QuestType;
import com.mobile_prog.habit_quest.models.UserQuestType;

import java.util.Vector;

public class QuestTypesService extends BaseService{
    private static QuestTypesService instance = null;

    private QuestTypesService() {
        super("quest_types", "QuestTypesService");
    }

    public static QuestTypesService getInstance() {
        if (instance == null) {
            instance = new QuestTypesService();
        }

        return instance;
    }

    public void getAll(Callable<Vector<QuestType>> callback) {
        db.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Vector<QuestType> qts = new Vector<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    QuestType qt = document.toObject(QuestType.class);
                    qt.setId(document.getId());
                    qts.add(qt);
                }

                callback.call(qts);
            } else {
                Log.d(TAG, "Failed when getting quest type documents");
            }
        });
    }

    public void getNotEnrolledByUser(String userId, Callable<Vector<QuestType>> callback) {
        UserQuestTypesService.getInstance().getByUser(userId, userQuestTypes -> {
            Vector<String> questIds = new Vector<>();
            for (UserQuestType uqt: userQuestTypes) {
                questIds.add(uqt.getQuestTypeId());
            }

            db.collection(COLLECTION_NAME).whereNotIn("__name__", questIds).get().addOnCompleteListener(task -> {
                Vector<QuestType> qts = new Vector<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        QuestType qt = document.toObject(QuestType.class);
                        qt.setId(document.getId());
                        qts.add(qt);
                    }

                    callback.call(qts);
                } else {
                    Log.d(TAG, "Failed when getting quest type documents");
                    callback.call(qts);
                }
            });
        });
    };

    public void getCurrentByUser(String userId, Callable<Vector<QuestType>> callback) {
        UserQuestTypesService.getInstance().getByUser(userId, userQuestTypes -> {
            Vector<QuestType> questTypes = new Vector<>();
            final Vector<Integer> counters = new Vector<>();
            counters.add(0);

            if (userQuestTypes.size() == 0) {
                callback.call(questTypes);
            }

            Vector<UserQuestType> filteredUserQuestType = new Vector<>();
            for (UserQuestType uqt: userQuestTypes) {
                if (!uqt.isDone()) filteredUserQuestType.add(uqt);
            }

            for (UserQuestType uqt: filteredUserQuestType) {
                db.collection(COLLECTION_NAME).whereEqualTo("__name__", uqt.getQuestTypeId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuestType qt = document.toObject(QuestType.class);
                            qt.setId(document.getId());
                            questTypes.add(qt);
                        }
                    } else {
                        Log.d(TAG, "Failed when getting quest type documents");
                    }

                    counters.set(0, counters.get(0) + 1);
                    if (counters.get(0) == filteredUserQuestType.size()) {
                        callback.call(questTypes);
                    }
                });
            }
        });
    }

    public void getHistoryByUser(String userId, Callable<Vector<QuestType>> callback) {
        UserQuestTypesService.getInstance().getByUser(userId, userQuestTypes -> {
            Vector<QuestType> questTypes = new Vector<>();
            final Vector<Integer> counters = new Vector<>();
            counters.add(0);

            if (userQuestTypes.size() == 0) {
                callback.call(questTypes);
            }

            Vector<UserQuestType> filteredUserQuestType = new Vector<>();
            for (UserQuestType uqt: userQuestTypes) {
                if (uqt.isDone()) filteredUserQuestType.add(uqt);
            }

            for (UserQuestType uqt: filteredUserQuestType) {
                db.collection(COLLECTION_NAME).whereEqualTo("__name__", uqt.getQuestTypeId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuestType qt = document.toObject(QuestType.class);
                            qt.setId(document.getId());
                            questTypes.add(qt);
                        }
                    } else {
                        Log.d(TAG, "Failed when getting quest type documents");
                    }

                    counters.set(0, counters.get(0) + 1);
                    if (counters.get(0) == filteredUserQuestType.size()) {
                        callback.call(questTypes);
                    }
                });
            }
        });
    }

    public void getByUser(String userId, Callable<Vector<QuestType>> callback) {
        UserQuestTypesService.getInstance().getByUser(userId, userQuestTypes -> {
            Vector<QuestType> questTypes = new Vector<>();
            final Vector<Integer> counters = new Vector<>();
            counters.add(0);

            if (userQuestTypes.size() == 0) {
                callback.call(questTypes);
            }

            for (UserQuestType uqt: userQuestTypes) {
                db.collection(COLLECTION_NAME).whereEqualTo("__name__", uqt.getQuestTypeId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuestType qt = document.toObject(QuestType.class);
                            qt.setId(document.getId());
                            questTypes.add(qt);
                        }
                    } else {
                        Log.d(TAG, "Failed when getting quest type documents");
                    }

                    counters.set(0, counters.get(0) + 1);
                    if (counters.get(0) == userQuestTypes.size()) {
                        callback.call(questTypes);
                    }
                });
            }
        });
    }

    public void getById(String questTypeId, Callable<QuestType> callback) {
        db.collection(COLLECTION_NAME).document(questTypeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                QuestType questType = new QuestType(
                        document.getId(),
                        document.getString("name"),
                        document.getString("description"),
                        document.getLong("day").intValue()
                );

                callback.call(questType);
            } else {
                Log.w(this.getClass().getName(), "Error getting documents.", task.getException());
                callback.call(null);
            }
        });
    }
}