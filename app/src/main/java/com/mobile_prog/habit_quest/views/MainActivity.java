package com.mobile_prog.habit_quest.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.mobile_prog.habit_quest.R;
import com.mobile_prog.habit_quest.adapters.QuestTypeAdapter;
import com.mobile_prog.habit_quest.contexts.AuthContext;
import com.mobile_prog.habit_quest.services.QuestTypesService;
import com.mobile_prog.habit_quest.services.UsersService;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView avatarImgView;
    private TextView userNameTv;
    private TextView userLevelTv;
    private Button addQuestBtn;
    private RecyclerView currentRv;
    private RecyclerView historyRv;
    private TextView expTv;
    private TextView noCurrent, noHistory;
    private BannerView bannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disableNightMode();
        initialize();
        configureAds();
        setUser();
        setRecyclerView();
    }

    private void initialize() {
        HwAds.init(this);

        bannerView = findViewById(R.id.hw_banner_view);
        bannerView.setBannerRefresh(60);

        avatarImgView = findViewById(R.id.main_user_avatar);
        userNameTv = findViewById(R.id.main_user_name);
        userLevelTv = findViewById(R.id.main_user_level);
        expTv = findViewById(R.id.main_exp_tv);
        addQuestBtn = findViewById(R.id.main_add_quest_btn);

        currentRv = findViewById(R.id.main_current_rv);
        historyRv = findViewById(R.id.main_history_rv);

        noCurrent = findViewById(R.id.txt_no_current_quest);
        noHistory = findViewById(R.id.txt_no_history_quest);

        addQuestBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuestTypeListActivity.class);
            startActivity(intent);
        });
    }

    private void configureAds() {
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);
    }

    private void disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void setUser() {
        userNameTv.setText(AuthContext.getName());
        userLevelTv.setText(AuthContext.getLevel().toString());
        expTv.setText(String.format("( Exp: %d / %d )", AuthContext.getExp(), AuthContext.getLevel() * 150));

        if (AuthContext.getAvatarUriString().isEmpty()) {
            Glide.with(this).load("https://i.pravatar.cc/300").into(avatarImgView);
        } else {
            Glide.with(this).load(AuthContext.getAvatarUriString()).into(avatarImgView);
        }
    }

    private void setRecyclerView() {
        currentRv.setLayoutManager(new LinearLayoutManager(this));
        historyRv.setLayoutManager(new LinearLayoutManager(this));

        fetchAndPopulateData();
    }

    @Override
    public void onResume(){
        super.onResume();

        fetchAndPopulateData();
        UsersService.getInstance().getUser(AuthContext.getId(), user -> {
            AuthContext.set(user);
            userLevelTv.setText(AuthContext.getLevel().toString());
            expTv.setText(String.format("( Exp: %d / %d )", AuthContext.getExp(), AuthContext.getLevel() * 150));
        });
    }

    private void fetchAndPopulateData() {
        QuestTypesService.getInstance().getCurrentByUser(AuthContext.getId(), questTypes -> {
            if(questTypes.size() > 0){
                noCurrent.setVisibility(View.GONE);
            }
            QuestTypeAdapter adapter = new QuestTypeAdapter(this, questTypes, false, true);
            currentRv.setAdapter(adapter);
        });

        QuestTypesService.getInstance().getHistoryByUser(AuthContext.getId(), questTypes -> {
            if(questTypes.size() > 0){
                noHistory.setVisibility(View.GONE);
            }
            QuestTypeAdapter adapter = new QuestTypeAdapter(this, questTypes, false, false);
            historyRv.setAdapter(adapter);
        });
    }
}