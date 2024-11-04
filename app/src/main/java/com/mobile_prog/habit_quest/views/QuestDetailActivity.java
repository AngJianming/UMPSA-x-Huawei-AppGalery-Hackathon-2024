package com.mobile_prog.habit_quest.views;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile_prog.habit_quest.R;
import com.mobile_prog.habit_quest.contexts.AuthContext;
import com.mobile_prog.habit_quest.models.Quest;
import com.mobile_prog.habit_quest.models.UserQuest;
import com.mobile_prog.habit_quest.services.UserQuestTypesService;
import com.mobile_prog.habit_quest.services.UserQuestsService;
import com.mobile_prog.habit_quest.services.UsersService;

public class QuestDetailActivity extends AppCompatActivity {

    private Quest quest;
    private UserQuest userQuest;
    private TextView questNameTv;
    private TextView questDescTv;
    private TextView questTipsTv;
    private Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);

        initialize();
        setQuest();
    }

    private void initialize() {
        this.questNameTv = findViewById(R.id.txt_detail_quest_name);
        this.questDescTv = findViewById(R.id.txt_detail_quest_desc);
        this.questTipsTv = findViewById(R.id.txt_quest_detail_tips);
        this.doneBtn = findViewById(R.id.quest_detail_done_btn);

        this.quest = (Quest) getIntent().getExtras().getSerializable("quest");
        this.userQuest = (UserQuest) getIntent().getExtras().getSerializable("user_quest_type");

        if (userQuest.isDone()) {
            this.doneBtn.setVisibility(View.GONE);
        }

        this.doneBtn.setOnClickListener(v -> {
            markAsDone();
        });
    }

    private void setQuest() {
        this.questNameTv.setText(quest.getName());
        this.questDescTv.setText(quest.getDescription());
        this.questTipsTv.setText(quest.getTips());
    }

    private void markAsDone() {
        Toast.makeText(this, "Marking as done, please wait...", Toast.LENGTH_SHORT).show();
        this.doneBtn.setEnabled(false);

        UserQuestsService.getInstance().markAsDoneById(userQuest.getId(), isSuccess -> {
            if (isSuccess) {
                int expAmount = 200;
                UserQuestTypesService.getInstance().checkIfShouldMarkDone(userQuest.getUserQuestTypeId(), __ -> {
                    UsersService.getInstance().increaseExpAndLevel(AuthContext.getId(), expAmount, success -> {
                        if (success) {
                            this.doneBtn.setVisibility(View.GONE);
                            Toast.makeText(this, "Congratulations, you have finished this quest, you got 200 exp!", Toast.LENGTH_SHORT).show();
                        } else {
                            this.doneBtn.setEnabled(true);
                        }
                    });
                });
            } else {
                Toast.makeText(this, "Ups, failed when marking this quest as done, please try again...", Toast.LENGTH_SHORT).show();
                this.doneBtn.setEnabled(true);
            }
        });
    }
}