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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.gamecenter.minigame.huawei.UI.MyCustomView;
import com.huawei.gamecenter.minigame.huawei.Until.Constant;
import com.huawei.gamecenter.minigame.huawei.Until.HMSLogHelper;
import com.huawei.gamecenter.minigame.huawei.Until.UntilTool;
import com.huawei.gamecenter.minigame.huawei.gmmeutil.ConfigFile;
import com.huawei.gamecenter.minigame.huawei.gmmeutil.GMMESdkUtil;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.jos.games.player.PlayersClientImpl;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AccountAuthResult;
import com.huawei.hms.support.account.result.AuthAccount;

import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MiniGame_Game_Act";
    private static final int SIGN_IN_INTENT = 3000;
    private static String currentId;
    private static String photoUri = null;
    private int currentLevel = 0;
    private long timeSecond = 0;
    private int currentScore = 0;

    private Player mPlayer;
    private MyCustomView gameDrawView;
    private TextView tvScore;
    private TextView tvTime;
    private TextView tvUserName;
    private CircleImageView gamePhoto;
    private TextView gameTopLevel;
    private Button gameOnStart;
    private Button gameOnPause;
    private AlertDialog alertDialog;
    private CountDownTimer countDownTimer;
    private static Context context;

    // ??????????????????
    private EditText openIdEt; // ???????????????????????????ID
    public static EditText roomIdEt; // ???????????????????????????Id
    private TextView initTv;  // ???????????????????????????
    private TextView joinRoomTv; // ??????????????????????????????
    private GameMediaEngine mMediaEngine;
    // ????????????
    private final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
    private static final int OPEN_SET_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ??????????????????(??????Android??????????????????)???(????????????????????????)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ?????????????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_view);
        mPlayer = getIntent().getParcelableExtra(Constant.PLAYER_INFO_KEY);
        currentId = mPlayer.getPlayerId();
        photoUri = getIntent().getStringExtra(Constant.PLAYER_ICON_URI);
        init();
        ExitApplication.getInstance().addActivity(this);
        context = GameActivity.this;

        initGMMEView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UntilTool.getInfo(this, mPlayer.getPlayerId()) == 0) {
            currentScore = 30;
        }
        currentScore = UntilTool.getInfo(this, mPlayer.getPlayerId());
        initData(mPlayer);
    }

    private void initData(Player mPlayer) {
        if (TextUtils.isEmpty(currentId)) {
            signIn();
        } else {
            tvUserName.setText(mPlayer.getDisplayName());
            setCircleImageView(photoUri, gamePhoto);
            gameLevelSetting(currentLevel);
            gameScoreSetting(currentScore);
        }
    }

    /**
     * MyCustomView.BeatEnemyListener   ?????????view??????????????????
     */
    private void init() {
        tvScore = findViewById(R.id.game_top_score);
        tvTime = findViewById(R.id.game_top_time);
        gameDrawView = findViewById(R.id.mini_game_view);
        gameOnStart = findViewById(R.id.game_onStart);
        gameOnStart.setOnClickListener(this);
        gameOnPause = findViewById(R.id.game_onPause);
        gameOnPause.setOnClickListener(this);
        tvUserName = findViewById(R.id.game_top_username);
        gamePhoto = findViewById(R.id.game_top_avatar);
        gamePhoto.setImageResource(R.mipmap.game_photo_man);
        gameTopLevel = findViewById(R.id.game_top_level);
        gameLevelSetting(currentLevel);
        gameScoreSetting(currentScore);

        gameDrawView.setBeatEnemyListener(new MyCustomView.BeatEnemyListener() {
            @Override
            public void onBeatEnemy(int showMode) {
                currentScore = currentScore + 3;
                gameScoreSetting(currentScore);
            }

            @Override
            public void onFire() {
                if (currentScore <= 1) {
                    cancelTimeCount();
                    gameDrawView.gameSwitch(true);
                    showAlertDialog(Constant.MODE_ONE);
                }
                currentScore--;
                gameScoreSetting(currentScore);
            }

            @Override
            public void gameEnd(int i) {
                cancelTimeCount();
                showAlertDialog(Constant.M0DE_TWO);
            }
        });
    }

    /**
     * ?????????????????????????????????
     */
    private void initGMMEView() {
        openIdEt = findViewById(R.id.openIdEt);
        roomIdEt = findViewById(R.id.roomIdEt);
        initTv = findViewById(R.id.initTv);
        joinRoomTv = findViewById(R.id.joinRoomTv);

        initTv.setOnClickListener(this);
        joinRoomTv.setOnClickListener(this);

        initPermissions();
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private void initPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, OPEN_SET_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == OPEN_SET_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "The permission application is rejected.");
                        return;
                    }
                    Log.d(TAG, "The permission application is allowed.");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.game_onStart) {
            gameDrawView.gameSwitch(false);
            gameOnStart.setVisibility(View.GONE);
            gameOnPause.setVisibility(View.VISIBLE);
            initTimeCount(timeSecond);
        }
        if (v.getId() == R.id.game_onPause) {
            if (!MyCustomView.isRefresh) {
                gameDrawView.gameSwitch(true);
                gameOnStart.setVisibility(View.VISIBLE);
                gameOnPause.setVisibility(View.INVISIBLE);
                updateScoreAndLevel();
                UntilTool.addInfo(this, currentId, currentScore);
                cancelTimeCount();
            }
        }
        // ????????????????????????????????????
        if (v.getId() == R.id.initTv) { // ?????????????????????
            String openId = openIdEt.getText().toString();
            if (TextUtils.isEmpty(openId)) {
                showToast("?????????opendId");
                return;
            }
            mMediaEngine = GMMESdkUtil.initGMMESDK(openId, GameActivity.this);
        } else if (v.getId() == R.id.joinRoomTv) { // ????????????????????????
            String roomId = roomIdEt.getText().toString();
            if (TextUtils.isEmpty(roomId)) {
                showToast("?????????roomId");
                return;
            }
            if (ConfigFile.GMMSDK_IS_INIT) {
                mMediaEngine.joinTeamRoom(roomId);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameDrawView.gameSwitch(true);
        gameOnStart.setVisibility(View.VISIBLE);
        gameOnPause.setVisibility(View.INVISIBLE);
        cancelTimeCount();
        updateScoreAndLevel();
        UntilTool.addInfo(this, currentId, currentScore);
    }

    /**
     * @param s ??????????????????????????????
     */
    public static void showToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    /**
     * @param clickMode ????????????????????????
     */
    private void showAlertDialog(int clickMode) {
        if (clickMode == Constant.MODE_THREE) {
            alertDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.game_continue)
                    .setCancelable(false)
                    .create();
            alertDialog.show();
            alertDialog.findViewById(R.id.btn_click_continue).setOnClickListener(v -> {
                currentLevel = ++currentLevel;
                gameLevelSetting(currentLevel);
                gameDrawView.setEmSpd(Constant.MODE_THREE, currentLevel);
                gameDrawView.gameSwitch(false);
                updateScoreAndLevel();
                // ???????????????
                initTimeCount(timeSecond);
                alertDialog.dismiss();
            });
        }
        // ?????????????????? ??????????????????
        if (clickMode == Constant.MODE_ONE) {
            alertDialog = new AlertDialog.Builder(this, R.style.simpleDialogStyle)
                    .setCancelable(false)
                    .create();
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.game_score_use_up, null);
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.findViewById(R.id.btn_scoreUseUp).setOnClickListener(v14 -> {
                currentScore = 30 + currentScore;
                showToast(getString(R.string.GameToast_successfulBuySore));
                gameScoreSetting(currentScore);
                gameDrawView.gameSwitch(false);
                updateScoreAndLevel();
                // ???????????????
                initTimeCount(timeSecond);
                alertDialog.dismiss();
            });
        }
        if (clickMode == Constant.M0DE_TWO) {
            alertDialog = new AlertDialog.Builder(this, R.style.simpleDialogStyle)
                    .setCancelable(false)
                    .create();
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.game_failed, null);
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.findViewById(R.id.btn_startAgain).setOnClickListener(v1 -> {
                gameDrawView.gameSwitch(false);
                gameDrawView.setEmSpd(Constant.M0DE_TWO, currentLevel);
                // ???????????????
                initTimeCount(timeSecond);
                // ???????????????????????????
                updateScoreAndLevel();
                alertDialog.dismiss();
            });
        }
        // ?????????????????????????????????????????????  ????????????
        if (clickMode == Constant.MODE_FOUR) {
            alertDialog = new AlertDialog.Builder(this, R.style.simpleDialogStyle)
                    .setCancelable(false)
                    .create();
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.game_clearance, null);
            alertDialog.show();
            alertDialog.getWindow().setContentView(v);
            alertDialog.findViewById(R.id.game_Again).setOnClickListener(v12 -> {
                currentLevel = 0;
                updateScoreAndLevel();
                gameLevelSetting(currentLevel);
                gameDrawView.setEmSpd(Constant.MODE_THREE, currentLevel);
                gameDrawView.gameSwitch(false);
                // ???????????????
                initTimeCount(timeSecond);
                alertDialog.dismiss();
            });
            // ????????????
            alertDialog.findViewById(R.id.game_exit).setOnClickListener(v13 -> {
                // ??????????????????,?????????????????????
                GameActivity.this.finish();
                alertDialog.dismiss();
            });
        }
    }

    private void initTimeCount(long time) {
        long timeReset;

        if (time > 0) {
            timeReset = time;
        } else {
            timeReset = 45;
        }
        countDownTimer = new CountDownTimer(1000 * timeReset, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeSecond = millisUntilFinished / 1000;
                HMSLogHelper.getSingletonInstance().debug(TAG, millisUntilFinished + "time : " + timeSecond);
                gameTimeSetting((int) timeSecond);
            }

            @Override
            public void onFinish() {
                HMSLogHelper.getSingletonInstance().debug(TAG, "time finish : " + timeSecond);
                timeSecond = 0;
                showAlertDialog(Constant.MODE_THREE);
                gameDrawView.gameSwitch(true);
            }
        }.start();
    }

    private void cancelTimeCount() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * @param levelNumber ????????????????????????
     */
    private void gameLevelSetting(int levelNumber) {
        String format = String.format(getString(R.string.GameToast_levelSetting), levelNumber + 1);
        if (levelNumber >= 9) {
            showAlertDialog(Constant.MODE_FOUR);
        } else {
            gameTopLevel.setText(format);
        }
    }

    /**
     * @param score ????????????????????????
     *              ???????????????????????????????????????
     */
    @SuppressLint("SetTextI18n")
    private void gameScoreSetting(int score) {
        tvScore.setText("         " + score);
    }

    /**
     * @param timeSecond ????????????????????????
     */
    @SuppressLint("SetTextI18n")
    private void gameTimeSetting(int timeSecond) {
        tvTime.setText(timeSecond + " s");
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
        Task<AuthAccount> authAccountTask = AccountAuthManager.getService(this, getAuthScopeParams()).silentSignIn();
        authAccountTask
                .addOnSuccessListener(
                        authAccount -> {
                            getCurrentPlayer();
                            photoUri = authAccount.getAvatarUriString();
                        })
                .addOnFailureListener(
                        e -> {
                            if (e instanceof ApiException) {
                                signInNewWay();
                            }
                        });
    }

    @SuppressWarnings("deprecation")
    public void signInNewWay() {
        Intent intent = AccountAuthManager.getService(this, getAuthScopeParams()).getSignInIntent();
        startActivityForResult(intent, SIGN_IN_INTENT);
    }

    public AccountAuthParams getAuthScopeParams() {
        return new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).createParams();
    }

    /**
     * Get the currently logged in player object and get player information from the ???Player??? object.
     * ???????????????????????????????????????Player??????????????????????????????
     */
    public void getCurrentPlayer() {
        PlayersClientImpl client = (PlayersClientImpl) Games.getPlayersClient(this);
        Task<Player> task = client.getCurrentPlayer();
        task.addOnSuccessListener(
                        player -> {
                            currentId = player.getPlayerId();
                            initData(player);
                        })
                .addOnFailureListener(
                        e -> {
                            if (e instanceof ApiException) {
                                String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                                if (((ApiException) e).getStatusCode() == 7400 || ((ApiException) e).getStatusCode() == 7018) {
                                    // 7400??????????????????????????????????????????????????????init??????
                                    // 7018??????????????????????????????????????????init??????
                                    // error code 7400 indicates that the user has not agreed to the joint operations privacy agreement
                                    // error code 7018 indicates that the init API is not called.
                                    HMSLogHelper.getSingletonInstance().debug(TAG, "getCurrentPlayer failed result is :" + result);
                                }
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_INTENT) {
            if (null == data) {
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
                    HMSLogHelper.getSingletonInstance().debug(TAG, "Sign in result: " + signInResult.toJson());
                    photoUri = signInResult.getAccount().getAvatarUriString();
                    getCurrentPlayer();
                } else {

                    HMSLogHelper.getSingletonInstance().debug(TAG, "Sign in failed: " + signInResult.getStatus().getStatusCode());
                    Toast.makeText(this, "Sign in failed: " + signInResult.getStatus().getStatusCode(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException var7) {

                HMSLogHelper.getSingletonInstance().debug(TAG, "Failed to convert json from signInResult.");
                Toast.makeText(this, "Failed to convert json from signInResult.", Toast.LENGTH_SHORT).show();
            }
        } else {
            HMSLogHelper.getSingletonInstance().debug(TAG, "unknown requestCode in onActivityResult");
            Toast.makeText(this, "unknown requestCode in onActivityResult", Toast.LENGTH_SHORT).show();
        }
    }

    private void setCircleImageView(String url, ImageView imageView) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(GameActivity.this)
                    .load(url)
                    .placeholder(R.mipmap.game_photo_man)
                    .fitCenter()
                    .into(imageView);
        }
    }

    private void updateScoreAndLevel() {
        tvUserName.setText(mPlayer.getDisplayName());
        setCircleImageView(photoUri, gamePhoto);
        gameLevelSetting(currentLevel);
        gameScoreSetting(currentScore);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UntilTool.addInfo(this, currentId, currentScore);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UntilTool.addInfo(this, currentId, currentScore);
        GameMediaEngine.destroy();
    }
}