package com.mobile_prog.habit_quest.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile_prog.habit_quest.R;

import java.util.Vector;

import com.mobile_prog.habit_quest.adapters.QuestAdapter;
import com.mobile_prog.habit_quest.contexts.AuthContext;
import com.mobile_prog.habit_quest.models.UserQuest;
import com.mobile_prog.habit_quest.services.QuestTypesService;
import com.mobile_prog.habit_quest.services.UserQuestTypesService;
import com.mobile_prog.habit_quest.services.UserQuestsService;

public class QuestListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    TextView questTypeName;
    Button abandon;

    private String questTypeId;
    private boolean isForCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_list);

        Bundle extras = getIntent().getExtras();
        questTypeId = extras.getString("quest_type_id");
        isForCurrent = extras.getBoolean("is_for_current");

        abandon = findViewById(R.id.btn_abandon);
        questTypeName = findViewById(R.id.txt_quest_list_name);
        recyclerView = findViewById(R.id.recyclerview_quest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        QuestTypesService.getInstance().getById(questTypeId, questType -> {
            questTypeName.setText(questType.getName());
        });

        fetchAndPopulateData();

        abandon.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage("Do you want abandon this quest?");
            builder.setTitle("Abandon Quest");
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            builder.setPositiveButton("Yes", (dialog, which) -> {
                Toast.makeText(v.getContext(), "Abandon quest...", Toast.LENGTH_SHORT).show();
                abandon.setEnabled(false);

                UserQuestTypesService.getInstance().delete(questTypeId, userQuestTypeId -> {
                    if(userQuestTypeId != null){
                        UserQuestsService.getInstance().deleteByUserQuestType(userQuestTypeId, isSuccess -> {
                            if(isSuccess){
                                Toast.makeText(v.getContext(), "Success abandon quest", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(v.getContext(), "Error when abandon quest", Toast.LENGTH_SHORT).show();
                            }
                            Intent toHome = new Intent(v.getContext(), MainActivity.class);
                            v.getContext().startActivity(toHome);

                            Activity act = (Activity) v.getContext();
                            act.finish();
                        });
                    }else{
                        Toast.makeText(v.getContext(), "Quest not found", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchAndPopulateData();
    }

    private void fetchAndPopulateData() {
        if (isForCurrent) {
            UserQuestsService.getInstance().getByUserAndQuestType(AuthContext.getId(), questTypeId, false,  userQuests -> {
                adapter = new QuestAdapter(this, userQuests);
                recyclerView.setAdapter(adapter);

                setAbandonBtnVisiblity(userQuests);
            });
        } else {
            UserQuestsService.getInstance().getByUserAndQuestType(AuthContext.getId(), questTypeId, true, userQuests -> {
                adapter = new QuestAdapter(this, userQuests);
                recyclerView.setAdapter(adapter);

                setAbandonBtnVisiblity(userQuests);
            });
        }
    }

    private void setAbandonBtnVisiblity(Vector<UserQuest> userQuests) {
        boolean isAllDone = true;
        for (UserQuest uq: userQuests) {
            if (!uq.isDone()) isAllDone = false;
        }
        if (isAllDone) abandon.setVisibility(View.INVISIBLE);
    }
}