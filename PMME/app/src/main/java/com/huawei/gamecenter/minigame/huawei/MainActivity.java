/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.huawei.gamecenter.minigame.huawei;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.huawei.gamecenter.minigame.huawei.UI.LoadingDialog;
import com.huawei.gamecenter.minigame.huawei.Until.Constant;
import com.huawei.gamecenter.minigame.huawei.Until.HMSLogHelper;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.AppParams;
import com.huawei.hms.jos.AppUpdateClient;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.JosStatusCodes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.jos.games.player.PlayersClientImpl;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AccountAuthResult;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.utils.ResourceLoaderUtil;
import com.huawei.updatesdk.service.appmgr.bean.ApkUpgradeInfo;
import com.huawei.updatesdk.service.otaupdate.CheckUpdateCallBack;
import com.huawei.updatesdk.service.otaupdate.UpdateKey;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_INTENT = 3000;
    private boolean hasInit = false;
    private LoadingDialog dialog;
    private String photoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.simple_activity_main);
        initView();
        init();
        ExitApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * ????????????????????????
     */
    private void initView() {
        findViewById(R.id.login_in_huawei).setOnClickListener(new MyClickListener());
        findViewById(R.id.visitors_login).setOnClickListener(new MyClickListener());
        dialog = new LoadingDialog(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideFloatWindowNewWay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFloatWindowNewWay();
    }

    public void init() {
        AccountAuthParams params = AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME;
        JosAppsClient appsClient = JosApps.getJosAppsClient(this);
        Task<Void> initTask;
        // Set the anti-addiction prompt context, this line must be added
        // ???????????????????????????Context?????????????????????
        ResourceLoaderUtil.setmContext(this);
        initTask = appsClient.init(
                new AppParams(params, () -> {
                    ExitApplication.getInstance().exit();
                    // The callback will return in two situations:
                    // 1. When a no-adult, real name user logs in to the game during the day, Huawei will pop up a box to remind the player that the game is not allowed. The player clicks "OK" and Huawei will return to the callback
                    // 2. The no-adult, real name user logs in the game at the time allowed by the state. At 9 p.m., Huawei will pop up a box to remind the player that it is time. The player clicks "I know" and Huawei will return to the callback
                    // You can realize the anti addiction function of the game here, such as saving the game, calling the account to exit the interface or directly the game process
                    // ??????????????????????????????????????????:
                    // 1.??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    // 2.?????????????????????????????????????????????????????????????????????9????????????????????????????????????????????????????????????????????????????????????????????????
                    // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????(???System.exit(0))
                }));
        initTask.addOnSuccessListener(aVoid -> {
            HMSLogHelper.getSingletonInstance().debug(TAG, "init success");
            hasInit = true;
            //  Make sure that the interface of showFloatWindow() is successfully called once after the game has been initialized successfully
            // ?????????????????????????????????????????????????????????????????????
            showFloatWindowNewWay();
            checkUpdate();
        }).addOnFailureListener(
                e -> {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        HMSLogHelper.getSingletonInstance().debug(TAG, "init failed statusCode:" + statusCode);
                        // Error code 7401 indicates that the user did not agree to Huawei joint operations privacy agreement
                        // ????????????7401????????????????????????????????????????????????
                        if (statusCode == JosStatusCodes.JOS_PRIVACY_PROTOCOL_REJECTED) {
                            HMSLogHelper.getSingletonInstance().debug(TAG, "has reject the protocol");
                            // You can exit the game or re-call the init interface.
                            // ????????????????????????????????????????????????????????????
                        }
                        // Handle other error codes.
                        // ???????????????????????????????????????
                    }
                });

    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Show the game buoy.
     * <p>
     * ?????????????????????
     */
    private void showFloatWindowNewWay() {
        if (hasInit) {
            // ????????????init??????????????????????????????
            Games.getBuoyClient(this).showFloatWindow();
        }
    }

    /**
     * Hide the displayed game buoy.
     * <p>
     * ????????????????????????????????????
     */
    private void hideFloatWindowNewWay() {
        Games.getBuoyClient(this).hideFloatWindow();
    }

    public AccountAuthParams getAuthParams() {
        return new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).createParams();
    }

    /**
     * Log in ,and return the login information (or error message) of the Huawei account that has
     * logged in to this application. During this process, the authorization interface will not be
     * displayed to Huawei account users.
     * <p>
     * ????????????????????????????????????????????????????????????(??????????????????)??????????????????????????????????????????????????????????????????
     */
    public void signIn() {
        // ????????????init???????????????????????????????????????
        // Be sure to call the login API after the init is successful
        Task<AuthAccount> authAccountTask = AccountAuthManager.getService(this, getAuthParams()).silentSignIn();
        authAccountTask
                .addOnSuccessListener(
                        authAccount -> {
                            HMSLogHelper.getSingletonInstance().debug(TAG, "signIn success");
                            getCurrentPlayer();
                            photoUri = authAccount.getAvatarUriString();
                        })
                .addOnFailureListener(
                        e -> {
                            if (e instanceof ApiException) {
                                ApiException apiException = (ApiException) e;
                                HMSLogHelper.getSingletonInstance().debug(TAG, "signIn failed:" + apiException.getStatusCode());
                                dismissDialog();
                                signInNewWay();
                            }
                        });
    }

    /**
     * Obtain the Intent of the Huawei account login authorization page, and open the Huawei account
     * login authorization page by calling startActivityForResult(Intent, int).
     * <p>
     * ??????????????????????????????????????????Intent??????????????????startActivityForResult(Intent, int)??? ????????????????????????
     * ????????????
     */
    @SuppressWarnings("deprecation")
    public void signInNewWay() {
        Intent intent = AccountAuthManager.getService(MainActivity.this, getAuthParams()).getSignInIntent();
        startActivityForResult(intent, SIGN_IN_INTENT);
    }

    /**
     * Get the currently logged in player object and get player information from the ???Player??? object.
     * <p>
     * ???????????????????????????????????????Player??????????????????????????????
     */
    public void getCurrentPlayer() {
        PlayersClientImpl client = (PlayersClientImpl) Games.getPlayersClient(this);
        Task<Player> task = client.getCurrentPlayer();
        task.addOnSuccessListener(
                player -> {
                    HMSLogHelper.getSingletonInstance().debug(TAG, player.getDisplayName());
                    // ?????????  ???????????????
                    checkSign(player);
                })
                .addOnFailureListener(
                        e -> {
                            if (e instanceof ApiException) {
                                String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                                dismissDialog();
                                HMSLogHelper.getSingletonInstance().debug(TAG, result);
                                if (((ApiException) e).getStatusCode() == 7400 || ((ApiException) e).getStatusCode() == 7018) {
                                    // 7400??????????????????????????????????????????????????????init??????
                                    // 7018??????????????????????????????????????????init??????
                                    // error code 7400 indicates that the user has not agreed to the joint operations privacy agreement
                                    // error code 7018 indicates that the init API is not called.
                                    init();
                                }
                            }
                        });
    }

    /**
     * ????????????
     *
     * @param player ????????????
     */
    private void checkSign(Player player) {
        @SuppressLint("AllowAllHostnameVerifier") OkHttpClient client = new OkHttpClient().newBuilder()
                .hostnameVerifier(new AllowAllHostnameVerifier())
                .build();
        // ??????FormBody????????????Builder?????????????????????
        FormBody mFormBody = new FormBody.Builder()
                .add("method", "external.hms.gs.checkPlayerSign")
                .add("appId", "105174767")
                .add("cpId", "70086000159286487")
                .add("ts", player.getSignTs())
                .add("playerId", player.getPlayerId())
                .add("playerLevel", player.getLevel() + "")
                .add("playerSSign", player.getPlayerSign())
                .add("openId", player.getOpenId())
                .add("openIdSign", player.getOpenIdSign())
                .build();

        Request request = new Request.Builder()
                .url("https://jos-api.cloud.huawei.com/gameservice/api/gbClientApi")
                .post(mFormBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HMSLogHelper.getSingletonInstance().debug(TAG, e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "check sign failed", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                HMSLogHelper.getSingletonInstance().debug(TAG, response.toString());
                runOnUiThread(() -> {
                    dismissDialog();
                    // ???????????????????????????????????????????????????????????????,?????????????????????ID?????????
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constant.PLAYER_INFO_KEY, player);
                    bundle.putString(Constant.PLAYER_ICON_URI, photoUri);
                    intent.putExtras(bundle);
                    startActivity(intent);
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_INTENT) {
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
            if (null == data) {
                HMSLogHelper.getSingletonInstance().debug(TAG, "signIn intent is null");
                return;
            }
            String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
            if (TextUtils.isEmpty(jsonSignInResult)) {
                HMSLogHelper.getSingletonInstance().debug(TAG, "SignIn result is empty");
                return;
            }
            try {
                AccountAuthResult signInResult = new AccountAuthResult().fromJson(jsonSignInResult);
                if (signInResult.getStatus().getStatusCode() == 0) {
                    HMSLogHelper.getSingletonInstance().debug(TAG, "Sign in success.");
                    photoUri = signInResult.getAccount().getAvatarUriString();
                    getCurrentPlayer();
                } else {
                    dismissDialog();
                    HMSLogHelper.getSingletonInstance().debug(TAG, "Sign in failed: " + signInResult.getStatus().getStatusCode());
                    Toast.makeText(MainActivity.this, "Sign in failed: " + signInResult.getStatus().getStatusCode(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException var7) {
                dismissDialog();
                HMSLogHelper.getSingletonInstance().debug(TAG, "Failed to convert json from signInResult.");
                Toast.makeText(MainActivity.this, "Failed to convert json from signInResult.", Toast.LENGTH_SHORT).show();
            }
        } else {
            dismissDialog();
            HMSLogHelper.getSingletonInstance().debug(TAG, "unknown requestCode in onActivityResult");
            Toast.makeText(MainActivity.this, "unknown requestCode in onActivityResult", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Games released in the Chinese mainland: The update API provided by Huawei must be called upon game launch.
     * Games released outside the Chinese mainland: It is optional for calling the update API provided by Huawei upon
     * game launch.
     * <p>
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????????????????
     */
    public void checkUpdate() {
        AppUpdateClient client = JosApps.getAppUpdateClient(this);
        client.checkAppUpdate(this, new UpdateCallBack(this));
    }

    private class UpdateCallBack implements CheckUpdateCallBack {
        private final WeakReference<Context> mContextWeakReference;

        private UpdateCallBack(Context context) {
            mContextWeakReference = new WeakReference<>(context);
        }

        @Override
        public void onUpdateInfo(Intent intent) {
            if (intent != null) {
                // ??????????????????
                int status = intent.getIntExtra(UpdateKey.STATUS, -99);
                HMSLogHelper.getSingletonInstance().debug(TAG, "check update status is:" + status);
                // ???????????????
                int rtnCode = intent.getIntExtra(UpdateKey.FAIL_CODE, -99);
                // ??????????????????
                String rtnMessage = intent.getStringExtra(UpdateKey.FAIL_REASON);
                // Toast.makeText(MainActivity.this, getString(R.string.update_des) + status, Toast.LENGTH_LONG).show();
                // ????????????????????????????????????????????????????????????????????????????????????
                boolean isExit = intent.getBooleanExtra(UpdateKey.MUST_UPDATE, false);
                HMSLogHelper.getSingletonInstance().debug(TAG, "rtnCode = " + rtnCode + "rtnMessage = " + rtnMessage);

                Serializable info = intent.getSerializableExtra(UpdateKey.INFO);
                // ??????info??????ApkUpgradeInfo??????????????????????????????
                if (info instanceof ApkUpgradeInfo) {
                    Context context = mContextWeakReference.get();
                    if (context != null) {
                        // showUpdateDialog?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????false??????
                        JosApps.getAppUpdateClient(context).showUpdateDialog(context,(ApkUpgradeInfo) info,false);
                    }
                    HMSLogHelper.getSingletonInstance().debug(TAG, "check update success and there is a new update");
                }
                HMSLogHelper.getSingletonInstance().debug(TAG, "check update isExit=" + isExit);
                if (isExit) {
                    // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    System.exit(0);
                }
            }
        }


        // ignored
        // ??????, ????????????
        @Override
        public void onMarketInstallInfo(Intent intent) {
            HMSLogHelper.getSingletonInstance().debug(TAG, "check update failed: info not instance of ApkUpgradeInfo");
        }

        // ignored
        // ??????, ????????????
        @Override
        public void onMarketStoreError(int responseCode) {
            HMSLogHelper.getSingletonInstance().debug(TAG, "check update failed");
        }

        // ignored
        // ??????, ????????????
        @Override
        public void onUpdateStoreError(int responseCode) {
            HMSLogHelper.getSingletonInstance().debug(TAG, "check update failed");
        }
    }

    /**
     * ????????????????????????????????????
     */
    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.login_in_huawei) {
                // ???????????????????????????
                dialog.setCanceledOnTouchOutside(false);
                // ???????????????????????????????????????
                dialog.setCancelable(false);
                dialog.show();
                signIn();
            }
        }
    }
}