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
export default class Start extends cc.Component {
  start() {
    this.gamelogin();
  }
  goToGame() {
    cc.director.loadScene('Hall');
  }

  gamelogin() {
    qg.gameLoginWithReal({
      forceLogin: 1,
      appid: '105386931',
      success(data) {
        // 登录成功后，可以存储帐号信息。
        Global.openId = data.playerId;
        Global.nickName = data.displayName;
        Global.playerIconUrl = data.imageUri;
        cc.log('openId:' + Global.openId);
        cc.log('玩家昵称：' + Global.nickName);
      },
      fail(data, code) {
        cc.log('game login with real fail:' + data + ', code:' + code);
        // 根据状态码处理游戏的逻辑。
        // 状态码为7004或者2012，表示玩家取消登录。
        // 此时，建议返回游戏界面，可以让玩家重新进行登录操作。
        if (code === 7004 || code === 2012) {
          cc.log('玩家取消登录，返回游戏界面让玩家重新登录。');
        }
        // 状态码为7021表示玩家取消实名认证。
        // 在中国大陆的情况下，此时需要禁止玩家进入游戏。
        if (code === 7021) {
          cc.log('The player has canceled identity verification. Forbid the player from entering the game.');
        }
      },
    });
  }
}
