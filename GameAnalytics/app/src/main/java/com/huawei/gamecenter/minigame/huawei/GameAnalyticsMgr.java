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

import android.app.Activity;
import android.widget.Toast;

import com.huawei.gamecenter.game.analytics.GameAnalytics;
import com.huawei.gamecenter.game.analytics.exception.AnalyticsException;
import com.huawei.gamecenter.game.analytics.model.AnalyticsParam;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.jos.games.player.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameAnalyticsMgr {

    public static boolean hasInitGameHA = false;//游戏分析SDK初始化状态

    public static void initGameHA(Player player, Activity activity) {
        AnalyticsParam analyticsParam = new AnalyticsParam();
        analyticsParam.setActivity(activity);// 游戏应用的Activity
        analyticsParam.setAgcClientSecret("73**************************************12");// 游戏应用在AGC项目中的客户端密钥
        analyticsParam.setAgcClientId("41****************48");// 游戏应用在AGC项目中的客户端ID
        analyticsParam.setPlayer(player);// 游戏Player对象
        analyticsParam.setAutoReportThreshold(10);// 设置上报阈值，单位为KB，默认为10KB。（可选）

        Task<Void> task = GameAnalytics.initWithKey(analyticsParam);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //初始化成功
                hasInitGameHA = true;
                GameActivity.showToast("游戏分析初始化成功");
                //设置公共属性
                Map<String, String> map = new HashMap<>();
                map.put("playerId", player.getPlayerId());
                map.put("playerName", player.getDisplayName());
                int resultcode = GameAnalytics.setCommonProp(map); // 成功返回0，失败返回错误码
                if (resultcode == 0) {
                    GameActivity.showToast("公共参数设置成功");
                } else {
                    GameActivity.showToast("公共参数设置失败");
                }
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // 初始化失败
                hasInitGameHA = false;
                if (exception instanceof AnalyticsException) {
                    AnalyticsException analyticsException = (AnalyticsException) exception;
                    GameActivity.showToast("游戏分析初始化失败，+'\n'+" + analyticsException.code + analyticsException.errMsg);
                }
            }
        });
    }

    public static void reportOnStreamEvent(String eventId, LinkedHashMap<String, String> paramMap) {
        // 数据上报
        if (hasInitGameHA) {
            int resultCode = GameAnalytics.onStreamEvent(eventId, paramMap); // 成功返回0，失败返回错误码
            if (resultCode == 0) {
                GameActivity.showToast(eventId + "事件上报成功" + '\n' + paramMap.toString());
            } else {
                GameActivity.showToast(eventId + "事件上报失败" + '\n' + paramMap.toString());
            }
        } else {
            GameActivity.showToast("请先初始化游戏分析服务");
        }

    }
}
