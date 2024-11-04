package com.mobile_prog.habit_quest.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile_prog.habit_quest.contexts.AuthContext;
import com.mobile_prog.habit_quest.services.UserQuestTypesService;
import com.mobile_prog.habit_quest.services.UserQuestsService;
import com.mobile_prog.habit_quest.views.QuestListActivity;
import com.mobile_prog.habit_quest.R;

import java.util.Vector;

import com.mobile_prog.habit_quest.models.QuestType;

public class QuestTypeAdapter extends RecyclerView.Adapter<QuestTypeAdapter.QuestTypeViewHolder> {

    LayoutInflater mInflater;
    Vector<QuestType> questTypes;
    Context context;
    private boolean isForEnroll = false;
    private boolean isForCurrent = false;

    public QuestTypeAdapter(Context context, Vector<QuestType> questTypes){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.questTypes = questTypes;
    }

    public QuestTypeAdapter(Context context, Vector<QuestType> questTypes, boolean isForEnroll){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.questTypes = questTypes;
        this.isForEnroll = true;
    }

    public QuestTypeAdapter(Context context, Vector<QuestType> questTypes, boolean isForEnroll, boolean isForCurrent){
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.questTypes = questTypes;
        this.isForEnroll = isForEnroll;
        this.isForCurrent = isForCurrent;
    }

    @NonNull
    @Override
    public QuestTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mItemView = mInflater.inflate(R.layout.quest_type_item, parent, false);
        return new QuestTypeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestTypeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        QuestType currentQuestType = questTypes.get(position);
        holder.questTypeName.setText(currentQuestType.getName());
        holder.questTypeDescription.setText(currentQuestType.getDescription());
        holder.questTypeDay.setText(currentQuestType.getDay() + " days");
        holder.btnDoQuest.setTag(position);

        holder.btnDoQuest.setOnClickListener(v -> {
            if (isForEnroll) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you want to take this quest?");
                builder.setTitle("Take Quest");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(v.getContext(), "Adding quest to your quest list...", Toast.LENGTH_SHORT).show();
                        holder.btnDoQuest.setEnabled(false);

                        UserQuestTypesService.getInstance().getOnGoingQuestByUserAndQuestType(AuthContext.getId(), currentQuestType.getId(), countQuestType -> {
                            if(countQuestType <= 0){
                                String questTypeId = questTypes.get(position).getId();
                                UserQuestTypesService.getInstance().add(AuthContext.getId(), questTypeId, userQuestTypeId -> {
                                    UserQuestsService.getInstance().addForUserQuestType(userQuestTypeId, questTypeId, __ -> {
                                        holder.btnDoQuest.setEnabled(true);
                                        Toast.makeText(v.getContext(), "Sucesfully added this quest to your quest list!", Toast.LENGTH_SHORT).show();
//                                        Intent toHome = new Intent(v.getContext(), MainActivity.class);
//                                        context.startActivity(toHome);

                                        Activity act = (Activity) v.getContext();
                                        act.finish();
                                    });
                                });
                            }else{
                                holder.btnDoQuest.setEnabled(true);
                                Toast.makeText(v.getContext(), "You have this quest on-going. Cannot take this quest again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            } else {
                // go to quest list
                Intent toQuestList = new Intent(context, QuestListActivity.class);
                toQuestList.putExtra("is_for_current", isForCurrent);
                toQuestList.putExtra("quest_type_id", questTypes.get(position).getId());
                context.startActivity(toQuestList);
            }
        });

        if (isForEnroll) {
            holder.btnDoQuest.setText("Take Quest");
        } else {
            holder.btnDoQuest.setText("Do Quest");
        }
    }

    @Override
    public int getItemCount() {
        return questTypes.size();
    }

    class QuestTypeViewHolder extends RecyclerView.ViewHolder {

        TextView questTypeName, questTypeDescription, questTypeDay;
        Button btnDoQuest;
        RecyclerView.Adapter adapter;

        public QuestTypeViewHolder(@NonNull View itemView, QuestTypeAdapter adapter) {
            super(itemView);

            questTypeName  = itemView.findViewById(R.id.txt_quest_type_name);
            questTypeDescription = itemView.findViewById(R.id.txt_quest_type_desc);
            questTypeDay = itemView.findViewById(R.id.txt_quest_type_day);
            btnDoQuest = itemView.findViewById(R.id.btn_do_quest);

            this.adapter = adapter;
        }
    }
}
