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


package com.huawei.gamecenter.minigame.huawei.Until;

import android.util.Log;

public class HMSLogHelper {
    private static final String HW_LOG_PRE = "HmsSdk_";
    private static HMSLogHelper singletonInstance;

    private HMSLogHelper() {
    }

    public static HMSLogHelper getSingletonInstance() {
        if (singletonInstance == null) {
            singletonInstance = new HMSLogHelper();
        }
        return singletonInstance;
    }

    public void debug(String tag, String msg) {
        Log.d(HW_LOG_PRE + tag, msg);
    }

    public void error(String tag, String msg) {
        Log.e(HW_LOG_PRE + tag, msg);
    }
}
