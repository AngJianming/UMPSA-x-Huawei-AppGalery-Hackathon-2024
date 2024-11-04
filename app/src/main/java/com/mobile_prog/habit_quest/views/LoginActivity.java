package com.mobile_prog.habit_quest.views;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;
import com.mobile_prog.habit_quest.R;
import com.mobile_prog.habit_quest.contexts.AuthContext;
import com.mobile_prog.habit_quest.services.UsersService;

public class LoginActivity extends AppCompatActivity {

    private HuaweiIdAuthButton huaweiSignInBtn;

    // for huawei single sign on
    private AccountAuthService mAuthService;
    private AccountAuthParams mAuthParams;
    private static final int REQUEST_CODE_SIGN_IN = 1000;
    private final String TAG = "HuaweiAuthLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        disableNightMode();
        initializeHuaweiSingleSignOn();
    }

    private void disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void initializeHuaweiSingleSignOn() {
        huaweiSignInBtn = findViewById(R.id.huawei_sign_in_btn);
        huaweiSignInBtn.setOnClickListener(v -> {
            huaweiSignInBtn.setEnabled(false);
            Toast.makeText(this, "Signing you in, please wait...", Toast.LENGTH_SHORT).show();
            silentSignInByHwId();
        });
    }

    private void silentSignInByHwId() {
        mAuthParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .createParams();
        mAuthService = AccountAuthManager.getService(this, mAuthParams);

        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(authAccount -> {
            onSignInSuccess(authAccount);
        });

        task.addOnFailureListener(e -> {
            huaweiSignInBtn.setEnabled(true);
           if (e instanceof ApiException) {
               ApiException apiException = (ApiException) e;
               Intent signInIntent = mAuthService.getSignInIntent();
               startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
           }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                AuthAccount authAccount = authAccountTask.getResult();
                onSignInSuccess(authAccount);
                Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            } else {
                Log.e(TAG, "sign in failed : " +((ApiException)authAccountTask.getException()).getStatusCode());
            }
        }
    }

    private void onSignInSuccess(AuthAccount account) {
        Context context = this;
        UsersService.getInstance().addUserIfNotExist(account, __ -> {
            UsersService.getInstance().getUser(account.getUnionId(), user -> {
                AuthContext.set(user);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        });
    }
}