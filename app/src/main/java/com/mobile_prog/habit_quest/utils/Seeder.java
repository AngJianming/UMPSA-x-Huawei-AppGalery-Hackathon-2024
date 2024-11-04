package com.mobile_prog.habit_quest.utils;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.mobile_prog.habit_quest.models.Quest;
import com.mobile_prog.habit_quest.models.QuestType;
import com.mobile_prog.habit_quest.models.User;
import com.mobile_prog.habit_quest.models.UserQuestType;
import com.mobile_prog.habit_quest.services.QuestTypesService;
import com.mobile_prog.habit_quest.services.QuestsService;
import com.mobile_prog.habit_quest.services.UserQuestTypesService;
import com.mobile_prog.habit_quest.services.UsersService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Seeder {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void seedQuestType() {
        ArrayList<QuestType> dummies = new ArrayList<>();
        dummies.add(new QuestType("-1", "Socialize Quest", "Socialize with your friends to gain more network", 30));
        dummies.add(new QuestType("-1", "Wealth Quest", "Manage your wealth by completing tasks in this quest", 30));
        dummies.add(new QuestType("-1", "Health Quest", "Improve your health for a better future with this simple tasks", 15));

        WriteBatch batch = db.batch();

        for (QuestType qt: dummies) {
            DocumentReference docRef = db.collection("quest_types").document();
            batch.set(docRef, qt);
        }

        batch.commit();
    }

    public static void seedUserQuestTypes() {
        WriteBatch batch = db.batch();
        UsersService.getInstance().getAll(users -> {
            QuestTypesService.getInstance().getAll(questTypes -> {
                for(QuestType qt: questTypes) {
                    for (User user: users) {
                        if (!qt.getName().contains("Wealth")) {
                            DocumentReference docRef = db.collection("user_quest_types").document();
                            Map<String, Object> userQuestType = new HashMap<>();
                            userQuestType.put("id", "-1");
                            userQuestType.put("quest_type_id", qt.getId());
                            userQuestType.put("user_id", user.getId());
                            userQuestType.put("is_done", false);
                            batch.set(docRef, userQuestType);
                        }
                    }
                }

                batch.commit();
            });
        });
    }

    public static void seedQuests() {
        WriteBatch batch = db.batch();

        //Socialize
        Vector<Quest> s = new Vector<>();
        s.add(new Quest("-1", null, "Say hello to 3 friends", "Say hello to your friends via social media or direct call", "Call them by name first"));
        s.add(new Quest("-1", null, "Talk with your family", "Start a conversation with one of your family", "Call them by name first"));
        s.add(new Quest("-1", null, "Ask 1 friend to hangout", "Ask your friend to hangout to the nearest cafe", "You could ask them by chat!"));
        s.add(new Quest("-1", null, "Help 1 people in need", "Go outdoor and find someone who needs your help", "Do it with pure heart!"));

        //Wealth
        Vector<Quest> w = new Vector<>();
        w.add(new Quest("-1", null, "Save Rp. 500000 a month", "Save Rp. 500.000 to your deposits in a month", "Save it in a place where you cant use it"));
        w.add(new Quest("-1", null, "Donate your money", "Donate your money to a people/organization who needs it", "You could use an application like KitaBisa"));
        w.add(new Quest("-1", null, "Spend less than Rp 200.000 a week", "Spend your money less than 200.000 a week", "Use a money tracker app to make it easier to track your outcome"));

        //Health
        Vector<Quest> h = new Vector<>();
        h.add(new Quest("-1", null, "Do 10 push ups a day", "Strengthen your muscle by doing 10 push ups a day", "Do it in the morning when you still have many energies"));
        h.add(new Quest("-1", null, "Do 10 Sit ups a day", "Strengthen your muscle by doing 10 sit ups a day", "Do it in the morning when you still have many energies"));
        h.add(new Quest("-1", null, "Do cardio for 30 minutes twice a week", "Do cardio exercise for 30 minutes twice in a week", "Follow youtube's cardiotutorial that match the duration"));
        h.add(new Quest("-1", null, "Do 10.000 walk steps twice a week", "Walk 10.000 steps twice a week", "Use health application  or smartwatch to help you with this"));

        QuestTypesService.getInstance().getAll(questTypes -> {
            for (QuestType qt: questTypes) {
                if (qt.getName().contains("Socialize")) {
                    for (Quest q: s) {
                        Map<String, Object> socializeQuest = new HashMap<>();
                        socializeQuest.put("type_id", qt.getId());
                        socializeQuest.put("name", q.getName());
                        socializeQuest.put("description", q.getDescription());
                        socializeQuest.put("tips", q.getTips());
                        DocumentReference docRef = db.collection("quests").document();
                        batch.set(docRef, socializeQuest);
                    }
                } else if (qt.getName().contains("Wealth")) {
                    for (Quest q: w) {
                        Map<String, Object> socializeQuest = new HashMap<>();
                        socializeQuest.put("type_id", qt.getId());
                        socializeQuest.put("name", q.getName());
                        socializeQuest.put("description", q.getDescription());
                        socializeQuest.put("tips", q.getTips());
                        DocumentReference docRef = db.collection("quests").document();
                        batch.set(docRef, socializeQuest);
                    }
                } else if (qt.getName().contains("Health")) {
                    for (Quest q: h) {
                        Map<String, Object> socializeQuest = new HashMap<>();
                        socializeQuest.put("type_id", qt.getId());
                        socializeQuest.put("name", q.getName());
                        socializeQuest.put("description", q.getDescription());
                        socializeQuest.put("tips", q.getTips());
                        DocumentReference docRef = db.collection("quests").document();
                        batch.set(docRef, socializeQuest);
                    }
                }
            }

            batch.commit();
        });
    }

    public static void seedUserQuests() {
        WriteBatch batch = db.batch();
        UserQuestTypesService.getInstance().getAll(userQuestTypes -> {
            QuestsService.getInstance().getAll(quests -> {
                for (UserQuestType uqt: userQuestTypes) {
                    for (Quest q: quests) {
                        if (q.getQuestTypeId().equals(uqt.getQuestTypeId())) {
                            DocumentReference docRef = db.collection("user_quests").document();
                            Map<String, Object> uq = new HashMap<>();
                            uq.put("id", "-1");
                            uq.put("quest_id", q.getId());
                            uq.put("user_quest_type_id", uqt.getId());
                            uq.put("is_done", false);
                            batch.set(docRef, uq);
                        }
                    }
                }

                batch.commit();
            });
        });
    }
}
