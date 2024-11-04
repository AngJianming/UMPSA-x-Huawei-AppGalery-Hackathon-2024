package com.mobile_prog.habit_quest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile_prog.habit_quest.R;
import com.mobile_prog.habit_quest.models.Quest;
import com.mobile_prog.habit_quest.services.QuestsService;
import com.mobile_prog.habit_quest.views.QuestDetailActivity;

import java.util.Vector;

import com.mobile_prog.habit_quest.models.UserQuest;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    LayoutInflater mInflater;
    Vector<UserQuest> quests;
    Context context;

    public QuestAdapter(Context context, Vector<UserQuest> quests){
        mInflater = LayoutInflater.from(context);
        this.quests = quests;
        this.context = context;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.quest_item, parent, false);
        return new QuestViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {

        UserQuest currentQuest = quests.get(position);
        holder.questName.setText("Fetching...");
        holder.questIsDone.setText(currentQuest.isDone() ? "Done" : "Not Done");
        holder.btnDoNow.setTag(position);

        if (currentQuest.isDone()) {
            holder.btnDoNow.setVisibility(View.INVISIBLE);
        }

        QuestsService.getInstance().getById(currentQuest.getQuestId(), quest -> {
            holder.questName.setText(quest.getName());
            holder.setQuest(quest);
        });

        holder.btnDoNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestDetailActivity.class);
                intent.putExtra("quest", holder.quest);
                intent.putExtra("user_quest_type", currentQuest);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }

    class QuestViewHolder extends RecyclerView.ViewHolder {

        TextView questName, questIsDone;
        Button btnDoNow;
        RecyclerView.Adapter adapter;
        private Quest quest;

        public QuestViewHolder(@NonNull View itemView, QuestAdapter adapter) {
            super(itemView);

            questName = itemView.findViewById(R.id.txt_quest_name);
            questIsDone = itemView.findViewById(R.id.txt_quest_is_done);
            btnDoNow = itemView.findViewById(R.id.btn_do_now);

            this.adapter = adapter;
        }

        public void setQuest(Quest quest) {
            this.quest = quest;
        }
    }
}
