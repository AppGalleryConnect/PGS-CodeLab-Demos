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

const { ccclass, property } = cc._decorator;

@ccclass
export default class JoinRoom extends cc.Component {
  @property(cc.EditBox)
  inputEdt: cc.EditBox = null;

  joinRoom() {
    const roomId = this.inputEdt.string;
    Global.client
      .joinRoom(roomId)
      .then((room) => {
        // 加入房间成功
        Global.room = room;
        Global.playerId = room.playerId;
        // 注册成功加入房间监听
        this.onJoin();
      })
      .catch((e) => {
        // 加入房间失败
        cc.log('加入房间失败，错误信息为:' + e);
      });
  }

  onJoin() {
    Global.room.onJoin((playerInfo) => {
      if (playerInfo.playerId === Global.playerId) {
        cc.log('onJoin广播---进入房间成功');
        cc.director.loadScene('Room');
      } else {
        cc.log('onJoin广播---新玩家进房,id:' + playerInfo.playerId);
      }
    });
  }
}
