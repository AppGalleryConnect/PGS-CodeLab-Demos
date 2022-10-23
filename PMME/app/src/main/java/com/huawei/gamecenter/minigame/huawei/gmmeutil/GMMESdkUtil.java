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


package com.huawei.gamecenter.minigame.huawei.gmmeutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;

import com.huawei.appgallery.log.Log;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.EngineCreateParams;
import com.huawei.gamecenter.minigame.huawei.GameActivity;
import com.huawei.gamecenter.minigame.huawei.R;

import java.io.IOException;
import java.util.List;

public class GMMESdkUtil {
    private static final String TAG = "GMMESdkUtil";

    @SuppressLint("StaticFieldLeak")
    private static GameActivity gameActivity;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static GameMediaEngine mMediaEngine;

    public static GameMediaEngine initGMMESDK(String openId, Context context) {
        gameActivity = new GameActivity();
        mContext = context;
        EngineCreateParams engineCreateParams = new EngineCreateParams();
        engineCreateParams.setOpenId(openId);  // 自定义的玩家标识
        engineCreateParams.setContext(context); // 应用的上下文
        engineCreateParams.setLogEnable(true); // 开启SDK日志记录
        try {
            engineCreateParams.setLogPath(context.getFilesDir().getCanonicalPath()); // 设置SDK日志的存储位置,这里存储的位置为/data/data/<package name>/files/
        } catch (IOException e) {
            e.printStackTrace();
        }
        engineCreateParams.setLogSize(1024 * 10); // 日志存储大小
        engineCreateParams.setCountryCode("CN"); // 国家码，用于网关路由，不设置默认CN
        engineCreateParams.setAgcAppId(ConfigFile.appId); // 游戏在AGC上注册的APP ID
        engineCreateParams.setClientId(ConfigFile.clientId); // 游戏在AGC上的客户端ID
        engineCreateParams.setClientSecret(ConfigFile.clientSecret); // 客户端ID对应的秘钥
        engineCreateParams.setApiKey(ConfigFile.apiKey); // 游戏在AGC上的API秘钥（凭据）
        mMediaEngine = GameMediaEngine.create(engineCreateParams, eventHandler);
        return mMediaEngine;
    }

    private static final IGameMMEEventHandler eventHandler = new IGameMMEEventHandler() {
        @Override
        public void onCreate(int code, String msg) {
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (MyCustomCode.RESPONSE_SUCCESS.getCode() == code) {
                        ConfigFile.GMMSDK_IS_INIT = true;
                        GameActivity.showToast(mContext.getResources().getString(R.string.init_success));
                    } else {
                        ConfigFile.GMMSDK_IS_INIT = false;
                        GameActivity.showToast(mContext.getResources().getString(R.string.init_failed) + ", returnCode = " + code + ", returnMsg = " + msg);
                    }
                }
            });
        }

        @Override
        public void onMutePlayer(String s, String s1, boolean b, int i, String s2) {

        }

        @Override
        public void onMuteAllPlayers(String s, List<String> list, boolean b, int i, String s1) {

        }

        @Override
        public void onJoinTeamRoom(String roomId, int code, String msg) {
            Log.i(TAG, "onJoinTeamRoom roomId = " + roomId + ", returnCode = " + code + ", msg = " + msg);
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (MyCustomCode.RESPONSE_SUCCESS.getCode() == code) {
                        mMediaEngine.enableSpeakersDetection(roomId, 1000);
                        Log.e(TAG, "enableSpeakersDetection方法已调用");
                        GameActivity.showToast("成功加入房间");
                    } else {
                        GameActivity.showToast("加入房间失败" + ", code = " + code + ", msg = " + msg);
                    }
                }
            });
        }

        @Override
        public void onJoinNationalRoom(String s, int i, String s1) {

        }

        @Override
        public void onSwitchRoom(String s, int i, String s1) {

        }

        @Override
        public void onLeaveRoom(String s, int i, String s1) {

        }

        @Override
        public void onSpeakersDetection(List<String> openIds) {
            Log.i(TAG, "--------onSpeakersDetection--------->" + openIds);
            if (openIds != null && openIds.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(mContext.getResources().getString(R.string.players));
                for (int i = 0; i < openIds.size(); i++) {
                    sb.append(openIds.get(i));
                    if (i != openIds.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(" 在发言");
                Log.i(TAG, "--------onSpeakersDetection--------->" + sb.toString());
                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GameActivity.showToast(sb.toString());
                    }
                });
            }
        }

        @Override
        public void onForbidAllPlayers(String s, List<String> list, boolean b, int i, String s1) {

        }

        @Override
        public void onForbidPlayer(String s, String s1, boolean b, int i, String s2) {

        }

        @Override
        public void onForbiddenByOwner(String s, List<String> list, boolean b) {

        }

        @Override
        public void onVoiceToText(String s, int i, String s1) {

        }

        @Override
        public void onPlayerOnline(String roomId, String openId) {
            Log.i(TAG, "playerOnLine roomId = " + roomId + ", openId = " + openId);
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GameActivity.showToast(mContext.getResources().getString(R.string.player_online) + openId + " " + mContext.getResources().getString(R.string.join_room));
                }
            });

        }

        @Override
        public void onPlayerOffline(String s, String s1) {

        }

        @Override
        public void onTransferOwner(String s, int i, String s1) {

        }

        @Override
        public void onDestroy(int code, String msg) {
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (MyCustomCode.RESPONSE_SUCCESS.getCode() == code) {
                        GameActivity.showToast(mContext.getResources().getString(R.string.destroy_success));
                    } else {
                        GameActivity.showToast(mContext.getResources().getString(R.string.destroy_failed) + ", returnCode = " + code + ", returnMsg = " + msg);
                    }
                }
            });
        }
    };
}
