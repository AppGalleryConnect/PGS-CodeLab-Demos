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
export default class RoomMgr extends cc.Component {
  @property(cc.Label)
  roomTitle: cc.Label = null;

  @property(cc.Sprite)
  player2: cc.Sprite = null;

  @property(cc.Label)
  waitLabel: cc.Label = null;

  @property(cc.Button)
  startGameBtn: cc.Button = null;

  start() {
    this.setRoomView();
    this.onJoin();
    this.onStartFramesync();
  }

  setRoomView() {
    this.roomTitle.string = '我的房间' + '\n' + 'ID:' + Global.client.room.roomCode;
    this.player2.node.active = false;
    this.waitLabel.node.active = true;
    Global.client.room.players.forEach((player) => {
      if (player.playerId !== Global.client.room.ownerId) {
        this.waitLabel.node.active = false;
        this.player2.node.active = true;
      }
    });
    if (Global.client.room.players.length === 2 && Global.playerId === Global.client.room.ownerId) {
      this.startGameBtn.node.active = true;
    } else {
      this.startGameBtn.node.active = false;
    }
  }

  startGame() {
    Global.room
      .startFrameSync()
      .then(() => {
        cc.log('正在开启帧同步');
      })
      .catch((e) => {
        cc.log('开启帧同步失败,' + e);
      });
  }

  onJoin() {
    Global.room.onJoin((playerInfo) => {
      if (playerInfo.playerId === Global.playerId) {
        cc.log('onJoin广播---进入房间成功');
      } else {
        cc.log('onJoin广播---新玩家进房,id:' + playerInfo.playerId);
        this.setRoomView();
      }
    });
  }

  onStartFramesync() {
    Global.room.onStartFrameSync(() => {
      // 接收帧同步开始通知，处理游戏逻辑
      cc.log('SDK广播---开启帧同步');
      cc.director.loadScene('Game2');
    });
  }
}
