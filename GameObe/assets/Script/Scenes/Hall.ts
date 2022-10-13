/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import Global from '../Global';

const { ccclass } = cc._decorator;

@ccclass
export default class Hall extends cc.Component {
  goSingleMode() {
    cc.director.loadScene('Game1');
  }

  goDualMode() {
    // 联机对战CodeLab使用
    const client = new window.GOBE.Client({
      appId: Global.appId, // 应用ID
      openId: Global.openId, // 玩家ID，区别不同用户
      clientId: Global.clientId, // 客户端ID
      clientSecret: Global.clientSecret, // 客户端密钥
    });
    client
      .init()
      .then((client) => {
        // 初始化成功
        cc.log('init sdk success');
        Global.client = client;
        Global.playerId = client.playerId;
        // 切换RoomEntry场景
        cc.director.loadScene('RoomEntry');
      })
      .catch((e) => {
        // 初始化失败
        cc.log('init sdk fail,errName:' + e);
      });
  }
}
