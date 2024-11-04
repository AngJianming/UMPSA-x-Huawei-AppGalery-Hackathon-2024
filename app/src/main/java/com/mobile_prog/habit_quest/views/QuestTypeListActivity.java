package com.mobile_prog.habit_quest.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mobile_prog.habit_quest.R;

import java.util.Vector;

import com.mobile_prog.habit_quest.adapters.QuestTypeAdapter;
import com.mobile_prog.habit_quest.models.QuestType;
import com.mobile_prog.habit_quest.services.QuestTypesService;

public class QuestTypeListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Vector<QuestType> questTypes;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_type_list);

        recyclerView = findViewById(R.id.recyclerview_quest_type);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        QuestTypesService.getInstance().getAll(questTypes -> {
            this.questTypes = questTypes;

            adapter = new QuestTypeAdapter(this, questTypes, true);
            recyclerView.setAdapter(adapter);
        });
    }
}